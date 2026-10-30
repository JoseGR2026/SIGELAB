package SIGELAB.view;

import SIGELAB.controller.LaboratorioController;
import SIGELAB.model.Laboratorio;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CatalogoLaboratorios extends JFrame {

    private final JPanel panelPrincipal;
    private JTextField textID;
    private JTextField textCodigo;
    private JTextField textNombre;
    private JTextField textUbicacion;
    private JTextField textcapacidad;
    private JTextField textEstado;

    private JButton nuevoButton;
    private JButton guardarButton;
    private JButton actualizarButton;
    private JButton limpiarButton;
    private JTable tablaLaboratorios;

    private static final String[] COLUMNAS = {"ID", "Código", "Nombre", "Ubicación", "Capacidad", "Estado"};

    private final LaboratorioController laboratorioController;
    private Laboratorio laboratorioSeleccionado;
    private int idLaboratorioSeleccionado = 0;

    private final DefaultTableModel modeloTabla = new DefaultTableModel(COLUMNAS, 0) {
        @Override
        public boolean isCellEditable(int fila, int columna) {
            return false;
        }
    };

    private List<Laboratorio> laboratoriosMostrados = new ArrayList<>();

    public CatalogoLaboratorios() {
        this(new LaboratorioController());
    }

    public CatalogoLaboratorios(LaboratorioController laboratorioController) {
        super("SIGELAB - Catálogo de Laboratorios");
        this.laboratorioController = laboratorioController;

        panelPrincipal = new JPanel(new BorderLayout(15, 15));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setContentPane(panelPrincipal);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(1100, 650);
        setMinimumSize(new Dimension(950, 550));
        setLocationRelativeTo(null);

        construirInterfaz();
        configurarFormulario();
        configurarTabla();
        registrarEventos();
        refrescarTabla();
        limpiarFormulario();
    }

    private void construirInterfaz() {
        // Título Superior
        JLabel lblTitulo = new JLabel("Catálogo y Gestión de Laboratorios", SwingConstants.LEFT);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setForeground(new Color(15, 23, 42));
        panelPrincipal.add(lblTitulo, BorderLayout.NORTH);

        // Panel Central (Dividido en 2 columnas: Izquierda Formulario, Derecha Tabla)
        JPanel panelCentro = new JPanel(new GridLayout(1, 2, 20, 0));

        // --- IZQUIERDA: Formulario de Datos ---
        JPanel panelIzquierdo = new JPanel(new BorderLayout(0, 15));

        JPanel panelFormulario = new JPanel(new GridBagLayout());
        panelFormulario.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225)),
                " Información del Laboratorio ",
                0, 0, new Font("Segoe UI", Font.BOLD, 12), new Color(51, 65, 85)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        textID = new JTextField();
        textCodigo = new JTextField();
        textNombre = new JTextField();
        textUbicacion = new JTextField();
        textcapacidad = new JTextField();
        textEstado = new JTextField();

        int fila = 0;
        agregarCampoFormulario(panelFormulario, gbc, fila++, new JLabel("ID:"), textID);
        agregarCampoFormulario(panelFormulario, gbc, fila++, new JLabel("Código:"), textCodigo);
        agregarCampoFormulario(panelFormulario, gbc, fila++, new JLabel("Nombre:"), textNombre);
        agregarCampoFormulario(panelFormulario, gbc, fila++, new JLabel("Ubicación:"), textUbicacion);
        agregarCampoFormulario(panelFormulario, gbc, fila++, new JLabel("Capacidad:"), textcapacidad);
        agregarCampoFormulario(panelFormulario, gbc, fila++, new JLabel("Estado:"), textEstado);

        panelIzquierdo.add(panelFormulario, BorderLayout.CENTER);

        // Panel inferior izquierdo: Botones de Acción
        JPanel panelBotones = new JPanel(new GridLayout(2, 2, 8, 8));
        panelBotones.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225)),
                " Acciones ",
                0, 0, new Font("Segoe UI", Font.BOLD, 12), new Color(51, 65, 85)
        ));

        nuevoButton = new JButton("Nuevo");
        guardarButton = new JButton("Guardar");
        actualizarButton = new JButton("Actualizar");
        limpiarButton = new JButton("Limpiar");

        estilizarBoton(nuevoButton, false);
        estilizarBoton(guardarButton, true); // Principal con acento azul
        estilizarBoton(actualizarButton, false);
        estilizarBoton(limpiarButton, false);

        panelBotones.add(nuevoButton);
        panelBotones.add(guardarButton);
        panelBotones.add(actualizarButton);
        panelBotones.add(limpiarButton);

        panelIzquierdo.add(panelBotones, BorderLayout.SOUTH);
        panelCentro.add(panelIzquierdo);

        // --- DERECHA: Tabla de Laboratorios ---
        JPanel panelDerecho = new JPanel(new BorderLayout(0, 10));
        panelDerecho.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225)),
                " Laboratorios Registrados ",
                0, 0, new Font("Segoe UI", Font.BOLD, 12), new Color(51, 65, 85)
        ));

        tablaLaboratorios = new JTable(modeloTabla);
        tablaLaboratorios.setRowHeight(28);
        tablaLaboratorios.setAutoCreateRowSorter(true);
        tablaLaboratorios.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollTabla = new JScrollPane(tablaLaboratorios);
        panelDerecho.add(scrollTabla, BorderLayout.CENTER);

        panelCentro.add(panelDerecho);
        panelPrincipal.add(panelCentro, BorderLayout.CENTER);

        // --- INFERIOR: Botón Cerrar o Regresar ---
        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        JButton btnCerrar = new JButton("Cerrar");
        estilizarBoton(btnCerrar, false);
        btnCerrar.addActionListener(e -> dispose());
        panelInferior.add(btnCerrar);

        panelPrincipal.add(panelInferior, BorderLayout.SOUTH);
    }

    private void agregarCampoFormulario(JPanel panel, GridBagConstraints gbc, int fila, JLabel label, JComponent componente) {
        gbc.gridx = 0; gbc.gridy = fila; gbc.weightx = 0.0;
        panel.add(label, gbc);

        gbc.gridx = 1; gbc.weightx = 1.0;
        componente.setPreferredSize(new Dimension(220, 32));
        panel.add(componente, gbc);
    }

    private void estilizarBoton(JButton boton, boolean esPrincipal) {
        boton.putClientProperty("JButton.buttonType", "roundRect");
        if (esPrincipal) {
            boton.putClientProperty("FlatLaf.style", "background: #2563eb; foreground: #ffffff; font: bold; arc: 10");
        } else {
            boton.putClientProperty("FlatLaf.style", "arc: 10");
        }
    }

    private void configurarFormulario() {
        textID.setEditable(false);
        actualizarButton.setEnabled(false);
        textEstado.setText("ACTIVO");
        textEstado.setEditable(false);
    }

    private void configurarTabla() {
        tablaLaboratorios.setModel(modeloTabla);
        tablaLaboratorios.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaLaboratorios.getSelectionModel().addListSelectionListener(evento -> {
            if (!evento.getValueIsAdjusting()) {
                alSeleccionarFila();
            }
        });
    }

    private void registrarEventos() {
        nuevoButton.addActionListener(evento -> limpiarFormulario());
        guardarButton.addActionListener(evento -> guardar());
        actualizarButton.addActionListener(evento -> actualizar());
        limpiarButton.addActionListener(evento -> limpiarFormulario());
    }

    private void refrescarTabla() {
        try {
            laboratoriosMostrados = new ArrayList<>(laboratorioController.listar());
            modeloTabla.setRowCount(0);
            for (Laboratorio lab : laboratoriosMostrados) {
                modeloTabla.addRow(new Object[]{
                        lab.getId(),
                        lab.getCodigo(),
                        lab.getNombre(),
                        lab.getUbicacion(),
                        lab.getCapacidad(),
                        lab.getEstado()
                });
            }
        } catch (Exception e) {
            mostrarError(e);
        }
    }

    private void alSeleccionarFila() {
        int fila = tablaLaboratorios.getSelectedRow();
        if (fila < 0 || fila >= laboratoriosMostrados.size()) {
            return;
        }
        laboratorioSeleccionado = laboratoriosMostrados.get(tablaLaboratorios.convertRowIndexToModel(fila));
        idLaboratorioSeleccionado = laboratorioSeleccionado.getId();

        textID.setText(String.valueOf(laboratorioSeleccionado.getId()));
        textID.setEnabled(false);
        textCodigo.setText(laboratorioSeleccionado.getCodigo());
        textNombre.setText(laboratorioSeleccionado.getNombre());
        textUbicacion.setText(laboratorioSeleccionado.getUbicacion());
        textcapacidad.setText(String.valueOf(laboratorioSeleccionado.getCapacidad()));
        textEstado.setText(laboratorioSeleccionado.getEstado());

        guardarButton.setEnabled(false);
        actualizarButton.setEnabled(true);
    }

    private void limpiarFormulario() {
        idLaboratorioSeleccionado = 0;
        laboratorioSeleccionado = null;
        tablaLaboratorios.clearSelection();

        textID.setText("");
        textID.setEnabled(false);
        textCodigo.setText("");
        textNombre.setText("");
        textUbicacion.setText("");
        textcapacidad.setText("");
        textEstado.setText("ACTIVO");

        guardarButton.setEnabled(true);
        actualizarButton.setEnabled(false);
        textCodigo.requestFocusInWindow();
    }

    private void guardar() {
        try {
            String codigo = textCodigo.getText().trim();
            String nombre = textNombre.getText().trim();
            String ubicacion = textUbicacion.getText().trim();
            String capacidadStr = textcapacidad.getText().trim();
            String estado = textEstado.getText().trim().toUpperCase();

            if (codigo.isEmpty() || nombre.isEmpty()) {
                mostrarError("El código y el nombre del laboratorio son obligatorios.");
                return;
            }

            int capacidad = capacidadStr.isEmpty() ? 0 : Integer.parseInt(capacidadStr);
            String estadoFinal = estado.isEmpty() ? "ACTIVO" : estado;

            if (idLaboratorioSeleccionado == 0) {
                laboratorioController.crearLaboratorio(codigo, nombre, ubicacion, capacidad, estadoFinal);
                JOptionPane.showMessageDialog(this, "El laboratorio se guardó correctamente.", "Catálogo de Laboratorios", JOptionPane.INFORMATION_MESSAGE);
            } else {
                laboratorioSeleccionado.setCodigo(codigo);
                laboratorioSeleccionado.setNombre(nombre);
                laboratorioSeleccionado.setUbicacion(ubicacion);
                laboratorioSeleccionado.setCapacidad(capacidad);
                laboratorioSeleccionado.setEstado(estadoFinal);

                laboratorioController.actualizar(laboratorioSeleccionado);
                JOptionPane.showMessageDialog(this, "El laboratorio se actualizó correctamente.", "Catálogo de Laboratorios", JOptionPane.INFORMATION_MESSAGE);
            }

            limpiarFormulario();
            refrescarTabla();
        } catch (NumberFormatException e) {
            mostrarError("La capacidad debe ser un número entero válido.");
        } catch (Exception e) {
            mostrarError(e);
        }
    }

    private void actualizar() {
        if (idLaboratorioSeleccionado == 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un laboratorio de la tabla para actualizar.", "Catálogo de Laboratorios", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        guardar();
    }

    private void mostrarError(Exception e) {
        String mensaje = e.getMessage() != null ? e.getMessage() : "Error desconocido";
        JOptionPane.showMessageDialog(this, mensaje, "No se pudo completar la operación", JOptionPane.WARNING_MESSAGE);
    }

    private void mostrarError(String mensaje) {
        if (mensaje == null || mensaje.isBlank()) mensaje = "Ocurrió un error inesperado.";
        JOptionPane.showMessageDialog(panelPrincipal, mensaje, "Catálogo de Laboratorios", JOptionPane.ERROR_MESSAGE);
    }

    public JPanel getPanelPrincipal() {
        return panelPrincipal;
    }
}