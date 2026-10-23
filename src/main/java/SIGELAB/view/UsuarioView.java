package SIGELAB.view;

import SIGELAB.controller.UsuarioController;
import SIGELAB.enums.RolUsuario;
import SIGELAB.integration.facial.FacialAuthClient;
import SIGELAB.integration.facial.FacialClientException;
import SIGELAB.integration.facial.FacialEnrollmentResult;
import SIGELAB.integration.facial.FacialStatus;
import SIGELAB.model.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

/** Pantalla de gestión de usuarios (solo ADMIN). Corregida con integración biométrica. */
public class UsuarioView extends JFrame {

    private JPanel Panelprincipal;
    private JTable tablaUsuarios;
    private JTextField textNombre;
    private JTextField textApellido;
    private JTextField textNombreUsuario;
    private JTextField textCorreo;
    private JComboBox<RolUsuario> comboRol;
    private JPasswordField textContrasena;
    private JTextField textReferenciaFacial;
    private JLabel labelEstado;
    private JButton btNuevo;
    private JButton btGuardar;
    private JButton btDesactivar;
    private JButton btActivar;
    private JButton regresarAlMenuButton;
    private JButton btnRegistrar_Rostro;
    private JButton btn_EliminarRostro;

    private static final String[] COLUMNAS = {"Usuario", "Nombre", "Apellido", "Correo", "Rol", "Estado"};

    private final UsuarioController controladorUsuario = new UsuarioController();
    private final FacialAuthClient facialAuthClient = new FacialAuthClient();
    private final Usuario usuarioAutenticado;
    private Usuario usuarioSeleccionado;
    private boolean perfilFacialRegistrado = false;

    private final DefaultTableModel modeloTabla = new DefaultTableModel(COLUMNAS, 0) {
        @Override
        public boolean isCellEditable(int fila, int columna) {
            return false;
        }
    };

    private List<Usuario> usuariosMostrados = new ArrayList<>();
    private int idUsuarioSeleccionado = 0;

    public UsuarioView(Usuario usuarioActual) {
        super("SIGELAB - Gestión de Usuarios");
        this.usuarioAutenticado = usuarioActual;
        setContentPane(Panelprincipal);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        configurarTabla();
        cargarRoles();
        registrarEventos();
        refrescarTabla();

        setSize(1000, 620);
        setLocationRelativeTo(null);
    }

    private void configurarTabla() {
        tablaUsuarios.setModel(modeloTabla);
        tablaUsuarios.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaUsuarios.getSelectionModel().addListSelectionListener(evento -> {
            if (!evento.getValueIsAdjusting()) {
                alSeleccionarFila();
            }
        });
        btnRegistrar_Rostro.setEnabled(false);
        btn_EliminarRostro.setEnabled(false);
    }

    private void cargarRoles() {
        try {
            comboRol.removeAllItems();
            for (RolUsuario rol : RolUsuario.values()) {
                comboRol.addItem(rol);
            }
            comboRol.setSelectedItem(null);
        } catch (Exception e) {
            mostrarError(e);
        }
    }

    private void registrarEventos() {
        btNuevo.addActionListener(evento -> limpiarFormulario());
        btGuardar.addActionListener(evento -> guardar());
        btDesactivar.addActionListener(evento -> cambiarEstado(false));
        btActivar.addActionListener(evento -> cambiarEstado(true));
        regresarAlMenuButton.addActionListener(evento -> salir());

        btnRegistrar_Rostro.addActionListener(evento -> registrarRostro());
        btn_EliminarRostro.addActionListener(evento -> eliminarRostro());
    }

    private void refrescarTabla() {
        try {
            usuariosMostrados = new ArrayList<>(controladorUsuario.listar());
            modeloTabla.setRowCount(0);
            for (Usuario usuario : usuariosMostrados) {
                String estadoTexto = usuario.isActivo() ? "ACTIVO" : "INACTIVO";
                modeloTabla.addRow(new Object[]{
                        usuario.getUsuario(),
                        usuario.getNombres(),
                        usuario.getApellidos(),
                        usuario.getCorreo(),
                        usuario.getRol(),
                        estadoTexto});
            }
        } catch (Exception e) {
            mostrarError(e);
        }
    }

