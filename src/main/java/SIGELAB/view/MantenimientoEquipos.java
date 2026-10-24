package SIGELAB.view;

import SIGELAB.controller.MantenimientoEquipoController;
import SIGELAB.model.EstadoEquipo;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MantenimientoEquipos extends JFrame {
    private JTable table1;
    private JButton enviarAMantenimientoButton;
    private JButton actualizarButton;
    private JButton reparadoButton;
    private JButton darDeBajaButton;
    private JTextField textObservaciones;
    private JButton salirButton;

    private final MantenimientoEquipoController controller;
    private List<EstadoEquipo> listaEquipos = new ArrayList<>();
    private EstadoEquipo equipoSeleccionado;

    private static final String[] COLUMNAS = {"ID", "Cód. Inventario", "Equipo", "Marca", "Modelo", "Nº Serie", "Estado", "Observaciones"};
    private final DefaultTableModel modeloTabla = new DefaultTableModel(COLUMNAS, 0) {
        @Override
        public boolean isCellEditable(int fila, int columna) { return false; }
    };

    public MantenimientoEquipos() {
        super("SIGELAB - Gestión de Mantenimiento y Taller");
        this.controller = new MantenimientoEquipoController();

        setSize(1050, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        inicializarComponentesPorCodigo();
        configurarEventos();
        cargarDatos();
    }

    private void inicializarComponentesPorCodigo() {
        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // 1. Panel Superior (Observaciones)
        JPanel panelNorte = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panelNorte.setBorder(BorderFactory.createTitledBorder("Detalle / Motivo de Mantenimiento"));
        panelNorte.add(new JLabel("Observaciones / Falla:"));
        textObservaciones = new JTextField(35);
        panelNorte.add(textObservaciones);

        // 2. Tabla Central
        table1 = new JTable(modeloTabla);
        table1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(table1);

        // 3. Panel Inferior (Botones)
        JPanel panelSur = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        enviarAMantenimientoButton = new JButton("Enviar a Mantenimiento");
        reparadoButton = new JButton("Finalizar (Reparado)");
        darDeBajaButton = new JButton("Dar de Baja");
        actualizarButton = new JButton("Actualizar");
        salirButton = new JButton("Salir");

        panelSur.add(enviarAMantenimientoButton);
        panelSur.add(reparadoButton);
        panelSur.add(darDeBajaButton);
        panelSur.add(actualizarButton);
        panelSur.add(salirButton);

        // Agregar al JFrame
        panelPrincipal.add(panelNorte, BorderLayout.NORTH);
        panelPrincipal.add(scrollPane, BorderLayout.CENTER);
        panelPrincipal.add(panelSur, BorderLayout.SOUTH);

        setContentPane(panelPrincipal);
    }

    private void configurarEventos() {
        table1.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int fila = table1.getSelectedRow();
                if (fila >= 0 && fila < listaEquipos.size()) {
                    equipoSeleccionado = listaEquipos.get(table1.convertRowIndexToModel(fila));
                    if (textObservaciones != null && equipoSeleccionado.getObservaciones() != null) {
                        textObservaciones.setText(equipoSeleccionado.getObservaciones());
                    }
                }
            }
        });

        actualizarButton.addActionListener(e -> {
            cargarDatos();
            textObservaciones.setText("");
        });

        enviarAMantenimientoButton.addActionListener(e -> {
            if (verificarSeleccion()) {
                String motivo = textObservaciones.getText().trim();
                if (motivo.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Debe ingresar una observación o motivo en el cuadro de texto.", "Aviso", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                try {
                    controller.enviarAMantenimiento(equipoSeleccionado.getId(), motivo);
                    JOptionPane.showMessageDialog(this, "Equipo enviado a mantenimiento exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    cargarDatos();
                    textObservaciones.setText("");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        reparadoButton.addActionListener(e -> {
            if (verificarSeleccion()) {
                int confirm = JOptionPane.showConfirmDialog(this, "¿El equipo fue reparado con éxito y pasa a estar DISPONIBLE?", "Confirmar Reparación", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        controller.finalizarMantenimiento(equipoSeleccionado.getId(), true);
                        JOptionPane.showMessageDialog(this, "El equipo ha regresado a disponibilidad.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                        cargarDatos();
                        textObservaciones.setText("");
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        darDeBajaButton.addActionListener(e -> {
            if (verificarSeleccion()) {
                int confirm = JOptionPane.showConfirmDialog(this, "¿Confirma que el equipo es irreparable y será DADO DE BAJA?", "Dar de Baja", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        controller.finalizarMantenimiento(equipoSeleccionado.getId(), false);
                        JOptionPane.showMessageDialog(this, "El equipo ha sido dado de baja.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
                        cargarDatos();
                        textObservaciones.setText("");
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        salirButton.addActionListener(e -> dispose());
    }

    private boolean verificarSeleccion() {
        if (equipoSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Por favor, seleccione un equipo de la tabla.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    private void cargarDatos() {
        try {
            listaEquipos = controller.listarEquipos();
            modeloTabla.setRowCount(0);
            for (EstadoEquipo eq : listaEquipos) {
                modeloTabla.addRow(new Object[]{
                        eq.getId(),
                        eq.getCodigoInventario(),
                        eq.getNombreEquipo(),
                        eq.getMarca(),
                        eq.getModelo(),
                        eq.getNumeroSerie(),
                        eq.getEstado(),
                        eq.getObservaciones()
                });
            }
            equipoSeleccionado = null;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar datos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}