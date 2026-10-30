package SIGELAB.service;

import SIGELAB.config.DatabaseConnection;
import SIGELAB.enums.RolUsuario;
import SIGELAB.model.Usuario;
import SIGELAB.repository.UsuarioRepository;
import SIGELAB.util.BusinessException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class UsuarioService {
    private final UsuarioRepository repository;

    public UsuarioService() {
        this(new UsuarioRepository());
    }

    public UsuarioService(UsuarioRepository repository) {
        this.repository = repository;
    }

    public Usuario crearUsuario(String nombres, String apellidos, String usuario, String password, RolUsuario rol, String correo) {
        if (usuario == null || usuario.isBlank()) throw new BusinessException("El usuario es obligatorio.");
        if (password == null || password.isBlank()) throw new BusinessException("La contraseña es obligatoria.");
        if (rol == null) throw new BusinessException("El rol es obligatorio.");
        if (repository.existeUsuario(usuario)) throw new BusinessException("El nombre de usuario ya está registrado.");

        Usuario nuevo = new Usuario(0, nombres, apellidos, usuario, password, correo, rol, true, null);
        repository.guardar(nuevo);
        return nuevo;
    }

    public boolean guardar(Usuario usuario) {
        validarUsuario(usuario);
        if (repository.existeUsuario(usuario.getUsuario())) {
            throw new BusinessException("El nombre de usuario ya está registrado.");
        }
        return repository.guardar(usuario);
    }

    public Optional<Usuario> buscar(int id) {
        return repository.buscarPorId(id);
    }

    public Optional<Usuario> buscarPorUsuario(String nombre) {
        return repository.buscarPorUsuario(nombre);
    }

    public List<Usuario> listar() {
        return repository.listar();
    }

    public boolean actualizar(Usuario usuario) {
        validarUsuario(usuario);
        if (usuario.getIdUsuario() <= 0) {
            throw new BusinessException("El usuario no tiene un ID válido.");
        }
        if (repository.existeUsuario(usuario.getUsuario(), usuario.getIdUsuario())) {
            throw new BusinessException("El nombre de usuario ya está registrado.");
        }
        return repository.actualizar(usuario);
    }

    public boolean actualizarConPassword(Usuario usuario, String password) {
        if (password == null || password.isBlank()) {
            throw new BusinessException("La contraseña es obligatoria.");
        }
        validarUsuario(usuario);
        if (usuario.getIdUsuario() <= 0) {
            throw new BusinessException("El usuario no tiene un ID válido.");
        }
        if (repository.existeUsuario(usuario.getUsuario(), usuario.getIdUsuario())) {
            throw new BusinessException("El nombre de usuario ya está registrado.");
        }
        usuario.setPasswordHash(password);
        return repository.actualizar(usuario);
    }

    public boolean desactivar(int id, int usuarioLogueadoId) {
        if (id <= 0) {
            throw new BusinessException("ID de usuario inválido para desactivar.");
        }
        return repository.desactivar(id, usuarioLogueadoId);
    }

    public boolean activar(int id) {
        return repository.activar(id);
    }

    public Optional<Usuario> autenticar(String usuario, String password) {
        if (usuario == null || password == null) {
            registrarAuditoria(1, "LOGIN_FALLIDO", "Usuario", 1, "Intento de inicio de sesión con credenciales nulas.");
            return Optional.empty();
        }

        Optional<Usuario> encontrado = repository.buscarPorUsuario(usuario);

        if (encontrado.isEmpty()) {
            registrarAuditoria(1, "LOGIN_FALLIDO", "Usuario", 1, "Intento fallido. Usuario no encontrado: " + usuario);
            return Optional.empty();
        }

        Usuario usr = encontrado.get();

        if (!usr.isActivo()) {
            registrarAuditoria(usr.getIdUsuario(), "LOGIN_FALLIDO", "Usuario", usr.getIdUsuario(), "Intento de inicio de sesión en cuenta inactiva: " + usuario);
            return Optional.empty();
        }

        boolean passwordValida = password.equals(usr.getPasswordHash());

        if (!passwordValida) {
            registrarAuditoria(usr.getIdUsuario(), "LOGIN_FALLIDO", "Usuario", usr.getIdUsuario(), "Contraseña incorrecta para el usuario: " + usuario);
            return Optional.empty();
        }

        // Registro exitoso en la tabla Auditoria
        registrarAuditoria(usr.getIdUsuario(), "LOGIN_EXITOSO", "Usuario", usr.getIdUsuario(), "Inicio de sesión exitoso para: " + usuario);
        return Optional.of(usr);
    }

    private void registrarAuditoria(int usuarioId, String accion, String entidad, int registroId, String descripcion) {
        String sql = "INSERT INTO Auditoria (usuario_id, accion, entidad, registro_id, descripcion, fecha_hora) VALUES (?, ?, ?, ?, ?, GETDATE())";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, usuarioId > 0 ? usuarioId : 1);
            ps.setString(2, accion);
            ps.setString(3, entidad);
            ps.setInt(4, registroId > 0 ? registroId : 1);
            ps.setString(5, descripcion);

            ps.executeUpdate();
            System.out.println("DEBUG AUDITORIA -> " + accion + " guardado correctamente en SQL Server.");

        } catch (SQLException e) {
            System.err.println("ERROR SQL AL GUARDAR AUDITORIA (" + accion + "): " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void validarUsuario(Usuario u) {
        if (u == null || u.getUsuario() == null || u.getUsuario().isBlank()) {
            throw new BusinessException("El usuario es obligatorio.");
        }
        if (u.getRol() == null) {
            throw new BusinessException("El rol es obligatorio.");
        }
    }
}