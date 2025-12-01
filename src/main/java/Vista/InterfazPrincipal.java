package Vista;

import Controlador.*;
import Modelo.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Interfaz Principal del Sistema Contable
 * Empresa: Comercial el mejor vendedor S.A.
 */
public class InterfazPrincipal extends JFrame {
    
    // Controladores
    private ControladorUsuario controladorUsuario;
    private ControladorTransaccion controladorTransaccion;
    private ControladorBitacora controladorBitacora;
    
    // Componentes UI - Formulario
    private JTextField txtFecha;
    private JComboBox<String> cmbTipoDocumento;
    private JTextField txtProveedorCliente;
    private JTextField txtMonto;
    private JTextField txtCuentaContable;
    private JTextField txtNumeroDocumento;
    private JCheckBox chkDeducible;
    private JTextArea txtDescripcion;
    
    // Componentes UI - Tabla y Estado
    private JTable tabla;
    private DefaultTableModel tableModel;
    private JLabel lblUsuario;
    private JLabel lblCantidadTransacciones;
    private JLabel lblTotalMontos;
    
    // Colores del tema
    private static final Color COLOR_PRIMARIO = new Color(41, 128, 185);
    private static final Color COLOR_SECUNDARIO = new Color(52, 152, 219);
    private static final Color COLOR_EXITO = new Color(39, 174, 96);
    private static final Color COLOR_ADVERTENCIA = new Color(243, 156, 18);
    private static final Color COLOR_FONDO = new Color(236, 240, 241);

