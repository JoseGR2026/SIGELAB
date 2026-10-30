
package SIGELAB.view;

import SIGELAB.controller.PrestamoVencidoController;
import SIGELAB.model.Usuario;

import javax.swing.*;
import java.awt.*;

public class MainForm {

    private JPanel panelPrincipal;
    private JLabel lblTitulo;
    private JButton btnSalir;
    private JButton btnInventario;
    private JButton btnPrestamos;
    private JButton btnRegistrarDevolucion;
    private JButton btnMantenimiento;
    private JButton btnAuditoria;
    private JButton btnReportes;
    private JButton btnLaboratorios;
    private JButton btnUsuarios;

    private final Usuario usuarioAutenticado;

    public MainForm() {
        this(null);
    }

    public MainForm(Usuario usuarioAutenticado) {
        this.usuarioAutenticado = usuarioAutenticado;
        configurarFormulario();
        configurarEventos();
        verificarPrestamosVencidos();
    }

    private void configurarFormulario() {
        String nombreCompleto = "Administrador";

        if (usuarioAutenticado != null) {
            nombreCompleto = usuarioAutenticado.getNombres()
                    + " " + usuarioAutenticado.getApellidos();
        }

        lblTitulo.setText("Bienvenido, Administrador: " + nombreCompleto);
    }

    private void configurarEventos() {
        // Módulos de los compañeros
        btnInventario.addActionListener(e -> abrirInventarioEquipos());
        btnLaboratorios.addActionListener(e -> abrirCatlogoLaboratorios());
        btnMantenimiento.addActionListener(e -> MantenimientoEquipos());
        btnUsuarios.addActionListener(e -> abrirUsuarios());

        // Módulo de préstamos y devoluciones
        btnPrestamos.addActionListener(e -> abrirPrestamos());
        btnRegistrarDevolucion.addActionListener(e -> abrirDevoluciones());

        // Historial y auditoría
        btnAuditoria.addActionListener(e -> abrirRegistroAuditoria());
        btnReportes.addActionListener(e -> abrirHistorialPrestamos());

        btnSalir.addActionListener(e -> salir());
    }

    private void abrirPrestamos() {
        if (!usuarioValido()) {
            mostrarAdvertenciaSesion();
            return;
        }

        PrestamoView ventana = new PrestamoView(usuarioAutenticado);
        ventana.setLocationRelativeTo(null);
        ventana.setVisible(true);
    }

    private void abrirDevoluciones() {
        if (!usuarioValido()) {
            mostrarAdvertenciaSesion();
            return;
        }

        DevolucionView ventana = new DevolucionView(usuarioAutenticado);
        ventana.setLocationRelativeTo(null);
        ventana.setVisible(true);
    }

    private void abrirHistorialPrestamos() {
        HistorialPrestamoView ventana = new HistorialPrestamoView();
        ventana.setLocationRelativeTo(null);
        ventana.setVisible(true);
    }

    private void abrirRegistroAuditoria() {
        AuditoriaConsultaView ventana = new AuditoriaConsultaView();
        ventana.setLocationRelativeTo(null);
        ventana.setVisible(true);
    }

    private boolean usuarioValido() {
        return usuarioAutenticado != null
                && usuarioAutenticado.getIdUsuario() > 0;
    }

    private void mostrarAdvertenciaSesion() {
        JOptionPane.showMessageDialog(
                panelPrincipal,
                "Debe iniciar sesión para utilizar este módulo.",
                "SIGELAB",
                JOptionPane.WARNING_MESSAGE
        );
    }

    private void verificarPrestamosVencidos() {
        if (!usuarioValido()) {
            return;
        }

        SwingWorker<Integer, Void> worker = new SwingWorker<>() {
            @Override
            protected Integer doInBackground() throws Exception {
                PrestamoVencidoController controller =
                        new PrestamoVencidoController();

                return controller.actualizarVencidos(
                        usuarioAutenticado.getIdUsuario()
                );
            }

            @Override
            protected void done() {
                try {
                    int actualizados = get();

                    if (actualizados > 0) {
                        System.out.println(
                                "SIGELAB: " + actualizados
                                        + " préstamo(s) marcado(s) como vencido(s)."
                        );
                    }
                } catch (Exception ex) {
                    System.err.println(
                            "Error al verificar préstamos vencidos: "
                                    + ex.getMessage()
                    );
                }
            }
        };

        worker.execute();
    }

    // Administración de usuarios (módulo del equipo)
    private void abrirUsuarios() {
        UsuarioView view = new UsuarioView(usuarioAutenticado);

        abrirVentana(
                "Administración de Usuarios",
                view.getPanelPrincipal(),
                1050,
                650
        );
    }

    // Catálogo de laboratorios (módulo del equipo)
    private void abrirCatlogoLaboratorios() {
        CatalogoLaboratorios ventana = new CatalogoLaboratorios();
        ventana.setVisible(true);
    }

    // Inventario (módulo del equipo)
    private void abrirInventarioEquipos() {
        InventarioEquipo ventana = new InventarioEquipo();
        ventana.setVisible(true);
    }

    // Mantenimiento (módulo del equipo)
    private void MantenimientoEquipos() {
        MantenimientoEquipos ventana = new MantenimientoEquipos();
        ventana.setVisible(true);
    }

    private void mostrarMensajeModulo(String modulo) {
        JOptionPane.showMessageDialog(
                panelPrincipal,
                "Abriendo el módulo de: " + modulo,
                "SIGELAB",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void abrirVentana(
            String titulo,
            JPanel panel,
            int ancho,
            int alto
    ) {
        JFrame ventana = new JFrame(titulo);
        ventana.setContentPane(panel);
        ventana.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        Dimension pantalla = Toolkit.getDefaultToolkit().getScreenSize();

        int maxAncho = Math.max(640, pantalla.width - 40);
        int maxAlto = Math.max(480, pantalla.height - 80);

        Dimension minimo = new Dimension(
                Math.min(ancho, maxAncho),
                Math.min(alto, maxAlto)
        );

        ventana.setMinimumSize(minimo);
        ventana.pack();

        int anchoFinal = Math.min(
                Math.max(ventana.getWidth(), minimo.width),
                maxAncho
        );

        int altoFinal = Math.min(
                Math.max(ventana.getHeight(), minimo.height),
                maxAlto
        );

        ventana.setSize(anchoFinal, altoFinal);
        ventana.setLocationRelativeTo(null);
        ventana.setVisible(true);
    }

    private void salir() {
        int respuesta = JOptionPane.showConfirmDialog(
                panelPrincipal,
                "¿Desea salir del sistema?",
                "Confirmar salida",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (respuesta == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    public JPanel getPanelPrincipal() {
        return panelPrincipal;
    }
}
