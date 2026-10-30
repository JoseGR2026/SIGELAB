
package SIGELAB.controller;

import SIGELAB.model.Prestamo;
import SIGELAB.service.PrestamoService;

import java.time.LocalDateTime;
import java.util.List;

public class PrestamoController {

    private final PrestamoService prestamoService;

    public PrestamoController() {
        this.prestamoService = new PrestamoService();
    }

    public boolean registrarPrestamo(
            String solicitante,
            LocalDateTime fechaDevolucionEsperada,
            int usuarioId,
            String observaciones,
            List<Integer> equiposIds) {

        Prestamo prestamo = new Prestamo();

        prestamo.setSolicitante(solicitante);
        prestamo.setFechaPrestamo(LocalDateTime.now());
        prestamo.setFechaDevolucionEsperada(
                fechaDevolucionEsperada);
        prestamo.setUsuarioId(usuarioId);
        prestamo.setEstado("ACTIVO");
        prestamo.setObservaciones(observaciones);

        return prestamoService.registrarPrestamo(
                prestamo, equiposIds);
    }
}
