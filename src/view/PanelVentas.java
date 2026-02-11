// view/PanelVentas.java
package view;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.border.*;
import java.awt.*;
import model.CarritoCompra;
import model.ItemCarrito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PanelVentas extends JPanel {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(PanelVentas.class);
    // Componentes para carrito
    public JComboBox<String> cmbProductos;
    public JTextField txtCantidad;
    public JButton btnAgregarAlCarrito, btnEliminarDelCarrito, btnProcesarVenta;
    public JTable tablaCarrito, tablaVentas;
    public DefaultTableModel modeloCarrito, modeloTabla;
    public JLabel lblTotalCarrito, lblCantidadItems;
    
    private transient CarritoCompra carrito;

    public PanelVentas() {
        setBackground(new Color(45, 45, 45));
        setBorder(new TitledBorder(new LineBorder(new Color(90, 90, 90)), " 🛒 Módulo de Ventas ", 
                  TitledBorder.LEADING, TitledBorder.TOP, 
                  new Font(Font.SANS_SERIF, Font.BOLD, 14), new Color(220, 220, 220)));
        
        inicializarComponentes();
        configurarLayout();
    }

    private void inicializarComponentes() {
        // COMPONENTES PARA CARRITO
        cmbProductos = new JComboBox<>();
        estiloComboBox(cmbProductos);
        
        txtCantidad = crearCampoTexto();
        txtCantidad.setText("1");
        txtCantidad.setToolTipText("Ingrese la cantidad a agregar al carrito");
        
        btnAgregarAlCarrito = crearBoton("➕ Agregar al Carrito", new Color(0, 123, 255));
        btnEliminarDelCarrito = crearBoton("Eliminar del Carrito", new Color(220, 53, 69));
        btnProcesarVenta = crearBoton("Procesar Venta", new Color(40, 167, 69));
        
        lblTotalCarrito = crearEtiqueta("$0.00");
        lblCantidadItems = crearEtiqueta("0");
        
        // Tabla de carrito
        String[] columnasCarrito = {"Código", "Producto", "Precio Unit.", "Cantidad", "Subtotal"};
        modeloCarrito = new DefaultTableModel(columnasCarrito, 0) {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 3) return Integer.class; // Cantidad
                if (columnIndex == 2 || columnIndex == 4) return String.class; // Precio y Subtotal
                return String.class;
            }
        };
        tablaCarrito = new JTable(modeloCarrito);
        estiloTabla(tablaCarrito);
        
        // Tabla de ventas
        String[] columnasVentas = {"Código", "Producto", "Cantidad", "Precio Unit.", "Total", "Fecha"};
        modeloTabla = new DefaultTableModel(columnasVentas, 0) {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaVentas = new JTable(modeloTabla);
        estiloTabla(tablaVentas);
    }

    private void configurarLayout() {
        setLayout(new BorderLayout(10, 10));
        
        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBackground(new Color(45, 45, 45));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Panel de pestañas
        JTabbedPane panelPestanas = new JTabbedPane();
        panelPestanas.setBackground(new Color(45, 45, 45));
        panelPestanas.setForeground(Color.WHITE);
        
        // Agregar pestañas - solo carrito e historial
        panelPestanas.addTab("🛒 Realizar Venta", crearPanelCarrito());
        panelPestanas.addTab("Historial Ventas", crearPanelHistorialVentas());
        
        panelPrincipal.add(panelPestanas, BorderLayout.CENTER);
        add(panelPrincipal, BorderLayout.CENTER);
    }

    private JPanel crearPanelCarrito() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(55, 55, 55));
        
        // Panel superior - formulario para agregar productos
        JPanel panelFormulario = crearFormularioCarrito();
        panel.add(panelFormulario, BorderLayout.NORTH);
        
        // Panel central - carrito
        JPanel panelCarrito = crearPanelCarritoContenido();
        panel.add(panelCarrito, BorderLayout.CENTER);
        
        return panel;
    }

    private JPanel crearFormularioCarrito() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(55, 55, 55));
        panel.setBorder(new TitledBorder(new LineBorder(new Color(90, 90, 90)), " Agregar Productos ", 
                    TitledBorder.LEADING, TitledBorder.TOP, 
                    new Font(Font.SANS_SERIF, Font.BOLD, 12), new Color(200, 200, 200)));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Fila 0: Producto
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(crearEtiqueta("Producto:"), gbc);
        
        gbc.gridx = 1;
        cmbProductos.setPreferredSize(new Dimension(200, 30));
        panel.add(cmbProductos, gbc);
        
        // Fila 0: Cantidad
        gbc.gridx = 2;
        panel.add(crearEtiqueta("Cantidad:"), gbc);
        
        gbc.gridx = 3;
        txtCantidad.setPreferredSize(new Dimension(80, 30));
        panel.add(txtCantidad, gbc);
        
        // Fila 1: Botones
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 4;
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panelBotones.setBackground(new Color(55, 55, 55));
        panelBotones.add(btnAgregarAlCarrito);
        panelBotones.add(btnProcesarVenta);
        panel.add(panelBotones, gbc);
        
        return panel;
    }

    private JPanel crearPanelCarritoContenido() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(55, 55, 55));
        
        // Panel de información del carrito
        JPanel panelInfo = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 5));
        panelInfo.setBackground(new Color(55, 55, 55));
        panelInfo.setBorder(new TitledBorder(new LineBorder(new Color(90, 90, 90)), " Resumen del Carrito ", 
                    TitledBorder.LEADING, TitledBorder.TOP, 
                    new Font(Font.SANS_SERIF, Font.BOLD, 12), new Color(200, 200, 200)));
        
        panelInfo.add(crearEtiqueta("Productos:"));
        panelInfo.add(lblCantidadItems);
        panelInfo.add(crearEtiqueta("Total:"));
        panelInfo.add(lblTotalCarrito);
        panelInfo.add(btnEliminarDelCarrito);
        
        // Tabla del carrito
        JScrollPane scrollCarrito = new JScrollPane(tablaCarrito);
        scrollCarrito.setBorder(new LineBorder(new Color(90, 90, 90)));
        scrollCarrito.setPreferredSize(new Dimension(0, 200));
        
        panel.add(panelInfo, BorderLayout.NORTH);
        panel.add(scrollCarrito, BorderLayout.CENTER);
        
        return panel;
    }

    private JPanel crearPanelHistorialVentas() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(55, 55, 55));
        panel.setBorder(new TitledBorder(new LineBorder(new Color(90, 90, 90)), " Historial de Ventas ", 
                    TitledBorder.LEADING, TitledBorder.TOP, 
                    new Font(Font.SANS_SERIF, Font.BOLD, 12), new Color(200, 200, 200)));
        
        JScrollPane scrollTabla = new JScrollPane(tablaVentas);
        scrollTabla.setBorder(new LineBorder(new Color(90, 90, 90)));
        
        panel.add(scrollTabla, BorderLayout.CENTER);
        return panel;
    }

    // Métodos auxiliares para crear componentes
    private JLabel crearEtiqueta(String texto) {
        JLabel etiqueta = new JLabel(texto);
        etiqueta.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        etiqueta.setForeground(new Color(200, 200, 200));
        return etiqueta;
    }

    private JTextField crearCampoTexto() {
        JTextField campoTexto = new JTextField();
        campoTexto.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        campoTexto.setBackground(new Color(60, 60, 60));
        campoTexto.setForeground(new Color(220, 220, 220));
        campoTexto.setCaretColor(Color.WHITE);
        campoTexto.setBorder(new LineBorder(new Color(90, 90, 90), 2));
        campoTexto.setPreferredSize(new Dimension(150, 30));
        return campoTexto;
    }

    private void estiloComboBox(JComboBox<String> comboBox) {
        comboBox.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        comboBox.setBackground(new Color(60, 60, 60));
        comboBox.setForeground(new Color(220, 220, 220));
        comboBox.setBorder(new LineBorder(new Color(90, 90, 90), 2));
    }

    private JButton crearBoton(String texto, Color colorFondo) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        boton.setBackground(colorFondo);
        boton.setForeground(Color.WHITE);
        boton.setFocusPainted(false);
        boton.setBorder(new LineBorder(colorFondo.darker(), 2));
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boton.setPreferredSize(new Dimension(180, 35));
        return boton;
    }

    private void estiloTabla(JTable tabla) {
        tabla.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        tabla.setRowHeight(25);
        tabla.setShowGrid(false);
        tabla.setBackground(new Color(60, 60, 60));
        tabla.setForeground(new Color(220, 220, 220));
        tabla.setSelectionBackground(new Color(0, 123, 255));
        tabla.setSelectionForeground(Color.WHITE);
        
        JTableHeader header = tabla.getTableHeader();
        header.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        header.setBackground(new Color(30, 30, 30));
        header.setForeground(new Color(0, 123, 255));
        header.setReorderingAllowed(false);
    }

    // Métodos para manejar el carrito
    public void actualizarCarritoEnTabla() {
        if (modeloCarrito != null && carrito != null) {
            modeloCarrito.setRowCount(0); // Limpiar tabla primero
            
            logger.info("Actualizando tabla del carrito. Items: " + carrito.getItems().size());
            
            for (ItemCarrito item : carrito.getItems()) {
                Object[] fila = {
                    item.getCodigoProducto(),
                    item.getNombreProducto(),
                    String.format("$%.2f", item.getPrecioUnitario()),
                    item.getCantidad(),
                    String.format("$%.2f", item.getSubtotal())
                };
                modeloCarrito.addRow(fila);
                logger.info("Agregado a tabla: " + item.getNombreProducto() + " x " + item.getCantidad());
            }
            
            // Actualizar total y cantidad
            lblTotalCarrito.setText(String.format("$%.2f", carrito.getTotal()));
            lblCantidadItems.setText(String.valueOf(carrito.getCantidadItems()));
            
            // Forzar repintado
            modeloCarrito.fireTableDataChanged();
            repaint();
            
            logger.info("Tabla del carrito actualizada. Total: $" + carrito.getTotal());
        } else {
<<<<<<< HEAD
            logger.error("❌ Error: carrito o modeloCarrito es null");
=======
            System.err.println("Error: carrito o modeloCarrito es null");
>>>>>>> origin/Test
        }
    }

    public void setCarrito(CarritoCompra carrito) {
        this.carrito = carrito;
        logger.info("Carrito asignado al panel de ventas");
        actualizarCarritoEnTabla();
    }

    public void limpiarFormulario() {
        txtCantidad.setText("1");
        if (cmbProductos.getItemCount() > 0) {
            cmbProductos.setSelectedIndex(0);
        }
    }
    
    // NUEVO: Método para obtener el carrito
    public CarritoCompra getCarrito() {
        return carrito;
    }
    
    // NUEVO: Método para actualizar combo de productos
    public void actualizarComboProductos(java.util.List<String> productos) {
        cmbProductos.removeAllItems();
        for (String producto : productos) {
            cmbProductos.addItem(producto);
        }
        if (cmbProductos.getItemCount() > 0) {
            cmbProductos.setSelectedIndex(0);
        }
    }
    
    // NUEVO: Método para obtener la cantidad ingresada
    public int getCantidad() {
        try {
            return Integer.parseInt(txtCantidad.getText().trim());
        } catch (NumberFormatException e) {
            return 1; // Valor por defecto
        }
    }
    
    // NUEVO: Método para obtener el producto seleccionado
    public String getProductoSeleccionado() {
        return (String) cmbProductos.getSelectedItem();
    }
}