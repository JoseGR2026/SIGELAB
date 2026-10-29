
package SIGELAB.view;

import SIGELAB.controller.HistorialPrestamoController;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class HistorialPrestamoView extends JFrame {

    private final HistorialPrestamoController controller;

    private JTable tablaHistorial;
    private DefaultTableModel modeloTabla;
    private JComboBox<String> cmbEstado;
    private JButton btnActualizar;
    private JButton btnCerrar;
    private JLabel lblTotal;

    public HistorialPrestamoView() {

        controller = new HistorialPrestamoController();

        configurarVentana();
        crearComponentes();
        configurarEventos();
        cargarHistorial();
    }

    private void configurarVentana() {

        setTitle("SIGELAB - Historial de préstamos");
        setSize(1150, 600);
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
                "Historial de préstamos"
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

        JPanel panelFiltros = new JPanel(
                new FlowLayout(FlowLayout.LEFT)
        );

        panelFiltros.add(
                new JLabel("Filtrar por estado:")
        );

        cmbEstado = new JComboBox<>(
                new String[]{
                        "TODOS",
                        "ACTIVO",
                        "VENCIDO",
                        "FINALIZADO"
                }
        );

        panelFiltros.add(cmbEstado);

        btnActualizar = new JButton("Actualizar lista");
        panelFiltros.add(btnActualizar);

        panelSuperior.add(
                panelFiltros, BorderLayout.SOUTH
        );

        panelPrincipal.add(
                panelSuperior, BorderLayout.NORTH
        );

        String[] columnas = {
                "Préstamo ID",
                "Solicitante",
                "Fecha préstamo",
                "Devolución esperada",
                "Estado préstamo",
                "Código equipo",
                "Equipo",
                "Estado devolución"
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

        tablaHistorial = new JTable(modeloTabla);
        tablaHistorial.setRowHeight(28);
        tablaHistorial.setAutoCreateRowSorter(true);
        tablaHistorial.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        JScrollPane scroll = new JScrollPane(
                tablaHistorial
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
                e -> cargarHistorial()
        );

        cmbEstado.addActionListener(
                e -> cargarHistorial()
        );

        btnCerrar.addActionListener(
                e -> dispose()
        );
    }

    private void cargarHistorial() {

        String filtro = (String) cmbEstado.getSelectedItem();

        btnActualizar.setEnabled(false);
        cmbEstado.setEnabled(false);
        lblTotal.setText("Consultando préstamos...");

        SwingWorker<List<Object[]>, Void> trabajador =
                new SwingWorker<>() {

                    @Override
                    protected List<Object[]> doInBackground() {

                        return controller.listarHistorial(filtro);
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
                            cmbEstado.setEnabled(true);
                        }
                    }
                };

        trabajador.execute();
    }

    private void mostrarError(String mensaje) {

        lblTotal.setText(
                "No se pudo cargar el historial."
        );

        JOptionPane.showMessageDialog(
                this,
                "Error al consultar los préstamos:\n" + mensaje,
                "SIGELAB - Error",
                JOptionPane.ERROR_MESSAGE
        );
    }
}
