
package SIGELAB.service;

import SIGELAB.repository.AuditoriaConsultaRepository;

import java.util.List;

public class AuditoriaConsultaService {

    private final AuditoriaConsultaRepository repository;

    public AuditoriaConsultaService() {
        this.repository = new AuditoriaConsultaRepository();
    }

    /**
     * Obtiene los registros de auditoría
     * almacenados en SQL Server.
     *
     * @return Lista de movimientos registrados.
     */
    public List<Object[]> listarAuditoria() {

        return repository.listarAuditoria();
    }
}
