package SIGELAB.view;

import SIGELAB.controller.UsuarioController;
import SIGELAB.integration.facial.FacialAuthClient;
import SIGELAB.integration.facial.FacialClientException;
import SIGELAB.integration.facial.FacialVerificationResult;
import SIGELAB.model.Usuario;

import javax.swing.*;
import java.awt.*;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

/** Pantalla de entrada al sistema (Diseño moderno en código puro). */
public class LoginForm {

    private final JPanel panelPrincipal;
    private final JTextField txtUsuario;
    private final JPasswordField txtPassword;
    private final JButton btnIngresar;
    private final JButton btnSalir;

    private final UsuarioController usuarioController;
    private final FacialAuthClient facialAuthClient;
    private boolean autenticando;

    // 🛠️ Cambia a 'false' cuando quieras activar la verificación facial real con Python.
    private static final boolean OMITIR_FACIAL_EN_PRUEBAS = true;

    public LoginForm() {
        this(new UsuarioController(), new FacialAuthClient());
    }

    public LoginForm(UsuarioController usuarioController) {
        this(usuarioController, new FacialAuthClient());
    }

    public LoginForm(UsuarioController usuarioController, FacialAuthClient facialAuthClient) {
        this.usuarioController = usuarioController;
        this.facialAuthClient = facialAuthClient;

        panelPrincipal = new JPanel(new GridBagLayout());
        txtUsuario = new JTextField();
        txtPassword = new JPasswordField();
        btnIngresar = new JButton("INGRESAR");
        btnSalir = new JButton("Salir");

        inicializarEstilosYDiseno();
        configurarEventos();
    }

