package SIGELAB.controller;

import SIGELAB.service.DevolucionService;

import java.util.ArrayList;
import java.util.List;

public class DevolucionController {
    private final DevolucionService service = new DevolucionService();

    public DevolucionController() {}

    public List<Object[]> listarPendientes() {
        return service.listarPendientes();
    }

    public List<String> listarSolicitantesConPendientes() {
        List<String> solicitantes = new ArrayList<>();
        try {
            List<Object[]> pendientes = service.listarPendientes();
            for (Object[] fila : pendientes) {
                if (fila.length > 1 && fila[1] != null) {
                    String solicitante = fila[1].toString().trim();
                    if (!solicitante.isEmpty() && !solicitantes.contains(solicitante)) {
                        solicitantes.add(solicitante);
                    }
                }
            }
        } catch (Exception e) {
            // Ignorado
        }
        return solicitantes;
    }

    public boolean registrarDevolucionMasiva(List<Integer> detalleIds, String condicion, int usuarioId) {
        try {
            for (int detalleId : detalleIds) {
                boolean registrado = service.registrarDevolucion(detalleId, condicion, usuarioId);
                if (!registrado) {
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            throw new RuntimeException("Error en devolución masiva: " + e.getMessage());
        }
    }
}