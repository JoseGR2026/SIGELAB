package SIGELAB.view;

import SIGELAB.controller.LaboratorioController;
import SIGELAB.model.Laboratorio;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.List;

public class CatalogoLaboratorios extends JFrame {

    private JPanel panelPrincipal;
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
        setContentPane(panelPrincipal);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        configurarFormulario();
        configurarTabla();
        registrarEventos();
        refrescarTabla();
        limpiarFormulario();

        setSize(1050, 650);
        setLocationRelativeTo(null);
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