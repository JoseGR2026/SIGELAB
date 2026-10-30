
package SIGELAB.controller;

import SIGELAB.service.AuditoriaConsultaService;

import java.util.List;

public class AuditoriaConsultaController {

    private final AuditoriaConsultaService service;

    /**
     * Constructor del controlador.
     */
    public AuditoriaConsultaController() {
        this.service = new AuditoriaConsultaService();
    }

    /**
     * Obtiene todos los registros de auditoría
     * almacenados en SQL Server.
     *
     * @return Lista de movimientos registrados.
     */
    public List<Object[]> listarAuditoria() {

        return service.listarAuditoria();
    }
}