    private void alSeleccionarFila() {
        int fila = tablaUsuarios.getSelectedRow();
        if (fila < 0 || fila >= usuariosMostrados.size()) {
            return;
        }
        usuarioSeleccionado = usuariosMostrados.get(tablaUsuarios.convertRowIndexToModel(fila));
        idUsuarioSeleccionado = usuarioSeleccionado.getIdUsuario();
        textNombre.setText(usuarioSeleccionado.getNombres());
        textApellido.setText(usuarioSeleccionado.getApellidos());
        textNombreUsuario.setText(usuarioSeleccionado.getUsuario());
        textCorreo.setText(usuarioSeleccionado.getCorreo());
        comboRol.setSelectedItem(usuarioSeleccionado.getRol());
        textContrasena.setText("");

        labelEstado.setText(usuarioSeleccionado.isActivo() ? "ACTIVO" : "INACTIVO");

        // Consultar el estado del perfil facial al seleccionar un usuario
        consultarEstadoRostro(usuarioSeleccionado.getIdUsuario());
    }

    private void limpiarFormulario() {
        idUsuarioSeleccionado = 0;
        usuarioSeleccionado = null;
        perfilFacialRegistrado = false;
        tablaUsuarios.clearSelection();
        textNombre.setText("");
        textApellido.setText("");
        textNombreUsuario.setText("");
        textCorreo.setText("");
        comboRol.setSelectedItem(null);
        textContrasena.setText("");
        labelEstado.setText("-");

        btnRegistrar_Rostro.setEnabled(false);
        btn_EliminarRostro.setEnabled(false);
        btnRegistrar_Rostro.setText("Registrar Rostro");

        textNombre.requestFocusInWindow();
    }

    private void guardar() {
        char[] caracteresContrasena = textContrasena.getPassword();
        String contrasena = new String(caracteresContrasena);
        Arrays.fill(caracteresContrasena, '0');

        try {
            RolUsuario rol = (RolUsuario) comboRol.getSelectedItem();

            if (idUsuarioSeleccionado == 0) {
                controladorUsuario.crearUsuario(
                        textNombre.getText(),
                        textApellido.getText(),
                        textNombreUsuario.getText(),
                        contrasena,
                        rol,
                        textCorreo.getText()
                );
            } else {
                JOptionPane.showMessageDialog(this, "Actualización pendiente de implementar en servicio.", "Usuarios", JOptionPane.INFORMATION_MESSAGE);
            }

            JOptionPane.showMessageDialog(this,
                    "El usuario se guardó correctamente.",
                    "Usuarios", JOptionPane.INFORMATION_MESSAGE);
            limpiarFormulario();
            refrescarTabla();
        } catch (Exception e) {
            mostrarError(e);
        }
    }

