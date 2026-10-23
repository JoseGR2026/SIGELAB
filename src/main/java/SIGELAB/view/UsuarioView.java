package SIGELAB.view;

import SIGELAB.controller.UsuarioController;
import SIGELAB.enums.RolUsuario;
import SIGELAB.model.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Pantalla de gestión de usuarios (solo ADMIN). Corregida. */
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

    private static final String[] COLUMNAS = {"Usuario", "Nombre", "Apellido", "Correo", "Rol", "Estado"};

    private final UsuarioController controladorUsuario = new UsuarioController();

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
        labelEstado.setText("-");
    }

    private void refrescarTabla() {
        try {
            usuariosMostrados = new ArrayList<>(controladorUsuario.listar());
            modeloTabla.setRowCount(0);
            for (Usuario usuario : usuariosMostrados) {
                // Se evalúa si está activo para mostrar el estado en texto
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
        Usuario usuario = usuariosMostrados.get(tablaUsuarios.convertRowIndexToModel(fila));
        idUsuarioSeleccionado = usuario.getIdUsuario();
        textNombre.setText(usuario.getNombres());
        textApellido.setText(usuario.getApellidos());
        textNombreUsuario.setText(usuario.getUsuario());
        textCorreo.setText(usuario.getCorreo());
        comboRol.setSelectedItem(usuario.getRol());
        textContrasena.setText("");

        // Manejo seguro por si no existe el campo de referencia facial en el modelo
        textReferenciaFacial.setText("");

        labelEstado.setText(usuario.isActivo() ? "ACTIVO" : "INACTIVO");
    }

    private void limpiarFormulario() {
        idUsuarioSeleccionado = 0;
        tablaUsuarios.clearSelection();
        textNombre.setText("");
        textApellido.setText("");
        textNombreUsuario.setText("");
        textCorreo.setText("");
        comboRol.setSelectedItem(null);
        textContrasena.setText("");
        textReferenciaFacial.setText("");
        labelEstado.setText("-");
        textNombre.requestFocusInWindow();
    }

    private void guardar() {
        char[] caracteresContrasena = textContrasena.getPassword();
        String contrasena = new String(caracteresContrasena);
        Arrays.fill(caracteresContrasena, '0');

        try {
            RolUsuario rol = (RolUsuario) comboRol.getSelectedItem();

            if (idUsuarioSeleccionado == 0) {
                // Pasamos todos los parámetros requeridos por el controlador en orden
                controladorUsuario.crearUsuario(
                        textNombre.getText(),
                        textApellido.getText(),
                        textNombreUsuario.getText(),
                        contrasena,
                        rol,
                        textCorreo.getText(),
                        textReferenciaFacial.getText()
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
                // Si tu controlador no tiene un método 'activar', puedes manejarlo mediante actualización o agregarlo en el controlador
                controladorUsuario.activar(idUsuarioSeleccionado); // Ajustar según los métodos disponibles en tu controlador
            } else {
                controladorUsuario.desactivar(idUsuarioSeleccionado);
            }
            limpiarFormulario();
            refrescarTabla();
        } catch (Exception e) {
            mostrarError(e);
        }
    }

    private void mostrarError(Exception e) {
        String mensaje = e.getMessage() != null ? e.getMessage() : "Error desconocido";
        JOptionPane.showMessageDialog(this, mensaje,
                "No se pudo completar la operación", JOptionPane.WARNING_MESSAGE);
    }

    public JPanel getPanelPrincipal() {
        return Panelprincipal;
    }
}