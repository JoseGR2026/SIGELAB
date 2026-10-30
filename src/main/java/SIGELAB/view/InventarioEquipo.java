package SIGELAB.view;

import SIGELAB.controller.EstadoEquipoController;
import SIGELAB.model.EstadoEquipo;
import SIGELAB.model.Laboratorio;
import SIGELAB.service.LaboratorioService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class InventarioEquipo extends JFrame {
    private JPanel PanelInventario;
    private JTable table1;
    private JTextField textID;
    private JTextField textCodigoInventario;
    private JTextField textNombreEquipo;
    private JComboBox<String> comboBoxTipo;
    private JComboBox<ItemLaboratorio> comboBoxLAB;
    private JComboBox<String> comboBoxEstado;
    private JTextField textObservaciones;
    private JButton nuevoButton;
    private JButton guardarButton;
    private JButton actualizarButton;
    private JButton limpiarButton;
    private JButton regresarAlMenuButton;
    private JTextField textField1; // Marca
    private JTextField textField2; // Modelo
    private JTextField textField3; // Número de serie

    private final EstadoEquipoController controller;
    private EstadoEquipo equipoSeleccionado;
    private int idSeleccionado = 0;

    private static final String[] COLUMNAS = {"ID", "Lab ID", "Cód. Inventario", "Equipo", "Marca", "Modelo", "Nº Serie", "Tipo", "Estado", "Observaciones"};
    private final DefaultTableModel modeloTabla = new DefaultTableModel(COLUMNAS, 0) {
        @Override
        public boolean isCellEditable(int fila, int columna) { return false; }
    };

    private List<EstadoEquipo> equiposMostrados = new ArrayList<>();

    public InventarioEquipo() {
        super("SIGELAB - Inventario y Estado de Equipos");
        this.controller = new EstadoEquipoController();

        construirInterfazModerna();

        setContentPane(PanelInventario);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        configurarFormulario();
        configurarTabla();
        cargarLaboratoriosEnCombo();
        registrarEventos();
        refrescarTabla();
        limpiarFormulario();

        setSize(1150, 680);
        setMinimumSize(new Dimension(950, 600));
        setLocationRelativeTo(null);
    }

    private void construirInterfazModerna() {
        PanelInventario = new JPanel(new BorderLayout(15, 15));
        PanelInventario.setBorder(new EmptyBorder(20, 20, 20, 20));
        PanelInventario.setBackground(new Color(248, 250, 252));

        // --- ENCABEZADO ---
        JPanel panelNorte = new JPanel(new BorderLayout());
        panelNorte.setOpaque(false);

        JLabel lblTitulo = new JLabel("Catálogo e Inventario de Equipos");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setForeground(new Color(15, 23, 42));

        JLabel lblSubtitulo = new JLabel("Gestión y control de dispositivos tecnológicos asignados a laboratorios.");
        lblSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSubtitulo.setForeground(new Color(100, 116, 139));

        JPanel panelTitulos = new JPanel(new GridLayout(2, 1, 0, 2));
        panelTitulos.setOpaque(false);
        panelTitulos.add(lblTitulo);
        panelTitulos.add(lblSubtitulo);
        panelNorte.add(panelTitulos, BorderLayout.WEST);

        regresarAlMenuButton = new JButton("← Menú Principal");
        estilizarBoton(regresarAlMenuButton, false);
        JPanel panelBotonRegresar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        panelBotonRegresar.setOpaque(false);
        panelBotonRegresar.add(regresarAlMenuButton);
        panelNorte.add(panelBotonRegresar, BorderLayout.EAST);

        PanelInventario.add(panelNorte, BorderLayout.NORTH);

        // --- CONTENIDO CENTRAL (SplitPane Formulario / Tabla) ---
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setResizeWeight(0.48);
        splitPane.setDividerSize(8);
        splitPane.setOpaque(false);
        splitPane.setBorder(null);

        // 1. Panel Izquierdo: Formulario de Información y Acciones
        JPanel panelIzquierdo = new JPanel(new BorderLayout(0, 15));
        panelIzquierdo.setOpaque(false);

        JPanel panelFormulario = new JPanel(new GridBagLayout());
        panelFormulario.setBackground(Color.WHITE);
        panelFormulario.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225)),
                new EmptyBorder(10, 10, 10, 10)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 8, 5, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        textID = new JTextField(15);
        textID.setEditable(false);
        textCodigoInventario = new JTextField(15);
        textNombreEquipo = new JTextField(15);
        textField1 = new JTextField(15); // Marca
        textField2 = new JTextField(15); // Modelo
        textField3 = new JTextField(15); // Serie
        comboBoxTipo = new JComboBox<>();
        comboBoxLAB = new JComboBox<>();
        comboBoxEstado = new JComboBox<>();
        textObservaciones = new JTextField(15);

        int fila = 0;
        agregarCampoForm(panelFormulario, gbc, fila++, new JLabel("ID:"), textID);
        agregarCampoForm(panelFormulario, gbc, fila++, new JLabel("Laboratorio:"), comboBoxLAB);
        agregarCampoForm(panelFormulario, gbc, fila++, new JLabel("Cód. Inventario:"), textCodigoInventario);
        agregarCampoForm(panelFormulario, gbc, fila++, new JLabel("Equipo:"), textNombreEquipo);
        agregarCampoForm(panelFormulario, gbc, fila++, new JLabel("Marca:"), textField1);
        agregarCampoForm(panelFormulario, gbc, fila++, new JLabel("Modelo:"), textField2);
        agregarCampoForm(panelFormulario, gbc, fila++, new JLabel("Nº Serie:"), textField3);
        agregarCampoForm(panelFormulario, gbc, fila++, new JLabel("Tipo:"), comboBoxTipo);
        agregarCampoForm(panelFormulario, gbc, fila++, new JLabel("Estado:"), comboBoxEstado);
        agregarCampoForm(panelFormulario, gbc, fila++, new JLabel("Observaciones:"), textObservaciones);

        JPanel panelScrollForm = new JPanel(new BorderLayout());
        panelScrollForm.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225)),
                " Información del Equipo ",
                0, 0, new Font("Segoe UI", Font.BOLD, 13), new Color(37, 99, 235)
        ));
        panelScrollForm.add(new JScrollPane(panelFormulario), BorderLayout.CENTER);
        panelIzquierdo.add(panelScrollForm, BorderLayout.CENTER);

        // Panel inferior de Botones de Acción
        JPanel panelAcciones = new JPanel(new GridLayout(2, 2, 8, 8));
        panelAcciones.setOpaque(false);
        panelAcciones.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225)),
                " Acciones ",
                0, 0, new Font("Segoe UI", Font.BOLD, 13), new Color(37, 99, 235)
        ));

        nuevoButton = new JButton("Nuevo");
        guardarButton = new JButton("Guardar");
        actualizarButton = new JButton("Actualizar");
        limpiarButton = new JButton("Limpiar");

        estilizarBoton(guardarButton, true);
        estilizarBoton(nuevoButton, false);
        estilizarBoton(actualizarButton, false);
        estilizarBoton(limpiarButton, false);

        panelAcciones.add(nuevoButton);
        panelAcciones.add(guardarButton);
        panelAcciones.add(actualizarButton);
        panelAcciones.add(limpiarButton);

        panelIzquierdo.add(panelAcciones, BorderLayout.SOUTH);
        splitPane.setLeftComponent(panelIzquierdo);

        // 2. Panel Derecho: Tabla Estilizada
        table1 = new JTable(modeloTabla);
        table1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table1.setRowHeight(28);
        table1.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table1.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table1.getTableHeader().setBackground(new Color(241, 245, 249));
        table1.getTableHeader().setForeground(new Color(51, 65, 85));

        JScrollPane scrollTabla = new JScrollPane(table1);
        scrollTabla.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225)),
                " Equipos Registrados en Inventario ",
                0, 0, new Font("Segoe UI", Font.BOLD, 13), new Color(37, 99, 235)
        ));
        scrollTabla.getViewport().setBackground(Color.WHITE);
        splitPane.setRightComponent(scrollTabla);

        PanelInventario.add(splitPane, BorderLayout.CENTER);
    }

    private void agregarCampoForm(JPanel panel, GridBagConstraints gbc, int fila, JLabel label, JComponent campo) {
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(new Color(71, 85, 105));
        gbc.gridx = 0; gbc.gridy = fila; gbc.weightx = 0.0;
        panel.add(label, gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(campo, gbc);
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

    private void configurarFormulario() {
        if (textID != null) textID.setEditable(false);
        if (actualizarButton != null) actualizarButton.setEnabled(false);

        if (comboBoxEstado != null) {
            comboBoxEstado.removeAllItems();
            comboBoxEstado.addItem("DISPONIBLE");
            comboBoxEstado.addItem("EN_USO");
            comboBoxEstado.addItem("MANTENIMIENTO");
            comboBoxEstado.addItem("DADO_DE_BAJA");
        }

        if (comboBoxTipo != null) {
            comboBoxTipo.removeAllItems();
            comboBoxTipo.addItem("Hardware");
            comboBoxTipo.addItem("Multimedia");
            comboBoxTipo.addItem("Redes");
            comboBoxTipo.addItem("Herramienta");
        }
    }

    private void configurarTabla() {
        if (table1 != null) {
            table1.setModel(modeloTabla);
            table1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            table1.getSelectionModel().addListSelectionListener(e -> {
                if (!e.getValueIsAdjusting()) seleccionarFila();
            });
        }
    }

    private void cargarLaboratoriosEnCombo() {
        if (comboBoxLAB == null) return;
        comboBoxLAB.removeAllItems();
        try {
            LaboratorioService labService = new LaboratorioService();
            List<Laboratorio> laboratorios = labService.obtenerLaboratorios();
            for (Laboratorio lab : laboratorios) {
                String etiqueta = lab.getCodigo() + " - " + lab.getNombre();
                comboBoxLAB.addItem(new ItemLaboratorio(lab.getId(), etiqueta));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar laboratorios: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void registrarEventos() {
        if (nuevoButton != null) nuevoButton.addActionListener(e -> limpiarFormulario());
        if (guardarButton != null) guardarButton.addActionListener(e -> guardar());
        if (actualizarButton != null) actualizarButton.addActionListener(e -> actualizar());
        if (limpiarButton != null) limpiarButton.addActionListener(e -> limpiarFormulario());
        if (regresarAlMenuButton != null) regresarAlMenuButton.addActionListener(e -> dispose());
    }

    private void refrescarTabla() {
        try {
            equiposMostrados = new ArrayList<>(controller.listar());
            modeloTabla.setRowCount(0);
            for (EstadoEquipo eq : equiposMostrados) {
                modeloTabla.addRow(new Object[]{
                        eq.getId(),
                        eq.getLaboratorioId(),
                        eq.getCodigoInventario(),
                        eq.getNombreEquipo(),
                        eq.getMarca(),
                        eq.getModelo(),
                        eq.getNumeroSerie(),
                        eq.getTipo(),
                        eq.getEstado(),
                        eq.getObservaciones()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void seleccionarFila() {
        if (table1 == null) return;
        int fila = table1.getSelectedRow();
        if (fila < 0 || fila >= equiposMostrados.size()) return;

        equipoSeleccionado = equiposMostrados.get(table1.convertRowIndexToModel(fila));
        idSeleccionado = equipoSeleccionado.getId();

        if (textID != null) textID.setText(String.valueOf(equipoSeleccionado.getId()));
        if (textCodigoInventario != null) textCodigoInventario.setText(equipoSeleccionado.getCodigoInventario());
        if (textNombreEquipo != null) textNombreEquipo.setText(equipoSeleccionado.getNombreEquipo());
        if (textField1 != null) textField1.setText(equipoSeleccionado.getMarca());
        if (textField2 != null) textField2.setText(equipoSeleccionado.getModelo());
        if (textField3 != null) textField3.setText(equipoSeleccionado.getNumeroSerie());
        if (textObservaciones != null) textObservaciones.setText(equipoSeleccionado.getObservaciones());
        if (comboBoxEstado != null) comboBoxEstado.setSelectedItem(equipoSeleccionado.getEstado());
        if (comboBoxTipo != null) comboBoxTipo.setSelectedItem(equipoSeleccionado.getTipo());

        if (comboBoxLAB != null) {
            int labIdEquipo = equipoSeleccionado.getLaboratorioId();
            for (int i = 0; i < comboBoxLAB.getItemCount(); i++) {
                ItemLaboratorio item = comboBoxLAB.getItemAt(i);
                if (item.getId() == labIdEquipo) {
                    comboBoxLAB.setSelectedIndex(i);
                    break;
                }
            }
        }

        if (guardarButton != null) guardarButton.setEnabled(false);
        if (actualizarButton != null) actualizarButton.setEnabled(true);
    }

    private void limpiarFormulario() {
        idSeleccionado = 0;
        equipoSeleccionado = null;
        if (table1 != null) table1.clearSelection();

        if (textID != null) textID.setText("");

        if (textCodigoInventario != null) {
            try {
                String siguienteCodigo = controller.generarSiguienteCodigo();
                textCodigoInventario.setText(siguienteCodigo);
            } catch (Exception e) {
                textCodigoInventario.setText("INV-001");
            }
        }

        if (textNombreEquipo != null) textNombreEquipo.setText("");
        if (textField1 != null) textField1.setText("");
        if (textField2 != null) textField2.setText("");
        if (textField3 != null) textField3.setText("");
        if (textObservaciones != null) textObservaciones.setText("");
        if (comboBoxLAB != null && comboBoxLAB.getItemCount() > 0) comboBoxLAB.setSelectedIndex(0);
        if (comboBoxEstado != null) comboBoxEstado.setSelectedIndex(0);
        if (comboBoxTipo != null && comboBoxTipo.getItemCount() > 0) comboBoxTipo.setSelectedIndex(0);

        if (guardarButton != null) guardarButton.setEnabled(true);
        if (actualizarButton != null) actualizarButton.setEnabled(false);
        if (textNombreEquipo != null) textNombreEquipo.requestFocusInWindow();
    }

    private void guardar() {
        try {
            ItemLaboratorio labSeleccionado = comboBoxLAB != null ? (ItemLaboratorio) comboBoxLAB.getSelectedItem() : null;
            int labId = labSeleccionado != null ? labSeleccionado.getId() : 0;

            String codigo = textCodigoInventario != null ? textCodigoInventario.getText().trim() : "";
            String nombre = textNombreEquipo != null ? textNombreEquipo.getText().trim() : "";
            String marca = textField1 != null ? textField1.getText().trim() : "";
            String modelo = textField2 != null ? textField2.getText().trim() : "";
            String numeroSerie = textField3 != null ? textField3.getText().trim() : "";
            String tipo = comboBoxTipo != null ? comboBoxTipo.getSelectedItem().toString() : "Hardware";
            String estado = comboBoxEstado != null ? comboBoxEstado.getSelectedItem().toString() : "DISPONIBLE";
            String observaciones = textObservaciones != null ? textObservaciones.getText().trim() : "";

            if (idSeleccionado == 0) {
                controller.crearEquipo(labId, codigo, nombre, marca, modelo, numeroSerie, tipo, estado, observaciones);
                JOptionPane.showMessageDialog(this, "Equipo registrado con éxito.", "Inventario", JOptionPane.INFORMATION_MESSAGE);
            } else {
                equipoSeleccionado.setLaboratorioId(labId);
                equipoSeleccionado.setCodigoInventario(codigo);
                equipoSeleccionado.setNombreEquipo(nombre);
                equipoSeleccionado.setMarca(marca);
                equipoSeleccionado.setModelo(modelo);
                equipoSeleccionado.setNumeroSerie(numeroSerie);
                equipoSeleccionado.setTipo(tipo);
                equipoSeleccionado.setEstado(estado);
                equipoSeleccionado.setObservaciones(observaciones);

                controller.actualizar(equipoSeleccionado);
                JOptionPane.showMessageDialog(this, "Equipo actualizado con éxito.", "Inventario", JOptionPane.INFORMATION_MESSAGE);
            }

            limpiarFormulario();
            refrescarTabla();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error de Validación", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizar() {
        if (idSeleccionado == 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un equipo de la tabla.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        guardar();
    }

    public JPanel getPanelInventario() {
        return PanelInventario;
    }

    private static class ItemLaboratorio {
        private final int id;
        private final String nombre;

        public ItemLaboratorio(int id, String nombre) {
            this.id = id;
            this.nombre = nombre;
        }

        public int getId() { return id; }

        @Override
        public String toString() { return nombre; }
    }
}