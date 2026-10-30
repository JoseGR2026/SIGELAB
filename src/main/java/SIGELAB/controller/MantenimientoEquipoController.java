package SIGELAB.controller;

import SIGELAB.model.EstadoEquipo;
import SIGELAB.repository.EstadoEquipoRepository;
import SIGELAB.service.EstadoEquipoService;

import java.util.List;

public class MantenimientoEquipoController {
    private final EstadoEquipoService service = new EstadoEquipoService();
    private final EstadoEquipoRepository repository = new EstadoEquipoRepository();

    public List<EstadoEquipo> listarEquipos() {
        return service.listar();
    }

    public boolean enviarAMantenimiento(int equipoId, String motivo) {
        return service.enviarAMantenimiento(equipoId, motivo);
    }

    public boolean finalizarMantenimiento(int equipoId, boolean reparadoConExito, String motivo) {
        boolean resultado = service.finalizarMantenimiento(equipoId, reparadoConExito, motivo);

        try {
            List<EstadoEquipo> equipos = repository.listar();
            for (EstadoEquipo eq : equipos) {
                if (eq.getId() == equipoId) {
                    eq.setObservaciones(motivo);
                    eq.setEstado(reparadoConExito ? "DISPONIBLE" : "DADO_DE_BAJA");
                    repository.actualizar(eq);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return resultado;
    }
}