
package SIGELAB.model;

import java.time.LocalDateTime;

public class Prestamo {

    private int id;
    private String solicitante;
    private LocalDateTime fechaPrestamo;
    private LocalDateTime fechaDevolucionEsperada;
    private int usuarioId;
    private String estado;
    private String observaciones;

    public Prestamo() {
    }

    public Prestamo(int id, String solicitante,
                    LocalDateTime fechaPrestamo,
                    LocalDateTime fechaDevolucionEsperada,
                    int usuarioId, String estado,
                    String observaciones) {
        this.id = id;
        this.solicitante = solicitante;
        this.fechaPrestamo = fechaPrestamo;
        this.fechaDevolucionEsperada = fechaDevolucionEsperada;
        this.usuarioId = usuarioId;
        this.estado = estado;
        this.observaciones = observaciones;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSolicitante() {
        return solicitante;
    }

    public void setSolicitante(String solicitante) {
        this.solicitante = solicitante;
    }

    public LocalDateTime getFechaPrestamo() {
        return fechaPrestamo;
    }

    public void setFechaPrestamo(LocalDateTime fechaPrestamo) {
        this.fechaPrestamo = fechaPrestamo;
    }

    public LocalDateTime getFechaDevolucionEsperada() {
        return fechaDevolucionEsperada;
    }

    public void setFechaDevolucionEsperada(
            LocalDateTime fechaDevolucionEsperada) {
        this.fechaDevolucionEsperada =
                fechaDevolucionEsperada;
    }

    public int getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(int usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
}
