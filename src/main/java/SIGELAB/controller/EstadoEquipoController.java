package SIGELAB.controller;

import SIGELAB.model.EstadoEquipo;
import SIGELAB.service.EstadoEquipoService;

import java.util.List;

public class EstadoEquipoController {
    private final EstadoEquipoService service = new EstadoEquipoService();

    public List<EstadoEquipo> listar() {
        return service.listar();
    }

    public boolean crearEquipo(int laboratorioId, String codigoInventario, String nombre,
                               String marca, String modelo, String numeroSerie,
                               String tipo, String estado, String observaciones) {
        return service.crearEquipo(laboratorioId, codigoInventario, nombre, marca, modelo, numeroSerie, tipo, estado, observaciones);
    }

    public boolean actualizar(EstadoEquipo equipo) {
        return service.actualizar(equipo);
    }

    public String generarSiguienteCodigo() {
        return service.generarSiguienteCodigo();
    }

    // Métodos que utiliza tu ventana MantenimientoEquipos:
    public boolean enviarAMantenimiento(int equipoId, String motivo) {
        return service.enviarAMantenimiento(equipoId, motivo);
    }

    public boolean finalizarMantenimiento(int equipoId, boolean reparadoConExito, String motivo) {
        return service.finalizarMantenimiento(equipoId, reparadoConExito, motivo);
    }
}