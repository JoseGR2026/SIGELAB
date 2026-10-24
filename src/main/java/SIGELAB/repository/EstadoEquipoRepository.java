package SIGELAB.repository;

import SIGELAB.config.DatabaseConnection;
import SIGELAB.model.EstadoEquipo;
import SIGELAB.util.JdbcSupport;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EstadoEquipoRepository {
    private static final String SELECT = "SELECT id, laboratorio_id, tipo_equipo_id, codigo_inventario, nombre, marca, modelo, numero_serie, estado, observaciones FROM Equipo ";

    public boolean guardar(EstadoEquipo equipo) {
        String sql = "INSERT INTO Equipo (laboratorio_id, tipo_equipo_id, codigo_inventario, nombre, marca, modelo, numero_serie, estado, observaciones) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement s = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            s.setInt(1, equipo.getLaboratorioId());
            s.setInt(2, equipo.getTipoEquipoId() > 0 ? equipo.getTipoEquipoId() : 1);
            s.setString(3, equipo.getCodigoInventario());
            s.setString(4, equipo.getNombreEquipo());
            s.setString(5, equipo.getMarca());
            s.setString(6, equipo.getModelo());
            s.setString(7, equipo.getNumeroSerie());
            s.setString(8, equipo.getEstado());
            s.setString(9, equipo.getObservaciones());

            if (s.executeUpdate() == 0) return false;
            try (ResultSet keys = s.getGeneratedKeys()) {
                if (!keys.next()) return false;
                equipo.setId(keys.getInt(1));
                return true;
            }
        } catch (SQLException e) {
            throw JdbcSupport.error("guardar el equipo", e);
        }
    }

    public List<EstadoEquipo> listar() {
        List<EstadoEquipo> lista = new ArrayList<>();
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement s = c.prepareStatement(SELECT + "ORDER BY id DESC");
             ResultSet rs = s.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
            return lista;
        } catch (SQLException e) {
            throw JdbcSupport.error("consultar equipos", e);
        }
    }

    public boolean actualizar(EstadoEquipo equipo) {
        String sql = "UPDATE Equipo SET laboratorio_id = ?, tipo_equipo_id = ?, codigo_inventario = ?, nombre = ?, marca = ?, modelo = ?, numero_serie = ?, estado = ?, observaciones = ? WHERE id = ?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement s = c.prepareStatement(sql)) {

            s.setInt(1, equipo.getLaboratorioId());
            s.setInt(2, equipo.getTipoEquipoId() > 0 ? equipo.getTipoEquipoId() : 1);
            s.setString(3, equipo.getCodigoInventario());
            s.setString(4, equipo.getNombreEquipo());
            s.setString(5, equipo.getMarca());
            s.setString(6, equipo.getModelo());
            s.setString(7, equipo.getNumeroSerie());
            s.setString(8, equipo.getEstado());
            s.setString(9, equipo.getObservaciones());
            s.setInt(10, equipo.getId());

            return s.executeUpdate() > 0;
        } catch (SQLException e) {
            throw JdbcSupport.error("actualizar el equipo", e);
        }
    }

    public boolean existeCodigo(String codigo) {
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement s = c.prepareStatement("SELECT 1 FROM Equipo WHERE codigo_inventario = ?")) {
            s.setString(1, codigo);
            try (ResultSet rs = s.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw JdbcSupport.error("verificar el código del equipo", e);
        }
    }

    public boolean existeCodigo(String codigo, int idExcluido) {
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement s = c.prepareStatement("SELECT 1 FROM Equipo WHERE codigo_inventario = ? AND id <> ?")) {
            s.setString(1, codigo);
            s.setInt(2, idExcluido);
            try (ResultSet rs = s.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw JdbcSupport.error("verificar el código del equipo", e);
        }
    }

    public String generarSiguienteCodigo() {
        String sql = "SELECT ISNULL(MAX(id), 0) + 1 AS siguiente_id FROM Equipo";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement s = c.prepareStatement(sql);
             ResultSet rs = s.executeQuery()) {
            if (rs.next()) {
                int siguienteId = rs.getInt("siguiente_id");
                return String.format("INV-%03d", siguienteId);
            }
            return "INV-001";
        } catch (SQLException e) {
            return "INV-001";
        }
    }

    private EstadoEquipo mapear(ResultSet rs) throws SQLException {
        EstadoEquipo eq = new EstadoEquipo();
        eq.setId(rs.getInt("id"));
        eq.setLaboratorioId(rs.getInt("laboratorio_id"));
        eq.setTipoEquipoId(rs.getInt("tipo_equipo_id"));
        eq.setCodigoInventario(rs.getString("codigo_inventario"));
        eq.setNombreEquipo(rs.getString("nombre"));
        eq.setMarca(rs.getString("marca"));
        eq.setModelo(rs.getString("modelo"));
        eq.setNumeroSerie(rs.getString("numero_serie"));
        eq.setTipo("Hardware");
        eq.setEstado(rs.getString("estado"));
        eq.setObservaciones(rs.getString("observaciones"));
        return eq;
    }
}