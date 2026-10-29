
package SIGELAB.view;

import SIGELAB.controller.AuditoriaConsultaController;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class AuditoriaConsultaView extends JFrame {

    private final AuditoriaConsultaController controller;

    private JTable tablaAuditoria;
    private DefaultTableModel modeloTabla;
    private JButton btnActualizar;
    private JButton btnCerrar;
    private JLabel lblTotal;

    public AuditoriaConsultaView() {

        controller = new AuditoriaConsultaController();

        configurarVentana();
        crearComponentes();
        configurarEventos();
        cargarAuditoria();
    }

    private void configurarVentana() {

        setTitle("SIGELAB - Registro de auditoría");
        setSize(1200, 600);
        setMinimumSize(new Dimension(900, 450));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    private void crearComponentes() {

        JPanel panelPrincipal = new JPanel(
                new BorderLayout(10, 10)
        );

        panelPrincipal.setBorder(
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        );

        JLabel lblTitulo = new JLabel(
                "Registro de auditoría"
        );

        lblTitulo.setFont(
                new Font("Arial", Font.BOLD, 22)
        );

        JPanel panelSuperior = new JPanel(
                new BorderLayout(10, 10)
        );

        panelSuperior.add(
                lblTitulo, BorderLayout.NORTH
        );

        JPanel panelAcciones = new JPanel(
                new FlowLayout(FlowLayout.LEFT)
        );

        btnActualizar = new JButton("Actualizar registros");
        panelAcciones.add(btnActualizar);

        panelSuperior.add(
                panelAcciones, BorderLayout.SOUTH
        );

        panelPrincipal.add(
                panelSuperior, BorderLayout.NORTH
        );

        String[] columnas = {
                "Auditoría ID",
                "Fecha y hora",
                "Usuario ID",
                "Acción",
                "Entidad",
                "Registro ID",
                "Descripción"
        };

        modeloTabla = new DefaultTableModel(
                columnas, 0
        ) {
            @Override
            public boolean isCellEditable(
                    int fila, int columna) {
                return false;
            }
        };

        tablaAuditoria = new JTable(modeloTabla);
        tablaAuditoria.setRowHeight(28);
        tablaAuditoria.setAutoCreateRowSorter(true);
        tablaAuditoria.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        tablaAuditoria.setAutoResizeMode(
                JTable.AUTO_RESIZE_OFF
        );

        tablaAuditoria.getColumnModel()
                .getColumn(0).setPreferredWidth(100);

        tablaAuditoria.getColumnModel()
                .getColumn(1).setPreferredWidth(180);

        tablaAuditoria.getColumnModel()
                .getColumn(2).setPreferredWidth(100);

        tablaAuditoria.getColumnModel()
                .getColumn(3).setPreferredWidth(190);

        tablaAuditoria.getColumnModel()
                .getColumn(4).setPreferredWidth(120);

        tablaAuditoria.getColumnModel()
                .getColumn(5).setPreferredWidth(100);

        tablaAuditoria.getColumnModel()
                .getColumn(6).setPreferredWidth(450);

        JScrollPane scroll = new JScrollPane(
                tablaAuditoria
        );

        panelPrincipal.add(
                scroll, BorderLayout.CENTER
        );

        JPanel panelInferior = new JPanel(
                new BorderLayout()
        );

        lblTotal = new JLabel(
                "Registros encontrados: 0"
        );

        panelInferior.add(
                lblTotal, BorderLayout.WEST
        );

        btnCerrar = new JButton("Cerrar");

        panelInferior.add(
                btnCerrar, BorderLayout.EAST
        );

        panelPrincipal.add(
                panelInferior, BorderLayout.SOUTH
        );

        setContentPane(panelPrincipal);
    }

    private void configurarEventos() {

        btnActualizar.addActionListener(
                e -> cargarAuditoria()
        );

        btnCerrar.addActionListener(
                e -> dispose()
        );
    }

    private void cargarAuditoria() {

        btnActualizar.setEnabled(false);
        lblTotal.setText("Consultando auditoría...");

        SwingWorker<List<Object[]>, Void> trabajador =
                new SwingWorker<>() {

                    @Override
                    protected List<Object[]> doInBackground() {

                        return controller.listarAuditoria();
                    }

                    @Override
                    protected void done() {

                        try {

                            List<Object[]> registros = get();

                            modeloTabla.setRowCount(0);

                            for (Object[] fila : registros) {
                                modeloTabla.addRow(fila);
                            }

                            lblTotal.setText(
                                    "Registros encontrados: "
                                            + registros.size()
                            );

                        } catch (InterruptedException e) {

                            Thread.currentThread().interrupt();

                            mostrarError(
                                    "La consulta fue interrumpida."
                            );

                        } catch (ExecutionException e) {

                            Throwable causa = e.getCause();

                            mostrarError(
                                    causa != null
                                            ? causa.getMessage()
                                            : e.getMessage()
                            );

                        } finally {

                            btnActualizar.setEnabled(true);
                        }
                    }
                };

        trabajador.execute();
    }

    private void mostrarError(String mensaje) {

        lblTotal.setText(
                "No se pudo cargar la auditoría."
        );

        JOptionPane.showMessageDialog(
                this,
                "Error al consultar la auditoría:\n" + mensaje,
                "SIGELAB - Error",
                JOptionPane.ERROR_MESSAGE
        );
    }
}
