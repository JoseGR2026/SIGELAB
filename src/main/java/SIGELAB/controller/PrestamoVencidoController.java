
package SIGELAB.controller;

import SIGELAB.service.PrestamoVencidoService;

public class PrestamoVencidoController {

    private final PrestamoVencidoService service;

    public PrestamoVencidoController() {
        this.service = new PrestamoVencidoService();
    }

    /**
     * Detecta y actualiza los préstamos vencidos.
     *
     * @param usuarioId ID del usuario responsable.
     * @return Cantidad de préstamos actualizados.
     */
    public int actualizarVencidos(int usuarioId) {

        if (usuarioId <= 0) {
            throw new IllegalArgumentException(
                    "Debe proporcionar un usuario válido.");
        }

        return service.actualizarVencidos(usuarioId);
    }
}
