// View/PanelGestionUsuarios.java
package view;

import javax.swing.*;

import javax.swing.table.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;

public class PanelGestionUsuarios extends JPanel {
    private static final long serialVersionUID = 1L;
    
    // Componentes de usuario
    public JTextField txtUsuario, txtNombreCompleto, txtBuscar, txtNombreRol;
    public JPasswordField txtContrasena;
    public JComboBox<String> cmbRol;
    public JButton btnGuardarUsuario, btnNuevoUsuario, btnEliminarUsuario, btnResetearContrasena;
    public JButton btnGuardarRol, btnNuevoRol;
    public JCheckBox chkGestionarProductos, chkAccederVentas, chkVerReportes, chkExportarDatos, chkGestionarUsuarios;
    public JButton btnBloquearUsuario, btnDesbloquearUsuario; // NUEVO
    public JButton btnEditarRol, btnEliminarRol; // NUEVO para roles
    // Tablas
    public JTable tablaUsuarios, tablaRoles;
    public DefaultTableModel modeloTablaUsuarios, modeloTablaRoles;

    public PanelGestionUsuarios() {
        setBackground(new Color(45, 45, 45));
        setBorder(new TitledBorder(new LineBorder(new Color(90, 90, 90)), " Gestión de Usuarios y Roles ", 
                  TitledBorder.LEADING, TitledBorder.TOP, 
                  new Font(Font.SANS_SERIF, Font.BOLD, 14), new Color(220, 220, 220)));
        
        inicializarComponentes();
        configurarLayout();
    }

    private void inicializarComponentes() {
        // Campos de texto
        txtUsuario = crearCampoTexto();
        txtNombreCompleto = crearCampoTexto();
        txtBuscar = crearCampoTexto();
        txtNombreRol = crearCampoTexto();
        txtContrasena = new JPasswordField();
        estiloCampoTexto(txtContrasena);

        // ComboBox
        cmbRol = new JComboBox<>();
        estiloComboBox(cmbRol);

        // Botones
        btnGuardarUsuario = crearBoton("💾 Guardar Usuario", new Color(0, 123, 255));
        btnNuevoUsuario = crearBoton("✨ Nuevo Usuario", new Color(40, 167, 69));
        btnEliminarUsuario = crearBoton("🗑️ Eliminar Usuario", new Color(220, 53, 69));
        btnResetearContrasena = crearBoton("🔑 Resetear Contraseña", new Color(255, 193, 7));
        btnGuardarRol = crearBoton("💾 Guardar Rol", new Color(108, 117, 125));
        btnNuevoRol = crearBoton("✨ Nuevo Rol", new Color(40, 167, 69));
        btnBloquearUsuario = crearBoton("🚫 Bloquear Usuario", new Color(255, 193, 7));
        btnDesbloquearUsuario = crearBoton("✅ Desbloquear Usuario", new Color(40, 167, 69));
        btnEditarRol = crearBoton("✏️ Editar Rol", new Color(108, 117, 125));
        btnEliminarRol = crearBoton("🗑️ Eliminar Rol", new Color(220, 53, 69));

        // Checkboxes de permisos
        chkGestionarProductos = crearCheckbox("Gestionar Productos");
        chkAccederVentas = crearCheckbox("Acceder a Ventas");
        chkVerReportes = crearCheckbox("Ver Reportes");
        chkExportarDatos = crearCheckbox("Exportar Datos");
        chkGestionarUsuarios = crearCheckbox("Gestionar Usuarios");

        // Tablas
        String[] columnasUsuarios = {"Usuario", "Nombre Completo", "Rol", "Fecha Creación", "Estado", "Bloqueado"};
        modeloTablaUsuarios = new DefaultTableModel(columnasUsuarios, 0) {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        
        String[] columnasRoles = {"Nombre Rol", "Permisos", "Usuarios", "Fecha Creación"};
        modeloTablaRoles = new DefaultTableModel(columnasRoles, 0) {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        
        tablaUsuarios = new JTable(modeloTablaUsuarios);
        tablaRoles = new JTable(modeloTablaRoles);
        estiloTabla(tablaUsuarios);
        estiloTabla(tablaRoles);
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
        
        // Agregar pestañas
        panelPestanas.addTab("👥 Gestión de Usuarios", crearPanelUsuarios());
        panelPestanas.addTab("🔐 Gestión de Roles", crearPanelRoles());
        
        panelPrincipal.add(panelPestanas, BorderLayout.CENTER);
        add(panelPrincipal, BorderLayout.CENTER);
    }

    private JPanel crearPanelUsuarios() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(55, 55, 55));
        
        // Formulario de usuario
        JPanel panelFormulario = crearFormularioUsuario();
        panel.add(panelFormulario, BorderLayout.NORTH);
        
        // Tabla de usuarios
        JPanel panelTabla = crearPanelTablaUsuarios();
        panel.add(panelTabla, BorderLayout.CENTER);
        
        return panel;
    }

