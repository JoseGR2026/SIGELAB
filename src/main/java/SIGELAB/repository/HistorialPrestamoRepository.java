
package SIGELAB.repository;

import SIGELAB.config.DatabaseConnection;
import SIGELAB.util.JdbcSupport;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HistorialPrestamoRepository {

    /**
     * Consulta el historial de préstamos.
     *
     * Cada fila representa un equipo asociado
     * a un préstamo.
     *
     * @param filtroEstado ACTIVO, VENCIDO,
     *                     FINALIZADO o TODOS.
     * @return Lista de registros del historial.
     */
    public List<Object[]> listarHistorial(String filtroEstado) {

        String estado = filtroEstado == null
                ? "TODOS"
                : filtroEstado.trim().toUpperCase();

        if (!estado.equals("TODOS")
                && !estado.equals("ACTIVO")
                && !estado.equals("VENCIDO")
                && !estado.equals("FINALIZADO")) {

            throw new IllegalArgumentException(
                    "El filtro de estado no es válido."
            );
        }

        String sql = """
                SELECT
                    p.id AS prestamo_id,
                    p.solicitante,
                    p.fecha_prestamo,
                    p.fecha_devolucion_esperada,
                    p.estado AS estado_prestamo,
                    e.codigo_inventario,
                    e.nombre AS nombre_equipo,
                    d.estado_devolucion
                FROM Prestamo p
                INNER JOIN DetallePrestamo d
                    ON d.prestamo_id = p.id
                INNER JOIN Equipo e
                    ON e.id = d.equipo_id
                WHERE (? = 'TODOS' OR p.estado = ?)
                ORDER BY p.id DESC, d.id ASC
                """;

        List<Object[]> historial = new ArrayList<>();

        try (Connection conexion =
                     DatabaseConnection.getConnection();
             PreparedStatement ps =
                     conexion.prepareStatement(sql)) {

            ps.setString(1, estado);
            ps.setString(2, estado);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    historial.add(new Object[]{
                            rs.getInt("prestamo_id"),
                            rs.getString("solicitante"),
                            rs.getTimestamp("fecha_prestamo"),
                            rs.getTimestamp(
                                    "fecha_devolucion_esperada"
                            ),
                            rs.getString("estado_prestamo"),
                            rs.getString("codigo_inventario"),
                            rs.getString("nombre_equipo"),
                            rs.getString("estado_devolucion")
                    });
                }
            }

            return historial;

        } catch (SQLException e) {

            throw JdbcSupport.error(
                    "consultar el historial de préstamos",
                    e
            );
        }
    }
}
