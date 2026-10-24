package SIGELAB.view;

import SIGELAB.model.Usuario;

import javax.swing.*;
import java.awt.*;

/** Menú principal de la aplicación SIGELAB. */
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
    }

    private void configurarFormulario() {
        String nombreCompleto = "Administrador";
        if (usuarioAutenticado != null) {
            nombreCompleto = usuarioAutenticado.getNombres() + " " + usuarioAutenticado.getApellidos();
        }
        lblTitulo.setText("Bienvenido, Administrador: " + nombreCompleto);
    }

    private void configurarEventos() {
        // Enlazamos el botón de inventario con tu nueva clase InventarioEquipo
        btnInventario.addActionListener(e -> abrirInventarioEquipos());

        btnPrestamos.addActionListener(e -> mostrarMensajeModulo("Préstamos de Equipos"));
        btnRegistrarDevolucion.addActionListener(e -> mostrarMensajeModulo("Registrar Devolución"));
        btnMantenimiento.addActionListener(e -> mostrarMensajeModulo("Equipos en Mantenimiento"));
        btnLaboratorios.addActionListener(e -> abrirCatlogoLaboratorios());
        btnUsuarios.addActionListener(e -> abrirUsuarios());
        btnAuditoria.addActionListener(e -> mostrarMensajeModulo("Registro de Auditoría"));
        btnReportes.addActionListener(e -> mostrarMensajeModulo("Reportes y Consultas"));
        btnSalir.addActionListener(e -> salir());
    }

    private void abrirUsuarios() {
        UsuarioView view = new UsuarioView(usuarioAutenticado);
        abrirVentana("Administración de Usuarios", view.getPanelPrincipal(), 1050, 650);
    }

    private void abrirCatlogoLaboratorios() {
        CatalogoLaboratorios ventana = new CatalogoLaboratorios();
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

    private void abrirInventarioEquipos() {
        InventarioEquipo ventana = new InventarioEquipo();
        ventana.setVisible(true);
    }

    /** Abre cualquier catálogo dentro de una ventana secundaria de forma segura. */
    private void abrirVentana(String titulo, JPanel panel, int ancho, int alto) {
        JFrame ventana = new JFrame(titulo);
        ventana.setContentPane(panel);
        ventana.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        Dimension pantalla = Toolkit.getDefaultToolkit().getScreenSize();

        // CORREGIDO: Se agregaron los operadores de resta (-) que faltaban
        int maxAncho = Math.max(640, pantalla.width - 40);
        int maxAlto = Math.max(480, pantalla.height - 80);

        Dimension minimo = new Dimension(Math.min(ancho, maxAncho), Math.min(alto, maxAlto));
        ventana.setMinimumSize(minimo);
        ventana.pack();
        int anchoFinal = Math.min(Math.max(ventana.getWidth(), minimo.width), maxAncho);
        int altoFinal = Math.min(Math.max(ventana.getHeight(), minimo.height), maxAlto);
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
                JOptionPane.QUESTION_MESSAGE);

        if (respuesta == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    public JPanel getPanelPrincipal() {
        return panelPrincipal;
    }
}