package SIGELAB.repository;

import SIGELAB.config.DatabaseConnection;
import SIGELAB.util.JdbcSupport;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/** Registra eventos relevantes de auditoría. El actor puede ser desconocido. */
public class AuditoriaRepository {
    public void registrar(Integer usuarioId, String accion, String entidad,
                          Integer registroId, String descripcion) {
        String sql = """
                INSERT INTO Auditoria
                    (usuario_id, fecha_hora, accion, entidad, registro_id, descripcion)
                VALUES (?, GETDATE(), ?, ?, ?, ?)
                """;
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            if (usuarioId == null || usuarioId <= 0) ps.setNull(1, java.sql.Types.INTEGER);
            else ps.setInt(1, usuarioId);
            ps.setString(2, accion);
            ps.setString(3, entidad);
            if (registroId == null || registroId <= 0) ps.setNull(4, java.sql.Types.INTEGER);
            else ps.setInt(4, registroId);
            ps.setString(5, descripcion);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw JdbcSupport.error("registrar el evento de auditoría", e);
        }
    }
}