    // Helpers de estilo (adelantados para evitar advertencias de análisis)
    private void estilizarCampoTexto(JTextField campo) {
        campo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199)),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)));
    }

    private void styleButton(JButton boton, Color fondo, int width) {
        boton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        boton.setBackground(fondo);
        boton.setForeground(Color.WHITE);
        boton.setFocusPainted(false);
        boton.setBorderPainted(false);
        boton.setPreferredSize(new Dimension(width, 35));
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private static class GradientPanel extends JPanel {
        private final Color start;
        private final Color end;
        GradientPanel(Color start, Color end) { this.start = start; this.end = end; setOpaque(true); }
        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int h = getHeight();
            GradientPaint gp = new GradientPaint(0, 0, start, 0, h, end);
            g2.setPaint(gp);
            g2.fillRect(0,0,getWidth(),h);
            g2.dispose();
        }
    }
    
    public InterfazPrincipal() {
        super("Sistema Contable - Comercial el mejor vendedor S.A.");
        
        // Inicializar controladores
        controladorUsuario = ControladorUsuario.getInstancia();
        controladorTransaccion = ControladorTransaccion.getInstancia();
        controladorBitacora = ControladorBitacora.getInstancia();
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        
        // Color de fondo
        getContentPane().setBackground(COLOR_FONDO);
        
        inicializarComponentes();
        cargarDatosTabla();
        actualizarEstadisticas();
    }
    
    private void inicializarComponentes() {
        crearBarraMenu();
        
        // Panel superior con título
        JPanel panelSuperior = crearPanelTitulo();
        add(panelSuperior, BorderLayout.NORTH);
        
        // Panel central con formulario y tabla
        JPanel panelCentral = new JPanel(new BorderLayout(10, 10));
        panelCentral.setBackground(COLOR_FONDO);
        panelCentral.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JPanel panelFormulario = crearPanelFormulario();
        panelCentral.add(panelFormulario, BorderLayout.NORTH);
        
        JScrollPane panelTabla = crearPanelTabla();
        panelCentral.add(panelTabla, BorderLayout.CENTER);
        
        add(panelCentral, BorderLayout.CENTER);
        
        // Panel inferior con barra de estado
        JPanel statusBar = crearBarraEstado();
        add(statusBar, BorderLayout.SOUTH);
    }
    
    /**
     * Crea el panel de formulario de registro de transacciones (versión adaptada al tema)
     */
    private JPanel crearPanelFormulario() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(COLOR_PRIMARIO, 2),
                "Formulario de Registro - Facturas y Gastos",
                0, 0, new Font("Segoe UI", Font.BOLD, 14), COLOR_PRIMARIO),
            BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));

        Font labelFont = new Font("Arial", Font.BOLD, 12);
        Font fieldFont = new Font("Arial", Font.PLAIN, 12);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8,8,8,8);
        gbc.anchor = GridBagConstraints.EAST;

        // Fecha
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel lblFecha = new JLabel("Fecha:"); lblFecha.setFont(labelFont); panel.add(lblFecha, gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST; gbc.fill = GridBagConstraints.HORIZONTAL;
        txtFecha = new JTextField(LocalDate.now().toString(),15); txtFecha.setFont(fieldFont); estilizarCampoTexto(txtFecha); panel.add(txtFecha, gbc);

        // Tipo Documento
        gbc.gridx = 2; gbc.gridy = 0; gbc.anchor = GridBagConstraints.EAST; gbc.fill = GridBagConstraints.NONE;
        JLabel lblTipo = new JLabel("Tipo:"); lblTipo.setFont(labelFont); panel.add(lblTipo, gbc);
        gbc.gridx = 3; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST; gbc.fill = GridBagConstraints.HORIZONTAL;
        cmbTipoDocumento = new JComboBox<>(new String[]{"Factura", "Gasto"}); cmbTipoDocumento.setFont(fieldFont); cmbTipoDocumento.addActionListener(e -> actualizarCamposSegunTipo()); panel.add(cmbTipoDocumento, gbc);

        // Proveedor/Cliente
        gbc.gridx = 0; gbc.gridy = 1; gbc.anchor = GridBagConstraints.EAST; gbc.fill = GridBagConstraints.NONE;
        JLabel lblProv = new JLabel("Proveedor/Cliente:"); lblProv.setFont(labelFont); panel.add(lblProv, gbc);
        gbc.gridx = 1; gbc.gridy = 1; gbc.anchor = GridBagConstraints.WEST; gbc.fill = GridBagConstraints.HORIZONTAL;
        txtProveedorCliente = new JTextField(); txtProveedorCliente.setFont(fieldFont); estilizarCampoTexto(txtProveedorCliente); panel.add(txtProveedorCliente, gbc);

        // Monto
        gbc.gridx = 2; gbc.gridy = 1; gbc.anchor = GridBagConstraints.EAST; gbc.fill = GridBagConstraints.NONE;
        JLabel lblMonto = new JLabel("Monto (USD):"); lblMonto.setFont(labelFont); panel.add(lblMonto, gbc);
        gbc.gridx = 3; gbc.gridy = 1; gbc.anchor = GridBagConstraints.WEST; gbc.fill = GridBagConstraints.HORIZONTAL;
        txtMonto = new JTextField(); txtMonto.setFont(fieldFont); estilizarCampoTexto(txtMonto); panel.add(txtMonto, gbc);

        // Cuenta Contable
        gbc.gridx = 0; gbc.gridy = 2; gbc.anchor = GridBagConstraints.EAST; gbc.fill = GridBagConstraints.NONE;
        JLabel lblCuenta = new JLabel("Cuenta Contable:"); lblCuenta.setFont(labelFont); panel.add(lblCuenta, gbc);
        gbc.gridx = 1; gbc.gridy = 2; gbc.anchor = GridBagConstraints.WEST; gbc.fill = GridBagConstraints.HORIZONTAL;
        txtCuentaContable = new JTextField(); txtCuentaContable.setFont(fieldFont); estilizarCampoTexto(txtCuentaContable); panel.add(txtCuentaContable, gbc);

        // Nº Documento
        gbc.gridx = 2; gbc.gridy = 2; gbc.anchor = GridBagConstraints.EAST; gbc.fill = GridBagConstraints.NONE;
        JLabel lblNumDoc = new JLabel("Nº Documento:"); lblNumDoc.setFont(labelFont); panel.add(lblNumDoc, gbc);
        gbc.gridx = 3; gbc.gridy = 2; gbc.anchor = GridBagConstraints.WEST; gbc.fill = GridBagConstraints.HORIZONTAL;
        txtNumeroDocumento = new JTextField(); txtNumeroDocumento.setFont(fieldFont); estilizarCampoTexto(txtNumeroDocumento); panel.add(txtNumeroDocumento, gbc);

        // Descripción
        gbc.gridx = 0; gbc.gridy = 3; gbc.anchor = GridBagConstraints.NORTHEAST; gbc.fill = GridBagConstraints.NONE;
        JLabel lblDesc = new JLabel("Descripción:"); lblDesc.setFont(labelFont); panel.add(lblDesc, gbc);
        gbc.gridx = 1; gbc.gridy = 3; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.WEST; gbc.fill = GridBagConstraints.BOTH;
        txtDescripcion = new JTextArea(3,20); txtDescripcion.setFont(fieldFont); txtDescripcion.setLineWrap(true); txtDescripcion.setWrapStyleWord(true);
        JScrollPane scrollDesc = new JScrollPane(txtDescripcion); panel.add(scrollDesc, gbc);
        gbc.gridwidth = 1;

        // Checkbox deducible
        gbc.gridx = 3; gbc.gridy = 3; gbc.anchor = GridBagConstraints.WEST; gbc.fill = GridBagConstraints.NONE;
        chkDeducible = new JCheckBox("Gasto Deducible de Impuestos"); chkDeducible.setFont(new Font("Arial", Font.PLAIN, 11)); chkDeducible.setBackground(Color.WHITE); chkDeducible.setEnabled(false); panel.add(chkDeducible, gbc);

        // Botones
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 4; gbc.anchor = GridBagConstraints.CENTER; gbc.fill = GridBagConstraints.NONE;
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5)); panelBotones.setOpaque(false);
        JButton btnRegistrar = new JButton("Registrar Transacción"); styleButton(btnRegistrar, COLOR_EXITO, 190); btnRegistrar.addActionListener(e -> registrarTransaccion());
        JButton btnLimpiar = new JButton("Limpiar Formulario"); styleButton(btnLimpiar, COLOR_ADVERTENCIA, 190); btnLimpiar.addActionListener(e -> limpiarFormulario());
        JButton btnActualizar = new JButton("Actualizar Tabla"); styleButton(btnActualizar, COLOR_SECUNDARIO, 190); btnActualizar.addActionListener(e -> { cargarDatosTabla(); actualizarEstadisticas(); });
        panelBotones.add(btnRegistrar); panelBotones.add(btnLimpiar); panelBotones.add(btnActualizar);
        panel.add(panelBotones, gbc);

        return panel;
    }
    
    /**
     * Crea el panel de título con información de la empresa
     */
    private JPanel crearPanelTitulo() {
        JPanel panel = new GradientPanel(COLOR_PRIMARIO, COLOR_SECUNDARIO);
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(18, 24, 18, 24));
        
        // Título principal
        JLabel lblTitulo = new JLabel("Sistema Contable Integrado");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setForeground(Color.WHITE);
        
        // Subtítulo con empresa
        JLabel lblEmpresa = new JLabel("Comercial el mejor vendedor S.A.");
        lblEmpresa.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lblEmpresa.setForeground(new Color(236, 240, 241));
        
        JPanel panelTextos = new JPanel(new GridLayout(2, 1, 0, 6));
        panelTextos.setOpaque(false);
        panelTextos.add(lblTitulo);
        panelTextos.add(lblEmpresa);
        
        panel.add(panelTextos, BorderLayout.WEST);
        
        return panel;
    }
    
    /**
     * Crea el panel con la tabla de transacciones
     */
    private JScrollPane crearPanelTabla() {
        String[] columnas = {"ID", "Fecha", "Tipo", "Proveedor/Cliente", "Monto USD", 
                            "Cuenta", "Nº Doc", "Estado", "Usuario Registró"};
        tableModel = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        
        tabla = new JTable(tableModel);
        tabla.setFont(new Font("Arial", Font.PLAIN, 11));
        tabla.setRowHeight(25);
        tabla.setFillsViewportHeight(true);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setSelectionBackground(new Color(52, 152, 219));
        tabla.setSelectionForeground(Color.WHITE);
        tabla.setGridColor(new Color(189, 195, 199));
        
        // Encabezado de tabla
        tabla.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tabla.getTableHeader().setBackground(COLOR_PRIMARIO);
        tabla.getTableHeader().setForeground(Color.WHITE);
        tabla.getTableHeader().setReorderingAllowed(false);
        
        // Ancho de columnas
        tabla.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
        tabla.getColumnModel().getColumn(1).setPreferredWidth(100); // Fecha
        tabla.getColumnModel().getColumn(2).setPreferredWidth(80);  // Tipo
        tabla.getColumnModel().getColumn(3).setPreferredWidth(150); // Proveedor
        tabla.getColumnModel().getColumn(4).setPreferredWidth(100); // Monto
        tabla.getColumnModel().getColumn(5).setPreferredWidth(100); // Cuenta
        tabla.getColumnModel().getColumn(6).setPreferredWidth(100); // Nº Doc
        tabla.getColumnModel().getColumn(7).setPreferredWidth(120); // Estado
        tabla.getColumnModel().getColumn(8).setPreferredWidth(150); // Usuario
        
        JScrollPane scrollPane = new JScrollPane(tabla);
        scrollPane.setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(COLOR_PRIMARIO, 2),
                "Libro Contable Digital - Registro de Transacciones",
                0, 0, new Font("Arial", Font.BOLD, 14), COLOR_PRIMARIO),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        
        // Doble clic para ver detalles
        tabla.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    mostrarDetalleTransaccion();
                }
            }
        });
        
        return scrollPane;
    }
    
    /**
     * Crea la barra de estado con información del usuario y estadísticas
     */
    private JPanel crearBarraEstado() {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setBackground(new Color(52, 73, 94));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(2, 0, 0, 0, COLOR_PRIMARIO),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        
        Usuario usuarioActual = controladorUsuario.getUsuarioActual();
        
        // Panel izquierdo - Usuario
        JPanel panelIzq = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panelIzq.setOpaque(false);
        
    lblUsuario = new JLabel("Usuario: " + usuarioActual.getNombreCompleto() + 
                   " | Rol: " + usuarioActual.getRol() + " | Conectado");
        lblUsuario.setFont(new Font("Arial", Font.BOLD, 11));
        lblUsuario.setForeground(Color.WHITE);
        panelIzq.add(lblUsuario);
        
        // Panel central - Estadísticas
        JPanel panelCentro = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        panelCentro.setOpaque(false);
        
    lblCantidadTransacciones = new JLabel("Transacciones: 0");
        lblCantidadTransacciones.setFont(new Font("Arial", Font.PLAIN, 11));
        lblCantidadTransacciones.setForeground(new Color(236, 240, 241));
        
    lblTotalMontos = new JLabel("Total: $0.00");
        lblTotalMontos.setFont(new Font("Arial", Font.PLAIN, 11));
        lblTotalMontos.setForeground(new Color(236, 240, 241));
        
        panelCentro.add(lblCantidadTransacciones);
        panelCentro.add(new JLabel("|") {{
            setForeground(new Color(149, 165, 166));
        }});
        panelCentro.add(lblTotalMontos);
        
        // Panel derecho - Fecha
        JPanel panelDer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        panelDer.setOpaque(false);
        
        JLabel lblFecha = new JLabel("Fecha: " + LocalDate.now().format(
            java.time.format.DateTimeFormatter.ofPattern("EEEE, dd 'de' MMMM 'de' yyyy", 
            java.util.Locale.of("es", "EC"))));
        lblFecha.setFont(new Font("Arial", Font.PLAIN, 11));
        lblFecha.setForeground(new Color(236, 240, 241));
        panelDer.add(lblFecha);
        
        panel.add(panelIzq, BorderLayout.WEST);
        panel.add(panelCentro, BorderLayout.CENTER);
        panel.add(panelDer, BorderLayout.EAST);
        
        return panel;
    }
    
    private void actualizarCamposSegunTipo() {
        boolean esGasto = cmbTipoDocumento.getSelectedItem().equals("Gasto");
        chkDeducible.setEnabled(esGasto);
        if (!esGasto) {
            chkDeducible.setSelected(false);
        }
    }
    
    /**
     * Registra una nueva transacción desde el formulario con validación mejorada
     */
    private void registrarTransaccion() {
        try {
            // 1. Validar fecha
            if (txtFecha.getText().trim().isEmpty()) {
                mostrarAdvertencia("Por favor ingrese una fecha válida.\n\nFormato esperado: AAAA-MM-DD\nEjemplo: 2025-01-15");
                txtFecha.requestFocus();
                return;
            }
            
            LocalDate fecha;
            try {
                fecha = LocalDate.parse(txtFecha.getText().trim());
            } catch (Exception e) {
                mostrarError("❌ Formato de fecha inválido\n\nDebe usar el formato: AAAA-MM-DD\nEjemplo: 2025-01-15");
                txtFecha.selectAll();
                txtFecha.requestFocus();
                return;
            }
            
            // 2. Validar proveedor/cliente
            String provCliente = txtProveedorCliente.getText().trim();
            if (provCliente.isEmpty()) {
                mostrarAdvertencia("Por favor ingrese el nombre del proveedor o cliente.\n\nEjemplo: Juan Pérez, Distribuidora ABC, etc.");
                txtProveedorCliente.requestFocus();
                return;
            }
            
            // 3. Validar y limpiar monto (eliminar símbolos $, USD, espacios, comas)
            if (txtMonto.getText().trim().isEmpty()) {
                mostrarAdvertencia("Por favor ingrese el monto de la transacción.\n\n✅ Ingrese solo números (puede usar punto decimal)\n✅ No use símbolos ni espacios\n\nEjemplo: 1500.50");
                txtMonto.requestFocus();
                return;
            }
            
            String montoLimpio = txtMonto.getText().trim()
                .replace("$", "")
                .replace("USD", "")
                .replace("usd", "")
                .replace(" ", "")
                .replace(",", "");
            
            double monto;
            try {
                monto = Double.parseDouble(montoLimpio);
            } catch (NumberFormatException e) {
                mostrarError("❌ El monto ingresado no es válido\n\n" +
                           "Por favor ingrese solo números.\n" +
                           "Puede usar punto decimal para centavos.\n\n" +
                           "✅ Ejemplos correctos:\n" +
                           "   • 1500\n" +
                           "   • 1500.50\n" +
                           "   • 250.99\n\n" +
                           "❌ NO use: símbolos ($, USD), comas, espacios");
                txtMonto.selectAll();
                txtMonto.requestFocus();
                return;
            }
            
            if (monto <= 0) {
                mostrarError("El monto debe ser mayor a $0.01\n\nPor favor ingrese un valor positivo.");
                txtMonto.selectAll();
                txtMonto.requestFocus();
                return;
            }
            
            // 4. Validar cuenta contable
            String cuenta = txtCuentaContable.getText().trim();
            if (cuenta.isEmpty()) {
                mostrarAdvertencia("Por favor ingrese el código de la cuenta contable.\n\nEjemplo: 1101, 5201, etc.");
                txtCuentaContable.requestFocus();
                return;
            }
            
            // 5. Validar número de documento
            String numDoc = txtNumeroDocumento.getText().trim();
            if (numDoc.isEmpty()) {
                mostrarAdvertencia("Por favor ingrese el número del documento.\n\nEjemplo: 001-001-000123456");
                txtNumeroDocumento.requestFocus();
                return;
            }
            
            // 6. Obtener datos adicionales
            String tipo = (String) cmbTipoDocumento.getSelectedItem();
            Usuario usuario = controladorUsuario.getUsuarioActual();
            
            // 7. Calcular IVA (15% en Ecuador)
            double iva = monto * 0.15;
            double total = monto + iva;
            
            // 8. Registrar transacción
            boolean exito;
            if (tipo.equals("Factura")) {
                exito = controladorTransaccion.registrarFactura(
                    fecha, provCliente, monto, cuenta, numDoc, usuario);
            } else {
                exito = controladorTransaccion.registrarGasto(
                    fecha, provCliente, monto, cuenta, numDoc, 
                    chkDeducible.isSelected(), usuario);
            }
            
            // 9. Mostrar resultado detallado
            if (exito) {
                String mensaje = String.format(
                    "✅ Transacción registrada exitosamente\n\n" +
                    "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n" +
                    "📋 Tipo: %s\n" +
                    "📅 Fecha: %s\n" +
                    "👤 %s: %s\n" +
                    "💵 Subtotal: $%,.2f\n" +
                    "🏦 IVA (15%%): $%,.2f\n" +
                    "💰 Total: $%,.2f\n" +
                    "🔢 Documento: %s\n" +
                    "📊 Estado: REGISTRADO\n\n" +
                    "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n" +
                    "ℹ️ La transacción está pendiente de aprobación\npor parte de Jefatura Financiera.",
                    tipo,
                    fecha.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                    tipo.equals("Factura") ? "Cliente" : "Proveedor",
                    provCliente,
                    monto,
                    iva,
                    total,
                    numDoc
                );
                
                mostrarExito(mensaje);
                limpiarFormulario();
                cargarDatosTabla();
                actualizarEstadisticas();
            } else {
                mostrarError("No se pudo registrar la transacción.\n\n" +
                           "Por favor verifique los datos e intente nuevamente.\n" +
                           "Si el problema persiste, contacte al administrador.");
            }
            
        } catch (Exception ex) {
            mostrarError("Error inesperado al registrar:\n\n" + 
                       ex.getMessage() + 
                       "\n\nContacte al administrador del sistema.");
            ex.printStackTrace();
        }
    }
    
    /**
     * Muestra un mensaje de éxito al usuario
     */
    private void mostrarExito(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, 
            "✅ Operación Exitosa", JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Muestra un mensaje de error al usuario
     */
    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, 
            "❌ Error", JOptionPane.ERROR_MESSAGE);
    }
    
    /**
     * Muestra un mensaje de advertencia al usuario
     */
    private void mostrarAdvertencia(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, 
            "⚠️ Atención", JOptionPane.WARNING_MESSAGE);
    }
    
    private void cargarDatosTabla() {
        tableModel.setRowCount(0);
        for (Transaccion t : controladorTransaccion.getTransaccionesActivas()) {
            Object[] fila = {
                t.getIdTransaccion(),
                t.getFecha(),
                t.getTipoDocumento(),
                t.getProveedorCliente(),
                String.format("$%.2f", t.getMonto()),
                t.getCuentaContable(),
                t.getNumeroDocumento(),
                t.getEstado(),
                t.getUsuarioRegistro().getNombreUsuario()
            };
            tableModel.addRow(fila);
        }
    }
    
    private void limpiarFormulario() {
        txtFecha.setText(LocalDate.now().toString());
        cmbTipoDocumento.setSelectedIndex(0);
        txtProveedorCliente.setText("");
        txtMonto.setText("");
        txtCuentaContable.setText("");
        txtNumeroDocumento.setText("");
        txtDescripcion.setText("");
        chkDeducible.setSelected(false);
        actualizarCamposSegunTipo();
    }
    
    /**
     * Elimina una transacción seleccionada (solo Jefatura Financiera)
     */
    private void eliminarTransaccion() {
        int filaSeleccionada = tabla.getSelectedRow();
        if (filaSeleccionada == -1) {
            mostrarAdvertencia("Por favor seleccione una transacción de la tabla\npara poder eliminarla.");
            return;
        }
        
        String id = String.valueOf(tableModel.getValueAt(filaSeleccionada, 0));
        String tipo = (String) tableModel.getValueAt(filaSeleccionada, 2);
        String provCliente = (String) tableModel.getValueAt(filaSeleccionada, 3);
        String montoStr = (String) tableModel.getValueAt(filaSeleccionada, 4);
        String estado = (String) tableModel.getValueAt(filaSeleccionada, 7);
        
        Usuario usuario = controladorUsuario.getUsuarioActual();
        
        // Verificar permisos
        if (!usuario.getRol().equals(Usuario.ROL_JEFATURA_FINANCIERA)) {
            mostrarError("❌ Acceso Denegado\n\n" +
                       "Solo los usuarios con rol de Jefatura Financiera\n" +
                       "pueden eliminar transacciones.\n\n" +
                       "Su rol actual: " + usuario.getRol());
            return;
        }
        
        // Advertencia sobre eliminación
        int confirmacion = JOptionPane.showConfirmDialog(this,
            String.format("⚠️ ¿Está seguro que desea eliminar esta transacción?\n\n" +
                        "Esta acción NO se puede deshacer.\n\n" +
                        "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n" +
                        "🔢 ID: %d\n" +
                        "📋 Tipo: %s\n" +
                        "👤 %s: %s\n" +
                        "💵 Monto: %s\n" +
                        "📊 Estado: %s\n\n" +
                        "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━",
                        id,
                        tipo,
                        tipo.equals("Factura") ? "Cliente" : "Proveedor",
                        provCliente,
                        montoStr,
                        estado),
            "⚠️ Confirmar Eliminación",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirmacion != JOptionPane.YES_OPTION) {
            return;
        }
        
        // Intentar eliminar
        if (controladorTransaccion.eliminarFactura(id, usuario)) {
            mostrarExito("✅ Transacción eliminada exitosamente\n\n" +
                       "ID: " + id + "\n" +
                       tipo + ": " + provCliente + "\n\n" +
                       "La eliminación ha sido registrada en la bitácora.");
            cargarDatosTabla();
            actualizarEstadisticas();
        } else {
            mostrarError("❌ No se pudo eliminar la transacción\n\n" +
                       "Posibles causas:\n" +
                       "• La transacción ya fue aprobada\n" +
                       "• No tiene permisos suficientes\n" +
                       "• La transacción no existe\n\n" +
                       "Solo se pueden eliminar transacciones en estado " + Transaccion.ESTADO_REGISTRADO + ".");
        }
    }
    
    /**
     * Aprueba una transacción seleccionada (solo Jefatura Financiera)
     */
    private void aprobarTransaccion() {
        int filaSeleccionada = tabla.getSelectedRow();
        if (filaSeleccionada == -1) {
            mostrarAdvertencia("Por favor seleccione una transacción de la tabla\npara poder aprobarla.");
            return;
        }
        
        int id = (int) tableModel.getValueAt(filaSeleccionada, 0);
        String tipo = (String) tableModel.getValueAt(filaSeleccionada, 2);
        String provCliente = (String) tableModel.getValueAt(filaSeleccionada, 3);
        String montoStr = (String) tableModel.getValueAt(filaSeleccionada, 4);
        String estado = (String) tableModel.getValueAt(filaSeleccionada, 7);
        
        Usuario usuario = controladorUsuario.getUsuarioActual();
        
        // Verificar permisos
        if (!usuario.getRol().equals(Usuario.ROL_JEFATURA_FINANCIERA)) {
            mostrarError("❌ Acceso Denegado\n\n" +
                       "Solo los usuarios con rol de Jefatura Financiera\n" +
                       "pueden aprobar transacciones.\n\n" +
                       "Su rol actual: " + usuario.getRol());
            return;
        }
        
        // Verificar que la transacción esté en estado REGISTRADO
        if (!estado.equals(Transaccion.ESTADO_REGISTRADO)) {
            mostrarAdvertencia("⚠️ Esta transacción no puede ser aprobada\n\n" +
                             "Estado actual: " + estado + "\n\n" +
                             "Solo se pueden aprobar transacciones en estado " + Transaccion.ESTADO_REGISTRADO + ".");
            return;
        }
        
        // Confirmación
        int confirmacion = JOptionPane.showConfirmDialog(this,
            String.format("¿Está seguro que desea aprobar esta transacción?\n\n" +
                        "📋 Tipo: %s\n" +
                        "👤 %s: %s\n" +
                        "💵 Monto: %s\n" +
                        "🔢 ID: %s",
                        tipo,
                        tipo.equals("Factura") ? "Cliente" : "Proveedor",
                        provCliente,
                        montoStr,
                        id),
            "Confirmar Aprobación",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        
        if (confirmacion != JOptionPane.YES_OPTION) {
            return;
        }
        
        // Aprobar transacción
        if (controladorTransaccion.aprobarTransaccion(String.valueOf(id), usuario)) {
            mostrarExito("✅ Transacción aprobada exitosamente\n\n" +
                       "ID: " + id + "\n" +
                       tipo + ": " + provCliente + "\n" +
                       "Monto: " + montoStr + "\n\n" +
                       "La transacción ha sido registrada en la bitácora.");
            cargarDatosTabla();
            actualizarEstadisticas();
        } else {
            mostrarError("No se pudo aprobar la transacción.\n\n" +
                       "Verifique que la transacción esté en estado REGISTRADO\n" +
                       "o contacte al administrador del sistema.");
        }
    }
    
    private void calcularIVA() {
        String anioStr = JOptionPane.showInputDialog(this, 
            "Ingrese el año para calcular IVA:", "2025");
        if (anioStr != null) {
            try {
                int anio = Integer.parseInt(anioStr);
                double retencion = controladorTransaccion.calcularRetencionIVA(anio);
                
                JOptionPane.showMessageDialog(this, 
                    String.format("Retención de IVA a pagar año %d: $%.2f\n\n" +
                                "Esto representa el 30%% del IVA en compras realizadas.\n" +
                                "Esta retención debe ser declarada al SRI.", 
                                anio, retencion), 
                    "Cálculo Retención IVA", JOptionPane.INFORMATION_MESSAGE);
                
                controladorBitacora.registrar(controladorUsuario.getUsuarioActual(), 
                    "CALCULAR_RETENCION_IVA", "Retención IVA año " + anio + ": $" + retencion);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Año inválido", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void verBitacora() {
        JDialog dialogo = new JDialog(this, "Bitácora de Auditoría", true);
        dialogo.setSize(800, 500);
        dialogo.setLocationRelativeTo(this);
        
        String[] columnas = {"ID", "Fecha/Hora", "Usuario", "Acción", "Descripción"};
        DefaultTableModel modelo = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        
        for (Bitacora b : controladorBitacora.getRegistros()) {
            modelo.addRow(new Object[]{
                b.getIdRegistro(),
                b.getFechaHoraFormateada(),
                b.getUsuario().getNombreUsuario(),
                b.getAccion(),
                b.getDescripcion()
            });
        }
        
        JTable tablaBitacora = new JTable(modelo);
        JScrollPane scroll = new JScrollPane(tablaBitacora);
        dialogo.add(scroll);
        dialogo.setVisible(true);
    }
    
    /**
     * Rechaza una transacción seleccionada (solo Jefatura Financiera)
     */
    private void rechazarTransaccion() {
        int filaSeleccionada = tabla.getSelectedRow();
        if (filaSeleccionada == -1) {
            mostrarAdvertencia("Por favor seleccione una transacción de la tabla\npara poder rechazarla.");
            return;
        }
        
        String id = String.valueOf(tableModel.getValueAt(filaSeleccionada, 0));
        String tipo = (String) tableModel.getValueAt(filaSeleccionada, 2);
        String provCliente = (String) tableModel.getValueAt(filaSeleccionada, 3);
        String montoStr = (String) tableModel.getValueAt(filaSeleccionada, 4);
        String estado = (String) tableModel.getValueAt(filaSeleccionada, 7);
        
        Usuario usuario = controladorUsuario.getUsuarioActual();
        
        // Verificar permisos
        if (!usuario.getRol().equals(Usuario.ROL_JEFATURA_FINANCIERA)) {
            mostrarError("❌ Acceso Denegado\n\n" +
                       "Solo los usuarios con rol de Jefatura Financiera\n" +
                       "pueden rechazar transacciones.\n\n" +
                       "Su rol actual: " + usuario.getRol());
            return;
        }
        
        // Verificar que la transacción esté en estado REGISTRADO
        if (!estado.equals(Transaccion.ESTADO_REGISTRADO)) {
            mostrarAdvertencia("⚠️ Esta transacción no puede ser rechazada\n\n" +
                             "Estado actual: " + estado + "\n\n" +
                             "Solo se pueden rechazar transacciones en estado " + Transaccion.ESTADO_REGISTRADO + ".");
            return;
        }
        
        // Mostrar información y solicitar motivo
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel lblInfo = new JLabel(String.format(
            "<html><b>📋 Información de la transacción:</b><br><br>" +
            "🔢 ID: %s<br>" +
            "📋 Tipo: %s<br>" +
            "👤 %s: %s<br>" +
            "💵 Monto: %s<br><br>" +
            "<b>Por favor ingrese el motivo del rechazo:</b></html>",
            id, tipo,
            tipo.equals("Factura") ? "Cliente" : "Proveedor",
            provCliente, montoStr));
        
        JTextArea txtMotivo = new JTextArea(3, 30);
        txtMotivo.setLineWrap(true);
        txtMotivo.setWrapStyleWord(true);
        txtMotivo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        
        panel.add(lblInfo, BorderLayout.NORTH);
        panel.add(new JScrollPane(txtMotivo), BorderLayout.CENTER);
        
        int opcion = JOptionPane.showConfirmDialog(this, panel,
            "Rechazar Transacción",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (opcion != JOptionPane.OK_OPTION) {
            return;
        }
        
        String motivo = txtMotivo.getText().trim();
        
        if (motivo.isEmpty()) {
            mostrarAdvertencia("Debe ingresar un motivo para rechazar la transacción.\n\n" +
                             "El motivo es importante para mantener un registro\n" +
                             "claro de las decisiones tomadas.");
            return;
        }
        
        // Rechazar transacción
        if (controladorTransaccion.rechazarTransaccion(id, usuario)) {
            mostrarExito("✅ Transacción rechazada exitosamente\n\n" +
                       "ID: " + id + "\n" +
                       tipo + ": " + provCliente + "\n" +
                       "Monto: " + montoStr + "\n\n" +
                       "Motivo del rechazo:\n" + motivo + "\n\n" +
                       "El rechazo ha sido registrado en la bitácora.");
            cargarDatosTabla();
            actualizarEstadisticas();
            controladorBitacora.registrar(usuario, "RECHAZAR_TRANSACCION", 
                "ID: " + id + " - Motivo: " + motivo);
        } else {
            mostrarError("❌ No se pudo rechazar la transacción\n\n" +
                       "Posibles causas:\n" +
                       "• La transacción no está en estado " + Transaccion.ESTADO_REGISTRADO + "\n" +
                       "• La transacción no existe\n" +
                       "• Error en el sistema\n\n" +
                       "Contacte al administrador si el problema persiste.");
        }
    }
    
    /**
     * Actualiza las estadísticas de la barra de estado
     */
    private void actualizarEstadisticas() {
        int cantidad = controladorTransaccion.getTransaccionesActivas().size();
        double total = controladorTransaccion.getTransaccionesActivas().stream()
            .mapToDouble(Transaccion::getMonto)
            .sum();
        
    lblCantidadTransacciones.setText("Transacciones: " + cantidad);
    lblTotalMontos.setText(String.format("Total: $%,.2f", total));
    }
    
    /**
     * Muestra estadísticas generales del sistema
     */
    private void mostrarEstadisticas() {
        java.util.List<Transaccion> transacciones = controladorTransaccion.getTransaccionesActivas();
        
        long facturas = transacciones.stream()
            .filter(t -> t.getTipoDocumento().equals("Factura"))
            .count();
        
        long gastos = transacciones.stream()
            .filter(t -> t.getTipoDocumento().equals("Gasto"))
            .count();
        
        double totalFacturas = transacciones.stream()
            .filter(t -> t.getTipoDocumento().equals("Factura"))
            .mapToDouble(Transaccion::getMonto)
            .sum();
        
        double totalGastos = transacciones.stream()
            .filter(t -> t.getTipoDocumento().equals("Gasto"))
            .mapToDouble(Transaccion::getMonto)
            .sum();
        
        long registradas = transacciones.stream()
            .filter(t -> t.getEstado().equals(Transaccion.ESTADO_REGISTRADO))
            .count();
        
        long aprobadas = transacciones.stream()
            .filter(t -> t.getEstado().equals(Transaccion.ESTADO_APROBADO))
            .count();
        
        long rechazadas = transacciones.stream()
            .filter(t -> t.getEstado().equals(Transaccion.ESTADO_RECHAZADO))
            .count();
        
        String mensaje = String.format(
            "📊 ESTADÍSTICAS DEL SISTEMA CONTABLE\n\n" +
            "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n" +
            "📈 TOTALES POR TIPO:\n" +
            "   • Facturas: %d (Total: $%,.2f)\n" +
            "   • Gastos: %d (Total: $%,.2f)\n\n" +
            "📋 ESTADO DE TRANSACCIONES:\n" +
            "   • Registradas: %d\n" +
            "   • Aprobadas: %d\n" +
            "   • Rechazadas: %d\n\n" +
            "💰 BALANCE:\n" +
            "   • Ingresos (Facturas): $%,.2f\n" +
            "   • Egresos (Gastos): $%,.2f\n" +
            "   • Balance Neto: $%,.2f\n\n" +
            "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
            "Total Transacciones: %d",
            facturas, totalFacturas,
            gastos, totalGastos,
            registradas, aprobadas, rechazadas,
            totalFacturas, totalGastos, (totalFacturas - totalGastos),
            transacciones.size()
        );
        
        JOptionPane.showMessageDialog(this, mensaje, 
            "Estadísticas Generales", JOptionPane.INFORMATION_MESSAGE);
        
        controladorBitacora.registrar(controladorUsuario.getUsuarioActual(), 
            "VER_ESTADISTICAS", "Consulta de estadísticas generales");
    }
    
    /**
     * Muestra los detalles completos de una transacción
     */
    private void mostrarDetalleTransaccion() {
        int filaSeleccionada = tabla.getSelectedRow();
        if (filaSeleccionada == -1) return;
        
        String id = (String) tableModel.getValueAt(filaSeleccionada, 0);
        Transaccion t = controladorTransaccion.getTransaccionesActivas().stream()
            .filter(trans -> trans.getIdTransaccion().equals(id))
            .findFirst()
            .orElse(null);
        
        if (t != null) {
            String detalles = String.format(
                "📄 DETALLES DE LA TRANSACCIÓN\n\n" +
                "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n" +
                "🔢 ID: %d\n" +
                "📅 Fecha: %s\n" +
                "📋 Tipo: %s\n" +
                "👤 Proveedor/Cliente: %s\n" +
                "💵 Monto: $%,.2f\n" +
                "🏦 Cuenta Contable: %s\n" +
                "🔢 Nº Documento: %s\n" +
                "📊 Estado: %s\n" +
                "👤 Usuario Registró: %s\n\n" +
                "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━",
                t.getIdTransaccion(),
                t.getFecha(),
                t.getTipoDocumento(),
                t.getProveedorCliente(),
                t.getMonto(),
                t.getCuentaContable(),
                t.getNumeroDocumento(),
                t.getEstado(),
                t.getUsuarioRegistro().getNombreCompleto()
            );
            
            JOptionPane.showMessageDialog(this, detalles, 
                "Detalle de Transacción #" + id, JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    /**
     * Muestra información sobre el sistema
     */
    private void mostrarAcercaDe() {
        String mensaje = 
            "📊 SISTEMA CONTABLE INTEGRADO\n\n" +
            "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n" +
            "🏢 Empresa:\n" +
            "   Comercial el mejor vendedor S.A.\n\n" +
            "👨‍💼 Contador:\n" +
            "   Diego Montesdeoca\n\n" +
            "📋 Características:\n" +
            "   ✓ Registro de Facturas y Gastos\n" +
            "   ✓ Cálculo Automático de IVA\n" +
            "   ✓ Control de Acceso por Roles\n" +
            "   ✓ Bitácora de Auditoría\n" +
            "   ✓ Respaldos Anuales\n" +
            "   ✓ Aprobación de Transacciones\n\n" +
            "🔧 Versión: 1.0.0\n" +
            "📅 Año: 2025\n\n" +
            "⚙️ Metodología:\n" +
            "   Desarrollo en Cascada Estructurada\n\n" +
            "🏛️ Arquitectura:\n" +
            "   Modelo-Vista-Controlador (MVC)\n\n" +
            "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n" +
            "© 2025 - Todos los derechos reservados";
        
        JOptionPane.showMessageDialog(this, mensaje, 
            "Acerca del Sistema", JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Muestra el manual de usuario
     */
    private void mostrarManualUsuario() {
        String manual = 
            "📖 MANUAL DE USUARIO - SISTEMA CONTABLE\n\n" +
            "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n" +
            "🔐 INICIO DE SESIÓN:\n" +
            "   1. Ingrese su usuario y contraseña\n" +
            "   2. Presione 'Ingresar' o Enter\n\n" +
            "📝 REGISTRAR TRANSACCIONES:\n" +
            "   1. Complete el formulario superior\n" +
            "   2. Seleccione tipo: Factura o Gasto\n" +
            "   3. Ingrese todos los datos requeridos\n" +
            "   4. Clic en '💾 Registrar Transacción'\n\n" +
            "✅ APROBAR TRANSACCIONES (Solo Jefatura):\n" +
            "   1. Seleccione una transacción en la tabla\n" +
            "   2. Menú: Transacciones > Aprobar\n" +
            "   3. O presione doble clic para ver detalles\n\n" +
            "❌ RECHAZAR TRANSACCIONES (Solo Jefatura):\n" +
            "   1. Seleccione una transacción\n" +
            "   2. Menú: Transacciones > Rechazar\n" +
            "   3. Ingrese el motivo del rechazo\n\n" +
            "🗑️ ELIMINAR TRANSACCIONES:\n" +
            "   1. Seleccione la transacción\n" +
            "   2. Menú: Transacciones > Eliminar\n" +
            "   3. Confirme la eliminación\n\n" +
            "💰 CALCULAR RETENCIÓN IVA:\n" +
            "   1. Menú: Reportes > Calcular Retención de IVA\n" +
            "   2. Ingrese el año deseado\n" +
            "   3. Ver el cálculo de retención (30% del IVA en compras)\n\n" +
            "📊 VER ESTADÍSTICAS:\n" +
            "   1. Menú: Reportes > Estadísticas Generales\n" +
            "   2. Revise los totales y balances\n\n" +
            "📝 BITÁCORA DE AUDITORÍA:\n" +
            "   1. Menú: Reportes > Ver Bitácora\n" +
            "   2. Revise todas las operaciones\n\n" +
            "💾 GENERAR RESPALDOS:\n" +
            "   1. Menú: Archivo > Generar Respaldo Anual\n" +
            "   2. Ingrese el año\n" +
            "   3. El archivo .exe se guardará\n\n" +
            "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n" +
            "ℹ️ ATAJOS DE TECLADO:\n" +
            "   • Enter en campos: Avanzar al siguiente\n" +
            "   • Doble clic en tabla: Ver detalles\n" +
            "   • Botón Actualizar: Recargar datos\n\n" +
            "⚠️ PERMISOS POR ROL:\n\n" +
            "   Asistente Contable:\n" +
            "   • Registrar transacciones\n" +
            "   • Ver todas las transacciones\n" +
            "   • Consultar reportes\n\n" +
            "   Jefatura Financiera:\n" +
            "   • Todo lo del Asistente\n" +
            "   • Aprobar transacciones\n" +
            "   • Rechazar transacciones\n" +
            "   • Eliminar transacciones\n" +
            "   • Generar respaldos\n\n" +
            "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━";
        
        JTextArea textArea = new JTextArea(manual);
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        textArea.setCaretPosition(0);
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(600, 500));
        
        JOptionPane.showMessageDialog(this, scrollPane, 
            "Manual de Usuario", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void cerrarSesion() {
        int opcion = JOptionPane.showConfirmDialog(this, 
            "¿Está seguro que desea cerrar sesión?\n\n" +
            "Se perderán los datos no guardados.", 
            "Confirmar Cierre de Sesión", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        
        if (opcion == JOptionPane.YES_OPTION) {
            controladorBitacora.registrar(controladorUsuario.getUsuarioActual(), 
                "CERRAR_SESION", "Usuario cerró sesión");
            controladorUsuario.cerrarSesion();
            this.dispose();
            SwingUtilities.invokeLater(() -> {
                new InterfazLoginMejorada().setVisible(true);
            });
        }
    }
    
    private void crearBarraMenu() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(Color.WHITE);
        
        // Obtener usuario actual y sus permisos
        Usuario usuarioActual = controladorUsuario.getUsuarioActual();
        boolean esJefatura = usuarioActual.getRol().equals(Usuario.ROL_JEFATURA_FINANCIERA);
        
        // Menú Archivo
        JMenu menuArchivo = new JMenu("Archivo");
        menuArchivo.setFont(new Font("Arial", Font.PLAIN, 13));
        menuArchivo.setToolTipText("Opciones de archivo y respaldo");
        
        // Funcionalidad de respaldo local en JSON eliminada en versión MongoDB 2.0
        
        JMenuItem itemSalir = new JMenuItem("Cerrar Sesión");
        itemSalir.setToolTipText("Cierra la sesión actual y regresa al login");
        itemSalir.addActionListener(e -> cerrarSesion());
        
        // Solo opción de cerrar sesión permanece
        menuArchivo.add(itemSalir);
        
        // Menú Transacciones
        JMenu menuTransacciones = new JMenu("Transacciones");
        menuTransacciones.setFont(new Font("Arial", Font.PLAIN, 13));
        menuTransacciones.setToolTipText("Gestión de facturas y gastos");
        
        JMenuItem itemNueva = new JMenuItem("Registrar Nueva Transacción");
        itemNueva.setToolTipText("Limpia el formulario para registrar una nueva factura o gasto");
        itemNueva.addActionListener(e -> limpiarFormulario());
        
        JMenuItem itemEliminar = new JMenuItem("Eliminar Transacción");
        itemEliminar.setToolTipText("Marca como eliminada la transacción seleccionada (requiere permisos)");
        itemEliminar.addActionListener(e -> eliminarTransaccion());
        itemEliminar.setEnabled(esJefatura); // Solo jefatura puede eliminar
        
        JMenuItem itemAprobar = new JMenuItem("Aprobar Transacción");
        itemAprobar.setToolTipText("Aprueba la transacción seleccionada (solo Jefatura Financiera)");
        itemAprobar.addActionListener(e -> aprobarTransaccion());
        itemAprobar.setEnabled(esJefatura); // Solo jefatura puede aprobar
        
        JMenuItem itemRechazar = new JMenuItem("Rechazar Transacción");
        itemRechazar.setToolTipText("Rechaza la transacción seleccionada (solo Jefatura Financiera)");
        itemRechazar.addActionListener(e -> rechazarTransaccion());
        itemRechazar.setEnabled(esJefatura); // Solo jefatura puede rechazar
        
        menuTransacciones.add(itemNueva);
        menuTransacciones.addSeparator();
        menuTransacciones.add(itemAprobar);
        menuTransacciones.add(itemRechazar);
        menuTransacciones.addSeparator();
        menuTransacciones.add(itemEliminar);
        
        // Menú Reportes
        JMenu menuReportes = new JMenu("Reportes");
        menuReportes.setFont(new Font("Arial", Font.PLAIN, 13));
        menuReportes.setToolTipText("Consultas y reportes contables");
        
        JMenuItem itemIVA = new JMenuItem("Calcular Retención de IVA");
        itemIVA.setToolTipText("Calcula la retención de IVA (30%) sobre compras del año");
        itemIVA.addActionListener(e -> calcularIVA());
        
        JMenuItem itemBitacora = new JMenuItem("Ver Bitácora de Auditoría");
        itemBitacora.setToolTipText("Muestra el registro de todas las operaciones del sistema");
        itemBitacora.addActionListener(e -> verBitacora());
        itemBitacora.setEnabled(esJefatura); // Solo jefatura puede ver bitácora completa
        
        JMenuItem itemEstadisticas = new JMenuItem("Estadísticas Generales");
        itemEstadisticas.setToolTipText("Muestra estadísticas y resumen de transacciones");
        itemEstadisticas.addActionListener(e -> mostrarEstadisticas());
        
        menuReportes.add(itemIVA);
        menuReportes.add(itemBitacora);
        menuReportes.add(itemEstadisticas);
        
        // Menú Ayuda
        JMenu menuAyuda = new JMenu("Ayuda");
        menuAyuda.setFont(new Font("Arial", Font.PLAIN, 13));
        
        JMenuItem itemAcerca = new JMenuItem("Acerca del Sistema");
        itemAcerca.setToolTipText("Información sobre el sistema contable");
        itemAcerca.addActionListener(e -> mostrarAcercaDe());
        
        JMenuItem itemManual = new JMenuItem("Manual de Usuario");
        itemManual.setToolTipText("Guía de uso del sistema");
        itemManual.addActionListener(e -> mostrarManualUsuario());
        
        menuAyuda.add(itemManual);
        menuAyuda.add(itemAcerca);
        
        menuBar.add(menuArchivo);
        menuBar.add(menuTransacciones);
        menuBar.add(menuReportes);
        menuBar.add(menuAyuda);
        setJMenuBar(menuBar);
        
        // Actualizar tooltips para elementos deshabilitados
        actualizarTooltipsPermisos(menuArchivo, esJefatura);
        actualizarTooltipsPermisos(menuTransacciones, esJefatura);
        actualizarTooltipsPermisos(menuReportes, esJefatura);
    }

    /**
     * Actualiza los tooltips de los elementos de menú para mostrar información de permisos
     */
    private void actualizarTooltipsPermisos(JMenu menu, boolean esJefatura) {
        for (Component comp : menu.getMenuComponents()) {
            if (comp instanceof JMenuItem) {
                JMenuItem item = (JMenuItem) comp;
                if (!item.isEnabled() && !esJefatura) {
                    // Agregar información de permisos al tooltip
                    String tooltipActual = item.getToolTipText();
                    if (tooltipActual != null) {
                        item.setToolTipText(tooltipActual + " - Requiere permisos de Jefatura Financiera");
                    } else {
                        item.setToolTipText("Requiere permisos de Jefatura Financiera");
                    }
                }
            }
        }
    }
}
