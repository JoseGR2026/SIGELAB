package SIGELAB.service;

import SIGELAB.repository.ReportesRepository;
import java.util.List;

public class ReportesService {

    private final ReportesRepository reportesRepository;

    public ReportesService() {
        this.reportesRepository = new ReportesRepository();
    }

    public List<Object[]> obtenerEquiposPorEstado(String estado) {
        if (estado == null || estado.trim().isEmpty()) {
            return List.of();
        }
        return reportesRepository.obtenerEquiposPorEstado(estado);
    }

    // Servicio para obtener los préstamos según su estado (ej. ACTIVO, VENCIDO, etc.)
    public List<Object[]> obtenerPrestamosPorEstado(String estadoPrestamo) {
        return reportesRepository.obtenerPrestamosPorEstado(estadoPrestamo);
    }

    // Servicio para obtener los préstamos entre un rango de fechas
    public List<Object[]> obtenerPrestamosPorFechas(String fechaInicio, String fechaFin) {
        return reportesRepository.obtenerPrestamosPorFechas(fechaInicio, fechaFin);
    }

    // Servicio para el Dashboard
    public int[] obtenerMetricasDashboard() {
        return reportesRepository.obtenerMetricasDashboard();
    }
}