    private void inicializarEstilosYDiseno() {
        // Placeholders y tamaños estéticos
        txtUsuario.putClientProperty("JTextField.placeholderText", "Ingrese su usuario o carnet");
        txtPassword.putClientProperty("JTextField.placeholderText", "Ingrese su contraseña");

        txtUsuario.setPreferredSize(new Dimension(230, 32));
        txtPassword.setPreferredSize(new Dimension(230, 32));

        // Estilos oficiales de FlatLaf para botones (Fondo azul vibrante y texto blanco)
        btnIngresar.putClientProperty("FlatLaf.style", "background: #2563eb; foreground: #ffffff; font: bold; arc: 10");
        btnSalir.putClientProperty("FlatLaf.style", "arc: 10");

        btnIngresar.setPreferredSize(new Dimension(115, 36));
        btnSalir.setPreferredSize(new Dimension(95, 36));

        // Panel tarjeta central limpio y con sombra/borde sutil
        JPanel cardPanel = new JPanel(new GridBagLayout());
        cardPanel.setBackground(Color.WHITE);
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(218, 224, 233), 1, true),
                BorderFactory.createEmptyBorder(35, 45, 35, 45)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Título del Sistema
        JLabel lblTitulo = new JLabel("SIGELAB", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitulo.setForeground(new Color(15, 23, 42));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        cardPanel.add(lblTitulo, gbc);

        JLabel lblSub = new JLabel("Control de Acceso a Laboratorios", SwingConstants.CENTER);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSub.setForeground(new Color(100, 116, 139));
        gbc.gridy = 1;
        cardPanel.add(lblSub, gbc);

        // Espaciador
        gbc.insets = new Insets(20, 8, 8, 8);

        // Usuario
        gbc.gridwidth = 1;
        gbc.gridy = 2; gbc.gridx = 0;
        JLabel lblUsr = new JLabel("Usuario:");
        lblUsr.setFont(new Font("Segoe UI", Font.BOLD, 13));
        cardPanel.add(lblUsr, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        cardPanel.add(txtUsuario, gbc);
        gbc.weightx = 0.0;

        // Contraseña
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.gridy = 3; gbc.gridx = 0;
        JLabel lblPwd = new JLabel("Contraseña:");
        lblPwd.setFont(new Font("Segoe UI", Font.BOLD, 13));
        cardPanel.add(lblPwd, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        cardPanel.add(txtPassword, gbc);
        gbc.weightx = 0.0;

        // Panel de Botones inferior
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        panelBotones.setOpaque(false);
        panelBotones.add(btnIngresar);
        panelBotones.add(btnSalir);

        gbc.gridy = 4; gbc.gridx = 0; gbc.gridwidth = 2;
        gbc.insets = new Insets(22, 8, 5, 8);
        cardPanel.add(panelBotones, gbc);

        // Fondo general gris claro muy suave
        panelPrincipal.setBackground(new Color(241, 245, 249));
        panelPrincipal.add(cardPanel, new GridBagConstraints());
    }

    private void configurarEventos() {
        btnIngresar.addActionListener(e -> ingresar());
        btnSalir.addActionListener(e -> System.exit(0));
        txtPassword.addActionListener(e -> ingresar());
        txtUsuario.addActionListener(e -> txtPassword.requestFocus());
    }

    private void ingresar() {
        if (autenticando) return;
        String usuario = txtUsuario.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (usuario.isBlank()) {
            mostrarError("Ingrese el usuario.");
            txtUsuario.requestFocus();
            return;
        }
        if (password.isBlank()) {
            mostrarError("Ingrese la contraseña.");
            txtPassword.requestFocus();
            return;
        }

        autenticando = true;
        btnIngresar.setEnabled(false);
        btnIngresar.setText("Verificando...");

        new SwingWorker<LoginOutcome, Void>() {
            @Override
            protected LoginOutcome doInBackground() {
                Optional<Usuario> autenticado = usuarioController.autenticar(usuario, password);
                if (autenticado.isEmpty()) return LoginOutcome.sinCredenciales();

                FacialVerificationResult facial;
                if (OMITIR_FACIAL_EN_PRUEBAS) {
                    facial = new FacialVerificationResult(true, true, 0, null, 0.0, true, 0, "Omitido por modo de pruebas locales.");
                } else {
                    facial = facialAuthClient.verify(autenticado.get().getIdUsuario());
                }
                return new LoginOutcome(autenticado.get(), facial);
            }

            @Override
            protected void done() {
                try {
                    LoginOutcome outcome = get();
                    if (outcome.credencialesInvalidas()) {
                        mostrarError("Usuario o contraseña incorrectos, o usuario inactivo.");
                        txtPassword.selectAll();
                        txtPassword.requestFocus();
                    } else if (!outcome.facial().matched()) {
                        mostrarError(outcome.facial().message());
                        txtPassword.selectAll();
                        txtPassword.requestFocus();
                    } else {
                        abrirMenu(outcome.usuario());
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    mostrarError("La verificación facial fue interrumpida.");
                } catch (ExecutionException e) {
                    Throwable cause = e.getCause();
                    if (cause instanceof FacialClientException facialError) {
                        mostrarError(facialError.getMessage());
                    } else if (cause instanceof RuntimeException runtimeError) {
                        mostrarError(runtimeError.getMessage());
                    } else {
                        mostrarError("No fue posible completar la autenticación.");
                    }
                } finally {
                    autenticando = false;
                    btnIngresar.setEnabled(true);
                    btnIngresar.setText("INGRESAR");
                }
            }
        }.execute();
    }

    private void abrirMenu(Usuario usuario) {
        Window ventanaActual = SwingUtilities.getWindowAncestor(panelPrincipal);
        if (ventanaActual != null) ventanaActual.dispose();

        MainForm mainForm = new MainForm(usuario);
        JFrame ventana = new JFrame("Sistema de Gestión y Control de Laboratorios");
        ventana.setContentPane(mainForm.getPanelPrincipal());
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ventana.setSize(750, 650);
        ventana.setLocationRelativeTo(null);
        ventana.setResizable(false);
        ventana.setVisible(true);
    }

    private void mostrarError(String mensaje) {
        if (mensaje == null || mensaje.isBlank()) mensaje = "Ocurrió un error inesperado.";
        JOptionPane.showMessageDialog(panelPrincipal, mensaje, "Inicio de sesión", JOptionPane.ERROR_MESSAGE);
    }

    public JPanel getPanelPrincipal() {
        return panelPrincipal;
    }

    private record LoginOutcome(Usuario usuario, FacialVerificationResult facial,
                                boolean credencialesInvalidas) {
        private LoginOutcome(Usuario usuario, FacialVerificationResult facial) {
            this(usuario, facial, false);
        }

        private static LoginOutcome sinCredenciales() {
            return new LoginOutcome(null, null, true);
        }
    }
}