    private JPanel crearPanelRoles() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(55, 55, 55));
        
        // Formulario de roles
        JPanel panelFormulario = crearFormularioRol();
        panel.add(panelFormulario, BorderLayout.NORTH);
        
        // Tabla de roles
        JPanel panelTabla = crearPanelTablaRoles();
        panel.add(panelTabla, BorderLayout.CENTER);
        
        return panel;
    }

    private JPanel crearFormularioUsuario() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(55, 55, 55));
        panel.setBorder(new TitledBorder(new LineBorder(new Color(90, 90, 90)), " Formulario de Usuario ", 
                    TitledBorder.LEADING, TitledBorder.TOP, 
                    new Font(Font.SANS_SERIF, Font.BOLD, 12), new Color(200, 200, 200)));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Fila 0: Usuario y Contraseña
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(crearEtiqueta("Usuario:"), gbc);
        
        gbc.gridx = 1;
        panel.add(txtUsuario, gbc);
        
        gbc.gridx = 2;
        panel.add(crearEtiqueta("Contraseña:"), gbc);
        
        gbc.gridx = 3;
        panel.add(txtContrasena, gbc);
        
        // Fila 1: Nombre Completo y Rol
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(crearEtiqueta("Nombre Completo:"), gbc);
        
        gbc.gridx = 1;
        panel.add(txtNombreCompleto, gbc);
        
        gbc.gridx = 2;
        panel.add(crearEtiqueta("Rol:"), gbc);
        
        gbc.gridx = 3;
        cmbRol.setPreferredSize(new Dimension(150, 30));
        panel.add(cmbRol, gbc);
        
        // Fila 2: Botones
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 4;
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panelBotones.setBackground(new Color(55, 55, 55));
        panelBotones.add(btnGuardarUsuario);
        panelBotones.add(btnNuevoUsuario);
        panelBotones.add(btnBloquearUsuario); // NUEVO
        panelBotones.add(btnDesbloquearUsuario); // NUEVO
        panelBotones.add(btnEliminarUsuario);
        panelBotones.add(btnResetearContrasena);
        panel.add(panelBotones, gbc);
        
        return panel;
    }

    private JPanel crearFormularioRol() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(55, 55, 55));
        panel.setBorder(new TitledBorder(new LineBorder(new Color(90, 90, 90)), " Formulario de Rol ", 
                    TitledBorder.LEADING, TitledBorder.TOP, 
                    new Font(Font.SANS_SERIF, Font.BOLD, 12), new Color(200, 200, 200)));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Fila 0: Nombre del Rol
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(crearEtiqueta("Nombre del Rol:"), gbc);
        
        gbc.gridx = 1; gbc.gridwidth = 3;
        panel.add(txtNombreRol, gbc);
        gbc.gridwidth = 1;
        
        // Fila 1: Permisos
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(crearEtiqueta("Permisos:"), gbc);
        
        gbc.gridx = 1; gbc.gridwidth = 3;
        JPanel panelPermisos = new JPanel(new GridLayout(2, 3, 10, 5));
        panelPermisos.setBackground(new Color(55, 55, 55));
        panelPermisos.add(chkGestionarProductos);
        panelPermisos.add(chkAccederVentas);
        panelPermisos.add(chkVerReportes);
        panelPermisos.add(chkExportarDatos);
        panelPermisos.add(chkGestionarUsuarios);
        panel.add(panelPermisos, gbc);
        gbc.gridwidth = 1;
        
        // Fila 2: Botones
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 4;
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panelBotones.setBackground(new Color(55, 55, 55));
        panelBotones.add(btnGuardarRol);
        panelBotones.add(btnNuevoRol);
        panelBotones.add(btnEditarRol); // NUEVO
        panelBotones.add(btnEliminarRol); // NUEVO
        panel.add(panelBotones, gbc);
        
        return panel;
    }

    private JPanel crearPanelTablaUsuarios() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(55, 55, 55));
        panel.setBorder(new TitledBorder(new LineBorder(new Color(90, 90, 90)), " Lista de Usuarios ", 
                    TitledBorder.LEADING, TitledBorder.TOP, 
                    new Font(Font.SANS_SERIF, Font.BOLD, 12), new Color(200, 200, 200)));
        
        // Panel de búsqueda
        JPanel panelBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelBusqueda.setBackground(new Color(55, 55, 55));
        panelBusqueda.add(crearEtiqueta("🔍 Buscar:"));
        txtBuscar.setPreferredSize(new Dimension(200, 30));
        panelBusqueda.add(txtBuscar);
        
        // Tabla
        JScrollPane scrollTabla = new JScrollPane(tablaUsuarios);
        scrollTabla.setBorder(new LineBorder(new Color(90, 90, 90)));
        scrollTabla.setPreferredSize(new Dimension(0, 250));
        
        panel.add(panelBusqueda, BorderLayout.NORTH);
        panel.add(scrollTabla, BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearPanelTablaRoles() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(55, 55, 55));
        panel.setBorder(new TitledBorder(new LineBorder(new Color(90, 90, 90)), " Lista de Roles ", 
                    TitledBorder.LEADING, TitledBorder.TOP, 
                    new Font(Font.SANS_SERIF, Font.BOLD, 12), new Color(200, 200, 200)));
        
        JScrollPane scrollTabla = new JScrollPane(tablaRoles);
        scrollTabla.setBorder(new LineBorder(new Color(90, 90, 90)));
        scrollTabla.setPreferredSize(new Dimension(0, 300));
        
        panel.add(scrollTabla, BorderLayout.CENTER);
        return panel;
    }

    // Métodos auxiliares
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

    private void estiloCampoTexto(JTextField campoTexto) {
        campoTexto.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        campoTexto.setBackground(new Color(60, 60, 60));
        campoTexto.setForeground(new Color(220, 220, 220));
        campoTexto.setCaretColor(Color.WHITE);
        campoTexto.setBorder(new LineBorder(new Color(90, 90, 90), 2));
        campoTexto.setPreferredSize(new Dimension(150, 30));
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
        boton.setPreferredSize(new Dimension(160, 35));
        return boton;
    }

    private JCheckBox crearCheckbox(String texto) {
        JCheckBox checkbox = new JCheckBox(texto);
        checkbox.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        checkbox.setBackground(new Color(55, 55, 55));
        checkbox.setForeground(new Color(220, 220, 220));
        checkbox.setFocusPainted(false);
        return checkbox;
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

    public String obtenerPermisosComoString() {
        List<String> permisos = new ArrayList<>();
        if (chkGestionarProductos.isSelected()) permisos.add("puedeGestionarProductos");
        if (chkAccederVentas.isSelected()) permisos.add("puedeVender");
        if (chkVerReportes.isSelected()) permisos.add("puedeVerReportes");
        if (chkExportarDatos.isSelected()) permisos.add("puedeExportar");
        if (chkGestionarUsuarios.isSelected()) permisos.add("puedeGestionarUsuarios");
        return String.join(",", permisos);
    }

    public void actualizarComboRoles() {
        cmbRol.removeAllItems();
        for (int i = 0; i < modeloTablaRoles.getRowCount(); i++) {
            cmbRol.addItem((String) modeloTablaRoles.getValueAt(i, 0));
        }
    }

    public void establecerPermisosDesdeString(String permisosStr) {
        // Limpiar todos los checkboxes primero
        chkGestionarProductos.setSelected(false);
        chkAccederVentas.setSelected(false);
        chkVerReportes.setSelected(false);
        chkExportarDatos.setSelected(false);
        chkGestionarUsuarios.setSelected(false);
        
        if (permisosStr != null && !permisosStr.isEmpty()) {
            String permisosLimpio = permisosStr.replaceAll("[<>\"'&;]", "");
            String[] permisos = permisosLimpio.split(",");
            for (String permiso : permisos) {
                switch (permiso.trim()) {
                    case "puedeGestionarProductos":
                        chkGestionarProductos.setSelected(true);
                        break;
                    case "puedeVender":
                        chkAccederVentas.setSelected(true);
                        break;
                    case "puedeVerReportes":
                        chkVerReportes.setSelected(true);
                        break;
                    case "puedeExportar":
                        chkExportarDatos.setSelected(true);
                        break;
                    case "puedeGestionarUsuarios":
                        chkGestionarUsuarios.setSelected(true);
                        break;
                }
            }
        }
    }
}