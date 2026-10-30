
package SIGELAB.repository;

import SIGELAB.config.DatabaseConnection;
import SIGELAB.util.JdbcSupport;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AuditoriaConsultaRepository {

    /**
     * Consulta los movimientos registrados
     * en la tabla Auditoria.
     *
     * @return Lista de registros de auditoría.
     */
    public List<Object[]> listarAuditoria() {

        String sql = """
                SELECT
                    a.id,
                    a.fecha_hora,
                    a.usuario_id,
                    a.accion,
                    a.entidad,
                    a.registro_id,
                    a.descripcion
                FROM Auditoria a
                ORDER BY a.fecha_hora DESC, a.id DESC
                """;

        List<Object[]> registros = new ArrayList<>();

        try (Connection conexion =
                     DatabaseConnection.getConnection();
             PreparedStatement ps =
                     conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                registros.add(new Object[]{
                        rs.getInt("id"),
                        rs.getTimestamp("fecha_hora"),
                        rs.getInt("usuario_id"),
                        rs.getString("accion"),
                        rs.getString("entidad"),
                        rs.getInt("registro_id"),
                        rs.getString("descripcion")
                });
            }

            return registros;

        } catch (SQLException e) {

            throw JdbcSupport.error(
                    "consultar los registros de auditoría",
                    e
            );
        }
    }
}
