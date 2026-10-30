
package SIGELAB.view;

import SIGELAB.controller.DevolucionController;
import SIGELAB.model.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class DevolucionView extends JFrame {

    private final Usuario usuarioAutenticado;
    private final DevolucionController controller;

    private JTable tablaPendientes;
    private DefaultTableModel modeloPendientes;
    private JComboBox<String> cmbCondicion;

    public DevolucionView(Usuario usuarioAutenticado) {
        this.usuarioAutenticado = usuarioAutenticado;
        this.controller = new DevolucionController();

        configurarVentana();
        construirInterfaz();
        cargarPendientes();
    }

    private void configurarVentana() {
        setTitle("SIGELAB - Registro de Devoluciones");
        setSize(1050, 650);
        setMinimumSize(new Dimension(850, 500));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    private void construirInterfaz() {

        JPanel principal = new JPanel(new BorderLayout(12, 12));
        principal.setBorder(
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        );

        JLabel titulo = new JLabel(
                "REGISTRO DE DEVOLUCIONES DE EQUIPOS",
                SwingConstants.CENTER
        );
        titulo.setFont(new Font("Arial", Font.BOLD, 20));
        principal.add(titulo, BorderLayout.NORTH);

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
        tablaPendientes.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );
        tablaPendientes.setAutoCreateRowSorter(true);
        tablaPendientes.setRowHeight(25);

        JPanel panelTabla = new JPanel(new BorderLayout(5, 5));
        panelTabla.setBorder(
                BorderFactory.createTitledBorder(
                        "Equipos pendientes de devolución"
                )
        );
        panelTabla.add(
                new JScrollPane(tablaPendientes),
                BorderLayout.CENTER
        );

        principal.add(panelTabla, BorderLayout.CENTER);

        JPanel panelInferior = new JPanel(new BorderLayout(10, 10));

        JPanel panelCondicion = new JPanel(
                new FlowLayout(FlowLayout.LEFT)
        );
        panelCondicion.setBorder(
                BorderFactory.createTitledBorder(
                        "Condición del equipo devuelto"
                )
        );

        cmbCondicion = new JComboBox<>(
                new String[]{"BUENO", "DANADO"}
        );

        panelCondicion.add(new JLabel("Estado de devolución:"));
        panelCondicion.add(cmbCondicion);

        panelInferior.add(panelCondicion, BorderLayout.NORTH);

        JPanel panelBotones = new JPanel(
                new FlowLayout(FlowLayout.RIGHT)
        );

        JButton btnActualizar = new JButton("Actualizar lista");
        JButton btnRegistrar = new JButton("Registrar devolución");
        JButton btnCerrar = new JButton("Cerrar");

        panelBotones.add(btnActualizar);
        panelBotones.add(btnRegistrar);
        panelBotones.add(btnCerrar);

        panelInferior.add(panelBotones, BorderLayout.SOUTH);

        principal.add(panelInferior, BorderLayout.SOUTH);

        setContentPane(principal);

        btnActualizar.addActionListener(
                e -> cargarPendientes()
        );

        btnRegistrar.addActionListener(
                e -> registrarDevolucion()
        );

        btnCerrar.addActionListener(
                e -> dispose()
        );
    }

    private void cargarPendientes() {

        try {
            modeloPendientes.setRowCount(0);

            List<Object[]> pendientes =
                    controller.listarPendientes();

            for (Object[] fila : pendientes) {
                modeloPendientes.addRow(fila);
            }

        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Error al consultar devoluciones:\n"
                            + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void registrarDevolucion() {

        if (usuarioAutenticado == null
                || usuarioAutenticado.getIdUsuario() <= 0) {

            JOptionPane.showMessageDialog(
                    this,
                    "No se encontró un usuario autenticado válido.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        int fila = tablaPendientes.getSelectedRow();

        if (fila < 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "Selecciona un equipo pendiente de devolución."
            );
            return;
        }

        int filaModelo =
                tablaPendientes.convertRowIndexToModel(fila);

        int prestamoId = (Integer)
                modeloPendientes.getValueAt(filaModelo, 0);

        String solicitante = (String)
                modeloPendientes.getValueAt(filaModelo, 1);

        int detalleId = (Integer)
                modeloPendientes.getValueAt(filaModelo, 2);

        String codigo = (String)
                modeloPendientes.getValueAt(filaModelo, 3);

        String condicion =
                (String) cmbCondicion.getSelectedItem();

        int confirmacion = JOptionPane.showConfirmDialog(
                this,
                "¿Deseas registrar esta devolución?\n\n"
                        + "Préstamo: #" + prestamoId
                        + "\nSolicitante: " + solicitante
                        + "\nEquipo: " + codigo
                        + "\nCondición: " + condicion,
                "Confirmar devolución",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (confirmacion != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            boolean registrado =
                    controller.registrarDevolucion(
                            detalleId,
                            condicion,
                            usuarioAutenticado.getIdUsuario()
                    );

            if (registrado) {
                JOptionPane.showMessageDialog(
                        this,
                        "Devolución registrada correctamente.",
                        "SIGELAB",
                        JOptionPane.INFORMATION_MESSAGE
                );

                cargarPendientes();
            }

        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "No se pudo registrar la devolución:\n"
                            + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
