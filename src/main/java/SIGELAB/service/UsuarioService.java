package SIGELAB.service;

import SIGELAB.enums.RolUsuario;
import SIGELAB.model.Docente;
import SIGELAB.model.Usuario;
import SIGELAB.repository.UsuarioRepository;
import SIGELAB.util.BusinessException;

import java.util.List;
import java.util.Optional;

public class UsuarioService {
    private final UsuarioRepository repository;
    public UsuarioService() { this(new UsuarioRepository()); }
    public UsuarioService(UsuarioRepository repository) { this.repository = repository; }

    public Usuario crearUsuario(String usuario, String password, RolUsuario rol, Docente docente) {
        if (usuario == null || usuario.isBlank()) throw new BusinessException("El usuario es obligatorio.");
        if (password == null || password.isBlank()) throw new BusinessException("La contraseña es obligatoria.");
        if (rol == null) throw new BusinessException("El rol es obligatorio.");
        if (repository.existeUsuario(usuario)) throw new BusinessException("El nombre de usuario ya está registrado.");

        // Guarda la contraseña en texto plano
        Usuario nuevo = new Usuario(usuario, password, rol, docente);
        repository.guardar(nuevo);
        return nuevo;
    }

    public boolean guardar(Usuario usuario) {
        validarUsuario(usuario);
        if (repository.existeUsuario(usuario.getUsuario())) throw new BusinessException("El nombre de usuario ya está registrado.");
        return repository.guardar(usuario);
    }

    public Optional<Usuario> buscar(int id) { return repository.buscarPorId(id); }
    public Optional<Usuario> buscarPorUsuario(String nombre) { return repository.buscarPorUsuario(nombre); }
    public List<Usuario> listar() { return repository.listar(); }

    public boolean actualizar(Usuario usuario) {
        validarUsuario(usuario);
        if (usuario.getIdUsuario() <= 0) throw new BusinessException("El usuario no tiene un ID válido.");
        if (repository.existeUsuario(usuario.getUsuario(), usuario.getIdUsuario())) throw new BusinessException("El nombre de usuario ya está registrado.");
        return repository.actualizar(usuario);
    }

    public boolean actualizarConPassword(Usuario usuario, String password) {
        if (password == null || password.isBlank()) throw new BusinessException("La contraseña es obligatoria.");
        validarUsuario(usuario);
        if (usuario.getIdUsuario() <= 0) throw new BusinessException("El usuario no tiene un ID válido.");
        if (repository.existeUsuario(usuario.getUsuario(), usuario.getIdUsuario())) throw new BusinessException("El nombre de usuario ya está registrado.");
        usuario.setPasswordHash(password); // Guarda la nueva contraseña en texto plano
        return repository.actualizar(usuario);
    }

    public boolean desactivar(int id) { return repository.desactivar(id); }

    // Autenticación simplificada en texto plano con equals
    public Optional<Usuario> autenticar(String usuario, String password) {
        if (usuario == null || password == null) return Optional.empty();
        Optional<Usuario> encontrado = repository.buscarPorUsuario(usuario);
        if (encontrado.isEmpty() || !encontrado.get().isActivo()) return Optional.empty();

        // Compara directamente la contraseña ingresada con la de la base de datos
        boolean passwordValida = password.equals(encontrado.get().getPasswordHash());
        return passwordValida ? encontrado : Optional.empty();
    }

    private void validarUsuario(Usuario u) {
        if (u == null || u.getUsuario() == null || u.getUsuario().isBlank())
            throw new BusinessException("El usuario es obligatorio.");
        if (u.getRol() == null)
            throw new BusinessException("El rol es obligatorio.");
    }
}