    private void cambiarEstado(boolean activar) {
        if (idUsuarioSeleccionado == 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un usuario de la tabla.",
                    "Usuarios", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String accion = activar ? "activar" : "desactivar";
        int respuesta = JOptionPane.showConfirmDialog(this,
                "¿Desea " + accion + " al usuario " + textNombreUsuario.getText() + "?",
                "Confirmar", JOptionPane.YES_NO_OPTION);
        if (respuesta != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            if (activar) {
                controladorUsuario.activar(idUsuarioSeleccionado);
            } else {
                controladorUsuario.desactivar(idUsuarioSeleccionado);
            }
            limpiarFormulario();
            refrescarTabla();
        } catch (Exception e) {
            mostrarError(e);
        }
    }

    private void registrarRostro() {
        if (usuarioSeleccionado == null) {
            mostrarError("Seleccione un usuario.");
            return;
        }
        int userId = usuarioSeleccionado.getIdUsuario();
        boolean reemplazar = perfilFacialRegistrado;
        String accion = reemplazar ? "actualizar" : "registrar";
        if (!confirmar("¿Desea " + accion + " el rostro de " + usuarioSeleccionado.getUsuario() + "?",
                "Registro facial")) return;

        btnRegistrar_Rostro.setEnabled(false);
        btn_EliminarRostro.setEnabled(false);

        new SwingWorker<FacialEnrollmentResult, Void>() {
            @Override
            protected FacialEnrollmentResult doInBackground() {
                return facialAuthClient.enroll(userId, reemplazar);
            }

            @Override
            protected void done() {
                try {
                    FacialEnrollmentResult result = get();
                    JOptionPane.showMessageDialog(Panelprincipal,
                            result.message(), "Registro facial", JOptionPane.INFORMATION_MESSAGE);
                    consultarEstadoRostro(userId);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    mostrarError("El registro facial fue interrumpido.");
                } catch (ExecutionException e) {
                    mostrarError(mensajeError(e.getCause(), "No fue posible registrar el rostro."));
                    consultarEstadoRostro(userId);
                }
            }
        }.execute();
    }

    private void eliminarRostro() {
        if (usuarioSeleccionado == null) {
            mostrarError("Seleccione un usuario.");
            return;
        }
        if (usuarioAutenticado == null || usuarioAutenticado.getRol() != RolUsuario.ADMIN) {
            mostrarError("Solo un administrador puede eliminar perfiles faciales.");
            return;
        }
        int userId = usuarioSeleccionado.getIdUsuario();
        if (!confirmar("¿Desea eliminar el perfil facial de " + usuarioSeleccionado.getUsuario() + "?",
                "Eliminar perfil facial")) return;

        btnRegistrar_Rostro.setEnabled(false);
        btn_EliminarRostro.setEnabled(false);

        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                facialAuthClient.deleteProfile(userId, usuarioAutenticado.getIdUsuario());
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    JOptionPane.showMessageDialog(Panelprincipal,
                            "Perfil facial eliminado correctamente.", "Perfil facial",
                            JOptionPane.INFORMATION_MESSAGE);
                    consultarEstadoRostro(userId);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    mostrarError("La eliminación del perfil fue interrumpida.");
                } catch (ExecutionException e) {
                    mostrarError(mensajeError(e.getCause(), "No fue posible eliminar el perfil facial."));
                    consultarEstadoRostro(userId);
                }
            }
        }.execute();
    }

    private void consultarEstadoRostro(int userId) {
        btnRegistrar_Rostro.setEnabled(false);
        btn_EliminarRostro.setEnabled(false);

        new SwingWorker<FacialStatus, Void>() {
            @Override
            protected FacialStatus doInBackground() {
                return facialAuthClient.status(userId);
            }

            @Override
            protected void done() {
                if (usuarioSeleccionado == null || usuarioSeleccionado.getIdUsuario() != userId) return;
                try {
                    FacialStatus status = get();
                    perfilFacialRegistrado = status.enrolled();
                    btnRegistrar_Rostro.setText(status.enrolled() ? "Actualizar Rostro" : "Registrar Rostro");
                    btnRegistrar_Rostro.setEnabled(true);
                    btn_EliminarRostro.setEnabled(status.enrolled()
                            && usuarioAutenticado != null
                            && usuarioAutenticado.getRol() == RolUsuario.ADMIN);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (ExecutionException e) {
                    perfilFacialRegistrado = false;
                    btnRegistrar_Rostro.setEnabled(true);
                    btnRegistrar_Rostro.setText("Registrar Rostro");
                }
            }
        }.execute();
    }

    private String mensajeError(Throwable error, String defecto) {
        if (error instanceof FacialClientException facial && facial.getMessage() != null) {
            return facial.getMessage();
        }
        return defecto;
    }

    private boolean confirmar(String mensaje, String titulo) {
        return JOptionPane.showConfirmDialog(Panelprincipal, mensaje, titulo,
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION;
    }

    private void mostrarError(Exception e) {
        String mensaje = e.getMessage() != null ? e.getMessage() : "Error desconocido";
        JOptionPane.showMessageDialog(this, mensaje,
                "No se pudo completar la operación", JOptionPane.WARNING_MESSAGE);
    }

    private void mostrarError(String mensaje) {
        if (mensaje == null || mensaje.isBlank()) mensaje = "Ocurrió un error inesperado.";
        JOptionPane.showMessageDialog(Panelprincipal, mensaje, "Usuarios", JOptionPane.ERROR_MESSAGE);
    }

    private void salir() {
        int respuesta = JOptionPane.showConfirmDialog(Panelprincipal,
                "¿Desea regresar al menú principal?", "Regresar",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (respuesta == JOptionPane.YES_OPTION) {
            java.awt.Window ventana = SwingUtilities.getWindowAncestor(Panelprincipal);
            if (ventana != null) ventana.dispose();
        }
    }

    public JPanel getPanelPrincipal() {
        return Panelprincipal;
    }
}