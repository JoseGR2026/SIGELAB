package SIGELAB.controller;

import SIGELAB.enums.RolUsuario;
import SIGELAB.model.Usuario;
import SIGELAB.service.UsuarioService;
import java.util.List;
import java.util.Optional;

public class UsuarioController {
    private final UsuarioService service;

    public UsuarioController() {
        this(new UsuarioService());
    }

    public UsuarioController(UsuarioService service) {
        this.service = service;
    }

    public Usuario crearUsuario(String nombres, String apellidos, String username, String password, RolUsuario rol, String correo) {
        // Llama al servicio que procesa y guarda todos los datos reales del formulario
        return service.crearUsuario(nombres, apellidos, username, password, rol, correo);
    }

    public Optional<Usuario> autenticar(String usuario, String password) {
        return service.autenticar(usuario, password);
    }

    public Optional<Usuario> buscar(int id) {
        return service.buscar(id);
    }

    public Optional<Usuario> buscarPorUsuario(String usuario) {
        return service.buscarPorUsuario(usuario);
    }

    public List<Usuario> listar() {
        return service.listar();
    }

    public boolean actualizar(Usuario usuario) {
        return service.actualizar(usuario);
    }

    public boolean actualizarConPassword(Usuario usuario, String password) {
        return service.actualizarConPassword(usuario, password);
    }

    public boolean desactivar(int id) {
        return service.desactivar(id);
    }
    public boolean activar(int id) {
        return service.activar(id);
    }

}