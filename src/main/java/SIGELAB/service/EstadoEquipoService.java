package SIGELAB.service;

import SIGELAB.model.EstadoEquipo;
import SIGELAB.repository.EstadoEquipoRepository;

import java.util.List;
import java.util.regex.Pattern;

public class EstadoEquipoService {
    private final EstadoEquipoRepository repository = new EstadoEquipoRepository();

    // CORREGIDO: Ahora acepta de 2 a 4 letras antes del guion (cubre INV-001 y CM-787 sin problemas)
    private static final Pattern PATRON_CODIGO = Pattern.compile("^[A-Za-z]{2,4}-\\w+$");

    public List<EstadoEquipo> listar() {
        return repository.listar();
    }



    public boolean crearEquipo(int laboratorioId, String codigoInventario, String nombre,
                               String marca, String modelo, String numeroSerie,
                               String tipo, String estado, String observaciones) {

        // 1. Validar Código de Inventario
        if (codigoInventario == null || codigoInventario.trim().isEmpty()) {
            throw new IllegalArgumentException("El código de inventario no puede estar vacío.");
        }
        if (!PATRON_CODIGO.matcher(codigoInventario.trim()).matches()) {
            throw new IllegalArgumentException("El código de inventario debe tener el formato correcto (Ejemplo: CM-787 o INV-001).");
        }

        // 2. Validar Número de Serie (Libre de restricciones rígidas)
        if (numeroSerie == null) {
            numeroSerie = "";
        }

        // 3. Validar Nombre del Equipo
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del equipo es obligatorio.");
        }

        // 4. Regla RN-04: Validar unicidad del código de inventario en la BD
        if (repository.existeCodigo(codigoInventario)) {
            throw new IllegalArgumentException("Ya existe un equipo registrado con el código: " + codigoInventario);
        }

        EstadoEquipo equipo = new EstadoEquipo();
        equipo.setLaboratorioId(laboratorioId);
        equipo.setTipoEquipoId(1);
        equipo.setCodigoInventario(codigoInventario.trim());
        equipo.setNombreEquipo(nombre.trim());
        equipo.setMarca(marca != null ? marca.trim() : "");
        equipo.setModelo(modelo != null ? modelo.trim() : "");
        equipo.setNumeroSerie(numeroSerie.trim());
        equipo.setTipo(tipo);
        equipo.setEstado(estado);
        equipo.setObservaciones(observaciones != null ? observaciones.trim() : "");

        return repository.guardar(equipo);
    }

    public boolean enviarAMantenimiento(int equipoId, String motivo) {
        EstadoEquipo equipoActual = repository.buscarPorId(equipoId);

        if (equipoActual != null && "DADO_DE_BAJA".equalsIgnoreCase(equipoActual.getEstado())) {
            throw new IllegalStateException("Acción denegada: El equipo ya se encuentra DADO DE BAJA y no puede ser reparado ni puesto como disponible.");
        }

        if (motivo == null || motivo.trim().isEmpty()) {
            throw new IllegalArgumentException("Debe especificar el motivo o la observación del mantenimiento.");
        }

        return repository.actualizarEstadoYObservaciones(equipoId, "MANTENIMIENTO", "Mantenimiento: " + motivo.trim());
    }

    public boolean finalizarMantenimiento(int equipoId, boolean reparadoConExito) {
        // 1. Consultar el equipo actual para verificar su estado en la base de datos
        EstadoEquipo equipoActual = repository.buscarPorId(equipoId);

        if (equipoActual != null && "DADO_DE_BAJA".equalsIgnoreCase(equipoActual.getEstado())) {
            throw new IllegalStateException("Acción denegada: El equipo ya se encuentra DADO DE BAJA y no puede ser reparado ni puesto como disponible.");
        }



        // 2. Si pasa la validación, continúa el flujo normal
        String nuevoEstado = reparadoConExito ? "DISPONIBLE" : "DADO_DE_BAJA";
        String observacionFinal = reparadoConExito ? "Reparado exitosamente - Disponible" : "Dado de baja por daños irreparables en taller";

        return repository.actualizarEstadoYObservaciones(equipoId, nuevoEstado, observacionFinal);
    }

    public boolean actualizar(EstadoEquipo equipo) {
        if (equipo.getCodigoInventario() == null || equipo.getCodigoInventario().trim().isEmpty()) {
            throw new IllegalArgumentException("El código de inventario no puede estar vacío.");
        }
        if (!PATRON_CODIGO.matcher(equipo.getCodigoInventario().trim()).matches()) {
            throw new IllegalArgumentException("El código de inventario debe tener el formato correcto (Ejemplo: CM-787 o INV-001).");
        }

        if (equipo.getNumeroSerie() == null) {
            equipo.setNumeroSerie("");
        }

        if (equipo.getNombreEquipo() == null || equipo.getNombreEquipo().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del equipo es obligatorio.");
        }

        if (repository.existeCodigo(equipo.getCodigoInventario(), equipo.getId())) {
            throw new IllegalArgumentException("El código de inventario '" + equipo.getCodigoInventario() + "' ya pertenece a otro equipo.");
        }

        return repository.actualizar(equipo);
    }

    public String generarSiguienteCodigo() {
        return repository.generarSiguienteCodigo();
    }
}
