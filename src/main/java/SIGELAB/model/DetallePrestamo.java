
package SIGELAB.model;

public class DetallePrestamo {

    private int id;
    private int prestamoId;
    private int equipoId;
    private String estadoDevolucion;

    public DetallePrestamo() {
    }

    public DetallePrestamo(int id, int prestamoId,
                           int equipoId, String estadoDevolucion) {
        this.id = id;
        this.prestamoId = prestamoId;
        this.equipoId = equipoId;
        this.estadoDevolucion = estadoDevolucion;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPrestamoId() {
        return prestamoId;
    }

    public void setPrestamoId(int prestamoId) {
        this.prestamoId = prestamoId;
    }

    public int getEquipoId() {
        return equipoId;
    }

    public void setEquipoId(int equipoId) {
        this.equipoId = equipoId;
    }

    public String getEstadoDevolucion() {
        return estadoDevolucion;
    }

    public void setEstadoDevolucion(String estadoDevolucion) {
        this.estadoDevolucion = estadoDevolucion;
    }

    @Override
    public String toString() {
        return "Equipo #" + equipoId
                + " - " + estadoDevolucion;
    }
}
