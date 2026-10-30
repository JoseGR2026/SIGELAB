package SIGELAB.view;

import SIGELAB.controller.MantenimientoEquipoController;
import SIGELAB.model.EstadoEquipo;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MantenimientoEquipos extends JFrame {
    private JTable table1;
    private JButton enviarAMantenimientoButton;
    private JButton actualizarButton;
    private JButton reparadoButton;
    private JButton darDeBajaButton;
    private JTextField textObservaciones;
    private JTextField txtEscanerQR;
    private JButton btnEscanearAccion;
    private JButton btnCargarQRArchivo;
    private JButton salirButton;

    private final MantenimientoEquipoController controller;
    private List<EstadoEquipo> listaEquipos = new ArrayList<>();
    private EstadoEquipo equipoSeleccionado;

    private static final String[] COLUMNAS = {"ID", "Cód. Inventario", "Equipo", "Marca", "Modelo", "Nº Serie", "Estado", "Observaciones"};
    private final DefaultTableModel modeloTabla = new DefaultTableModel(COLUMNAS, 0) {
        @Override
        public boolean isCellEditable(int fila, int columna) { return false; }
    };

    public MantenimientoEquipos() {
        super("SIGELAB - Gestión de Mantenimiento y Taller");
        this.controller = new MantenimientoEquipoController();

        setSize(1150, 720);
        setMinimumSize(new Dimension(950, 600));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        inicializarComponentesModernos();
        configurarEventos();
        cargarDatos();
    }

    private void inicializarComponentesModernos() {
        JPanel panelPrincipal = new JPanel(new BorderLayout(15, 15));
        panelPrincipal.setBorder(new EmptyBorder(20, 20, 20, 20));
        panelPrincipal.setBackground(new Color(248, 250, 252));

        // --- ENCABEZADO ---
        JPanel panelNorte = new JPanel(new BorderLayout());
        panelNorte.setOpaque(false);

        JLabel lblTitulo = new JLabel("Gestión de Mantenimiento y Taller");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setForeground(new Color(15, 23, 42));

        JLabel lblSubtitulo = new JLabel("Control de equipos averiados, diagnósticos, reparaciones y bajas.");
        lblSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSubtitulo.setForeground(new Color(100, 116, 139));

        JPanel panelTitulos = new JPanel(new GridLayout(2, 1, 0, 2));
        panelTitulos.setOpaque(false);
        panelTitulos.add(lblTitulo);
        panelTitulos.add(lblSubtitulo);
        panelNorte.add(panelTitulos, BorderLayout.WEST);

        panelPrincipal.add(panelNorte, BorderLayout.NORTH);

        // --- CONTENEDOR SUPERIOR (Filtro QR y Observaciones Manuales) ---
        JPanel panelNorteContenedor = new JPanel(new BorderLayout(0, 12));
        panelNorteContenedor.setOpaque(false);

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
        panelNorteContenedor.add(panelQR, BorderLayout.NORTH);

        // Panel Observaciones / Motivo 100% manual e independiente
        JPanel panelObservaciones = new JPanel(new BorderLayout(10, 5));
        panelObservaciones.setBackground(Color.WHITE);
        panelObservaciones.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(new Color(203, 213, 225)),
                        " Detalle / Motivo de la Operación ",
                        0, 0, new Font("Segoe UI", Font.BOLD, 13), new Color(37, 99, 235)
                ),
                new EmptyBorder(10, 10, 10, 10)
        ));

        JLabel lblObs = new JLabel("Observaciones / Motivo:");
        lblObs.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblObs.setForeground(new Color(71, 85, 105));

        textObservaciones = new JTextField();
        textObservaciones.putClientProperty("JTextField.placeholderText", "Ingrese sus observaciones de forma manual para cualquier acción...");
        panelObservaciones.add(lblObs, BorderLayout.WEST);
        panelObservaciones.add(textObservaciones, BorderLayout.CENTER);

        panelNorteContenedor.add(panelObservaciones, BorderLayout.CENTER);
        panelPrincipal.add(panelNorteContenedor, BorderLayout.NORTH);

        // --- TABLA CENTRAL ---
        table1 = new JTable(modeloTabla);
        table1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table1.setRowHeight(30);
        table1.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table1.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table1.getTableHeader().setBackground(new Color(241, 245, 249));
        table1.getTableHeader().setForeground(new Color(51, 65, 85));

        JScrollPane scrollPane = new JScrollPane(table1);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225)),
                " Equipos en Sistema ",
                0, 0, new Font("Segoe UI", Font.BOLD, 13), new Color(37, 99, 235)
        ));
        panelPrincipal.add(scrollPane, BorderLayout.CENTER);

        // --- PANEL INFERIOR (Botones de Acción) ---
        JPanel panelSur = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panelSur.setOpaque(false);

        enviarAMantenimientoButton = new JButton("Enviar a Mantenimiento");
        reparadoButton = new JButton("Finalizar (Reparado)");
        darDeBajaButton = new JButton("Dar de Baja");
        actualizarButton = new JButton("Actualizar");
        salirButton = new JButton("Salir");

        estilizarBoton(enviarAMantenimientoButton, true);
        estilizarBoton(reparadoButton, false);
        estilizarBoton(darDeBajaButton, false);
        estilizarBoton(actualizarButton, false);
        estilizarBoton(salirButton, false);

        panelSur.add(enviarAMantenimientoButton);
        panelSur.add(reparadoButton);
        panelSur.add(darDeBajaButton);
        panelSur.add(actualizarButton);
        panelSur.add(salirButton);

        panelPrincipal.add(panelSur, BorderLayout.SOUTH);

        setContentPane(panelPrincipal);
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

    private void configurarEventos() {
        table1.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int fila = table1.getSelectedRow();
                if (fila >= 0 && fila < listaEquipos.size()) {
                    equipoSeleccionado = listaEquipos.get(table1.convertRowIndexToModel(fila));
                }
            }
        });

        actualizarButton.addActionListener(e -> {
            cargarDatos();
            textObservaciones.setText("");
            txtEscanerQR.setText("");
        });

        enviarAMantenimientoButton.addActionListener(e -> {
            if (verificarSeleccion()) {
                String motivo = textObservaciones.getText().trim();
                if (motivo.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Debe ingresar una observación o motivo en el cuadro de texto.", "Aviso", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                try {
                    controller.enviarAMantenimiento(equipoSeleccionado.getId(), motivo);
                    JOptionPane.showMessageDialog(this, "Equipo enviado a mantenimiento exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    cargarDatos();
                    textObservaciones.setText("");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        reparadoButton.addActionListener(e -> {
            if (verificarSeleccion()) {
                String motivo = textObservaciones.getText().trim();
                if (motivo.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Debe ingresar una observación detallando la reparación realizada.", "Aviso", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                int confirm = JOptionPane.showConfirmDialog(this, "¿El equipo fue reparado con éxito y pasa a estar DISPONIBLE?", "Confirmar Reparación", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        controller.finalizarMantenimiento(equipoSeleccionado.getId(), true, motivo);
                        JOptionPane.showMessageDialog(this, "El equipo ha regresado a disponibilidad.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                        cargarDatos();
                        textObservaciones.setText("");
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        darDeBajaButton.addActionListener(e -> {
            if (verificarSeleccion()) {
                String motivo = textObservaciones.getText().trim();
                if (motivo.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Debe ingresar el motivo por el cual se da de baja el equipo.", "Aviso", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                int confirm = JOptionPane.showConfirmDialog(this, "¿Confirma que el equipo es irreparable y será DADO DE BAJA?", "Dar de Baja", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        controller.finalizarMantenimiento(equipoSeleccionado.getId(), false, motivo);
                        JOptionPane.showMessageDialog(this, "El equipo ha sido dado de baja.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
                        cargarDatos();
                        textObservaciones.setText("");
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        salirButton.addActionListener(e -> dispose());

        txtEscanerQR.addActionListener(e -> filtrarTabla());
        btnEscanearAccion.addActionListener(e -> filtrarTabla());
        btnCargarQRArchivo.addActionListener(e -> leerQrDesdeArchivo());
    }

    private void filtrarTabla() {
        String filtro = txtEscanerQR.getText().trim().toLowerCase();
        try {
            modeloTabla.setRowCount(0);
            for (EstadoEquipo eq : listaEquipos) {
                if (filtro.isEmpty() ||
                        eq.getCodigoInventario().toLowerCase().contains(filtro) ||
                        eq.getNombreEquipo().toLowerCase().contains(filtro) ||
                        (eq.getMarca() != null && eq.getMarca().toLowerCase().contains(filtro)) ||
                        eq.getEstado().toLowerCase().contains(filtro)) {

                    modeloTabla.addRow(new Object[]{
                            eq.getId(),
                            eq.getCodigoInventario(),
                            eq.getNombreEquipo(),
                            eq.getMarca(),
                            eq.getModelo(),
                            eq.getNumeroSerie(),
                            eq.getEstado(),
                            eq.getObservaciones()
                    });
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al filtrar equipos:\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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

                for (int i = 0; i < table1.getRowCount(); i++) {
                    String codigoTabla = (String) table1.getValueAt(i, 1);
                    if (codigoTabla.equalsIgnoreCase(codigoBuscado)) {
                        table1.setRowSelectionInterval(i, i);
                        table1.scrollRectToVisible(table1.getCellRect(i, 0, true));
                        return;
                    }
                }

                JOptionPane.showMessageDialog(this, "El equipo con código '" + codigoBuscado + "' no se encuentra en la lista actual.", "Aviso", JOptionPane.WARNING_MESSAGE);

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

    private boolean verificarSeleccion() {
        if (equipoSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Por favor, seleccione un equipo de la tabla.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    private void cargarDatos() {
        try {
            listaEquipos = controller.listarEquipos();
            filtrarTabla();
            equipoSeleccionado = null;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar datos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}