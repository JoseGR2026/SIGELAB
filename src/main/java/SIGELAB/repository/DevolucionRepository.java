
package SIGELAB.repository;

import SIGELAB.config.DatabaseConnection;
import SIGELAB.util.JdbcSupport;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DevolucionRepository {

    /**
     * Lista los equipos que todavía están pendientes
     * de devolución.
     */
    public List<Object[]> listarPendientes() {

        List<Object[]> resultados = new ArrayList<>();

        String sql = """
                SELECT
                    p.id AS prestamo_id,
                    p.solicitante,
                    d.id AS detalle_id,
                    e.codigo_inventario,
                    e.nombre,
                    p.fecha_devolucion_esperada
                FROM Prestamo p
                INNER JOIN DetallePrestamo d
                    ON d.prestamo_id = p.id
                INNER JOIN Equipo e
                    ON e.id = d.equipo_id
                WHERE d.estado_devolucion = 'PENDIENTE'
                  AND p.estado IN ('ACTIVO', 'VENCIDO')
                ORDER BY p.id, d.id
                """;

        try (Connection conexion =
                     DatabaseConnection.getConnection();

             PreparedStatement ps =
                     conexion.prepareStatement(sql);

             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                resultados.add(new Object[]{
                        rs.getInt("prestamo_id"),
                        rs.getString("solicitante"),
                        rs.getInt("detalle_id"),
                        rs.getString("codigo_inventario"),
                        rs.getString("nombre"),
                        rs.getTimestamp("fecha_devolucion_esperada")
                });
            }

            return resultados;

        } catch (SQLException e) {

            throw JdbcSupport.error(
                    "consultar devoluciones pendientes",
                    e
            );
        }
    }

    /**
     * Registra la devolución de un equipo.
     *
     * Todos los cambios se ejecutan dentro de
     * una única transacción de SQL Server.
     */
    public boolean registrarDevolucion(
            int detalleId,
            String condicion,
            int usuarioId) {

        if (detalleId <= 0 || usuarioId <= 0) {

            throw new IllegalArgumentException(
                    "El detalle y el usuario deben ser válidos."
            );
        }

        if (!"BUENO".equals(condicion)
                && !"DANADO".equals(condicion)) {

            throw new IllegalArgumentException(
                    "La condición debe ser BUENO o DANADO."
            );
        }

        String consultarPrestamoId = """
                SELECT prestamo_id
                FROM DetallePrestamo
                WHERE id = ?
                """;

        String bloquearPrestamo = """
                SELECT estado
                FROM Prestamo WITH (UPDLOCK, HOLDLOCK)
                WHERE id = ?
                """;

        String consultarDetalle = """
                SELECT
                    d.equipo_id,
                    d.estado_devolucion,
                    e.estado AS estado_equipo
                FROM DetallePrestamo d WITH (UPDLOCK, HOLDLOCK)
                INNER JOIN Equipo e WITH (UPDLOCK, HOLDLOCK)
                    ON e.id = d.equipo_id
                WHERE d.id = ?
                  AND d.prestamo_id = ?
                """;

        String actualizarDetalle = """
                UPDATE DetallePrestamo
                SET estado_devolucion = ?
                WHERE id = ?
                  AND prestamo_id = ?
                  AND estado_devolucion = 'PENDIENTE'
                """;

        String actualizarEquipo = """
                UPDATE Equipo
                SET estado = ?
                WHERE id = ?
                  AND estado = 'PRESTADO'
                """;

        String contarPendientes = """
                SELECT COUNT(*)
                FROM DetallePrestamo
                WHERE prestamo_id = ?
                  AND estado_devolucion = 'PENDIENTE'
                """;

        String finalizarPrestamo = """
                UPDATE Prestamo
                SET estado = 'FINALIZADO'
                WHERE id = ?
                  AND estado IN ('ACTIVO', 'VENCIDO')
                """;

        String insertarAuditoria = """
                INSERT INTO Auditoria
                (usuario_id, accion, entidad, registro_id, descripcion)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (Connection conexion =
                     DatabaseConnection.getConnection()) {

            conexion.setAutoCommit(false);

            try {

                int prestamoId;
                int equipoId;

                // 1. Identificar el préstamo.
                try (PreparedStatement ps =
                             conexion.prepareStatement(
                                     consultarPrestamoId
                             )) {

                    ps.setInt(1, detalleId);

                    try (ResultSet rs = ps.executeQuery()) {

                        if (!rs.next()) {

                            throw new IllegalStateException(
                                    "No existe el detalle seleccionado."
                            );
                        }

                        prestamoId = rs.getInt("prestamo_id");
                    }
                }

                // 2. Bloquear el préstamo durante la operación.
                // Esto serializa las devoluciones
                // de un mismo préstamo.
                try (PreparedStatement ps =
                             conexion.prepareStatement(
                                     bloquearPrestamo
                             )) {

                    ps.setInt(1, prestamoId);

                    try (ResultSet rs = ps.executeQuery()) {

                        if (!rs.next()) {

                            throw new IllegalStateException(
                                    "No existe el préstamo seleccionado."
                            );
                        }

                        String estado =
                                rs.getString("estado");

                        if (!"ACTIVO".equalsIgnoreCase(estado)
                                && !"VENCIDO".equalsIgnoreCase(estado)) {

                            throw new IllegalStateException(
                                    "El préstamo no permite devoluciones. "
                                            + "Estado: " + estado
                            );
                        }
                    }
                }

                // 3. Bloquear y validar el detalle y el equipo.
                try (PreparedStatement ps =
                             conexion.prepareStatement(
                                     consultarDetalle
                             )) {

                    ps.setInt(1, detalleId);
                    ps.setInt(2, prestamoId);

                    try (ResultSet rs = ps.executeQuery()) {

                        if (!rs.next()) {

                            throw new IllegalStateException(
                                    "No existe el detalle del préstamo."
                            );
                        }

                        if (!"PENDIENTE".equalsIgnoreCase(
                                rs.getString("estado_devolucion"))) {

                            throw new IllegalStateException(
                                    "Este equipo ya fue devuelto."
                            );
                        }

                        if (!"PRESTADO".equalsIgnoreCase(
                                rs.getString("estado_equipo"))) {

                            throw new IllegalStateException(
                                    "El equipo no está en estado PRESTADO."
                            );
                        }

                        equipoId = rs.getInt("equipo_id");
                    }
                }

                String nuevoEstadoEquipo =
                        "BUENO".equals(condicion)
                                ? "DISPONIBLE"
                                : "DANADO";

                // 4. Registrar la condición de devolución.
                try (PreparedStatement ps =
                             conexion.prepareStatement(
                                     actualizarDetalle
                             )) {

                    ps.setString(1, condicion);
                    ps.setInt(2, detalleId);
                    ps.setInt(3, prestamoId);

                    if (ps.executeUpdate() != 1) {

                        throw new SQLException(
                                "No se pudo actualizar el detalle."
                        );
                    }
                }

                // 5. Actualizar el estado del inventario.
                try (PreparedStatement ps =
                             conexion.prepareStatement(
                                     actualizarEquipo
                             )) {

                    ps.setString(1, nuevoEstadoEquipo);
                    ps.setInt(2, equipoId);

                    if (ps.executeUpdate() != 1) {

                        throw new SQLException(
                                "No se pudo actualizar el equipo."
                        );
                    }
                }

                // 6. Consultar cuántos equipos
                // siguen pendientes de devolución.
                int pendientes;

                try (PreparedStatement ps =
                             conexion.prepareStatement(
                                     contarPendientes
                             )) {

                    ps.setInt(1, prestamoId);

                    try (ResultSet rs = ps.executeQuery()) {

                        if (!rs.next()) {

                            throw new SQLException(
                                    "No se pudo consultar el préstamo."
                            );
                        }

                        pendientes = rs.getInt(1);
                    }
                }

                // 7. Finalizar solamente cuando
                // todos los equipos regresaron.
                if (pendientes == 0) {

                    try (PreparedStatement ps =
                                 conexion.prepareStatement(
                                         finalizarPrestamo
                                 )) {

                        ps.setInt(1, prestamoId);

                        if (ps.executeUpdate() != 1) {

                            throw new SQLException(
                                    "No se pudo finalizar el préstamo."
                            );
                        }
                    }
                }

                // 8. Registrar auditoría
                // dentro de la misma transacción.
                try (PreparedStatement ps =
                             conexion.prepareStatement(
                                     insertarAuditoria
                             )) {

                    ps.setInt(1, usuarioId);
                    ps.setString(2, "REGISTRAR_DEVOLUCION");
                    ps.setString(3, "DetallePrestamo");
                    ps.setInt(4, detalleId);

                    ps.setString(
                            5,
                            "Devolución del equipo #" + equipoId
                                    + " del préstamo #" + prestamoId
                                    + ". Condición: " + condicion
                                    + ". Nuevo estado: " + nuevoEstadoEquipo
                                    + ". Pendientes restantes: " + pendientes
                    );

                    if (ps.executeUpdate() != 1) {

                        throw new SQLException(
                                "No se pudo registrar la auditoría."
                        );
                    }
                }

                // 9. Confirmar todos los cambios.
                conexion.commit();

                return true;

            } catch (SQLException | RuntimeException e) {

                // Revertir todos los cambios
                // si ocurre un error.
                try {

                    conexion.rollback();

                } catch (SQLException rollbackError) {

                    e.addSuppressed(rollbackError);
                }

                throw e;
            }

        } catch (SQLException e) {

            throw JdbcSupport.error(
                    "registrar la devolución",
                    e
            );
        }
    }
}
