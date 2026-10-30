package SIGELAB.view;

import SIGELAB.service.ReportesService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

public class ReportesView extends JFrame {

    private final ReportesService reportesService;

    // Tablas para cada consulta
    private JComboBox<String> cmbEstadoEquipo;
    private JTable tablaEquipos;
    private DefaultTableModel modeloEquipos;

    private JComboBox<String> cmbEstadoPrestamo;
    private JTable tablaPrestamos;
    private DefaultTableModel modeloPrestamos;

    private JTextField txtFechaInicio, txtFechaFin;
    private JTable tablaFechas;
    private DefaultTableModel modeloFechas;

    private JTextField txtCodigoEquipo;
    private JTable tablaHistorialEquipo;
    private DefaultTableModel modeloHistorialEquipo;

    private JTable tablaHistorialMantenimiento;
    private DefaultTableModel modeloHistorialMantenimiento;

    public ReportesView() {
        this.reportesService = new ReportesService();
        configurarVentana();
        construirInterfaz();
    }

    private void configurarVentana() {
        setTitle("SIGELAB - Consultas y Reportes del Sistema");
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    private void construirInterfaz() {
        JPanel principal = new JPanel(new BorderLayout(15, 15));
        principal.setBorder(new EmptyBorder(15, 15, 15, 15));
        principal.setBackground(new Color(248, 250, 252));

        // Título superior
        JLabel lblTitulo = new JLabel("Módulo de Consultas y Reportes Institucionales");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitulo.setForeground(new Color(15, 23, 42));
        principal.add(lblTitulo, BorderLayout.NORTH);

        // Pestañas con las 5 consultas mínimas obligatorias
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 13));

        tabbedPane.addTab("1. Equipos por Estado", crearPanelEquipos());
        tabbedPane.addTab("2. Préstamos por Estado", crearPanelPrestamosEstado());
        tabbedPane.addTab("3. Préstamos entre Fechas", crearPanelPrestamosFechas());
        tabbedPane.addTab("4. Historial de Equipo", crearPanelHistorialEquipo());
        tabbedPane.addTab("5. Historial Mantenimiento", crearPanelHistorialMantenimiento());

        principal.add(tabbedPane, BorderLayout.CENTER);

        // Botón cerrar inferior
        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.putClientProperty("FlatLaf.style", "arc: 8");
        btnCerrar.addActionListener(e -> dispose());
        JPanel panelSur = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelSur.setOpaque(false);
        panelSur.add(btnCerrar);
        principal.add(panelSur, BorderLayout.SOUTH);

