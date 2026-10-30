
package SIGELAB.controller;

import SIGELAB.service.HistorialPrestamoService;

import java.util.List;

public class HistorialPrestamoController {

    private final HistorialPrestamoService service;

    public HistorialPrestamoController() {
        this.service = new HistorialPrestamoService();
    }

    /**
     * Obtiene el historial de préstamos.
     *
     * @param filtroEstado TODOS, ACTIVO,
     *                     VENCIDO o FINALIZADO.
     * @return Registros de préstamos y sus equipos.
     */
    public List<Object[]> listarHistorial(String filtroEstado) {

        return service.listarHistorial(filtroEstado);
    }
}
