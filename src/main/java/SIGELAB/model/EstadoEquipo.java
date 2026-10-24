package SIGELAB.model;

public class EstadoEquipo {
    private int id;
    private int laboratorioId;
    private int tipoEquipoId;
    private String codigoInventario;
    private String nombreEquipo;
    private String marca;
    private String modelo;
    private String numeroSerie;
    private String tipo;
    private String estado;
    private String observaciones;

    public EstadoEquipo() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getLaboratorioId() { return laboratorioId; }
    public void setLaboratorioId(int laboratorioId) { this.laboratorioId = laboratorioId; }

    public int getTipoEquipoId() { return tipoEquipoId; }
    public void setTipoEquipoId(int tipoEquipoId) { this.tipoEquipoId = tipoEquipoId; }

    public String getCodigoInventario() { return codigoInventario; }
    public void setCodigoInventario(String codigoInventario) { this.codigoInventario = codigoInventario; }

    public String getNombreEquipo() { return nombreEquipo; }
    public void setNombreEquipo(String nombreEquipo) { this.nombreEquipo = nombreEquipo; }

    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }

    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }

    public String getNumeroSerie() { return numeroSerie; }
    public void setNumeroSerie(String numeroSerie) { this.numeroSerie = numeroSerie; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
}