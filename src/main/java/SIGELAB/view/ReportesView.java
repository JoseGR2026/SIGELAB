package SIGELAB.view;

import SIGELAB.service.ReportesService;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ReportesView extends JFrame {

    private final ReportesService reportesService;

    // Componentes Dashboard
    private JLabel lblTotalEquipos;
    private JLabel lblEquiposDisponibles;
    private JLabel lblPrestamosActivos;
    private JLabel lblPrestamosVencidos;

    // Componentes Pestaña 2: Equipos por Estado
    private JComboBox<String> cmbEstadoEquipo;
    private JTable tablaEquipos;
    private DefaultTableModel modeloTablaEquipos;

    // Componentes Pestaña 3: Préstamos por Estado
    private JComboBox<String> cmbEstadoPrestamo;
    private JTable tablaPrestamos;
    private DefaultTableModel modeloTablaPrestamos;

    // Componentes Pestaña 4: Préstamos entre Fechas
    private JTextField txtFechaInicio;
    private JTextField txtFechaFin;
    private JTable tablaPrestamosFechas;
    private DefaultTableModel modeloTablaPrestamosFechas;

    public ReportesView() {
        this.reportesService = new ReportesService();
        configurarVentana();
    }

    private void configurarVentana() {
        setTitle("SIGELAB - Dashboard y Reportes");
        setSize(950, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Dashboard", crearPanelDashboard());
        tabbedPane.addTab("Equipos por Estado", crearPanelEquipos());
        tabbedPane.addTab("Préstamos por Estado", crearPanelPrestamos());
        tabbedPane.addTab("Préstamos entre Fechas", crearPanelPrestamosFechas());

        add(tabbedPane);
    }

    private JPanel crearPanelDashboard() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel lblTitulo = new JLabel("Resumen General del Laboratorio", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 20));
        panel.add(lblTitulo, BorderLayout.NORTH);

        // Grid para las tarjetas de métricas (2 filas x 2 columnas)
        JPanel panelTarjetas = new JPanel(new GridLayout(2, 2, 20, 20));

        lblTotalEquipos = crearTarjeta("Total de Equipos", "0", new Color(41, 128, 185), panelTarjetas);
        lblEquiposDisponibles = crearTarjeta("Equipos Disponibles", "0", new Color(39, 174, 96), panelTarjetas);
        lblPrestamosActivos = crearTarjeta("Préstamos Activos", "0", new Color(243, 156, 18), panelTarjetas);
        lblPrestamosVencidos = crearTarjeta("Préstamos Vencidos", "0", new Color(192, 57, 43), panelTarjetas);

        panel.add(panelTarjetas, BorderLayout.CENTER);

        JButton btnActualizar = new JButton("Actualizar Dashboard");
        btnActualizar.setFont(new Font("Arial", Font.BOLD, 14));
        btnActualizar.addActionListener(e -> cargarMetricasDashboard());

        JPanel panelBoton = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelBoton.add(btnActualizar);
        panel.add(panelBoton, BorderLayout.SOUTH);

        cargarMetricasDashboard();
        return panel;
    }

    private JLabel crearTarjeta(String titulo, String valorInicial, Color colorFondo, JPanel contenedor) {
        JPanel tarjeta = new JPanel(new BorderLayout());
        tarjeta.setBackground(colorFondo);
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(colorFondo.darker(), 2),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel lblTitulo = new JLabel(titulo, SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));
        lblTitulo.setForeground(Color.WHITE);

        JLabel lblValor = new JLabel(valorInicial, SwingConstants.CENTER);
        lblValor.setFont(new Font("Arial", Font.BOLD, 36));
        lblValor.setForeground(Color.WHITE);

        tarjeta.add(lblTitulo, BorderLayout.NORTH);
        tarjeta.add(lblValor, BorderLayout.CENTER);

        contenedor.add(tarjeta);
        return lblValor;
    }

    private JPanel crearPanelEquipos() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel panelFiltro = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panelFiltro.add(new JLabel("Estado del Equipo:"));

        cmbEstadoEquipo = new JComboBox<>(new String[]{"DISPONIBLE", "PRESTADO", "DAÑADO", "MANTENIMIENTO", "BAJA"});
        panelFiltro.add(cmbEstadoEquipo);

        JButton btnGenerar = new JButton("Generar Reporte");
        btnGenerar.addActionListener(e -> cargarDatosEquipos());
        panelFiltro.add(btnGenerar);

        panel.add(panelFiltro, BorderLayout.NORTH);

        modeloTablaEquipos = new DefaultTableModel(new String[]{"Código Inventario", "Nombre del Equipo", "Laboratorio", "Estado"}, 0);
        tablaEquipos = new JTable(modeloTablaEquipos);
        panel.add(new JScrollPane(tablaEquipos), BorderLayout.CENTER);

        cargarDatosEquipos();
        return panel;
    }

    private JPanel crearPanelPrestamos() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel panelFiltro = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panelFiltro.add(new JLabel("Estado del Préstamo:"));

        cmbEstadoPrestamo = new JComboBox<>(new String[]{"ACTIVO", "VENCIDO", "FINALIZADO"});
        panelFiltro.add(cmbEstadoPrestamo);

        JButton btnGenerarPrestamos = new JButton("Generar Reporte");
        btnGenerarPrestamos.addActionListener(e -> cargarDatosPrestamos());
        panelFiltro.add(btnGenerarPrestamos);

        panel.add(panelFiltro, BorderLayout.NORTH);

        modeloTablaPrestamos = new DefaultTableModel(new String[]{"ID Préstamo", "Solicitante", "Fecha Préstamo", "Fecha Esperada", "Estado"}, 0);
        tablaPrestamos = new JTable(modeloTablaPrestamos);
        panel.add(new JScrollPane(tablaPrestamos), BorderLayout.CENTER);

        cargarDatosPrestamos();
        return panel;
    }

    private JPanel crearPanelPrestamosFechas() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel panelFiltro = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panelFiltro.add(new JLabel("Desde (YYYY-MM-DD):"));
        txtFechaInicio = new JTextField(10);
        txtFechaInicio.setText("2026-01-01");
        panelFiltro.add(txtFechaInicio);

        panelFiltro.add(new JLabel("Hasta (YYYY-MM-DD):"));
        txtFechaFin = new JTextField(10);
        txtFechaFin.setText("2026-12-31");
        panelFiltro.add(txtFechaFin);

        JButton btnGenerarFechas = new JButton("Consultar Rango");
        btnGenerarFechas.addActionListener(e -> cargarDatosPrestamosFechas());
        panelFiltro.add(btnGenerarFechas);

        panel.add(panelFiltro, BorderLayout.NORTH);

        modeloTablaPrestamosFechas = new DefaultTableModel(new String[]{"ID Préstamo", "Solicitante", "Fecha Préstamo", "Fecha Esperada", "Estado"}, 0);
        tablaPrestamosFechas = new JTable(modeloTablaPrestamosFechas);
        panel.add(new JScrollPane(tablaPrestamosFechas), BorderLayout.CENTER);

        cargarDatosPrestamosFechas();
        return panel;
    }

    private void cargarMetricasDashboard() {
        int[] metricas = reportesService.obtenerMetricasDashboard();
        lblTotalEquipos.setText(String.valueOf(metricas[0]));
        lblEquiposDisponibles.setText(String.valueOf(metricas[1]));
        lblPrestamosActivos.setText(String.valueOf(metricas[2]));
        lblPrestamosVencidos.setText(String.valueOf(metricas[3]));
    }

    private void cargarDatosEquipos() {
        modeloTablaEquipos.setRowCount(0);
        String estadoSeleccionado = (String) cmbEstadoEquipo.getSelectedItem();
        List<Object[]> resultados = reportesService.obtenerEquiposPorEstado(estadoSeleccionado);

        for (Object[] fila : resultados) {
            modeloTablaEquipos.addRow(fila);
        }
    }

    private void cargarDatosPrestamos() {
        modeloTablaPrestamos.setRowCount(0);
        String estadoSeleccionado = (String) cmbEstadoPrestamo.getSelectedItem();
        List<Object[]> resultados = reportesService.obtenerPrestamosPorEstado(estadoSeleccionado);

        for (Object[] fila : resultados) {
            modeloTablaPrestamos.addRow(fila);
        }
    }

    private void cargarDatosPrestamosFechas() {
        modeloTablaPrestamosFechas.setRowCount(0);
        String fechaInicio = txtFechaInicio.getText().trim();
        String fechaFin = txtFechaFin.getText().trim();

        List<Object[]> resultados = reportesService.obtenerPrestamosPorFechas(fechaInicio, fechaFin);

        for (Object[] fila : resultados) {
            modeloTablaPrestamosFechas.addRow(fila);
        }
    }
}