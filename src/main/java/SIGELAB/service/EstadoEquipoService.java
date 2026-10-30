package SIGELAB.service;

import SIGELAB.config.DatabaseConnection;
import SIGELAB.model.EstadoEquipo;
import SIGELAB.repository.EstadoEquipoRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.regex.Pattern;

public class EstadoEquipoService {
    private final EstadoEquipoRepository repository = new EstadoEquipoRepository();

    private static final Pattern PATRON_CODIGO = Pattern.compile("^[A-Za-z]{2,4}-\\w+$");

    public List<EstadoEquipo> listar() {
        return repository.listar();
    }

    public boolean enviarAMantenimiento(int equipoId, String motivo) {
        EstadoEquipo equipoActual = repository.buscarPorId(equipoId);

        if (equipoActual != null && "DADO_DE_BAJA".equalsIgnoreCase(equipoActual.getEstado())) {
            throw new IllegalStateException("Acción denegada: El equipo ya se encuentra DADO DE BAJA.");
        }

        if (motivo == null || motivo.trim().isEmpty()) {
            throw new IllegalArgumentException("Debe especificar el motivo o la observación del mantenimiento.");
        }

        boolean actualizado = repository.actualizarEstadoYObservaciones(equipoId, "MANTENIMIENTO", "Mantenimiento: " + motivo.trim());
        if (actualizado) {
            registrarAuditoria(1, "EQUIPO_ENVIADO_MANTENIMIENTO", "Equipo", equipoId, "Equipo enviado a mantenimiento. Motivo: " + motivo.trim());
        }
        return actualizado;
    }

    public boolean finalizarMantenimiento(int equipoId, boolean reparadoConExito, String motivo) {
        EstadoEquipo equipoActual = repository.buscarPorId(equipoId);

        if (equipoActual != null && "DADO_DE_BAJA".equalsIgnoreCase(equipoActual.getEstado())) {
            throw new IllegalStateException("Acción denegada: El equipo ya se encuentra DADO DE BAJA.");
        }

        if (motivo == null || motivo.trim().isEmpty()) {
            motivo = reparadoConExito ? "Reparado exitosamente - Disponible" : "Dado de baja por daños irreparables";
        }

        String nuevoEstado = reparadoConExito ? "DISPONIBLE" : "DADO_DE_BAJA";
        String observacionFinal = reparadoConExito ? "Reparado: " + motivo.trim() : "Dado de baja: " + motivo.trim();

        boolean actualizado = repository.actualizarEstadoYObservaciones(equipoId, nuevoEstado, observacionFinal);
        if (actualizado) {
            String descripcion = reparadoConExito ?
                    "Mantenimiento finalizado con éxito (Reparado). Detalle: " + motivo.trim() :
                    "Equipo dado de baja permanentemente. Motivo: " + motivo.trim();

            registrarAuditoria(1, "MANTENIMIENTO_FINALIZADO", "Equipo", equipoId, descripcion);
        }
        return actualizado;
    }

    public boolean crearEquipo(int laboratorioId, String codigoInventario, String nombre,
                               String marca, String modelo, String numeroSerie,
                               String tipo, String estado, String observaciones) {
        if (codigoInventario == null || !PATRON_CODIGO.matcher(codigoInventario.trim()).matches()) {
            throw new IllegalArgumentException("Formato de código de inventario inválido.");
        }
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del equipo es obligatorio.");
        }
        if (repository.existeCodigo(codigoInventario)) {
            throw new IllegalArgumentException("Ya existe un equipo con el código: " + codigoInventario);
        }

        EstadoEquipo equipo = new EstadoEquipo();
        equipo.setLaboratorioId(laboratorioId);
        equipo.setCodigoInventario(codigoInventario.trim());
        equipo.setNombreEquipo(nombre.trim());
        equipo.setMarca(marca != null ? marca.trim() : "");
        equipo.setModelo(modelo != null ? modelo.trim() : "");
        equipo.setNumeroSerie(numeroSerie != null ? numeroSerie.trim() : "");
        equipo.setTipo(tipo);
        equipo.setEstado(estado);
        equipo.setObservaciones(observaciones != null ? observaciones.trim() : "");

        boolean guardado = repository.guardar(equipo);
        if (guardado) {
            registrarAuditoria(1, "CREACION", "Equipo", equipo.getId(), "Se registró el equipo " + codigoInventario);
        }
        return guardado;
    }

    public boolean actualizar(EstadoEquipo equipo) {
        return repository.actualizar(equipo);
    }

    public String generarSiguienteCodigo() {
        return repository.generarSiguienteCodigo();
    }

    private void registrarAuditoria(int usuarioId, String accion, String entidad, int registroId, String descripcion) {
        String sql = "INSERT INTO Auditoria (usuario_id, accion, entidad, registro_id, descripcion, fecha_hora) VALUES (?, ?, ?, ?, ?, GETDATE())";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, usuarioId > 0 ? usuarioId : 1);
            ps.setString(2, accion);
            ps.setString(3, entidad);
            ps.setInt(4, registroId > 0 ? registroId : 1);
            ps.setString(5, descripcion);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}