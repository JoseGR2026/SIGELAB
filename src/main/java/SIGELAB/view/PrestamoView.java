
package SIGELAB.view;

import SIGELAB.controller.PrestamoController;
import SIGELAB.model.EstadoEquipo;
import SIGELAB.model.Usuario;
import SIGELAB.repository.EstadoEquipoRepository;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PrestamoView extends JFrame {

    private final Usuario usuarioAutenticado;
    private final PrestamoController controller;
    private final EstadoEquipoRepository equipoRepository;

    private JTextField txtSolicitante;
    private JSpinner fechaDevolucion;
    private JTextArea txtObservaciones;

    private JTable tablaDisponibles;
    private JTable tablaSeleccionados;

    private DefaultTableModel modeloDisponibles;
    private DefaultTableModel modeloSeleccionados;

    private final List<Integer> equiposSeleccionados =
            new ArrayList<>();

    public PrestamoView(Usuario usuarioAutenticado) {
        this.usuarioAutenticado = usuarioAutenticado;
        this.controller = new PrestamoController();
        this.equipoRepository = new EstadoEquipoRepository();

        configurarVentana();
        construirInterfaz();
        cargarEquiposDisponibles();
    }

    private void configurarVentana() {
        setTitle("SIGELAB - Registro de Préstamos");
        setSize(1050, 700);
        setMinimumSize(new Dimension(850, 600));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    private void construirInterfaz() {
        JPanel principal = new JPanel(new BorderLayout(10, 10));
        principal.setBorder(
                BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel titulo = new JLabel(
                "REGISTRO DE PRÉSTAMOS DE EQUIPOS",
                SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 20));
        principal.add(titulo, BorderLayout.NORTH);

        JPanel formulario = new JPanel(new GridLayout(3, 2, 10, 10));
        formulario.setBorder(
                BorderFactory.createTitledBorder("Datos del préstamo"));

        txtSolicitante = new JTextField();

        fechaDevolucion = new JSpinner(
                new SpinnerDateModel());
        fechaDevolucion.setEditor(
                new JSpinner.DateEditor(
                        fechaDevolucion, "dd/MM/yyyy HH:mm"));

        // Fecha inicial sugerida: mañana.
        fechaDevolucion.setValue(
                java.sql.Timestamp.valueOf(
                        LocalDate.now()
                                .plusDays(1)
                                .atTime(17, 0)));

        txtObservaciones = new JTextArea(3, 20);
        txtObservaciones.setLineWrap(true);
        txtObservaciones.setWrapStyleWord(true);

        formulario.add(new JLabel("Solicitante:"));
        formulario.add(txtSolicitante);
        formulario.add(new JLabel("Devolución esperada:"));
        formulario.add(fechaDevolucion);
        formulario.add(new JLabel("Observaciones:"));
        formulario.add(new JScrollPane(txtObservaciones));

        modeloDisponibles = new DefaultTableModel(
                new Object[]{"ID", "Código", "Equipo", "Estado"}, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };

        tablaDisponibles = new JTable(modeloDisponibles);
        tablaDisponibles.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION);

        modeloSeleccionados = new DefaultTableModel(
                new Object[]{"ID", "Código", "Equipo"}, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };

        tablaSeleccionados = new JTable(modeloSeleccionados);
        tablaSeleccionados.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION);

        JPanel panelTablas = new JPanel(new GridLayout(1, 2, 10, 10));

        JPanel panelDisponibles = new JPanel(new BorderLayout(5, 5));
        panelDisponibles.setBorder(
                BorderFactory.createTitledBorder("Equipos disponibles"));
        panelDisponibles.add(
                new JScrollPane(tablaDisponibles), BorderLayout.CENTER);

        JButton btnAgregar = new JButton("Agregar equipo");
        panelDisponibles.add(btnAgregar, BorderLayout.SOUTH);

        JPanel panelSeleccionados = new JPanel(new BorderLayout(5, 5));
        panelSeleccionados.setBorder(
                BorderFactory.createTitledBorder("Equipos del préstamo"));
        panelSeleccionados.add(
                new JScrollPane(tablaSeleccionados), BorderLayout.CENTER);

        JButton btnQuitar = new JButton("Quitar equipo");
        panelSeleccionados.add(btnQuitar, BorderLayout.SOUTH);

        panelTablas.add(panelDisponibles);
        panelTablas.add(panelSeleccionados);

        JPanel contenido = new JPanel(new BorderLayout(10, 10));
        contenido.add(formulario, BorderLayout.NORTH);
        contenido.add(panelTablas, BorderLayout.CENTER);

        principal.add(contenido, BorderLayout.CENTER);

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton btnActualizar = new JButton("Actualizar equipos");
        JButton btnRegistrar = new JButton("Registrar préstamo");
        JButton btnCerrar = new JButton("Cerrar");

        acciones.add(btnActualizar);
        acciones.add(btnRegistrar);
        acciones.add(btnCerrar);

        principal.add(acciones, BorderLayout.SOUTH);

        setContentPane(principal);

        btnAgregar.addActionListener(e -> agregarEquipo());
        btnQuitar.addActionListener(e -> quitarEquipo());
        btnActualizar.addActionListener(
                e -> cargarEquiposDisponibles());
        btnRegistrar.addActionListener(
                e -> registrarPrestamo());
        btnCerrar.addActionListener(e -> dispose());
    }

    private void cargarEquiposDisponibles() {
        try {
            modeloDisponibles.setRowCount(0);

            List<EstadoEquipo> equipos = equipoRepository.listar();

            for (EstadoEquipo equipo : equipos) {
                if ("DISPONIBLE".equalsIgnoreCase(equipo.getEstado())
                        && !equiposSeleccionados.contains(equipo.getId())) {

                    modeloDisponibles.addRow(new Object[]{
                            equipo.getId(),
                            equipo.getCodigoInventario(),
                            equipo.getNombreEquipo(),
                            equipo.getEstado()
                    });
                }
            }

        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Error al cargar equipos:\n" + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void agregarEquipo() {
        int fila = tablaDisponibles.getSelectedRow();

        if (fila < 0) {
            JOptionPane.showMessageDialog(
                    this, "Selecciona un equipo disponible.");
            return;
        }

        int filaModelo = tablaDisponibles.convertRowIndexToModel(fila);

        int id = (Integer) modeloDisponibles.getValueAt(filaModelo, 0);

        if (equiposSeleccionados.contains(id)) {
            JOptionPane.showMessageDialog(
                    this, "Este equipo ya fue agregado.");
            return;
        }

        equiposSeleccionados.add(id);

        modeloSeleccionados.addRow(new Object[]{
                id,
                modeloDisponibles.getValueAt(filaModelo, 1),
                modeloDisponibles.getValueAt(filaModelo, 2)
        });

        modeloDisponibles.removeRow(filaModelo);
    }

    private void quitarEquipo() {
        int fila = tablaSeleccionados.getSelectedRow();

        if (fila < 0) {
            JOptionPane.showMessageDialog(
                    this, "Selecciona un equipo para quitar.");
            return;
        }

        int filaModelo = tablaSeleccionados.convertRowIndexToModel(fila);

        int id = (Integer) modeloSeleccionados.getValueAt(filaModelo, 0);

        equiposSeleccionados.remove(Integer.valueOf(id));
        modeloSeleccionados.removeRow(filaModelo);

        cargarEquiposDisponibles();
    }

    private void registrarPrestamo() {
        if (usuarioAutenticado == null
                || usuarioAutenticado.getIdUsuario() <= 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "No se encontró un usuario autenticado válido.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            java.util.Date fecha =
                    (java.util.Date) fechaDevolucion.getValue();

            LocalDateTime fechaEsperada =
                    new java.sql.Timestamp(
                            fecha.getTime()).toLocalDateTime();

            boolean registrado = controller.registrarPrestamo(
                    txtSolicitante.getText(),
                    fechaEsperada,
                    usuarioAutenticado.getIdUsuario(),
                    txtObservaciones.getText(),
                    new ArrayList<>(equiposSeleccionados)
            );

            if (registrado) {
                JOptionPane.showMessageDialog(
                        this,
                        "Préstamo registrado correctamente.",
                        "SIGELAB",
                        JOptionPane.INFORMATION_MESSAGE);

                txtSolicitante.setText("");
                txtObservaciones.setText("");
                equiposSeleccionados.clear();
                modeloSeleccionados.setRowCount(0);

                cargarEquiposDisponibles();
            }

        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "No se pudo registrar el préstamo:\n"
                            + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
