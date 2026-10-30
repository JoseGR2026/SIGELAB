package SIGELAB.view;

import SIGELAB.controller.UsuarioController;
import SIGELAB.enums.RolUsuario;
import SIGELAB.integration.facial.FacialAuthClient;
import SIGELAB.integration.facial.FacialClientException;
import SIGELAB.integration.facial.FacialEnrollmentResult;
import SIGELAB.integration.facial.FacialStatus;
import SIGELAB.model.Usuario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class UsuarioView extends JFrame {

    private JPanel panelPrincipal;
    private JTable tablaUsuarios;
    private JTextField textNombre;
    private JTextField textApellido;
    private JTextField textNombreUsuario;
    private JTextField textCorreo;
    private JComboBox<RolUsuario> comboRol;
    private JPasswordField textContrasena;
    private JLabel labelEstado;
    private JButton btNuevo;
    private JButton btGuardar;
    private JButton btActualizar;
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

        construirDiseñoModerno();

        setContentPane(panelPrincipal);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(1150, 720);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);

        configurarTabla();
        cargarRoles();
        registrarEventos();
        refrescarTabla();
        limpiarFormulario();
    }

    private void construirDiseñoModerno() {
        panelPrincipal = new JPanel(new BorderLayout(15, 15));
        panelPrincipal.setBorder(new EmptyBorder(20, 20, 20, 20));
        panelPrincipal.setBackground(new Color(248, 250, 252));

        // --- ENCABEZADO ---
        JPanel panelNorte = new JPanel(new BorderLayout());
        panelNorte.setOpaque(false);

        JLabel lblTitulo = new JLabel("Gestión de Usuarios del Sistema");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setForeground(new Color(15, 23, 42));

        JLabel lblSubtitulo = new JLabel("Administre las cuentas de acceso, roles y perfiles biométricos faciales.");
        lblSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSubtitulo.setForeground(new Color(100, 116, 139));

        JPanel panelTitulos = new JPanel(new GridLayout(2, 1, 0, 2));
        panelTitulos.setOpaque(false);
        panelTitulos.add(lblTitulo);
        panelTitulos.add(lblSubtitulo);
        panelNorte.add(panelTitulos, BorderLayout.WEST);

        regresarAlMenuButton = new JButton("Menú Principal");
        estilizarBoton(regresarAlMenuButton, false);
        JPanel panelBotonRegresar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        panelBotonRegresar.setOpaque(false);
        panelBotonRegresar.add(regresarAlMenuButton);
        panelNorte.add(panelBotonRegresar, BorderLayout.EAST);

        panelPrincipal.add(panelNorte, BorderLayout.NORTH);

        // --- CONTENIDO CENTRAL (SplitPane Tabla / Formulario) ---
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setResizeWeight(0.58);
        splitPane.setDividerSize(8);
        splitPane.setOpaque(false);
        splitPane.setBorder(null);

        // 1. Panel Izquierdo: Tabla estilizada
        tablaUsuarios = new JTable(modeloTabla);
        tablaUsuarios.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaUsuarios.setRowHeight(30);
        tablaUsuarios.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tablaUsuarios.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tablaUsuarios.getTableHeader().setBackground(new Color(241, 245, 249));
        tablaUsuarios.getTableHeader().setForeground(new Color(51, 65, 85));

        JScrollPane scrollTabla = new JScrollPane(tablaUsuarios);
        scrollTabla.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225)),
                " Lista de Usuarios Registrados ",
                0, 0, new Font("Segoe UI", Font.BOLD, 13), new Color(37, 99, 235)
        ));
        scrollTabla.getViewport().setBackground(Color.WHITE);
        splitPane.setLeftComponent(scrollTabla);

        // 2. Panel Derecho: Formulario y Acciones
        JPanel panelDerecho = new JPanel(new BorderLayout(0, 15));
        panelDerecho.setOpaque(false);

        // Formulario de campos
        JPanel panelFormulario = new JPanel(new GridBagLayout());
        panelFormulario.setBackground(Color.WHITE);
        panelFormulario.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(new Color(203, 213, 225)),
                        " Detalles del Usuario ",
                        0, 0, new Font("Segoe UI", Font.BOLD, 13), new Color(37, 99, 235)
                ),
                new EmptyBorder(10, 10, 10, 10)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        textNombre = new JTextField(15);
        textApellido = new JTextField(15);
        textNombreUsuario = new JTextField(15);
        textCorreo = new JTextField(15);
        comboRol = new JComboBox<>();
        textContrasena = new JPasswordField(15);
        labelEstado = new JLabel("ACTIVO");
        labelEstado.setFont(new Font("Segoe UI", Font.BOLD, 13));
        labelEstado.setForeground(new Color(22, 163, 74));

        int fila = 0;
        agregarCampoForm(panelFormulario, gbc, fila++, new JLabel("Nombre:"), textNombre);
        agregarCampoForm(panelFormulario, gbc, fila++, new JLabel("Apellido:"), textApellido);
        agregarCampoForm(panelFormulario, gbc, fila++, new JLabel("Username:"), textNombreUsuario);
        agregarCampoForm(panelFormulario, gbc, fila++, new JLabel("Correo:"), textCorreo);
        agregarCampoForm(panelFormulario, gbc, fila++, new JLabel("Rol:"), comboRol);
        agregarCampoForm(panelFormulario, gbc, fila++, new JLabel("Contraseña:"), textContrasena);
        agregarCampoForm(panelFormulario, gbc, fila++, new JLabel("Estado:"), labelEstado);

        panelDerecho.add(panelFormulario, BorderLayout.CENTER);

        // Panel inferior de Botones de Acción y Biometría
        JPanel panelContenedorBotones = new JPanel(new GridLayout(4, 2, 10, 10));
        panelContenedorBotones.setOpaque(false);
        panelContenedorBotones.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225)),
                " Acciones y Biometría ",
                0, 0, new Font("Segoe UI", Font.BOLD, 13), new Color(37, 99, 235)
        ));

        btNuevo = new JButton("Nuevo");
        btGuardar = new JButton("Guardar");
        btActualizar = new JButton("Actualizar");
        btActivar = new JButton("Activar");
        btDesactivar = new JButton("Desactivar");
        btnRegistrar_Rostro = new JButton("Registrar Rostro");
        btn_EliminarRostro = new JButton("Eliminar Rostro");

        estilizarBoton(btGuardar, true);
        estilizarBoton(btNuevo, false);
        estilizarBoton(btActualizar, false);
        estilizarBoton(btActivar, false);
        estilizarBoton(btDesactivar, false);
        estilizarBoton(btnRegistrar_Rostro, false);
        estilizarBoton(btn_EliminarRostro, false);

        panelContenedorBotones.add(btNuevo);
        panelContenedorBotones.add(btGuardar);
        panelContenedorBotones.add(btActualizar);
        panelContenedorBotones.add(btActivar);
        panelContenedorBotones.add(btDesactivar);
        panelContenedorBotones.add(new JLabel(""));
        panelContenedorBotones.add(btnRegistrar_Rostro);
        panelContenedorBotones.add(btn_EliminarRostro);

        panelDerecho.add(panelContenedorBotones, BorderLayout.SOUTH);
        splitPane.setRightComponent(panelDerecho);

        panelPrincipal.add(splitPane, BorderLayout.CENTER);
    }

    private void agregarCampoForm(JPanel panel, GridBagConstraints gbc, int fila, JLabel label, JComponent campo) {
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(new Color(71, 85, 105));
        gbc.gridx = 0; gbc.gridy = fila; gbc.weightx = 0.0;
        panel.add(label, gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(campo, gbc);
    }

    private void estilizarBoton(JButton boton, boolean esPrincipal) {
        if (boton == null) return;
        boton.setFocusPainted(false);
        boton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        boton.putClientProperty("JButton.buttonType", "roundRect");
        if (esPrincipal) {
            boton.putClientProperty("FlatLaf.style", "background: #2563eb; foreground: #ffffff; arc: 8");
        } else {
            boton.putClientProperty("FlatLaf.style", "arc: 8");
        }
    }

    private void configurarTabla() {
        if (tablaUsuarios != null) {
            tablaUsuarios.setModel(modeloTabla);
            tablaUsuarios.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            tablaUsuarios.getSelectionModel().addListSelectionListener(evento -> {
                if (!evento.getValueIsAdjusting()) {
                    alSeleccionarFila();
                }
            });
        }
        if (btnRegistrar_Rostro != null) btnRegistrar_Rostro.setEnabled(false);
        if (btn_EliminarRostro != null) btn_EliminarRostro.setEnabled(false);
        if (btActualizar != null) btActualizar.setEnabled(false);
    }

    private void cargarRoles() {
        if (comboRol == null) return;
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
        if (btNuevo != null) btNuevo.addActionListener(evento -> limpiarFormulario());
        if (btGuardar != null) btGuardar.addActionListener(evento -> guardar());
        if (btActualizar != null) btActualizar.addActionListener(evento -> actualizarUsuario());
        if (btDesactivar != null) btDesactivar.addActionListener(evento -> cambiarEstado(false));
        if (btActivar != null) btActivar.addActionListener(evento -> cambiarEstado(true));
        if (regresarAlMenuButton != null) regresarAlMenuButton.addActionListener(evento -> salir());

        if (btnRegistrar_Rostro != null) btnRegistrar_Rostro.addActionListener(evento -> registrarRostro());
        if (btn_EliminarRostro != null) btn_EliminarRostro.addActionListener(evento -> eliminarRostro());
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
        if (tablaUsuarios == null) return;
        int fila = tablaUsuarios.getSelectedRow();
        if (fila < 0 || fila >= usuariosMostrados.size()) {
            return;
        }
        usuarioSeleccionado = usuariosMostrados.get(tablaUsuarios.convertRowIndexToModel(fila));
        idUsuarioSeleccionado = usuarioSeleccionado.getIdUsuario();

        if (textNombre != null) textNombre.setText(usuarioSeleccionado.getNombres());
        if (textApellido != null) textApellido.setText(usuarioSeleccionado.getApellidos());
        if (textNombreUsuario != null) textNombreUsuario.setText(usuarioSeleccionado.getUsuario());
        if (textCorreo != null) textCorreo.setText(usuarioSeleccionado.getCorreo());
        if (comboRol != null) comboRol.setSelectedItem(usuarioSeleccionado.getRol());
        if (textContrasena != null) textContrasena.setText("");

        if (labelEstado != null) {
            boolean activo = usuarioSeleccionado.isActivo();
            labelEstado.setText(activo ? "ACTIVO" : "INACTIVO");
            labelEstado.setForeground(activo ? new Color(22, 163, 74) : new Color(220, 38, 38));
        }

        if (btGuardar != null) btGuardar.setEnabled(false);
        if (btActualizar != null) btActualizar.setEnabled(true);

        consultarEstadoRostro(usuarioSeleccionado.getIdUsuario());
    }

    private void limpiarFormulario() {
        idUsuarioSeleccionado = 0;
        usuarioSeleccionado = null;
        perfilFacialRegistrado = false;
        if (tablaUsuarios != null) tablaUsuarios.clearSelection();
        if (textNombre != null) textNombre.setText("");
        if (textApellido != null) textApellido.setText("");
        if (textNombreUsuario != null) textNombreUsuario.setText("");
        if (textCorreo != null) textCorreo.setText("");
        if (comboRol != null) comboRol.setSelectedItem(null);
        if (textContrasena != null) textContrasena.setText("");

        if (labelEstado != null) {
            labelEstado.setText("ACTIVO");
            labelEstado.setForeground(new Color(22, 163, 74));
        }

        if (btGuardar != null) btGuardar.setEnabled(true);
        if (btActualizar != null) btActualizar.setEnabled(false);

        if (btnRegistrar_Rostro != null) {
            btnRegistrar_Rostro.setEnabled(false);
            btnRegistrar_Rostro.setText("Registrar Rostro");
        }
        if (btn_EliminarRostro != null) btn_EliminarRostro.setEnabled(false);

        if (textNombre != null) textNombre.requestFocusInWindow();
    }

    private void guardar() {
        if (textContrasena == null) return;
        char[] caracteresContrasena = textContrasena.getPassword();
        String contrasena = new String(caracteresContrasena);
        Arrays.fill(caracteresContrasena, '0');

        try {
            RolUsuario rol = comboRol != null ? (RolUsuario) comboRol.getSelectedItem() : null;

            if (idUsuarioSeleccionado == 0) {
                controladorUsuario.crearUsuario(
                        textNombre.getText(),
                        textApellido.getText(),
                        textNombreUsuario.getText(),
                        contrasena,
                        rol,
                        textCorreo.getText()
                );

                List<Usuario> listaActualizada = controladorUsuario.listar();
                for (Usuario u : listaActualizada) {
                    if (u.getUsuario().equalsIgnoreCase(textNombreUsuario.getText().trim())) {
                        if (!u.isActivo()) {
                            controladorUsuario.activar(u.getIdUsuario());
                        }
                        break;
                    }
                }

                JOptionPane.showMessageDialog(this,
                        "El usuario se guardó correctamente y está activo.",
                        "Usuarios", JOptionPane.INFORMATION_MESSAGE);
                limpiarFormulario();
                refrescarTabla();
            }
        } catch (Exception e) {
            mostrarError(e);
        }
    }

    private void actualizarUsuario() {
        if (idUsuarioSeleccionado == 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un usuario de la tabla para actualizar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        char[] caracteresContrasena = textContrasena.getPassword();
        String contrasena = new String(caracteresContrasena);
        Arrays.fill(caracteresContrasena, '0');

        try {
            RolUsuario rol = comboRol != null ? (RolUsuario) comboRol.getSelectedItem() : null;

            usuarioSeleccionado.setNombres(textNombre.getText().trim());
            usuarioSeleccionado.setApellidos(textApellido.getText().trim());
            usuarioSeleccionado.setUsuario(textNombreUsuario.getText().trim());
            usuarioSeleccionado.setCorreo(textCorreo.getText().trim());
            usuarioSeleccionado.setRol(rol);

            if (!contrasena.isEmpty()) {
                usuarioSeleccionado.setPasswordHash(contrasena);
            }

            controladorUsuario.actualizar(usuarioSeleccionado);

            JOptionPane.showMessageDialog(this,
                    "El usuario y la contraseña se actualizaron correctamente en la base de datos.",
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
        String nombreUsr = textNombreUsuario != null ? textNombreUsuario.getText() : "";
        int respuesta = JOptionPane.showConfirmDialog(this,
                "¿Desea " + accion + " al usuario " + nombreUsr + "?",
                "Confirmar", JOptionPane.YES_NO_OPTION);
        if (respuesta != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            if (activar) {
                controladorUsuario.activar(idUsuarioSeleccionado);
            } else {
                int operadorId = (usuarioAutenticado != null) ? usuarioAutenticado.getIdUsuario() : idUsuarioSeleccionado;
                controladorUsuario.desactivar(idUsuarioSeleccionado, operadorId);
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

        if (btnRegistrar_Rostro != null) btnRegistrar_Rostro.setEnabled(false);
        if (btn_EliminarRostro != null) btn_EliminarRostro.setEnabled(false);

        new SwingWorker<FacialEnrollmentResult, Void>() {
            @Override
            protected FacialEnrollmentResult doInBackground() {
                return facialAuthClient.enroll(userId, reemplazar);
            }

            @Override
            protected void done() {
                try {
                    FacialEnrollmentResult result = get();
                    JOptionPane.showMessageDialog(panelPrincipal,
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

        if (btnRegistrar_Rostro != null) btnRegistrar_Rostro.setEnabled(false);
        if (btn_EliminarRostro != null) btn_EliminarRostro.setEnabled(false);

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
                    JOptionPane.showMessageDialog(panelPrincipal,
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
        if (btnRegistrar_Rostro != null) btnRegistrar_Rostro.setEnabled(false);
        if (btn_EliminarRostro != null) btn_EliminarRostro.setEnabled(false);

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
                    if (btnRegistrar_Rostro != null) {
                        btnRegistrar_Rostro.setText(status.enrolled() ? "Actualizar Rostro" : "Registrar Rostro");
                        btnRegistrar_Rostro.setEnabled(true);
                    }
                    if (btn_EliminarRostro != null) {
                        btn_EliminarRostro.setEnabled(status.enrolled()
                                && usuarioAutenticado != null
                                && usuarioAutenticado.getRol() == RolUsuario.ADMIN);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (ExecutionException e) {
                    perfilFacialRegistrado = false;
                    if (btnRegistrar_Rostro != null) {
                        btnRegistrar_Rostro.setEnabled(true);
                        btnRegistrar_Rostro.setText("Registrar Rostro");
                    }
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
        return JOptionPane.showConfirmDialog(panelPrincipal, mensaje, titulo,
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION;
    }

    private void mostrarError(Exception e) {
        String mensaje = e.getMessage() != null ? e.getMessage() : "Error desconocido";
        JOptionPane.showMessageDialog(this, mensaje,
                "No se pudo completar la operación", JOptionPane.WARNING_MESSAGE);
    }

    private void mostrarError(String mensaje) {
        if (mensaje == null || mensaje.isBlank()) mensaje = "Ocurrió un error inesperado.";
        JOptionPane.showMessageDialog(panelPrincipal, mensaje, "Usuarios", JOptionPane.ERROR_MESSAGE);
    }

    private void salir() {
        int respuesta = JOptionPane.showConfirmDialog(panelPrincipal,
                "¿Desea regresar al menú principal?", "Regresar",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (respuesta == JOptionPane.YES_OPTION) {
            Window ventana = SwingUtilities.getWindowAncestor(panelPrincipal);
            if (ventana != null) ventana.dispose();
        }
    }

    public JPanel getPanelPrincipal() {
        return panelPrincipal;
    }
}