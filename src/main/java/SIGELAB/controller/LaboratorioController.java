package SIGELAB.controller;

import SIGELAB.model.Laboratorio;
import SIGELAB.service.LaboratorioService;

import java.util.List;
import java.util.Optional;

public class LaboratorioController {
    private final LaboratorioService service;

    public LaboratorioController() {
        this.service = new LaboratorioService();
    }

    public LaboratorioController(LaboratorioService service) {
        this.service = service;
    }

    public List<Laboratorio> listar() {
        return service.obtenerLaboratorios();
    }

    public Optional<Laboratorio> buscar(int id) {
        return service.buscarPorId(id);
    }

    public Laboratorio crearLaboratorio(String codigo, String nombre, String ubicacion, int capacidad, String estado) throws Exception {
        Laboratorio lab = new Laboratorio();
        lab.setCodigo(codigo);
        lab.setNombre(nombre);
        lab.setUbicacion(ubicacion);
        lab.setCapacidad(capacidad);
        lab.setEstado(estado);
        service.registrarLaboratorio(lab);
        return lab;
    }

    public boolean actualizar(Laboratorio lab) throws Exception {
        service.actualizarLaboratorio(lab);
        return true;
    }
}