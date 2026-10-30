package SIGELAB.service;

import SIGELAB.repository.ReportesRepository;
import java.util.List;

public class ReportesService {
    private final ReportesRepository repository = new ReportesRepository();

    public List<Object[]> obtenerEquiposPorEstado(String estado) { return repository.obtenerEquiposPorEstado(estado); }
    public List<Object[]> obtenerPrestamosPorEstado(String estado) { return repository.obtenerPrestamosPorEstado(estado); }
    public List<Object[]> obtenerPrestamosPorFechas(String ini, String fin) { return repository.obtenerPrestamosPorFechas(ini, fin); }
    public List<Object[]> obtenerHistorialEquipo(String codigo) { return repository.obtenerHistorialEquipo(codigo); }
    public List<Object[]> obtenerHistorialMantenimiento() { return repository.obtenerHistorialMantenimiento(); }
}