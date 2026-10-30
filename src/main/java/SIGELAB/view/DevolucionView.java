package SIGELAB.view;

import SIGELAB.controller.DevolucionController;
import SIGELAB.model.Usuario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class DevolucionView extends JFrame {

    private final Usuario usuarioAutenticado;
    private final DevolucionController controller;

    private JTable tablaPendientes;
    private DefaultTableModel modeloPendientes;
    private JComboBox<String> cmbCondicion;
    private JTextArea txtObservaciones;
    private JLabel lblSolicitanteInfo;
    private JButton btnCambiarSolicitante;

    private String solicitanteSeleccionado = "";
    private List<Object[]> todosLosPendientes = new ArrayList<>();

    public DevolucionView(Usuario usuarioAutenticado) {
        this.usuarioAutenticado = usuarioAutenticado;
        this.controller = new DevolucionController(); // Inicializado primero

        configurarVentana();
        construirInterfaz();

        // Mostrar diálogo selector de solicitante al abrir la ventana de manera segura
        SwingUtilities.invokeLater(this::dialogoSeleccionarSolicitante);
    }

    private void configurarVentana() {
        setTitle("SIGELAB - Registro de Devoluciones");
        setSize(1150, 750);
        setMinimumSize(new Dimension(950, 600));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    private void dialogoSeleccionarSolicitante() {
        try {
            List<String> solicitantes = controller.listarSolicitantesConPendientes();
            if (solicitantes == null || solicitantes.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No hay equipos pendientes de devolución en el sistema.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
                dispose();
                return;
            }

            SeleccionSolicitanteDialog dialogo = new SeleccionSolicitanteDialog(this, solicitantes);
            dialogo.setVisible(true);

            if (dialogo.isConfirmado()) {
                this.solicitanteSeleccionado = dialogo.getSolicitanteSeleccionado();
                if (lblSolicitanteInfo != null) {
                    lblSolicitanteInfo.setText("Solicitante en curso: " + this.solicitanteSeleccionado);
                }
                cargarPendientesPorSolicitante();
            } else {
                dispose();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar los solicitantes: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            dispose();
        }
    }

    private void construirInterfaz() {
        JPanel principal = new JPanel(new BorderLayout(15, 15));
        principal.setBorder(new EmptyBorder(20, 20, 20, 20));
        principal.setBackground(new Color(248, 250, 252));

        // --- ENCABEZADO ---
        JPanel panelNorte = new JPanel(new BorderLayout());
        panelNorte.setOpaque(false);

        JLabel lblTitulo = new JLabel("Registro de Devolución de Equipos");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setForeground(new Color(15, 23, 42));

        JLabel lblSubtitulo = new JLabel("Gestión de devoluciones parciales o totales por solicitante.");
        lblSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSubtitulo.setForeground(new Color(100, 116, 139));

        JPanel panelTitulos = new JPanel(new GridLayout(2, 1, 0, 2));
        panelTitulos.setOpaque(false);
        panelTitulos.add(lblTitulo);
        panelTitulos.add(lblSubtitulo);
        panelNorte.add(panelTitulos, BorderLayout.WEST);

        JButton btnCerrarSuperior = new JButton("Menú Principal");
        estilizarBoton(btnCerrarSuperior, false);
        btnCerrarSuperior.addActionListener(e -> dispose());
        JPanel panelBotonRegresar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        panelBotonRegresar.setOpaque(false);
        panelBotonRegresar.add(btnCerrarSuperior);
        panelNorte.add(panelBotonRegresar, BorderLayout.EAST);

        principal.add(panelNorte, BorderLayout.NORTH);

        // --- BARRA DE SOLICITANTE SELECCIONADO ---
        JPanel panelSolicitanteBar = new JPanel(new BorderLayout(10, 5));
        panelSolicitanteBar.setBackground(Color.WHITE);
        panelSolicitanteBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225)),
                new EmptyBorder(10, 12, 10, 12)
        ));

        lblSolicitanteInfo = new JLabel("Solicitante en curso: Ninguno");
        lblSolicitanteInfo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblSolicitanteInfo.setForeground(new Color(30, 41, 59));

        btnCambiarSolicitante = new JButton("Cambiar Solicitante");
        estilizarBoton(btnCambiarSolicitante, false);
        btnCambiarSolicitante.setPreferredSize(new Dimension(160, 30));
        btnCambiarSolicitante.addActionListener(e -> SwingUtilities.invokeLater(this::dialogoSeleccionarSolicitante));

        panelSolicitanteBar.add(lblSolicitanteInfo, BorderLayout.WEST);
        panelSolicitanteBar.add(btnCambiarSolicitante, BorderLayout.EAST);

        JPanel panelCentroContenedor = new JPanel(new BorderLayout(0, 12));
        panelCentroContenedor.setOpaque(false);
        panelCentroContenedor.add(panelSolicitanteBar, BorderLayout.NORTH);

        // --- TABLA CENTRAL (Selección Múltiple) ---
        modeloPendientes = new DefaultTableModel(
                new Object[]{
                        "Préstamo ID",
                        "Solicitante",
                        "Detalle ID",
                        "Código",
                        "Equipo",
                        "Devolución esperada"
                }, 0
        ) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };

        tablaPendientes = new JTable(modeloPendientes);
        tablaPendientes.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        tablaPendientes.setAutoCreateRowSorter(true);
        tablaPendientes.setRowHeight(30);
        tablaPendientes.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tablaPendientes.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tablaPendientes.getTableHeader().setBackground(new Color(241, 245, 249));
        tablaPendientes.getTableHeader().setForeground(new Color(51, 65, 85));

        JScrollPane scrollTabla = new JScrollPane(tablaPendientes);
        scrollTabla.getViewport().setBackground(Color.WHITE);
        scrollTabla.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225)),
                " Equipos Pendientes del Solicitante (Seleccione uno o varios para Devolución Parcial) ",
                0, 0, new Font("Segoe UI", Font.BOLD, 13), new Color(37, 99, 235)
        ));
        panelCentroContenedor.add(scrollTabla, BorderLayout.CENTER);

        principal.add(panelCentroContenedor, BorderLayout.CENTER);

        // --- PANEL INFERIOR ---
        JPanel panelInferior = new JPanel(new BorderLayout(10, 12));
        panelInferior.setOpaque(false);

        JPanel panelDatosDevolucion = new JPanel(new GridBagLayout());
        panelDatosDevolucion.setBackground(Color.WHITE);
        panelDatosDevolucion.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(new Color(203, 213, 225)),
                        " Detalles de la Devolución ",
                        0, 0, new Font("Segoe UI", Font.BOLD, 13), new Color(37, 99, 235)
                ),
                new EmptyBorder(10, 10, 10, 10)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 10, 6, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        cmbCondicion = new JComboBox<>(new String[]{"DISPONIBLE", "DANADO"});
        cmbCondicion.setPreferredSize(new Dimension(250, 30));

        txtObservaciones = new JTextArea(2, 20);
        txtObservaciones.setLineWrap(true);
        txtObservaciones.setWrapStyleWord(true);
        JScrollPane scrollObs = new JScrollPane(txtObservaciones);
        scrollObs.setPreferredSize(new Dimension(250, 55));

        int fila = 0;
        agregarCampoFormulario(panelDatosDevolucion, gbc, fila++, new JLabel("Estado de devolución:"), cmbCondicion);

        gbc.gridx = 0; gbc.gridy = fila; gbc.weightx = 0.0;
        panelDatosDevolucion.add(new JLabel("Observaciones:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        panelDatosDevolucion.add(scrollObs, gbc);

        panelInferior.add(panelDatosDevolucion, BorderLayout.NORTH);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panelBotones.setOpaque(false);

        JButton btnDevolucionParcial = new JButton("Devolución Parcial (Seleccionados)");
        JButton btnDevolucionTotal = new JButton("Devolución Total (Todos)");
        JButton btnCerrar = new JButton("Cerrar");

        estilizarBoton(btnDevolucionParcial, false);
        estilizarBoton(btnDevolucionTotal, true);
        estilizarBoton(btnCerrar, false);

        panelBotones.add(btnDevolucionParcial);
        panelBotones.add(btnDevolucionTotal);
        panelBotones.add(btnCerrar);

        panelInferior.add(panelBotones, BorderLayout.SOUTH);
        principal.add(panelInferior, BorderLayout.SOUTH);

        setContentPane(principal);

        btnDevolucionParcial.addActionListener(e -> registrarDevolucionMasiva(false));
        btnDevolucionTotal.addActionListener(e -> {
            if (tablaPendientes.getRowCount() > 0) {
                tablaPendientes.selectAll();
                registrarDevolucionMasiva(true);
            } else {
                JOptionPane.showMessageDialog(this, "No hay equipos pendientes para devolver.", "Aviso", JOptionPane.WARNING_MESSAGE);
            }
        });
        btnCerrar.addActionListener(e -> dispose());
    }

    private void agregarCampoFormulario(JPanel panel, GridBagConstraints gbc, int fila, JLabel label, JComponent componente) {
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(new Color(71, 85, 105));
        gbc.gridx = 0; gbc.gridy = fila; gbc.weightx = 0.0;
        panel.add(label, gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(componente, gbc);
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

    private void cargarPendientesPorSolicitante() {
        try {
            modeloPendientes.setRowCount(0);
            todosLosPendientes = controller.listarPendientes();

            int count = 0;
            for (Object[] fila : todosLosPendientes) {
                if (fila.length > 1 && fila[1] != null) {
                    String sol = fila[1].toString().trim();
                    if (sol.equalsIgnoreCase(solicitanteSeleccionado)) {
                        modeloPendientes.addRow(fila);
                        count++;
                    }
                }
            }

            if (count == 0) {
                JOptionPane.showMessageDialog(this, "El solicitante seleccionado ya no tiene equipos pendientes.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
                SwingUtilities.invokeLater(this::dialogoSeleccionarSolicitante);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar equipos: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void registrarDevolucionMasiva(boolean esTotal) {
        if (usuarioAutenticado == null || usuarioAutenticado.getIdUsuario() <= 0) {
            JOptionPane.showMessageDialog(this, "No se encontró un usuario autenticado válido.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int[] filasSeleccionadas = tablaPendientes.getSelectedRows();
        if (filasSeleccionadas.length == 0) {
            JOptionPane.showMessageDialog(this, "Por favor, seleccione al menos un equipo en la tabla para registrar la devolución.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String tipoDev = esTotal ? "Devolución Total" : "Devolución Parcial";
        int confirmacion = JOptionPane.showConfirmDialog(
                this,
                "¿Desea confirmar la " + tipoDev + " de " + filasSeleccionadas.length + " equipo(s) seleccionado(s) para " + solicitanteSeleccionado + "?",
                "Confirmar devolución",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (confirmacion != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            List<Integer> detalleIds = new ArrayList<>();
            for (int fila : filasSeleccionadas) {
                int filaModelo = tablaPendientes.convertRowIndexToModel(fila);
                int detalleId = ((Number) modeloPendientes.getValueAt(filaModelo, 2)).intValue();
                detalleIds.add(detalleId);
            }

            String seleccionCombo = (String) cmbCondicion.getSelectedItem();
            String condicionFinal = (seleccionCombo != null && seleccionCombo.equalsIgnoreCase("DANADO")) ? "DANADO" : "DISPONIBLE";

            boolean registrado = controller.registrarDevolucionMasiva(
                    detalleIds,
                    condicionFinal,
                    usuarioAutenticado.getIdUsuario()
            );

            if (registrado) {
                JOptionPane.showMessageDialog(this, tipoDev + " registrada correctamente.", "SIGELAB", JOptionPane.INFORMATION_MESSAGE);
                txtObservaciones.setText("");
                cmbCondicion.setSelectedIndex(0);
                cargarPendientesPorSolicitante();
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "No se pudo registrar la devolución:\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static class SeleccionSolicitanteDialog extends JDialog {
        private JComboBox<String> comboSolicitantes;
        private boolean confirmado = false;
        private String solicitanteSeleccionado = "";

        public SeleccionSolicitanteDialog(JFrame parent, List<String> solicitantesConPendientes) {
            super(parent, "SIGELAB - Seleccionar Solicitante", true);
            setSize(420, 220);
            setLocationRelativeTo(parent);
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);

            JPanel panel = new JPanel(new BorderLayout(15, 15));
            panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            panel.setBackground(new Color(248, 250, 252));

            JLabel lbl = new JLabel("Seleccione el solicitante con préstamos pendientes:");
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
            lbl.setForeground(new Color(71, 85, 105));

            comboSolicitantes = new JComboBox<>();
            for (String sol : solicitantesConPendientes) {
                comboSolicitantes.addItem(sol);
            }
            comboSolicitantes.setFont(new Font("Segoe UI", Font.PLAIN, 13));

            JPanel panelCentro = new JPanel(new BorderLayout(5, 5));
            panelCentro.setOpaque(false);
            panelCentro.add(lbl, BorderLayout.NORTH);
            panelCentro.add(comboSolicitantes, BorderLayout.CENTER);

            JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
            panelBotones.setOpaque(false);

            JButton btnCancelar = new JButton("Cancelar");
            JButton btnContinuar = new JButton("Continuar");

            estilizarBotonDialogo(btnCancelar, false);
            estilizarBotonDialogo(btnContinuar, true);

            btnCancelar.addActionListener(e -> dispose());
            btnContinuar.addActionListener(e -> {
                if (comboSolicitantes.getSelectedItem() != null) {
                    solicitanteSeleccionado = comboSolicitantes.getSelectedItem().toString();
                    confirmado = true;
                    dispose();
                }
            });

            panelBotones.add(btnCancelar);
            panelBotones.add(btnContinuar);

            panel.add(panelCentro, BorderLayout.CENTER);
            panel.add(panelBotones, BorderLayout.SOUTH);
            setContentPane(panel);
        }


        private void estilizarBotonDialogo(JButton boton, boolean esPrincipal) {
            boton.setFocusPainted(false);
            boton.setFont(new Font("Segoe UI", Font.BOLD, 12));
            boton.putClientProperty("JButton.buttonType", "roundRect");
            if (esPrincipal) {
                boton.putClientProperty("FlatLaf.style", "background: #2563eb; foreground: #ffffff; arc: 8");
            } else {
                boton.putClientProperty("FlatLaf.style", "arc: 8");
            }
        }

        public boolean isConfirmado() { return confirmado; }
        public String getSolicitanteSeleccionado() { return solicitanteSeleccionado; }
    }
}