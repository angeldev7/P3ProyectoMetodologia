// View/PanelProductos.java
package view;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.border.*;
import java.awt.*;

public class PanelProductos extends JPanel {
    private static final long serialVersionUID = 1L;
    public JTextField txtCodigo, txtNombre, txtStock, txtPrecio, txtStockMinimo, txtBuscar;
    public JTextArea txtDescripcion;
    public JButton btnGuardarProducto, btnNuevoProducto, btnEliminarProducto;
    public JTable tablaProductos;
    public DefaultTableModel modeloTabla;
    public TableRowSorter<DefaultTableModel> ordenador;

    public PanelProductos() {
        setBackground(new Color(45, 45, 45));
        setBorder(new TitledBorder(new LineBorder(new Color(90, 90, 90)), " Gestión de Productos ", 
                  TitledBorder.LEADING, TitledBorder.TOP, 
                  new Font(Font.SANS_SERIF, Font.BOLD, 14), new Color(220, 220, 220)));
        
        inicializarComponentes();
        configurarLayout();
    }

    private void inicializarComponentes() {
        // Campos de texto
        txtCodigo = crearCampoTexto();
        txtNombre = crearCampoTexto();
        txtStock = crearCampoTexto();
        txtPrecio = crearCampoTexto();
        txtStockMinimo = crearCampoTexto();
        txtBuscar = crearCampoTexto();

        // Área de texto
        txtDescripcion = new JTextArea(3, 20);
        txtDescripcion.setLineWrap(true);
        txtDescripcion.setWrapStyleWord(true);
        estiloAreaTexto(txtDescripcion);

        // Botones
        btnGuardarProducto = crearBoton("💾 Guardar Producto", new Color(0, 123, 255));
        btnNuevoProducto = crearBoton("✨ Nuevo Producto", new Color(40, 167, 69));
        btnEliminarProducto = crearBoton("🗑️ Eliminar Producto", new Color(220, 53, 69));

        // Tabla
        String[] nombresColumnas = {"Código", "Nombre", "Descripción", "Stock", "Precio", "Stock Mínimo", "Estado"};
        modeloTabla = new DefaultTableModel(nombresColumnas, 0) {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        
        tablaProductos = new JTable(modeloTabla);
        estiloTabla(tablaProductos);
        
        ordenador = new TableRowSorter<>(modeloTabla);
        tablaProductos.setRowSorter(ordenador);
    }

    private void configurarLayout() {
        setLayout(new BorderLayout(10, 10));
        
        // Panel principal con padding
        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBackground(new Color(45, 45, 45));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Panel de formulario
        JPanel panelFormulario = crearPanelFormulario();
        panelPrincipal.add(panelFormulario, BorderLayout.NORTH);
        
        // Panel de búsqueda
        JPanel panelBusqueda = crearPanelBusqueda();
        panelPrincipal.add(panelBusqueda, BorderLayout.CENTER);
        
        // Panel de tabla
        JPanel panelTabla = crearPanelTabla();
        panelPrincipal.add(panelTabla, BorderLayout.SOUTH);
        
        add(panelPrincipal, BorderLayout.CENTER);
    }

    private JPanel crearPanelFormulario() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(55, 55, 55));
        panel.setBorder(new TitledBorder(new LineBorder(new Color(90, 90, 90)), " Formulario de Producto ", 
                    TitledBorder.LEADING, TitledBorder.TOP, 
                    new Font(Font.SANS_SERIF, Font.BOLD, 12), new Color(200, 200, 200)));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Fila 0: Código y Nombre
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(crearEtiqueta("Código Producto:"), gbc);
        
        gbc.gridx = 1;
        panel.add(txtCodigo, gbc);
        
        gbc.gridx = 2;
        panel.add(crearEtiqueta("Nombre Producto:"), gbc);
        
        gbc.gridx = 3;
        panel.add(txtNombre, gbc);
        
        // Fila 1: Descripción
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(crearEtiqueta("Descripción:"), gbc);
        
        gbc.gridx = 1; gbc.gridwidth = 3;
        JScrollPane scrollDescripcion = new JScrollPane(txtDescripcion);
        scrollDescripcion.setPreferredSize(new Dimension(0, 60));
        panel.add(scrollDescripcion, gbc);
        gbc.gridwidth = 1;
        
        // Fila 2: Stock, Precio, Stock Mínimo
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(crearEtiqueta("Stock:"), gbc);
        
        gbc.gridx = 1;
        panel.add(txtStock, gbc);
        
        gbc.gridx = 2;
        panel.add(crearEtiqueta("Precio:"), gbc);
        
        gbc.gridx = 3;
        panel.add(txtPrecio, gbc);
        
        // Fila 3: Stock Mínimo y botones
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(crearEtiqueta("Stock Mínimo:"), gbc);
        
        gbc.gridx = 1;
        panel.add(txtStockMinimo, gbc);
        
        gbc.gridx = 2; gbc.gridwidth = 2;
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panelBotones.setBackground(new Color(55, 55, 55));
        panelBotones.add(btnGuardarProducto);
        panelBotones.add(btnNuevoProducto);
        panelBotones.add(btnEliminarProducto);
        panel.add(panelBotones, gbc);
        
        return panel;
    }

    private JPanel crearPanelBusqueda() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBackground(new Color(55, 55, 55));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        
        panel.add(crearEtiqueta("🔍 Buscar:"));
        panel.add(txtBuscar);
        txtBuscar.setPreferredSize(new Dimension(200, 30));
        
        return panel;
    }

    private JPanel crearPanelTabla() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(55, 55, 55));
        panel.setBorder(new TitledBorder(new LineBorder(new Color(90, 90, 90)), " Lista de Productos ", 
                    TitledBorder.LEADING, TitledBorder.TOP, 
                    new Font(Font.SANS_SERIF, Font.BOLD, 12), new Color(200, 200, 200)));
        
        JScrollPane scrollTabla = new JScrollPane(tablaProductos);
        scrollTabla.setBorder(new LineBorder(new Color(90, 90, 90)));
        scrollTabla.setPreferredSize(new Dimension(0, 250));
        
        panel.add(scrollTabla, BorderLayout.CENTER);
        return panel;
    }

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

    private void estiloAreaTexto(JTextArea areaTexto) {
        areaTexto.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        areaTexto.setBackground(new Color(60, 60, 60));
        areaTexto.setForeground(new Color(220, 220, 220));
        areaTexto.setCaretColor(Color.WHITE);
        areaTexto.setBorder(new LineBorder(new Color(90, 90, 90), 2));
    }

    private JButton crearBoton(String texto, Color colorFondo) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        boton.setBackground(colorFondo);
        boton.setForeground(Color.WHITE);
        boton.setFocusPainted(false);
        boton.setBorder(new LineBorder(colorFondo.darker(), 2));
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boton.setPreferredSize(new Dimension(160, 35));
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

    public void filtrarTabla(String consulta) {
        if (consulta.trim().length() == 0) {
            ordenador.setRowFilter(null);
        } else {
            ordenador.setRowFilter(RowFilter.regexFilter("(?i)" + consulta));
        }
    }
}