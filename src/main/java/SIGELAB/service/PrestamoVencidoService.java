
package SIGELAB.service;

import SIGELAB.repository.PrestamoVencidoRepository;

public class PrestamoVencidoService {

    private final PrestamoVencidoRepository repository;

    public PrestamoVencidoService() {
        this.repository = new PrestamoVencidoRepository();
    }

    /**
     * Detecta préstamos vencidos y actualiza su estado.
     *
     * Solo se actualizan préstamos ACTIVO cuya fecha
     * esperada de devolución ya pasó y que todavía
     * tienen equipos pendientes.
     *
     * @param usuarioId ID del usuario responsable.
     * @return Cantidad de préstamos marcados como vencidos.
     */
    public int actualizarVencidos(int usuarioId) {

        if (usuarioId <= 0) {
            throw new IllegalArgumentException(
                    "Debe proporcionar un usuario responsable válido.");
        }

        return repository.actualizarVencidos(usuarioId);
    }
}
