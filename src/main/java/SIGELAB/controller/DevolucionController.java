
package SIGELAB.controller;

import SIGELAB.service.DevolucionService;

import java.util.List;

public class DevolucionController {

    private final DevolucionService devolucionService;

    public DevolucionController() {
        this.devolucionService = new DevolucionService();
    }

    /**
     * Obtiene los equipos que todavía están
     * pendientes de devolución.
     */
    public List<Object[]> listarPendientes() {
        return devolucionService.listarPendientes();
    }

    /**
     * Registra la devolución de un equipo.
     *
     * @param detalleId ID del detalle del préstamo.
     * @param condicion BUENO o DANADO.
     * @param usuarioId ID del usuario responsable.
     * @return true si se registró correctamente.
     */
    public boolean registrarDevolucion(
            int detalleId,
            String condicion,
            int usuarioId) {

        return devolucionService.registrarDevolucion(
                detalleId,
                condicion,
                usuarioId
        );
    }
}