        setContentPane(principal);
    }

    private JPanel crearPanelEquipos() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.setOpaque(false);

        JPanel filtro = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filtro.add(new JLabel("Estado:"));
        // Se actualizan los estados para que coincidan exactamente con la BD ('DADO_DE_BAJA')
        cmbEstadoEquipo = new JComboBox<>(new String[]{"DISPONIBLE", "PRESTADO", "MANTENIMIENTO", "DADO_DE_BAJA"});
        cmbEstadoEquipo.addActionListener(e -> cargarEquipos());
        filtro.add(cmbEstadoEquipo);
        panel.add(filtro, BorderLayout.NORTH);

        modeloEquipos = new DefaultTableModel(new String[]{"Código", "Nombre", "Laboratorio", "Estado"}, 0);
        tablaEquipos = new JTable(modeloEquipos);
        panel.add(new JScrollPane(tablaEquipos), BorderLayout.CENTER);
        cargarEquipos();
        return panel;
    }

    private JPanel crearPanelPrestamosEstado() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.setOpaque(false);

        JPanel filtro = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filtro.add(new JLabel("Estado Préstamo:"));
        cmbEstadoPrestamo = new JComboBox<>(new String[]{"ACTIVO", "VENCIDO", "FINALIZADO"});
        cmbEstadoPrestamo.addActionListener(e -> cargarPrestamosEstado());
        filtro.add(cmbEstadoPrestamo);
        panel.add(filtro, BorderLayout.NORTH);

        modeloPrestamos = new DefaultTableModel(new String[]{"ID", "Solicitante", "Fecha Préstamo", "Devolución Esperada", "Estado"}, 0);
        tablaPrestamos = new JTable(modeloPrestamos);
        panel.add(new JScrollPane(tablaPrestamos), BorderLayout.CENTER);
        cargarPrestamosEstado();
        return panel;
    }

    private JPanel crearPanelPrestamosFechas() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.setOpaque(false);

        JPanel filtro = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filtro.add(new JLabel("Desde (YYYY-MM-DD):"));
        txtFechaInicio = new JTextField("2026-01-01", 10);
        filtro.add(txtFechaInicio);
        filtro.add(new JLabel("Hasta (YYYY-MM-DD):"));
        txtFechaFin = new JTextField("2026-12-31", 10);
        filtro.add(txtFechaFin);
        JButton btnConsultar = new JButton("Consultar Rango");
        btnConsultar.addActionListener(e -> cargarPrestamosFechas());
        filtro.add(btnConsultar);
        panel.add(filtro, BorderLayout.NORTH);

        modeloFechas = new DefaultTableModel(new String[]{"ID", "Solicitante", "Fecha Préstamo", "Devolución Esperada", "Estado"}, 0);
        tablaFechas = new JTable(modeloFechas);
        panel.add(new JScrollPane(tablaFechas), BorderLayout.CENTER);
        cargarPrestamosFechas();
        return panel;
    }

    private JPanel crearPanelHistorialEquipo() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.setOpaque(false);

        JPanel filtro = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        filtro.setBackground(Color.WHITE);
        filtro.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225)),
                new EmptyBorder(8, 8, 8, 8)
        ));

        JLabel lblQR = new JLabel("Buscar equipo (Código o Nombre):");
        lblQR.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblQR.setForeground(new Color(71, 85, 105));

        txtCodigoEquipo = new JTextField(20);
        txtCodigoEquipo.putClientProperty("JTextField.placeholderText", "Ej. INV-001 o Dell...");

        JButton btnBuscar = new JButton("Filtrar");
        btnBuscar.addActionListener(e -> cargarHistorialEquipo());

        JButton btnCargarQR = new JButton("Cargar QR");
        btnCargarQR.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            File carpetaQrs = new File("qrs_inventario");
            if (carpetaQrs.exists()) {
                fileChooser.setCurrentDirectory(carpetaQrs);
            }
            fileChooser.setDialogTitle("Seleccionar imagen de Código QR");

            int resultado = fileChooser.showOpenDialog(this);
            if (resultado == JFileChooser.APPROVE_OPTION) {
                File archivo = fileChooser.getSelectedFile();
                try {
                    BufferedImage imagen = javax.imageio.ImageIO.read(archivo);
                    if (imagen == null) {
                        JOptionPane.showMessageDialog(this, "El archivo seleccionado no es una imagen válida.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    int ancho = imagen.getWidth(), alto = imagen.getHeight();
                    int[] pixels = imagen.getRGB(0, 0, ancho, alto, null, 0, ancho);
                    com.google.zxing.LuminanceSource source = new com.google.zxing.RGBLuminanceSource(ancho, alto, pixels);
                    com.google.zxing.BinaryBitmap bitmap = new com.google.zxing.BinaryBitmap(new com.google.zxing.common.HybridBinarizer(source));
                    com.google.zxing.Result resultadoQR = new com.google.zxing.MultiFormatReader().decode(bitmap);

                    String texto = resultadoQR.getText();
                    if (texto.contains("SIGELAB-EQ:")) {
                        texto = texto.split("\\|")[0].replace("SIGELAB-EQ:", "").trim();
                    }
                    txtCodigoEquipo.setText(texto);
                    cargarHistorialEquipo();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "No se pudo leer el código QR de la imagen:\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        filtro.add(lblQR);
        filtro.add(txtCodigoEquipo);
        filtro.add(btnBuscar);
        filtro.add(btnCargarQR);

        panel.add(filtro, BorderLayout.NORTH);

        modeloHistorialEquipo = new DefaultTableModel(new String[]{"Fecha y Hora", "Acción", "Descripción"}, 0);
        tablaHistorialEquipo = new JTable(modeloHistorialEquipo);
        panel.add(new JScrollPane(tablaHistorialEquipo), BorderLayout.CENTER);

        return panel;
    }

    private JPanel crearPanelHistorialMantenimiento() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.setOpaque(false);

        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panelSuperior.setOpaque(false);
        JLabel lblInfo = new JLabel("Registro histórico de eventos de taller y mantenimiento:");
        lblInfo.setFont(new Font("Segoe UI", Font.BOLD, 12));

        JButton btnActualizarMant = new JButton("Actualizar Historial");
        btnActualizarMant.addActionListener(e -> cargarHistorialMantenimiento());

        panelSuperior.add(lblInfo);
        panelSuperior.add(btnActualizarMant);
        panel.add(panelSuperior, BorderLayout.NORTH);

        modeloHistorialMantenimiento = new DefaultTableModel(
                new String[]{"Fecha Ingreso", "Cód. Inventario", "Equipo", "Tipo", "Responsable", "Estado", "Resultado", "Observaciones"}, 0
        );
        tablaHistorialMantenimiento = new JTable(modeloHistorialMantenimiento);
        panel.add(new JScrollPane(tablaHistorialMantenimiento), BorderLayout.CENTER);

        cargarHistorialMantenimiento();
        return panel;
    }

    private void cargarEquipos() {
        modeloEquipos.setRowCount(0);
        String estado = (String) cmbEstadoEquipo.getSelectedItem();
        for (Object[] row : reportesService.obtenerEquiposPorEstado(estado)) {
            modeloEquipos.addRow(row);
        }
    }

    private void cargarPrestamosEstado() {
        modeloPrestamos.setRowCount(0);
        String estado = (String) cmbEstadoPrestamo.getSelectedItem();
        for (Object[] row : reportesService.obtenerPrestamosPorEstado(estado)) {
            modeloPrestamos.addRow(row);
        }
    }

    private void cargarPrestamosFechas() {
        modeloFechas.setRowCount(0);
        String ini = txtFechaInicio.getText().trim();
        String fin = txtFechaFin.getText().trim();
        for (Object[] row : reportesService.obtenerPrestamosPorFechas(ini, fin)) {
            modeloFechas.addRow(row);
        }
    }

    private void cargarHistorialEquipo() {
        modeloHistorialEquipo.setRowCount(0);
        String codigo = txtCodigoEquipo.getText().trim();
        for (Object[] row : reportesService.obtenerHistorialEquipo(codigo)) {
            modeloHistorialEquipo.addRow(row);
        }
    }

    private void cargarHistorialMantenimiento() {
        modeloHistorialMantenimiento.setRowCount(0);
        for (Object[] row : reportesService.obtenerHistorialMantenimiento()) {
            modeloHistorialMantenimiento.addRow(row);
        }
    }
}