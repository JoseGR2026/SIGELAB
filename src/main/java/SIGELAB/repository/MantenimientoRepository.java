package SIGELAB.repository;

import SIGELAB.config.DatabaseConnection;
import SIGELAB.util.JdbcSupport;

import java.sql.*;

public class MantenimientoRepository {

    // Método auxiliar para consultar el estado actual del equipo en la BD
    public String obtenerEstadoEquipo(int equipoId) {
        String sql = "SELECT estado FROM Equipo WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, equipoId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("estado");
                }
            }
        } catch (SQLException e) {
            throw JdbcSupport.error("consultar el estado del equipo", e);
        }
        return null;
    }

    public boolean registrarIngreso(int equipoId, String tipo, String descripcion, String responsable) {
        String sqlEquipo = "UPDATE Equipo SET estado = 'MANTENIMIENTO', observaciones = ? WHERE id = ?";
        String sqlMantenimiento = "INSERT INTO Mantenimiento (equipo_id, tipo_mantenimiento, descripcion, responsable, estado) VALUES (?, ?, ?, ?, 'EN_PROCESO')";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement psEq = conn.prepareStatement(sqlEquipo);
                 PreparedStatement psMant = conn.prepareStatement(sqlMantenimiento)) {

                psEq.setString(1, "Mantenimiento: " + descripcion);
                psEq.setInt(2, equipoId);
                psEq.executeUpdate();

                psMant.setInt(1, equipoId);
                psMant.setString(2, tipo != null ? tipo : "CORRECTIVO");
                psMant.setString(3, descripcion);
                psMant.setString(4, responsable != null ? responsable : "Taller SIGELAB");
                psMant.executeUpdate();

                conn.commit();
                return true;
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            }
        } catch (SQLException e) {
            throw JdbcSupport.error("registrar el ingreso a mantenimiento", e);
        }
    }

    public boolean finalizarMantenimiento(int equipoId, boolean reparadoConExito, String observaciones) {
        String nuevoEstado = reparadoConExito ? "DISPONIBLE" : "DADO_DE_BAJA";
        String resultadoMant = reparadoConExito ? "REPARADO" : "NO_REPARABLE";

        String sqlMant = "UPDATE Mantenimiento SET fecha_salida = GETDATE(), resultado = ?, estado = 'FINALIZADO', observaciones = ? WHERE equipo_id = ? AND estado = 'EN_PROCESO'";
        String sqlEq = "UPDATE Equipo SET estado = ?, observaciones = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement psMant = conn.prepareStatement(sqlMant);
                 PreparedStatement psEq = conn.prepareStatement(sqlEq)) {

                psMant.setString(1, resultadoMant);
                psMant.setString(2, observaciones);
                psMant.setInt(3, equipoId);
                psMant.executeUpdate();

                psEq.setString(1, nuevoEstado);
                psEq.setString(2, observaciones);
                psEq.setInt(3, equipoId);
                psEq.executeUpdate();

                conn.commit();
                return true;
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            }
        } catch (SQLException e) {
            throw JdbcSupport.error("finalizar el mantenimiento", e);
        }
    }
}