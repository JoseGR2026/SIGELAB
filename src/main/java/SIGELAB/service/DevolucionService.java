package SIGELAB.service;

import SIGELAB.repository.DevolucionRepository;

import java.util.List;
import java.util.Locale;

public class DevolucionService {

    private final DevolucionRepository repository;

    public DevolucionService() {
        this.repository = new DevolucionRepository();
    }

    public List<Object[]> listarPendientes() {
        return repository.listarPendientes();
    }

    public boolean registrarDevolucion(
            int detalleId,
            String condicion,
            int usuarioId) {

        if (detalleId <= 0) {
            throw new IllegalArgumentException("Debe seleccionar un equipo válido para devolver.");
        }

        if (usuarioId <= 0) {
            throw new IllegalArgumentException("No se encontró un usuario responsable válido.");
        }

        if (condicion == null || condicion.isBlank()) {
            throw new IllegalArgumentException("Debe indicar la condición del equipo.");
        }

        String condicionNormalizada = condicion.trim().toUpperCase(Locale.ROOT);

        if ("DAÑADO".equals(condicionNormalizada)) {
            condicionNormalizada = "DANADO";
        }

        if (!"DISPONIBLE".equals(condicionNormalizada)
                && !"BUENO".equals(condicionNormalizada)
                && !"DANADO".equals(condicionNormalizada)) {

            throw new IllegalArgumentException("La condición debe ser DISPONIBLE o DANADO.");
        }

        return repository.registrarDevolucion(
                detalleId,
                condicionNormalizada,
                usuarioId
        );
    }
}