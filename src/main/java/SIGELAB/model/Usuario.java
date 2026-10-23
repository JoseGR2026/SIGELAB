package SIGELAB.model;

import SIGELAB.enums.RolUsuario;

import java.time.LocalDateTime;

public class Usuario {
    private int idUsuario;
    private String nombres;
    private String apellidos;
    private String usuario; // Corresponde al username
    private String passwordHash;
    private String correo;
    private RolUsuario rol;
    private Docente docente; // Agregado para que encaje con el Service
    private boolean activo;
    private LocalDateTime fechaCreacion;
    private String referenciaFacial;

    public Usuario() { }

    // Constructor que usa UsuarioService.java (con Docente)


    public Usuario(String usuario, String passwordHash, RolUsuario rol, String nombres, String apellidos, String correo) {
        this(0, nombres, apellidos, usuario, passwordHash, correo, rol, null, true, null);
    }

    public Usuario(int idUsuario, String nombres, String apellidos, String usuario, String passwordHash,
                   String correo, RolUsuario rol, Docente docente, boolean activo, LocalDateTime fechaCreacion) {
        this.idUsuario = idUsuario;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.usuario = usuario;
        this.passwordHash = passwordHash;
        this.correo = correo;
        this.rol = rol;
        this.docente = docente;
        this.activo = activo;
        this.fechaCreacion = fechaCreacion;
    }

    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public RolUsuario getRol() { return rol; }
    public void setRol(RolUsuario rol) { this.rol = rol; }

    public Docente getDocente() { return docente; }
    public void setDocente(Docente docente) { this.docente = docente; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    @Override
    public String toString() {
        return "Usuario{" + "id=" + idUsuario + ", username='" + usuario + '\'' +
                ", rol=" + rol + ", activo=" + activo + '}';
    }

    public void setReferenciaFacial(String referenciaFacial) {
        this.referenciaFacial = referenciaFacial;
    }

    public String getReferenciaFacial() {
        return referenciaFacial;
    }
}