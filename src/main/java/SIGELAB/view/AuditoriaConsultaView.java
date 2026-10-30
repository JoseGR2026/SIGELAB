package SIGELAB.view;

import SIGELAB.controller.AuditoriaConsultaController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class AuditoriaConsultaView extends JFrame {

    private final AuditoriaConsultaController controller;

    private JTable tablaAuditoria;
    private DefaultTableModel modeloTabla;
    private JComboBox<String> cmbAccionesFiltro;
    private JButton btnActualizar;
    private JButton btnCerrar;
    private JLabel lblTotal;

    private List<Object[]> listaCompletaAuditoria;

    public AuditoriaConsultaView() {
        controller = new AuditoriaConsultaController();

        configurarVentana();
        crearComponentes();
        configurarEventos();
        cargarAuditoria();
    }

    private void configurarVentana() {
        setTitle("SIGELAB - Registro de Auditoría");
        setSize(1150, 720);
        setMinimumSize(new Dimension(950, 600));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    private void crearComponentes() {
        JPanel panelPrincipal = new JPanel(new BorderLayout(15, 15));
        panelPrincipal.setBorder(new EmptyBorder(20, 20, 20, 20));
        panelPrincipal.setBackground(new Color(248, 250, 252));

        // --- ENCABEZADO ---
        JPanel panelNorte = new JPanel(new BorderLayout());
        panelNorte.setOpaque(false);

        JLabel lblTitulo = new JLabel("Registro de Auditoría");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setForeground(new Color(15, 23, 42));

        JLabel lblSubtitulo = new JLabel("Seguimiento y control de eventos del sistema (Login, Préstamos, Mantenimiento, etc.).");
        lblSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSubtitulo.setForeground(new Color(100, 116, 139));

        JPanel panelTitulos = new JPanel(new GridLayout(2, 1, 0, 2));
        panelTitulos.setOpaque(false);
        panelTitulos.add(lblTitulo);
        panelTitulos.add(lblSubtitulo);
        panelNorte.add(panelTitulos, BorderLayout.WEST);

        panelPrincipal.add(panelNorte, BorderLayout.NORTH);

        // --- PANEL DE FILTROS Y ACCIONES SUPERIOR ---
        JPanel panelAcciones = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 5));
        panelAcciones.setBackground(Color.WHITE);
        panelAcciones.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225)),
                new EmptyBorder(8, 10, 8, 10)
        ));

        JLabel lblFiltroAccion = new JLabel("Filtrar por Acción:");
        lblFiltroAccion.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblFiltroAccion.setForeground(new Color(71, 85, 105));

        cmbAccionesFiltro = new JComboBox<>(new String[]{
                "-- TODAS LAS ACCIONES --",
                "LOGIN_EXITOSO",
                "LOGIN_FALLIDO",
                "PRESTAMO_CREADO",
                "EQUIPO_DEVUELTO",
                "EQUIPO_ENVIADO_MANTENIMIENTO",
                "MANTENIMIENTO_FINALIZADO",
                "USUARIO_DESACTIVADO"
        });
        cmbAccionesFiltro.setPreferredSize(new Dimension(280, 30));
        cmbAccionesFiltro.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        btnActualizar = new JButton("Actualizar registros");
        estilizarBoton(btnActualizar, true);
        btnActualizar.setPreferredSize(new Dimension(150, 30));

        panelAcciones.add(lblFiltroAccion);
        panelAcciones.add(cmbAccionesFiltro);
        panelAcciones.add(btnActualizar);

        JPanel panelCentroContenedor = new JPanel(new BorderLayout(0, 12));
        panelCentroContenedor.setOpaque(false);
        panelCentroContenedor.add(panelAcciones, BorderLayout.NORTH);

        // --- TABLA DE REGISTROS ---
        String[] columnas = {
                "Auditoría ID",
                "Fecha y hora",
                "Usuario ID",
                "Acción",
                "Entidad",
                "Registro ID",
                "Descripción"
        };

        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };

        tablaAuditoria = new JTable(modeloTabla);
        tablaAuditoria.setRowHeight(30);
        tablaAuditoria.setAutoCreateRowSorter(true);
        tablaAuditoria.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaAuditoria.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tablaAuditoria.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tablaAuditoria.getTableHeader().setBackground(new Color(241, 245, 249));
        tablaAuditoria.getTableHeader().setForeground(new Color(51, 65, 85));

        // Anchos de columnas
        tablaAuditoria.getColumnModel().getColumn(0).setPreferredWidth(100);
        tablaAuditoria.getColumnModel().getColumn(1).setPreferredWidth(180);
        tablaAuditoria.getColumnModel().getColumn(2).setPreferredWidth(100);
        tablaAuditoria.getColumnModel().getColumn(3).setPreferredWidth(210);
        tablaAuditoria.getColumnModel().getColumn(4).setPreferredWidth(130);
        tablaAuditoria.getColumnModel().getColumn(5).setPreferredWidth(100);
        tablaAuditoria.getColumnModel().getColumn(6).setPreferredWidth(430);

        JScrollPane scroll = new JScrollPane(tablaAuditoria);
        scroll.getViewport().setBackground(Color.WHITE);
        scroll.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225)),
                " Historial de Eventos Registrados ",
                0, 0, new Font("Segoe UI", Font.BOLD, 13), new Color(37, 99, 235)
        ));
        panelCentroContenedor.add(scroll, BorderLayout.CENTER);

        panelPrincipal.add(panelCentroContenedor, BorderLayout.CENTER);

        // --- PANEL INFERIOR ---
        JPanel panelInferior = new JPanel(new BorderLayout());
        panelInferior.setOpaque(false);
        panelInferior.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

        lblTotal = new JLabel("Registros encontrados: 0");
        lblTotal.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblTotal.setForeground(new Color(71, 85, 105));
        panelInferior.add(lblTotal, BorderLayout.WEST);

        btnCerrar = new JButton("Cerrar");
        estilizarBoton(btnCerrar, false);
        panelInferior.add(btnCerrar, BorderLayout.EAST);

        panelPrincipal.add(panelInferior, BorderLayout.SOUTH);

        setContentPane(panelPrincipal);
    }

    private void estilizarBoton(JButton boton, boolean esPrincipal) {
        if (boton == null) return;
        boton.setFocusPainted(false);
        boton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        boton.putClientProperty("JButton.buttonType", "roundRect");
        if (esPrincipal) {
            boton.putClientProperty("FlatLaf.style", "background: #2563eb; foreground: #ffffff; arc: 8");
        } else {
            boton.putClientProperty("FlatLaf.style", "arc: 8");
        }
    }

    private void configurarEventos() {
        btnActualizar.addActionListener(e -> cargarAuditoria());
        cmbAccionesFiltro.addActionListener(e -> filtrarPorAccionSeleccionada());
        btnCerrar.addActionListener(e -> dispose());
    }

    private void cargarAuditoria() {
        btnActualizar.setEnabled(false);
        lblTotal.setText("Consultando auditoría...");

        SwingWorker<List<Object[]>, Void> trabajador = new SwingWorker<>() {
            @Override
            protected List<Object[]> doInBackground() {
                return controller.listarAuditoria();
            }

            @Override
            protected void done() {
                try {
                    listaCompletaAuditoria = get();
                    cmbAccionesFiltro.setSelectedIndex(0);
                    actualizarTablaConRegistros(listaCompletaAuditoria);

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    mostrarError("La consulta fue interrumpida.");
                } catch (ExecutionException e) {
                    Throwable causa = e.getCause();
                    mostrarError(causa != null ? causa.getMessage() : e.getMessage());
                } finally {
                    btnActualizar.setEnabled(true);
                }
            }
        };

        trabajador.execute();
    }

    private void filtrarPorAccionSeleccionada() {
        if (listaCompletaAuditoria == null) return;

        int indexSeleccionado = cmbAccionesFiltro.getSelectedIndex();
        if (indexSeleccionado <= 0) {
            actualizarTablaConRegistros(listaCompletaAuditoria);
            return;
        }

        String seleccion = (String) cmbAccionesFiltro.getSelectedItem();
        List<Object[]> registrosFiltrados = new java.util.ArrayList<>();

        for (Object[] fila : listaCompletaAuditoria) {
            String accionFila = String.valueOf(fila[3]).toUpperCase(); // Columna de Acción en BD
            boolean coincide = false;

            if (seleccion.equals("PRESTAMO_CREADO") && (accionFila.contains("PRESTAMO") || accionFila.contains("REGISTRAR_PRESTAMO"))) {
                coincide = true;
            } else if (seleccion.equals("EQUIPO_DEVUELTO") && (accionFila.contains("DEVOLUCION") || accionFila.contains("REGISTRAR_DEVOLUCION"))) {
                coincide = true;
            } else if (accionFila.equals(seleccion)) {
                coincide = true;
            }

            if (coincide) {
                registrosFiltrados.add(fila);
            }
        }

        actualizarTablaConRegistros(registrosFiltrados);
    }

    private void actualizarTablaConRegistros(List<Object[]> registros) {
        modeloTabla.setRowCount(0);
        for (Object[] fila : registros) {
            modeloTabla.addRow(fila);
        }
        lblTotal.setText("Registros encontrados: " + registros.size());
    }

    private void mostrarError(String mensaje) {
        lblTotal.setText("No se pudo cargar la auditoría.");
        JOptionPane.showMessageDialog(
                this,
                "Error al consultar la auditoría:\n" + mensaje,
                "SIGELAB - Error",
                JOptionPane.ERROR_MESSAGE
        );
    }
}