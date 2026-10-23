package SIGELAB.service;

import SIGELAB.model.Laboratorio;
import SIGELAB.repository.LaboratorioRepository;
import SIGELAB.util.BusinessException;

import java.util.List;
import java.util.Optional;

public class LaboratorioService {
    private final LaboratorioRepository repository = new LaboratorioRepository();

    public List<Laboratorio> obtenerLaboratorios() {
        return repository.listar();
    }

    public Optional<Laboratorio> buscarPorId(int id) {
        return repository.buscarPorId(id);
    }

    public void registrarLaboratorio(Laboratorio lab) {
        validarLaboratorio(lab);

        if (repository.existeCodigo(lab.getCodigo().trim())) {
            throw new BusinessException("Ya existe un laboratorio registrado con el código: " + lab.getCodigo());
        }

        if (!repository.guardar(lab)) {
            throw new RuntimeException("No se pudo registrar el laboratorio en la base de datos.");
        }
    }

    public void actualizarLaboratorio(Laboratorio lab) {
        if (lab.getId() <= 0) {
            throw new BusinessException("ID de laboratorio inválido para actualizar.");
        }

        validarLaboratorio(lab);

        if (repository.existeCodigo(lab.getCodigo().trim(), lab.getId())) {
            throw new BusinessException("El código ya está siendo utilizado por otro laboratorio.");
        }

        if (!repository.actualizar(lab)) {
            throw new RuntimeException("No se pudo actualizar el laboratorio.");
        }
    }

    private void validarLaboratorio(Laboratorio lab) {
        if (lab == null) {
            throw new BusinessException("Los datos del laboratorio no pueden ser nulos.");
        }

        // 1. Código estricto: Formato institucional exacto (Ej: LAB-01, LAB-02)
        String codigo = lab.getCodigo() != null ? lab.getCodigo().trim().toUpperCase() : "";
        if (!codigo.matches("^[A-Z]{3}-\\d{2}$")) {
            throw new BusinessException("El código debe tener el formato institucional exacto (ej. LAB-01).");
        }
        lab.setCodigo(codigo); // Fuerza a guardarse en mayúsculas

        // 2. Nombre estricto: Solo letras y espacios (mínimo 5 caracteres, sin números ni basura)
        String nombre = lab.getNombre() != null ? lab.getNombre().trim() : "";
        if (nombre.length() < 5 || !nombre.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$")) {
            throw new BusinessException("El nombre del laboratorio debe contener únicamente letras y espacios (mínimo 5 caracteres).");
        }

        // 3. Ubicación estricta: Mínimo 6 caracteres permitiendo letras, números, espacios y guiones
        String ubicacion = lab.getUbicacion() != null ? lab.getUbicacion().trim() : "";
        if (ubicacion.length() < 6 || !ubicacion.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ0-9\\s\\-]+$")) {
            throw new BusinessException("La ubicación debe ser descriptiva (ej. Edificio A - Nivel 2).");
        }

        // 4. Capacidad real permitida
        int capacidad = lab.getCapacidad();
        if (capacidad <= 0 || capacidad > 300) {
            throw new BusinessException("La capacidad debe ser un número entero válido entre 1 y 300.");
        }
    }
}