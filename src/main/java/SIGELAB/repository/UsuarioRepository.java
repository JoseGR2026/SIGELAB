package SIGELAB.repository;

import SIGELAB.config.DatabaseConnection;
import SIGELAB.enums.RolUsuario;
import SIGELAB.model.Usuario;
import SIGELAB.util.JdbcSupport;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UsuarioRepository {
    private static final String SELECT = "SELECT u.id, u.nombre, u.apellido, u.username, u.password_hash, u.correo, r.nombre AS rol_nombre, u.estado, u.fecha_creacion, u.referencia_facial " +
            "FROM Usuario u LEFT JOIN Rol r ON r.id = u.rol_id ";

    public boolean guardar(Usuario usuario) {
        String sql = "INSERT INTO Usuario (nombre, apellido, username, password_hash, correo, rol_id, estado, referencia_facial) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement s = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            s.setString(1, usuario.getNombres());
            s.setString(2, usuario.getApellidos());
            s.setString(3, usuario.getUsuario());
            s.setString(4, usuario.getPasswordHash());
            s.setString(5, usuario.getCorreo());
            s.setInt(6, usuario.getRol() == RolUsuario.ADMIN ? 1 : 2); // 1 = ADMIN, 2 = ENCARGADO
            s.setString(7, usuario.isActivo() ? "ACTIVO" : "INACTIVO");
            s.setString(8, usuario.getReferenciaFacial());

            if (s.executeUpdate() == 0) return false;
            try (ResultSet keys = s.getGeneratedKeys()) {
                if (!keys.next()) return false;
                usuario.setIdUsuario(keys.getInt(1));
                return true;
            }
        } catch (SQLException e) { throw JdbcSupport.error("guardar el usuario", e); }
    }

    public Optional<Usuario> buscarPorId(int id) {
        return buscar(SELECT + "WHERE u.id = ?", id);
    }

    public Optional<Usuario> buscarPorUsuario(String nombre) {
        System.out.println("DEBUG REPO -> Buscando en BD el username: [" + nombre + "]");
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement s = c.prepareStatement(SELECT + " WHERE u.username = ?")) {
            s.setString(1, nombre);
            try (ResultSet rs = s.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();

            }

        } catch (SQLException e) {
            throw JdbcSupport.error("buscar el usuario", e);
        }
    }

    public List<Usuario> listar() {
        List<Usuario> r = new ArrayList<>();
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement s = c.prepareStatement(SELECT + "ORDER BY u.username");
             ResultSet rs = s.executeQuery()) {
            while (rs.next()) r.add(mapear(rs));
            return r;
        } catch (SQLException e) {
            throw JdbcSupport.error("consultar usuarios", e);
        }
    }

    public boolean existeUsuario(String nombre) {
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement s = c.prepareStatement("SELECT 1 FROM Usuario WHERE username = ?")) {
            s.setString(1, nombre);
            try (ResultSet rs = s.executeQuery()) { return rs.next(); }
        } catch (SQLException e) {
            throw JdbcSupport.error("verificar el usuario", e);
        }
    }

    public boolean actualizar(Usuario u) {
        String sql = "UPDATE Usuario SET nombre = ?, apellido = ?, username = ?, password_hash = ?, correo = ?, rol_id = ?, estado = ?, referencia_facial = ? WHERE id = ?";
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement s = c.prepareStatement(sql)) {
            s.setString(1, u.getNombres());
            s.setString(2, u.getApellidos());
            s.setString(3, u.getUsuario());
            s.setString(4, u.getPasswordHash());
            s.setString(5, u.getCorreo());
            s.setInt(6, u.getRol() == RolUsuario.ADMIN ? 1 : 2);
            s.setString(7, u.isActivo() ? "ACTIVO" : "INACTIVO");
            s.setString(8, u.getReferenciaFacial());
            s.setInt(9, u.getIdUsuario());
            return s.executeUpdate() > 0;
        } catch (SQLException e) {
            throw JdbcSupport.error("actualizar el usuario", e);
        }
    }

    public boolean desactivar(int id) {
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement s = c.prepareStatement("UPDATE Usuario SET estado = 'INACTIVO' WHERE id = ?")) {
            s.setInt(1, id);
            return s.executeUpdate() > 0;
        } catch (SQLException e) {
            throw JdbcSupport.error("desactivar el usuario", e);
        }
    }

    public boolean activar(int id) {
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement s = c.prepareStatement("UPDATE Usuario SET estado = 'ACTIVO' WHERE id = ?")) {
            s.setInt(1, id);
            return s.executeUpdate() > 0;
        } catch (SQLException e) {
            throw JdbcSupport.error("activar el usuario", e);
        }
    }

    public int contar() {
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement s = c.prepareStatement("SELECT COUNT(*) FROM Usuario");
             ResultSet rs = s.executeQuery()) {
            rs.next();
            return rs.getInt(1);
        } catch (SQLException e) {
            throw JdbcSupport.error("contar usuarios", e);
        }
    }

    public boolean existeUsuario(String nombre, int idExcluido) {
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement s = c.prepareStatement("SELECT 1 FROM Usuario WHERE username = ? AND id <> ?")) {
            s.setString(1, nombre);
            s.setInt(2, idExcluido);
            try (ResultSet rs = s.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw JdbcSupport.error("verificar el usuario", e);
        }
    }

    private Optional<Usuario> buscar(String sql, int id) {
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement s = c.prepareStatement(sql)) {
            s.setInt(1, id);
            try (ResultSet rs = s.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw JdbcSupport.error("buscar el usuario", e);
        }
    }

    private Usuario mapear(ResultSet rs) throws SQLException {
        String rolStr = rs.getString("rol_nombre");
        RolUsuario rolEnum = ("ADMIN".equalsIgnoreCase(rolStr)) ? RolUsuario.ADMIN : RolUsuario.ENCARGADO;
        boolean activo = "ACTIVO".equalsIgnoreCase(rs.getString("estado"));

        Usuario usuario = new Usuario();
        usuario.setIdUsuario(rs.getInt("id"));
        usuario.setNombres(rs.getString("nombre"));
        usuario.setApellidos(rs.getString("apellido"));
        usuario.setUsuario(rs.getString("username"));
        usuario.setPasswordHash(rs.getString("password_hash"));
        usuario.setCorreo(rs.getString("correo"));
        usuario.setRol(rolEnum);
        usuario.setActivo(activo);

        try {
            usuario.setReferenciaFacial(rs.getString("referencia_facial"));
        } catch (Exception ignored) {}

        try {
            usuario.setFechaCreacion(rs.getObject("fecha_creacion", LocalDateTime.class));
        } catch (Exception ignored) {}

        return usuario;
    }

    public Optional<Usuario> buscarPorReferenciaFacial(String referenciaFacial) {
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement s = c.prepareStatement(SELECT + "WHERE u.referencia_facial = ?")) {
            s.setString(1, referenciaFacial);
            try (ResultSet rs = s.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw JdbcSupport.error("buscar usuario por referencia facial", e);
        }
    }
}