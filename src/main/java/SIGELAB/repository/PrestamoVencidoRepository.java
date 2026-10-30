
package SIGELAB.repository;

import SIGELAB.config.DatabaseConnection;
import SIGELAB.util.JdbcSupport;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PrestamoVencidoRepository {

    /**
     * Actualiza préstamos vencidos y registra
     * cada cambio en auditoría.
     *
     * @param usuarioId Usuario responsable.
     * @return Cantidad de préstamos actualizados.
     */
    public int actualizarVencidos(int usuarioId) {

        if (usuarioId <= 0) {
            throw new IllegalArgumentException(
                    "Debe indicar un usuario válido.");
        }

        String buscarVencidos = """
                SELECT p.id
                FROM Prestamo p WITH (UPDLOCK, HOLDLOCK)
                WHERE p.estado = 'ACTIVO'
                  AND p.fecha_devolucion_esperada < GETDATE()
                  AND EXISTS (
                      SELECT 1
                      FROM DetallePrestamo d
                      WHERE d.prestamo_id = p.id
                        AND d.estado_devolucion = 'PENDIENTE'
                  )
                ORDER BY p.id
                """;

        String actualizarPrestamo = """
                UPDATE Prestamo
                SET estado = 'VENCIDO'
                WHERE id = ?
                  AND estado = 'ACTIVO'
                """;

        String insertarAuditoria = """
                INSERT INTO Auditoria
                (usuario_id, accion, entidad, registro_id, descripcion)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (Connection conexion = DatabaseConnection.getConnection()) {

            conexion.setAutoCommit(false);

            try {
                List<Integer> prestamosVencidos = new ArrayList<>();

                // 1. Buscar préstamos vencidos.
                try (PreparedStatement ps =
                             conexion.prepareStatement(buscarVencidos);
                     ResultSet rs = ps.executeQuery()) {

                    while (rs.next()) {
                        prestamosVencidos.add(rs.getInt("id"));
                    }
                }

                int actualizados = 0;

                try (PreparedStatement actualizar =
                             conexion.prepareStatement(actualizarPrestamo);
                     PreparedStatement auditoria =
                             conexion.prepareStatement(insertarAuditoria)) {

                    for (int prestamoId : prestamosVencidos) {

                        // 2. Actualizar estado.
                        actualizar.setInt(1, prestamoId);

                        if (actualizar.executeUpdate() != 1) {
                            throw new SQLException(
                                    "No se pudo actualizar el préstamo #"
                                            + prestamoId);
                        }

                        // 3. Registrar auditoría.
                        auditoria.setInt(1, usuarioId);
                        auditoria.setString(2, "MARCAR_VENCIDO");
                        auditoria.setString(3, "Prestamo");
                        auditoria.setInt(4, prestamoId);
                        auditoria.setString(
                                5,
                                "El préstamo #" + prestamoId
                                        + " superó su fecha esperada "
                                        + "de devolución y tiene "
                                        + "equipos pendientes."
                        );

                        if (auditoria.executeUpdate() != 1) {
                            throw new SQLException(
                                    "No se pudo auditar el préstamo #"
                                            + prestamoId);
                        }

                        actualizados++;
                    }
                }

                // 4. Confirmar los cambios.
                conexion.commit();

                return actualizados;

            } catch (SQLException | RuntimeException e) {

                try {
                    conexion.rollback();
                } catch (SQLException rollbackError) {
                    e.addSuppressed(rollbackError);
                }

                throw e;
            }

        } catch (SQLException e) {
            throw JdbcSupport.error(
                    "actualizar préstamos vencidos", e);
        }
    }
}
