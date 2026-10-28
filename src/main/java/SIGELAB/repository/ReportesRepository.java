package SIGELAB.repository;

import SIGELAB.config.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReportesRepository {

    // Consulta 1: Obtener equipos filtrados por su estado (DISPONIBLE, PRESTADO, DAÑADO, etc.)
    public List<Object[]> obtenerEquiposPorEstado(String estadoFiltro) {
        List<Object[]> lista = new ArrayList<>();

        String sql = "SELECT e.codigo_inventario, e.nombre, l.nombre AS laboratorio, e.estado " +
                "FROM Equipo e " +
                "LEFT JOIN Laboratorio l ON e.laboratorio_id = l.id " +
                "WHERE e.estado = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, estadoFiltro);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Object[] fila = new Object[]{
                            rs.getString("codigo_inventario"),
                            rs.getString("nombre"),
                            rs.getString("laboratorio"),
                            rs.getString("estado")
                    };
                    lista.add(fila);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    // Consulta 2: Obtener préstamos activos, vencidos o finalizados
    public List<Object[]> obtenerPrestamosPorEstado(String estadoPrestamo) {
        List<Object[]> lista = new ArrayList<>();

        // Corregido: fecha_devolucion_esperada es el nombre real en tu BD
        String sql = "SELECT id, solicitante, fecha_prestamo, fecha_devolucion_esperada, estado " +
                "FROM Prestamo " +
                "WHERE estado = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, estadoPrestamo);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Object[] fila = new Object[]{
                            rs.getInt("id"),
                            rs.getString("solicitante"),
                            rs.getTimestamp("fecha_prestamo"),
                            rs.getTimestamp("fecha_devolucion_esperada"),
                            rs.getString("estado")
                    };
                    lista.add(fila);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener reporte de préstamos: " + e.getMessage());
            e.printStackTrace();
        }

        return lista;
    }

    // Consulta 3: Obtener préstamos entre un rango de fechas
    public List<Object[]> obtenerPrestamosPorFechas(String fechaInicio, String fechaFin) {
        List<Object[]> lista = new ArrayList<>();

        String sql = "SELECT id, solicitante, fecha_prestamo, fecha_devolucion_esperada, estado " +
                "FROM Prestamo " +
                "WHERE fecha_prestamo BETWEEN ? AND ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Asumimos formato de fecha "YYYY-MM-DD" que ingresen en la vista
            pstmt.setString(1, fechaInicio);
            pstmt.setString(2, fechaFin);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Object[] fila = new Object[]{
                            rs.getInt("id"),
                            rs.getString("solicitante"),
                            rs.getTimestamp("fecha_prestamo"),
                            rs.getTimestamp("fecha_devolucion_esperada"),
                            rs.getString("estado")
                    };
                    lista.add(fila);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al consultar préstamos por fechas: " + e.getMessage());
            e.printStackTrace();
        }

        return lista;
    }

    // Consulta 4: Métricas rápidas para el Dashboard
    public int[] obtenerMetricasDashboard() {
        int totalEquipos = 0;
        int equiposDisponibles = 0;
        int prestamosActivos = 0;
        int prestamosVencidos = 0;

        String sqlEquipos = "SELECT COUNT(*) FROM Equipo";
        String sqlDisponibles = "SELECT COUNT(*) FROM Equipo WHERE estado = 'DISPONIBLE'";
        String sqlPrestamosActivos = "SELECT COUNT(*) FROM Prestamo WHERE estado = 'ACTIVO'";
        String sqlPrestamosVencidos = "SELECT COUNT(*) FROM Prestamo WHERE estado = 'VENCIDO'";

        try (Connection conn = DatabaseConnection.getConnection()) {

            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sqlEquipos)) {
                if (rs.next()) totalEquipos = rs.getInt(1);
            }
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sqlDisponibles)) {
                if (rs.next()) equiposDisponibles = rs.getInt(1);
            }
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sqlPrestamosActivos)) {
                if (rs.next()) prestamosActivos = rs.getInt(1);
            }
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sqlPrestamosVencidos)) {
                if (rs.next()) prestamosVencidos = rs.getInt(1);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener métricas del Dashboard: " + e.getMessage());
            e.printStackTrace();
        }

        return new int[]{totalEquipos, equiposDisponibles, prestamosActivos, prestamosVencidos};
    }

}

