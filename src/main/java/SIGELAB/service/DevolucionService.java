
package SIGELAB.service;

import SIGELAB.repository.DevolucionRepository;

import java.util.List;
import java.util.Locale;

public class DevolucionService {

    private final DevolucionRepository repository;

    public DevolucionService() {
        this.repository = new DevolucionRepository();
    }

    /**
     * Consulta los equipos pendientes de devolución.
     */
    public List<Object[]> listarPendientes() {
        return repository.listarPendientes();
    }

    /**
     * Registra la devolución de un equipo.
     *
     * @param detalleId ID del detalle del préstamo.
     * @param condicion BUENO o DANADO.
     * @param usuarioId Usuario que registra la devolución.
     * @return true si la devolución se registró.
     */
    public boolean registrarDevolucion(
            int detalleId,
            String condicion,
            int usuarioId) {

        // 1. Validar el detalle del préstamo.
        if (detalleId <= 0) {
            throw new IllegalArgumentException(
                    "Debe seleccionar un equipo válido para devolver.");
        }

        // 2. Validar el usuario responsable.
        if (usuarioId <= 0) {
            throw new IllegalArgumentException(
                    "No se encontró un usuario responsable válido.");
        }

        // 3. Validar que se indique la condición.
        if (condicion == null || condicion.isBlank()) {
            throw new IllegalArgumentException(
                    "Debe indicar la condición del equipo.");
        }

        // 4. Normalizar la condición.
        String condicionNormalizada =
                condicion.trim().toUpperCase(Locale.ROOT);

        // 5. Aceptar únicamente los estados permitidos.
        if (!"BUENO".equals(condicionNormalizada)
                && !"DANADO".equals(condicionNormalizada)) {

            throw new IllegalArgumentException(
                    "La condición debe ser BUENO o DANADO.");
        }

        // 6. Registrar la devolución en SQL Server.
        // El repositorio actualizará el detalle, el equipo,
        // el préstamo y la auditoría en una transacción.
        return repository.registrarDevolucion(
                detalleId,
                condicionNormalizada,
                usuarioId
        );
    }
}
