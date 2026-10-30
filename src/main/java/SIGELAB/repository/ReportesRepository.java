package SIGELAB.repository;

import SIGELAB.config.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReportesRepository {

    public List<Object[]> obtenerEquiposPorEstado(String estadoFiltro) {
        List<Object[]> lista = new ArrayList<>();
        String sql = "SELECT e.codigo_inventario, e.nombre, ISNULL(l.nombre, 'General') AS laboratorio, e.estado " +
                "FROM Equipo e LEFT JOIN Laboratorio l ON e.laboratorio_id = l.id " +
                "WHERE e.estado = ? ORDER BY e.codigo_inventario ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, estadoFiltro);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Object[]{rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4)});
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    public List<Object[]> obtenerPrestamosPorEstado(String estadoPrestamo) {
        List<Object[]> lista = new ArrayList<>();
        String sql = "SELECT id, solicitante, fecha_prestamo, fecha_devolucion_esperada, estado " +
                "FROM Prestamo WHERE estado = ? ORDER BY fecha_prestamo DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, estadoPrestamo);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Object[]{rs.getInt(1), rs.getString(2), rs.getTimestamp(3), rs.getTimestamp(4), rs.getString(5)});
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    public List<Object[]> obtenerPrestamosPorFechas(String fechaInicio, String fechaFin) {
        List<Object[]> lista = new ArrayList<>();
        String sql = "SELECT id, solicitante, fecha_prestamo, fecha_devolucion_esperada, estado " +
                "FROM Prestamo WHERE fecha_prestamo BETWEEN ? AND ? ORDER BY fecha_prestamo DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, fechaInicio);
            pstmt.setString(2, fechaFin);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Object[]{rs.getInt(1), rs.getString(2), rs.getTimestamp(3), rs.getTimestamp(4), rs.getString(5)});
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    public List<Object[]> obtenerHistorialEquipo(String codigoInventario) {
        List<Object[]> lista = new ArrayList<>();

        // Se usa CAST(... AS VARCHAR(MAX)) para evitar el error de tipos TEXT en el UNION de SQL Server
        String sql = "SELECT a.fecha_hora, a.accion, CAST(a.descripcion AS VARCHAR(MAX)) AS descripcion " +
                "FROM Auditoria a " +
                "JOIN Equipo e ON a.registro_id = e.id AND a.entidad = 'Equipo' " +
                "WHERE e.codigo_inventario LIKE ? " +
                "UNION " +
                "SELECT m.fecha_ingreso AS fecha_hora, CONCAT('MANTENIMIENTO: ', m.tipo_mantenimiento) AS accion, CAST(CONCAT('Estado: ', m.estado, ' - Observación: ', ISNULL(m.observaciones, '')) AS VARCHAR(MAX)) AS descripcion " +
                "FROM Mantenimiento m " +
                "JOIN Equipo e ON m.equipo_id = e.id " +
                "WHERE e.codigo_inventario LIKE ? " +
                "ORDER BY fecha_hora DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String filtro = "%" + (codigoInventario != null ? codigoInventario.trim() : "") + "%";
            pstmt.setString(1, filtro);
            pstmt.setString(2, filtro);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Object[]{
                            rs.getTimestamp("fecha_hora"),
                            rs.getString("accion"),
                            rs.getString("descripcion")
                    });
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public List<Object[]> obtenerHistorialMantenimiento() {
        List<Object[]> lista = new ArrayList<>();
        // Consulta directa a la tabla Mantenimiento unida con Equipo para traer el código y nombre reales
        String sql = "SELECT m.fecha_ingreso, e.codigo_inventario, e.nombre, m.tipo_mantenimiento, m.responsable, m.estado, m.resultado, m.observaciones " +
                "FROM Mantenimiento m " +
                "JOIN Equipo e ON m.equipo_id = e.id " +
                "ORDER BY m.fecha_ingreso DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(new Object[]{
                        rs.getTimestamp("fecha_ingreso"),
                        rs.getString("codigo_inventario"),
                        rs.getString("nombre"),
                        rs.getString("tipo_mantenimiento"),
                        rs.getString("responsable"),
                        rs.getString("estado"),
                        rs.getString("resultado"),
                        rs.getString("observaciones")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
}