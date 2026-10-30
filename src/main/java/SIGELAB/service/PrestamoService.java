
package SIGELAB.service;

import SIGELAB.model.Prestamo;
import SIGELAB.repository.PrestamoRepository;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PrestamoService {

    private final PrestamoRepository repository;

    public PrestamoService() {
        this.repository = new PrestamoRepository();
    }

    public boolean registrarPrestamo(
            Prestamo prestamo,
            List<Integer> equiposIds) {

        // 1. Validar que exista el préstamo.
        if (prestamo == null) {
            throw new IllegalArgumentException(
                    "Debe proporcionar los datos del préstamo.");
        }

        // 2. Validar el solicitante.
        if (prestamo.getSolicitante() == null
                || prestamo.getSolicitante().isBlank()) {
            throw new IllegalArgumentException(
                    "Debe ingresar el nombre del solicitante.");
        }

        if (prestamo.getSolicitante().trim().length() > 150) {
            throw new IllegalArgumentException(
                    "El nombre del solicitante no puede superar 150 caracteres.");
        }

        // 3. Validar el usuario responsable.
        if (prestamo.getUsuarioId() <= 0) {
            throw new IllegalArgumentException(
                    "Debe indicar un usuario responsable válido.");
        }

        // 4. Establecer la fecha del préstamo.
        if (prestamo.getFechaPrestamo() == null) {
            prestamo.setFechaPrestamo(LocalDateTime.now());
        }

        // 5. Validar la fecha esperada de devolución.
        if (prestamo.getFechaDevolucionEsperada() == null) {
            throw new IllegalArgumentException(
                    "Debe indicar la fecha esperada de devolución.");
        }

        if (!prestamo.getFechaDevolucionEsperada()
                .isAfter(prestamo.getFechaPrestamo())) {
            throw new IllegalArgumentException(
                    "La fecha de devolución debe ser posterior a la fecha del préstamo.");
        }

        // 6. Validar que haya equipos seleccionados.
        if (equiposIds == null || equiposIds.isEmpty()) {
            throw new IllegalArgumentException(
                    "Debe seleccionar al menos un equipo.");
        }

        // 7. Validar identificadores y equipos repetidos.
        Set<Integer> equiposUnicos = new HashSet<>();

        for (Integer equipoId : equiposIds) {

            if (equipoId == null || equipoId <= 0) {
                throw new IllegalArgumentException(
                        "Se encontró un identificador de equipo inválido.");
            }

            if (!equiposUnicos.add(equipoId)) {
                throw new IllegalArgumentException(
                        "No puede agregar el mismo equipo dos veces.");
            }
        }

        // 8. Normalizar el nombre del solicitante.
        prestamo.setSolicitante(
                prestamo.getSolicitante().trim());

        // 9. Registrar el préstamo en SQL Server.
        // El repositorio comprueba la disponibilidad
        // y realiza la operación mediante una transacción.
        return repository.guardar(prestamo, equiposIds);
    }
}
