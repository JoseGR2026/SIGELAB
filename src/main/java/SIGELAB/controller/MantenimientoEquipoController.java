package SIGELAB.controller;

import SIGELAB.model.EstadoEquipo;
import SIGELAB.service.EstadoEquipoService;

import java.util.List;

public class MantenimientoEquipoController {
    private final EstadoEquipoService service = new EstadoEquipoService();

    public List<EstadoEquipo> listarEquipos() {
        return service.listar();
    }

    public boolean enviarAMantenimiento(int equipoId, String motivo) {
        return service.enviarAMantenimiento(equipoId, motivo);
    }

    public boolean finalizarMantenimiento(int equipoId, boolean reparadoConExito) {
        return service.finalizarMantenimiento(equipoId, reparadoConExito);
    }
}