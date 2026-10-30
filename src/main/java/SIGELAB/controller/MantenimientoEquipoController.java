package SIGELAB.controller;

import SIGELAB.model.EstadoEquipo;
import SIGELAB.service.EstadoEquipoService;
import SIGELAB.service.MantenimientoService;

import java.util.List;

public class MantenimientoEquipoController {
    private final EstadoEquipoService estadoEquipoService = new EstadoEquipoService();
    private final MantenimientoService mantenimientoService = new MantenimientoService();

    // Método que llama tu vista MantenimientoEquipos para llenar la tabla
    public List<EstadoEquipo> listarEquipos() {
        return estadoEquipoService.listar();
    }

    // Método para enviar a mantenimiento (registra en equipo y en la tabla Mantenimiento)
    public boolean enviarAMantenimiento(int equipoId, String motivo) {
        return mantenimientoService.enviarAMantenimiento(equipoId, motivo);
    }

    // Método para finalizar mantenimiento (reparado o dado de baja)
    public boolean finalizarMantenimiento(int equipoId, boolean reparadoConExito, String motivo) {
        return mantenimientoService.finalizarMantenimiento(equipoId, reparadoConExito, motivo);
    }
}