
import SIGELAB.controller.UsuarioController;
import SIGELAB.enums.RolUsuario;
import SIGELAB.view.LoginForm;

import javax.swing.*;

public class  Main {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            asegurarUsuarioAdministrador();
            iniciarServicioFacialAutomatico();

            LoginForm loginForm = new LoginForm();
            JFrame frame = new JFrame("Sistema de Gestión y Control de Laboratorios");
            frame.setContentPane(loginForm.getPanelPrincipal());
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(430, 300);
            frame.setLocationRelativeTo(null);
            frame.setResizable(false);
            frame.setVisible(true);
        });
    }
    public static void iniciarServicioFacialAutomatico() {
        try {

            // Ruta donde tienes tu script de inicio
            String rutaProyecto = "C:\\Users\\Jose Gonzalez\\CLionProjects\\SIGELAB\\facial-service";

            // Comando para ejecutar PowerShell con el script start-facial.ps1
            ProcessBuilder builder = new ProcessBuilder(
                    "powershell.exe",
                    "-ExecutionPolicy", "Bypass",
                    "-File", ".\\start-facial.ps1"
            );
            builder.directory(new java.io.File(rutaProyecto));

            // Inicia el proceso en segundo plano de manera independiente
            builder.start();
            System.out.println("Servicio facial iniciado automáticamente.");
        } catch (Exception e) {
            System.err.println("No se pudo iniciar el servicio facial automáticamente: " + e.getMessage());
        }
    }
    /** Crea las credenciales iniciales solo si todavía no existen. */
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
