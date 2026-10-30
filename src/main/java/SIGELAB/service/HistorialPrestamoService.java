
package SIGELAB.service;

import SIGELAB.repository.HistorialPrestamoRepository;

import java.util.List;
import java.util.Locale;

public class HistorialPrestamoService {

    private final HistorialPrestamoRepository repository;

    public HistorialPrestamoService() {
        this.repository = new HistorialPrestamoRepository();
    }

    /**
     * Consulta el historial de préstamos.
     *
     * @param filtroEstado TODOS, ACTIVO,
     *                     VENCIDO o FINALIZADO.
     * @return Lista de préstamos y sus equipos.
     */
    public List<Object[]> listarHistorial(String filtroEstado) {

        String estado = filtroEstado == null
                ? "TODOS"
                : filtroEstado.trim().toUpperCase(Locale.ROOT);

        if (!estado.equals("TODOS")
                && !estado.equals("ACTIVO")
                && !estado.equals("VENCIDO")
                && !estado.equals("FINALIZADO")) {

            throw new IllegalArgumentException(
                    "El estado seleccionado no es válido."
            );
        }

        return repository.listarHistorial(estado);
    }
}
