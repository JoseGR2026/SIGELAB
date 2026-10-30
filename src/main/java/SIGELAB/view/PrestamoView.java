package SIGELAB.view;

import SIGELAB.controller.PrestamoController;
import SIGELAB.model.EstadoEquipo;
import SIGELAB.model.Usuario;
import SIGELAB.repository.EstadoEquipoRepository;
import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PrestamoView extends JFrame {

    private final Usuario usuarioAutenticado;
    private final PrestamoController controller;
    private final EstadoEquipoRepository equipoRepository;

    private JTextField txtSolicitante;
    private JTextField txtEscanerQR;
    private JButton btnEscanearAccion;
    private JButton btnCargarQRArchivo;
    private JDateChooser dateChooserDevolucion;
    private JTextArea txtObservaciones;

    private JTable tablaDisponibles;
    private JTable tablaSeleccionados;

    private DefaultTableModel modeloDisponibles;
    private DefaultTableModel modeloSeleccionados;

    private final List<Integer> equiposSeleccionados = new ArrayList<>();

    public PrestamoView(Usuario usuarioAutenticado) {
        this.usuarioAutenticado = usuarioAutenticado;
        this.controller = new PrestamoController();
        this.equipoRepository = new EstadoEquipoRepository();

        configurarVentana();
        construirDiseñoModerno();
        cargarEquiposInventario();
    }

    private void configurarVentana() {
        setTitle("SIGELAB - Registro de Préstamos");
        setSize(1150, 750);
        setMinimumSize(new Dimension(950, 650));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    private void construirDiseñoModerno() {
        JPanel principal = new JPanel(new BorderLayout(15, 15));
        principal.setBorder(new EmptyBorder(20, 20, 20, 20));
        principal.setBackground(new Color(248, 250, 252));

        // --- ENCABEZADO ---
        JPanel panelNorte = new JPanel(new BorderLayout());
        panelNorte.setOpaque(false);

        JLabel lblTitulo = new JLabel("Registro de Préstamos de Equipos");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setForeground(new Color(15, 23, 42));

        JLabel lblSubtitulo = new JLabel("Gestión de salidas de dispositivos y control de devoluciones.");
        lblSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSubtitulo.setForeground(new Color(100, 116, 139));

        JPanel panelTitulos = new JPanel(new GridLayout(2, 1, 0, 2));
        panelTitulos.setOpaque(false);
        panelTitulos.add(lblTitulo);
        panelTitulos.add(lblSubtitulo);
        panelNorte.add(panelTitulos, BorderLayout.WEST);

        principal.add(panelNorte, BorderLayout.NORTH);

        // --- CONTENEDOR SUPERIOR (Filtro QR y Formulario) ---
        JPanel panelContenedorSuperior = new JPanel(new BorderLayout(0, 12));
        panelContenedorSuperior.setOpaque(false);

        // Panel Filtro / QR
        JPanel panelQR = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panelQR.setBackground(Color.WHITE);
        panelQR.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225)),
                new EmptyBorder(8, 8, 8, 8)
        ));

        JLabel lblQR = new JLabel("Buscar equipo:");
        lblQR.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblQR.setForeground(new Color(71, 85, 105));

        txtEscanerQR = new JTextField(22);
        txtEscanerQR.putClientProperty("JTextField.placeholderText", "Filtrar por código o nombre...");
        txtEscanerQR.setPreferredSize(new Dimension(210, 30));

        btnEscanearAccion = new JButton("Filtrar");
        btnEscanearAccion.setPreferredSize(new Dimension(95, 30));
        estilizarBoton(btnEscanearAccion, false);

        btnCargarQRArchivo = new JButton("Cargar QR");
        btnCargarQRArchivo.setPreferredSize(new Dimension(120, 30));
        estilizarBoton(btnCargarQRArchivo, false);

        panelQR.add(lblQR);
        panelQR.add(txtEscanerQR);
        panelQR.add(btnEscanearAccion);
        panelQR.add(btnCargarQRArchivo);
        panelContenedorSuperior.add(panelQR, BorderLayout.NORTH);

        // Formulario de Datos del Préstamo (GridBagLayout para aspecto limpio y profesional)
        JPanel formulario = new JPanel(new GridBagLayout());
        formulario.setBackground(Color.WHITE);
        formulario.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(new Color(203, 213, 225)),
                        " Datos del Préstamo ",
                        0, 0, new Font("Segoe UI", Font.BOLD, 13), new Color(37, 99, 235)
                ),
                new EmptyBorder(10, 10, 10, 10)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 10, 6, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        txtSolicitante = new JTextField(25);

        dateChooserDevolucion = new JDateChooser();
        dateChooserDevolucion.setDateFormatString("dd/MM/yyyy");
        dateChooserDevolucion.setDate(java.sql.Date.valueOf(LocalDate.now().plusDays(1)));
        ((JTextField) dateChooserDevolucion.getDateEditor()).setEditable(false);

        txtObservaciones = new JTextArea(2, 25);
        txtObservaciones.setLineWrap(true);
        txtObservaciones.setWrapStyleWord(true);
        JScrollPane scrollObs = new JScrollPane(txtObservaciones);

        int fila = 0;
        agregarCampoForm(formulario, gbc, fila++, new JLabel("Solicitante:"), txtSolicitante);
        agregarCampoForm(formulario, gbc, fila++, new JLabel("Devolución esperada:"), dateChooserDevolucion);
        agregarCampoForm(formulario, gbc, fila++, new JLabel("Observaciones:"), scrollObs);

        panelContenedorSuperior.add(formulario, BorderLayout.CENTER);
        principal.add(panelContenedorSuperior, BorderLayout.NORTH);

        // --- TABLAS CENTRALES ---
        modeloDisponibles = new DefaultTableModel(new Object[]{"ID", "Código", "Equipo", "Estado"}, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) { return false; }
        };

        tablaDisponibles = new JTable(modeloDisponibles);
        tablaDisponibles.setRowHeight(28);
        tablaDisponibles.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaDisponibles.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tablaDisponibles.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tablaDisponibles.getTableHeader().setBackground(new Color(241, 245, 249));
        tablaDisponibles.getTableHeader().setForeground(new Color(51, 65, 85));

        modeloSeleccionados = new DefaultTableModel(new Object[]{"ID", "Código", "Equipo"}, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) { return false; }
        };

        tablaSeleccionados = new JTable(modeloSeleccionados);
        tablaSeleccionados.setRowHeight(28);
        tablaSeleccionados.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaSeleccionados.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tablaSeleccionados.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tablaSeleccionados.getTableHeader().setBackground(new Color(241, 245, 249));
        tablaSeleccionados.getTableHeader().setForeground(new Color(51, 65, 85));

        JPanel panelTablas = new JPanel(new GridLayout(1, 2, 12, 12));
        panelTablas.setOpaque(false);

        // Panel Disponibles
        JPanel panelDisponibles = new JPanel(new BorderLayout(5, 5));
        panelDisponibles.setOpaque(false);
        JScrollPane scrollDisp = new JScrollPane(tablaDisponibles);
        scrollDisp.getViewport().setBackground(Color.WHITE);
        scrollDisp.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225)),
                " Inventario de Equipos ",
                0, 0, new Font("Segoe UI", Font.BOLD, 13), new Color(37, 99, 235)
        ));
        panelDisponibles.add(scrollDisp, BorderLayout.CENTER);

        JButton btnAgregar = new JButton("Agregar equipo");
        estilizarBoton(btnAgregar, false);
        panelDisponibles.add(btnAgregar, BorderLayout.SOUTH);

        // Panel Seleccionados
        JPanel panelSeleccionados = new JPanel(new BorderLayout(5, 5));
        panelSeleccionados.setOpaque(false);
        JScrollPane scrollSelec = new JScrollPane(tablaSeleccionados);
        scrollSelec.getViewport().setBackground(Color.WHITE);
        scrollSelec.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225)),
                " Equipos del Préstamo ",
                0, 0, new Font("Segoe UI", Font.BOLD, 13), new Color(37, 99, 235)
        ));
        panelSeleccionados.add(scrollSelec, BorderLayout.CENTER);

        JButton btnQuitar = new JButton("Quitar equipo");
        estilizarBoton(btnQuitar, false);
        panelSeleccionados.add(btnQuitar, BorderLayout.SOUTH);

        panelTablas.add(panelDisponibles);
        panelTablas.add(panelSeleccionados);

        principal.add(panelTablas, BorderLayout.CENTER);

        // --- ACCIONES INFERIORES ---
        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        acciones.setOpaque(false);

        JButton btnActualizar = new JButton("Actualizar equipos");
        JButton btnRegistrar = new JButton("Registrar préstamo");
        JButton btnCerrar = new JButton("Cerrar");

        estilizarBoton(btnActualizar, false);
        estilizarBoton(btnRegistrar, true); // Botón principal en azul institucional
        estilizarBoton(btnCerrar, false);

        acciones.add(btnActualizar);
        acciones.add(btnRegistrar);
        acciones.add(btnCerrar);

        principal.add(acciones, BorderLayout.SOUTH);

        setContentPane(principal);

        // --- REGISTRO DE EVENTOS ---
        btnAgregar.addActionListener(e -> agregarEquipo());
        btnQuitar.addActionListener(e -> quitarEquipo());
        btnActualizar.addActionListener(e -> cargarEquiposInventario());
        btnRegistrar.addActionListener(e -> registrarPrestamo());
        btnCerrar.addActionListener(e -> dispose());

        txtEscanerQR.addActionListener(e -> filtrarTabla());
        btnEscanearAccion.addActionListener(e -> filtrarTabla());
        btnCargarQRArchivo.addActionListener(e -> leerQrDesdeArchivo());
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

    private void leerQrDesdeArchivo() {
        JFileChooser fileChooser = new JFileChooser();
        File carpetaQrs = new File("qrs_inventario");
        if (carpetaQrs.exists()) {
            fileChooser.setCurrentDirectory(carpetaQrs);
        }
        fileChooser.setDialogTitle("Seleccionar imagen de Código QR");

        int resultado = fileChooser.showOpenDialog(this);
        if (resultado == JFileChooser.APPROVE_OPTION) {
            File archivoSeleccionado = fileChooser.getSelectedFile();
            try {
                BufferedImage imagen = javax.imageio.ImageIO.read(archivoSeleccionado);
                if (imagen == null) {
                    JOptionPane.showMessageDialog(this, "El archivo seleccionado no es una imagen válida.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int ancho = imagen.getWidth();
                int alto = imagen.getHeight();
                int[] pixels = imagen.getRGB(0, 0, ancho, alto, null, 0, ancho);

                com.google.zxing.LuminanceSource source = new com.google.zxing.RGBLuminanceSource(ancho, alto, pixels);
                com.google.zxing.BinaryBitmap bitmap = new com.google.zxing.BinaryBitmap(new com.google.zxing.common.HybridBinarizer(source));

                com.google.zxing.Result resultadoQR = new com.google.zxing.MultiFormatReader().decode(bitmap);
                String codigoLeido = resultadoQR.getText();

                String codigoBuscado = extraerCodigoInventario(codigoLeido);

                List<EstadoEquipo> equipos = equipoRepository.listar();
                EstadoEquipo equipoEncontrado = null;
                for (EstadoEquipo eq : equipos) {
                    if (eq.getCodigoInventario().equalsIgnoreCase(codigoBuscado)) {
                        equipoEncontrado = eq;
                        break;
                    }
                }

                if (equipoEncontrado == null) {
                    JOptionPane.showMessageDialog(
                            this,
                            "El equipo con código '" + codigoBuscado + "' no existe en el inventario.",
                            "SIGELAB - Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                    return;
                }

                if (!"DISPONIBLE".equalsIgnoreCase(equipoEncontrado.getEstado())) {
                    JOptionPane.showMessageDialog(
                            this,
                            "ADVERTENCIA: El equipo '" + equipoEncontrado.getNombreEquipo() + "' (" + codigoBuscado + ") NO se puede prestar.\nEstado actual en el sistema: " + equipoEncontrado.getEstado(),
                            "Equipo No Disponible",
                            JOptionPane.WARNING_MESSAGE
                    );
                    return;
                }

                if (equiposSeleccionados.contains(equipoEncontrado.getId())) {
                    JOptionPane.showMessageDialog(this, "El equipo ya se encuentra agregado en este préstamo.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }

                equiposSeleccionados.add(equipoEncontrado.getId());
                modeloSeleccionados.addRow(new Object[]{
                        equipoEncontrado.getId(),
                        equipoEncontrado.getCodigoInventario(),
                        equipoEncontrado.getNombreEquipo()
                });

                cargarEquiposInventario();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "No se pudo leer ningún código QR de la imagen:\n" + ex.getMessage(), "Error de lectura", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private String extraerCodigoInventario(String textoQR) {
        if (textoQR.contains("SIGELAB-EQ:")) {
            try {
                String parte = textoQR.split("\\|")[0];
                return parte.replace("SIGELAB-EQ:", "").trim();
            } catch (Exception e) {
                return textoQR;
            }
        }
        return textoQR;
    }

    private void filtrarTabla() {
        String filtro = txtEscanerQR.getText().trim().toLowerCase();
        try {
            modeloDisponibles.setRowCount(0);
            List<EstadoEquipo> equipos = equipoRepository.listar();

            for (EstadoEquipo equipo : equipos) {
                if (!equiposSeleccionados.contains(equipo.getId())) {

                    if (filtro.isEmpty() ||
                            equipo.getCodigoInventario().toLowerCase().contains(filtro) ||
                            equipo.getNombreEquipo().toLowerCase().contains(filtro) ||
                            equipo.getEstado().toLowerCase().contains(filtro)) {

                        modeloDisponibles.addRow(new Object[]{
                                equipo.getId(),
                                equipo.getCodigoInventario(),
                                equipo.getNombreEquipo(),
                                equipo.getEstado()
                        });
                    }
                }
            }
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, "Error al filtrar equipos:\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarEquiposInventario() {
        txtEscanerQR.setText("");
        filtrarTabla();
    }

    private void agregarEquipo() {
        int fila = tablaDisponibles.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Selecciona un equipo de la tabla.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int filaModelo = tablaDisponibles.convertRowIndexToModel(fila);
        int id = (Integer) modeloDisponibles.getValueAt(filaModelo, 0);
        String codigo = (String) modeloDisponibles.getValueAt(filaModelo, 1);

        try {
            List<EstadoEquipo> equipos = equipoRepository.listar();
            EstadoEquipo equipoActual = null;
            for (EstadoEquipo eq : equipos) {
                if (eq.getId() == id) {
                    equipoActual = eq;
                    break;
                }
            }

            if (equipoActual == null || !"DISPONIBLE".equalsIgnoreCase(equipoActual.getEstado())) {
                JOptionPane.showMessageDialog(
                        this,
                        "ADVERTENCIA: El equipo '" + (equipoActual != null ? equipoActual.getNombreEquipo() : codigo) + "' NO se puede prestar.\nEstado actual en el sistema: " + (equipoActual != null ? equipoActual.getEstado() : "NO ENCONTRADO"),
                        "Equipo No Disponible",
                        JOptionPane.WARNING_MESSAGE
                );
                cargarEquiposInventario();
                return;
            }

            if (equiposSeleccionados.contains(id)) {
                return;
            }

            equiposSeleccionados.add(id);
            modeloSeleccionados.addRow(new Object[]{
                    id,
                    equipoActual.getCodigoInventario(),
                    equipoActual.getNombreEquipo()
            });

            cargarEquiposInventario();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al verificar el estado del equipo: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void quitarEquipo() {
        int fila = tablaSeleccionados.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Selecciona un equipo para quitar.");
            return;
        }
        int filaModelo = tablaSeleccionados.convertRowIndexToModel(fila);
        int id = (Integer) modeloSeleccionados.getValueAt(filaModelo, 0);

        equiposSeleccionados.remove(Integer.valueOf(id));
        modeloSeleccionados.removeRow(filaModelo);
        cargarEquiposInventario();
    }

    private void registrarPrestamo() {
        if (usuarioAutenticado == null || usuarioAutenticado.getIdUsuario() <= 0) {
            JOptionPane.showMessageDialog(this, "No se encontró un usuario autenticado válido.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (equiposSeleccionados.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar al menos un equipo disponible para el préstamo.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            java.util.Date fecha = dateChooserDevolucion.getDate();
            if (fecha == null) {
                JOptionPane.showMessageDialog(this, "Debe seleccionar una fecha de devolución válida.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            LocalDateTime fechaEsperada = new java.sql.Timestamp(fecha.getTime()).toLocalDateTime();

            boolean registrado = controller.registrarPrestamo(
                    txtSolicitante.getText(),
                    fechaEsperada,
                    usuarioAutenticado.getIdUsuario(),
                    txtObservaciones.getText(),
                    new ArrayList<>(equiposSeleccionados)
            );

            if (registrado) {
                JOptionPane.showMessageDialog(this, "Préstamo registrado correctamente.", "SIGELAB", JOptionPane.INFORMATION_MESSAGE);

                txtSolicitante.setText("");
                txtObservaciones.setText("");
                equiposSeleccionados.clear();
                modeloSeleccionados.setRowCount(0);

                cargarEquiposInventario();
                txtEscanerQR.requestFocusInWindow();
            }

        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, "No se pudo registrar el préstamo:\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}