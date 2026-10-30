package SIGELAB.service;

import SIGELAB.repository.MantenimientoRepository;

public class MantenimientoService {
    private final MantenimientoRepository repository = new MantenimientoRepository();

    public boolean enviarAMantenimiento(int equipoId, String motivo) {
        if (motivo == null || motivo.trim().isEmpty()) {
            throw new IllegalArgumentException("Debe especificar el motivo del mantenimiento.");
        }

        // Validar estado actual del equipo
        String estadoActual = repository.obtenerEstadoEquipo(equipoId);
        if (estadoActual == null) {
            throw new IllegalStateException("El equipo seleccionado no existe en la base de datos.");
        }

        if ("MANTENIMIENTO".equalsIgnoreCase(estadoActual)) {
            throw new IllegalStateException("El equipo ya se encuentra en mantenimiento y no puede ser enviado nuevamente.");
        }
        if ("DADO_DE_BAJA".equalsIgnoreCase(estadoActual)) {
            throw new IllegalStateException("No se puede enviar a mantenimiento un equipo que ha sido dado de baja.");
        }
        if ("PRESTADO".equalsIgnoreCase(estadoActual)) {
            throw new IllegalStateException("No se puede enviar a mantenimiento un equipo que se encuentra actualmente prestado.");
        }

        return repository.registrarIngreso(equipoId, "CORRECTIVO", motivo.trim(), "Taller SIGELAB");
    }

    public boolean finalizarMantenimiento(int equipoId, boolean reparadoConExito, String motivo) {
        if (motivo == null || motivo.trim().isEmpty()) {
            motivo = reparadoConExito ? "Reparado exitosamente" : "Dado de baja por daños irreparables";
        }

        // Validar estado actual del equipo
        String estadoActual = repository.obtenerEstadoEquipo(equipoId);
        if (estadoActual == null) {
            throw new IllegalStateException("El equipo seleccionado no existe en la base de datos.");
        }

        if (reparadoConExito) {
            // Intentando poner DISPONIBLE
            if ("DISPONIBLE".equalsIgnoreCase(estadoActual)) {
                throw new IllegalStateException("El equipo ya se encuentra disponible; no se puede volver a poner disponible.");
            }
            if ("DADO_DE_BAJA".equalsIgnoreCase(estadoActual)) {
                throw new IllegalStateException("No se puede cambiar a disponible un equipo que está dado de baja.");
            }
            if ("PRESTADO".equalsIgnoreCase(estadoActual)) {
                throw new IllegalStateException("No se puede cambiar a disponible un equipo que se encuentra prestado.");
            }
        } else {
            // Intentando DAR DE BAJA
            if ("DADO_DE_BAJA".equalsIgnoreCase(estadoActual)) {
                throw new IllegalStateException("El equipo ya se encuentra dado de baja.");
            }
        }

        return repository.finalizarMantenimiento(equipoId, reparadoConExito, motivo.trim());
    }
}