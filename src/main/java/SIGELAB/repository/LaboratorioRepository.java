package SIGELAB.repository;

import SIGELAB.config.DatabaseConnection;
import SIGELAB.model.Laboratorio;
import SIGELAB.util.JdbcSupport;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LaboratorioRepository {
    private static final String SELECT = "SELECT id, codigo, nombre, ubicacion, capacidad, estado FROM Laboratorio ";

    public boolean guardar(Laboratorio lab) {
        String sql = "INSERT INTO Laboratorio (codigo, nombre, ubicacion, capacidad, estado) VALUES (?, ?, ?, ?, ?)";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement s = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            s.setString(1, lab.getCodigo());
            s.setString(2, lab.getNombre());
            s.setString(3, lab.getUbicacion());
            s.setInt(4, lab.getCapacidad());
            s.setString(5, lab.getEstado());

            if (s.executeUpdate() == 0) return false;
            try (ResultSet keys = s.getGeneratedKeys()) {
                if (!keys.next()) return false;
                lab.setId(keys.getInt(1));
                return true;
            }
        } catch (SQLException e) {
            throw JdbcSupport.error("guardar el laboratorio", e);
        }
    }

    public boolean actualizar(Laboratorio lab) {
        String sql = "UPDATE Laboratorio SET codigo = ?, nombre = ?, ubicacion = ?, capacidad = ?, estado = ? WHERE id = ?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement s = c.prepareStatement(sql)) {

            s.setString(1, lab.getCodigo());
            s.setString(2, lab.getNombre());
            s.setString(3, lab.getUbicacion());
            s.setInt(4, lab.getCapacidad());
            s.setString(5, lab.getEstado());
            s.setInt(6, lab.getId());

            return s.executeUpdate() > 0;
        } catch (SQLException e) {
            throw JdbcSupport.error("actualizar el laboratorio", e);
        }
    }

    public List<Laboratorio> listar() {
        List<Laboratorio> lista = new ArrayList<>();
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement s = c.prepareStatement(SELECT + "ORDER BY codigo");
             ResultSet rs = s.executeQuery()) {

            while (rs.next()) {
                lista.add(mapear(rs));
            }
            return lista;
        } catch (SQLException e) {
            throw JdbcSupport.error("consultar laboratorios", e);
        }
    }

    public Optional<Laboratorio> buscarPorId(int id) {
        return buscar(SELECT + "WHERE id = ?", id);
    }

    public boolean existeCodigo(String codigo) {
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement s = c.prepareStatement("SELECT 1 FROM Laboratorio WHERE codigo = ?")) {
            s.setString(1, codigo);
            try (ResultSet rs = s.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw JdbcSupport.error("verificar el código del laboratorio", e);
        }
    }

    public boolean existeCodigo(String codigo, int idExcluido) {
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement s = c.prepareStatement("SELECT 1 FROM Laboratorio WHERE codigo = ? AND id <> ?")) {
            s.setString(1, codigo);
            s.setInt(2, idExcluido);
            try (ResultSet rs = s.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw JdbcSupport.error("verificar el código del laboratorio", e);
        }
    }

    private Optional<Laboratorio> buscar(String sql, int id) {
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement s = c.prepareStatement(sql)) {
            s.setInt(1, id);
            try (ResultSet rs = s.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw JdbcSupport.error("buscar el laboratorio", e);
        }
    }

    private Laboratorio mapear(ResultSet rs) throws SQLException {
        Laboratorio lab = new Laboratorio();
        lab.setId(rs.getInt("id"));
        lab.setCodigo(rs.getString("codigo"));
        lab.setNombre(rs.getString("nombre"));
        lab.setUbicacion(rs.getString("ubicacion"));
        lab.setCapacidad(rs.getInt("capacidad"));
        lab.setEstado(rs.getString("estado"));
        return lab;
    }
}