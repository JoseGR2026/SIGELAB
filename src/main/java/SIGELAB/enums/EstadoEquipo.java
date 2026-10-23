package SIGELAB.enums;

public enum EstadoEquipo {
    DISPONIBLE,
    PRESTADO,
    DANADO,
    MANTENIMIENTO,
    BAJA;

    public static EstadoEquipo desdeTexto(String texto) {
        if (texto == null) return DISPONIBLE;
        return switch (texto.toUpperCase()) {
            case "PRESTADO" -> PRESTADO;
            case "DANADO", "DAÑADO" -> DANADO;
            case "MANTENIMIENTO" -> MANTENIMIENTO;
            case "BAJA" -> BAJA;
            default -> DISPONIBLE;
        };
    }
}