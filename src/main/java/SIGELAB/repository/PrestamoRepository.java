
package SIGELAB.repository;

import SIGELAB.config.DatabaseConnection;
import SIGELAB.model.Prestamo;
import SIGELAB.util.JdbcSupport;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PrestamoRepository {

    public boolean guardar(
            Prestamo prestamo,
            List<Integer> equiposIds) {

        // Validar datos obligatorios.
        if (prestamo == null
                || equiposIds == null
                || equiposIds.isEmpty()) {

            throw new IllegalArgumentException(
                    "Debe indicar el préstamo y al menos un equipo."
            );
        }

        if (prestamo.getFechaPrestamo() == null
                || prestamo.getFechaDevolucionEsperada() == null) {

            throw new IllegalArgumentException(
                    "Las fechas del préstamo son obligatorias."
            );
        }

        // Evitar equipos repetidos o inválidos.
        Set<Integer> idsUnicos = new HashSet<>(equiposIds);

        if (idsUnicos.size() != equiposIds.size()
                || idsUnicos.stream().anyMatch(
                id -> id == null || id <= 0)) {

            throw new IllegalArgumentException(
                    "La lista contiene equipos repetidos o inválidos."
            );
        }

        // Ordenar los equipos para bloquearlos
        // siempre en el mismo orden.
        List<Integer> idsOrdenados =
                new ArrayList<>(equiposIds);

        idsOrdenados.sort(Integer::compareTo);

        String validarEquipo = """
                SELECT estado
                FROM Equipo WITH (UPDLOCK, HOLDLOCK)
                WHERE id = ?
                """;

        String insertarPrestamo = """
                INSERT INTO Prestamo
                (solicitante, fecha_prestamo,
                 fecha_devolucion_esperada, usuario_id,
                 estado, observaciones)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        String insertarDetalle = """
                INSERT INTO DetallePrestamo
                (prestamo_id, equipo_id, estado_devolucion)
                VALUES (?, ?, ?)
                """;

        String actualizarEquipo = """
                UPDATE Equipo
                SET estado = 'PRESTADO'
                WHERE id = ?
                  AND estado = 'DISPONIBLE'
                """;

        String insertarAuditoria = """
                INSERT INTO Auditoria
                (usuario_id, accion, entidad,
                 registro_id, descripcion)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (Connection conexion =
                     DatabaseConnection.getConnection()) {

            conexion.setAutoCommit(false);

            try {

                // 1. Validar disponibilidad de equipos.
                try (PreparedStatement ps =
                             conexion.prepareStatement(validarEquipo)) {

                    for (int equipoId : idsOrdenados) {

                        ps.setInt(1, equipoId);

                        try (ResultSet rs = ps.executeQuery()) {

                            if (!rs.next()) {

                                throw new IllegalStateException(
                                        "No existe el equipo #"
                                                + equipoId
                                );
                            }

                            String estado =
                                    rs.getString("estado");

                            if (!"DISPONIBLE".equalsIgnoreCase(estado)) {

                                throw new IllegalStateException(
                                        "El equipo #" + equipoId
                                                + " no está disponible. Estado: "
                                                + estado
                                );
                            }
                        }
                    }
                }

                // 2. Registrar el préstamo.
                int prestamoId;

                try (PreparedStatement ps =
                             conexion.prepareStatement(
                                     insertarPrestamo,
                                     Statement.RETURN_GENERATED_KEYS
                             )) {

                    ps.setString(
                            1,
                            prestamo.getSolicitante()
                    );

                    ps.setTimestamp(
                            2,
                            Timestamp.valueOf(
                                    prestamo.getFechaPrestamo()
                            )
                    );

                    ps.setTimestamp(
                            3,
                            Timestamp.valueOf(
                                    prestamo.getFechaDevolucionEsperada()
                            )
                    );

                    ps.setInt(
                            4,
                            prestamo.getUsuarioId()
                    );

                    ps.setString(5, "ACTIVO");

                    ps.setString(
                            6,
                            prestamo.getObservaciones()
                    );

                    if (ps.executeUpdate() != 1) {

                        throw new SQLException(
                                "No se pudo registrar el préstamo."
                        );
                    }

                    try (ResultSet keys =
                                 ps.getGeneratedKeys()) {

                        if (!keys.next()) {

                            throw new SQLException(
                                    "No se obtuvo el ID del préstamo."
                            );
                        }

                        prestamoId = keys.getInt(1);
                    }
                }

                // 3. Actualizar equipos y guardar detalles.
                try (PreparedStatement equipo =
                             conexion.prepareStatement(
                                     actualizarEquipo
                             );

                     PreparedStatement detalle =
                             conexion.prepareStatement(
                                     insertarDetalle
                             )) {

                    for (int equipoId : idsOrdenados) {

                        equipo.setInt(1, equipoId);

                        if (equipo.executeUpdate() != 1) {

                            throw new SQLException(
                                    "No se pudo actualizar el equipo #"
                                            + equipoId
                            );
                        }

                        detalle.setInt(1, prestamoId);
                        detalle.setInt(2, equipoId);
                        detalle.setString(3, "PENDIENTE");

                        if (detalle.executeUpdate() != 1) {

                            throw new SQLException(
                                    "No se pudo guardar el detalle del equipo #"
                                            + equipoId
                            );
                        }
                    }
                }

                // 4. Registrar la operación en auditoría.
                try (PreparedStatement ps =
                             conexion.prepareStatement(
                                     insertarAuditoria
                             )) {

                    ps.setInt(
                            1,
                            prestamo.getUsuarioId()
                    );

                    ps.setString(
                            2,
                            "REGISTRAR_PRESTAMO"
                    );

                    ps.setString(
                            3,
                            "Prestamo"
                    );

                    ps.setInt(
                            4,
                            prestamoId
                    );

                    ps.setString(
                            5,
                            "Préstamo registrado para "
                                    + prestamo.getSolicitante()
                                    + ". Equipos: "
                                    + idsOrdenados
                    );

                    if (ps.executeUpdate() != 1) {

                        throw new SQLException(
                                "No se pudo registrar la auditoría."
                        );
                    }
                }

                // 5. Confirmar toda la operación.
                conexion.commit();

                prestamo.setId(prestamoId);
                prestamo.setEstado("ACTIVO");

                return true;

            } catch (SQLException | RuntimeException e) {

                // Revertir los cambios si ocurre un error.
                try {

                    conexion.rollback();

                } catch (SQLException rollbackError) {

                    e.addSuppressed(rollbackError);
                }

                throw e;
            }

        } catch (SQLException e) {

            throw JdbcSupport.error(
                    "registrar el préstamo",
                    e
            );
        }
    }
}
