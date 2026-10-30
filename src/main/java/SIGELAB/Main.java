package SIGELAB;

import SIGELAB.controller.UsuarioController;
import SIGELAB.enums.RolUsuario;
import SIGELAB.view.LoginForm;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        // Configurar FlatLaf en Modo Claro con acento azul y bordes redondeados
        try {
            UIManager.put("Component.accentColor", "blue");
            UIManager.put("Button.arc", 10);
            UIManager.put("Component.arc", 8);
            UIManager.put("TextComponent.arc", 8);

            FlatLightLaf.setup();
        } catch (Exception ex) {
            System.err.println("Fallo al inicializar FlatLaf Light: " + ex.getMessage());
        }

        SwingUtilities.invokeLater(() -> {
            asegurarUsuarioAdministrador();
            iniciarServicioFacialAutomatico();

            LoginForm loginForm = new LoginForm();
            JFrame frame = new JFrame("Sistema de Gestión y Control de Laboratorios");
            frame.setContentPane(loginForm.getPanelPrincipal());
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            // 📌 Aumentado el tamaño de la ventana para que la tarjeta respire perfectamente
            frame.setSize(540, 410);

            frame.setLocationRelativeTo(null);
            frame.setResizable(false);
            frame.setVisible(true);
        });
    }

    public static void iniciarServicioFacialAutomatico() {
        try {
            String rutaProyecto = "C:\\Users\\Jose Gonzalez\\CLionProjects\\SIGELAB\\facial-service";
            ProcessBuilder builder = new ProcessBuilder(
                    "powershell.exe",
                    "-ExecutionPolicy", "Bypass",
                    "-File", ".\\start-facial.ps1"
            );
            builder.directory(new java.io.File(rutaProyecto));
            builder.start();
            System.out.println("Servicio facial iniciado automáticamente.");
        } catch (Exception e) {
            System.err.println("No se pudo iniciar el servicio facial automáticamente: " + e.getMessage());
        }
    }

    private static void asegurarUsuarioAdministrador() {
        try {
            UsuarioController controller = new UsuarioController();
            if (controller.buscarPorUsuario("admin").isEmpty()) {
                controller.crearUsuario("Admin", "Sistema", "admin", "1234", RolUsuario.ADMIN, "admin@sigelab.com");
            }
        } catch (RuntimeException e) {
            System.err.println("No fue posible verificar el usuario admin: " + e.getMessage());
        }
    }
}