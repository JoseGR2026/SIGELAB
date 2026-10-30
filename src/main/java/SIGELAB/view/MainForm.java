package SIGELAB.view;

import SIGELAB.controller.PrestamoVencidoController;
import SIGELAB.model.Usuario;

import javax.swing.*;
import java.awt.*;

public class MainForm extends JFrame {

    private final JPanel panelPrincipal;
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
        super("SIGELAB - Panel Principal");
        this.usuarioAutenticado = usuarioAutenticado;

        panelPrincipal = new JPanel(new BorderLayout());
        setContentPane(panelPrincipal);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1050, 650);
        setMinimumSize(new Dimension(850, 550));
        setLocationRelativeTo(null);

        construirInterfazDashboard();
        configurarEventos();
        verificarPrestamosVencidos();
    }

    private void construirInterfazDashboard() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(248, 250, 252));
        sidebar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(226, 232, 240)),
                BorderFactory.createEmptyBorder(20, 15, 20, 15)
        ));
        sidebar.setPreferredSize(new Dimension(240, getHeight()));

        JLabel lblLogo = new JLabel("SIGELAB");
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblLogo.setForeground(new Color(37, 99, 235));
        lblLogo.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(lblLogo);

        JLabel lblSubLogo = new JLabel("Control de Laboratorios");
        lblSubLogo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSubLogo.setForeground(new Color(100, 116, 139));
        lblSubLogo.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(lblSubLogo);

        sidebar.add(Box.createRigidArea(new Dimension(0, 25)));

        btnPrestamos = crearBotonSidebar("Préstamos");
        btnRegistrarDevolucion = crearBotonSidebar("Devoluciones");
        btnInventario = crearBotonSidebar("Inventario");
        btnLaboratorios = crearBotonSidebar("Laboratorios");
        btnMantenimiento = crearBotonSidebar("Mantenimiento");
        btnUsuarios = crearBotonSidebar("Usuarios");
        btnReportes = crearBotonSidebar("Historial / Reportes");
        btnAuditoria = crearBotonSidebar("Auditoría");

        sidebar.add(btnPrestamos);
        sidebar.add(Box.createRigidArea(new Dimension(0, 8)));
        sidebar.add(btnRegistrarDevolucion);
        sidebar.add(Box.createRigidArea(new Dimension(0, 8)));
        sidebar.add(btnInventario);
        sidebar.add(Box.createRigidArea(new Dimension(0, 8)));
        sidebar.add(btnLaboratorios);
        sidebar.add(Box.createRigidArea(new Dimension(0, 8)));
        sidebar.add(btnMantenimiento);
        sidebar.add(Box.createRigidArea(new Dimension(0, 8)));
        sidebar.add(btnUsuarios);
        sidebar.add(Box.createRigidArea(new Dimension(0, 8)));
        sidebar.add(btnReportes);
        sidebar.add(Box.createRigidArea(new Dimension(0, 8)));
        sidebar.add(btnAuditoria);

        panelPrincipal.add(sidebar, BorderLayout.WEST);

        JPanel contenidoPanel = new JPanel(new BorderLayout());
        contenidoPanel.setBackground(Color.WHITE);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)),
                BorderFactory.createEmptyBorder(15, 25, 15, 25)
        ));

        String nombreCompleto = "Administrador";
        String rolTexto = "Administrador";
        if (usuarioAutenticado != null) {
            nombreCompleto = usuarioAutenticado.getNombres() + " " + usuarioAutenticado.getApellidos();
            if (usuarioAutenticado.getRol() != null) {
                rolTexto = usuarioAutenticado.getRol().name();
            }
        }

        lblTitulo = new JLabel("Bienvenido, " + nombreCompleto + " (" + rolTexto + ")");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitulo.setForeground(new Color(15, 23, 42));
        header.add(lblTitulo, BorderLayout.WEST);

        btnSalir = new JButton("Cerrar Sesión");
        btnSalir.putClientProperty("JButton.buttonType", "roundRect");
        btnSalir.putClientProperty("FlatLaf.style", "arc: 8");
        header.add(btnSalir, BorderLayout.EAST);

        contenidoPanel.add(header, BorderLayout.NORTH);

        JPanel centroDashboard = new JPanel(new GridBagLayout());
        centroDashboard.setBackground(new Color(248, 250, 252));

        JPanel tarjetaBienvenida = new JPanel(new GridLayout(2, 1, 5, 5));
        tarjetaBienvenida.setBackground(Color.WHITE);
        tarjetaBienvenida.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1, true),
                BorderFactory.createEmptyBorder(35, 45, 35, 45)
        ));

        JLabel lblBienvenidaTitulo = new JLabel("Sistema de Gestión y Control de Laboratorios", SwingConstants.CENTER);
        lblBienvenidaTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblBienvenidaTitulo.setForeground(new Color(30, 41, 59));

        JLabel lblBienvenidaDesc = new JLabel("Seleccione una opción en el menú lateral izquierdo para operar el sistema.", SwingConstants.CENTER);
        lblBienvenidaDesc.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblBienvenidaDesc.setForeground(new Color(100, 116, 139));

        tarjetaBienvenida.add(lblBienvenidaTitulo);
        tarjetaBienvenida.add(lblBienvenidaDesc);

        centroDashboard.add(tarjetaBienvenida);
        contenidoPanel.add(centroDashboard, BorderLayout.CENTER);

        panelPrincipal.add(contenidoPanel, BorderLayout.CENTER);
    }

    private JButton crearBotonSidebar(String texto) {
        JButton boton = new JButton(texto);
        boton.setAlignmentX(Component.CENTER_ALIGNMENT);
        boton.setMaximumSize(new Dimension(210, 38));
        boton.setPreferredSize(new Dimension(210, 38));
        boton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        boton.setHorizontalAlignment(SwingConstants.LEFT);
        boton.putClientProperty("JButton.buttonType", "roundRect");
        boton.putClientProperty("FlatLaf.style", "arc: 8");
        return boton;
    }

    private void configurarEventos() {
        if (btnInventario != null) btnInventario.addActionListener(e -> abrirInventarioEquipos());
        if (btnLaboratorios != null) btnLaboratorios.addActionListener(e -> abrirCatlogoLaboratorios());
        if (btnMantenimiento != null) btnMantenimiento.addActionListener(e -> MantenimientoEquipos());
        if (btnPrestamos != null) btnPrestamos.addActionListener(e -> abrirPrestamos());
        if (btnRegistrarDevolucion != null) btnRegistrarDevolucion.addActionListener(e -> abrirDevoluciones());
        if (btnReportes != null) btnReportes.addActionListener(e -> abrirHistorialPrestamos());

        if (btnUsuarios != null) {
            btnUsuarios.addActionListener(e -> {
                if (esEncargado()) {
                    mostrarAvisoAdminRestriccion("Gestión de Usuarios");
                    return;
                }
                abrirUsuarios();
            });
        }

        if (btnAuditoria != null) {
            btnAuditoria.addActionListener(e -> {
                if (esEncargado()) {
                    mostrarAvisoAdminRestriccion("Módulo de Auditoría");
                    return;
                }
                abrirRegistroAuditoria();
            });
        }

        if (btnSalir != null) btnSalir.addActionListener(e -> salir());
    }

    private boolean esEncargado() {
        if (usuarioAutenticado == null || usuarioAutenticado.getRol() == null) {
            return false;
        }
        String rol = usuarioAutenticado.getRol().name().trim().toLowerCase();
        return rol.contains("encargado") || rol.equals("user") || rol.equals("empleado");
    }

    private void mostrarAvisoAdminRestriccion(String modulo) {
        JOptionPane.showMessageDialog(
                panelPrincipal,
                "Acceso restringido.\nEl módulo de \"" + modulo + "\" es una opción exclusiva del Administrador.\nPor favor, comunícate con él para realizar esta acción.",
                "SIGELAB - Restricción de Rol",
                JOptionPane.WARNING_MESSAGE
        );
    }

    private void abrirPrestamos() {
        if (!usuarioValido()) { mostrarAdvertenciaSesion(); return; }
        PrestamoView ventana = new PrestamoView(usuarioAutenticado);
        ventana.setLocationRelativeTo(null);
        ventana.setVisible(true);
    }

    private void abrirDevoluciones() {
        if (!usuarioValido()) { mostrarAdvertenciaSesion(); return; }
        DevolucionView ventana = new DevolucionView(usuarioAutenticado);
        ventana.setLocationRelativeTo(null);
        ventana.setVisible(true);
    }

    private void abrirHistorialPrestamos() {
        ReportesView ventana = new ReportesView();
        ventana.setLocationRelativeTo(null);
        ventana.setVisible(true);
    }

    private void abrirRegistroAuditoria() {
        AuditoriaConsultaView ventana = new AuditoriaConsultaView();
        ventana.setLocationRelativeTo(null);
        ventana.setVisible(true);
    }

    private boolean usuarioValido() {
        return usuarioAutenticado != null && usuarioAutenticado.getIdUsuario() > 0;
    }

    private void mostrarAdvertenciaSesion() {
        JOptionPane.showMessageDialog(panelPrincipal, "Debe iniciar sesión para utilizar este módulo.", "SIGELAB", JOptionPane.WARNING_MESSAGE);
    }

    private void verificarPrestamosVencidos() {
        if (!usuarioValido()) return;
        SwingWorker<Integer, Void> worker = new SwingWorker<>() {
            @Override
            protected Integer doInBackground() throws Exception {
                PrestamoVencidoController controller = new PrestamoVencidoController();
                return controller.actualizarVencidos(usuarioAutenticado.getIdUsuario());
            }
            @Override
            protected void done() {
                try {
                    int actualizados = get();
                    if (actualizados > 0) {
                        System.out.println("SIGELAB: " + actualizados + " préstamo(s) marcado(s) como vencido(s).");
                    }
                } catch (Exception ex) {
                    System.err.println("Error al verificar préstamos vencidos: " + ex.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void abrirUsuarios() {
        UsuarioView view = new UsuarioView(usuarioAutenticado);
        view.setVisible(true);
    }

    private void abrirCatlogoLaboratorios() {
        CatalogoLaboratorios ventana = new CatalogoLaboratorios();
        ventana.setVisible(true);
    }

    private void abrirInventarioEquipos() {
        InventarioEquipo ventana = new InventarioEquipo();
        ventana.setVisible(true);
    }

    private void MantenimientoEquipos() {
        MantenimientoEquipos ventana = new MantenimientoEquipos();
        ventana.setVisible(true);
    }

    private void salir() {
        int respuesta = JOptionPane.showConfirmDialog(panelPrincipal, "¿Desea salir del sistema?", "Confirmar salida", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (respuesta == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    public JPanel getPanelPrincipal() {
        return panelPrincipal;
    }
}