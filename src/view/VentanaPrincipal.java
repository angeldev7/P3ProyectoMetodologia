// view/VentanaPrincipal.java
package view;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import App.Main;

public class VentanaPrincipal extends JFrame {
    private static final long serialVersionUID = 1L;
    
    // Paneles principales
    public PanelProductos panelProductos;
    public PanelVentas panelVentas;
    public PanelReportes panelReportes;
    public PanelGestionUsuarios panelGestionUsuarios;
    
    // Componentes de la interfaz
    private JTabbedPane panelPestanas;
    private JMenuBar barraMenu;
    private JLabel lblEstadoUsuario;
    
    public VentanaPrincipal() {
        configurarVentana();
        inicializarComponentes();
        configurarMenu();
        configurarLayout();
        aplicarTemaOscuro();
    }
    
    private void configurarVentana() {
        setTitle("🔧 Ferretería Carlín - Sistema de Gestión");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(1200, 700));
    }
    
    private void inicializarComponentes() {
        // Inicializar todos los paneles
        panelProductos = new PanelProductos();
        panelVentas = new PanelVentas();
        panelReportes = new PanelReportes();
        panelGestionUsuarios = new PanelGestionUsuarios();
        
        // Panel de pestañas
        panelPestanas = new JTabbedPane();
        panelPestanas.setBackground(new Color(45, 45, 45));
        panelPestanas.setForeground(Color.WHITE);
        
        // Barra de menú
        barraMenu = new JMenuBar();
        
        // Etiqueta de estado del usuario
        lblEstadoUsuario = new JLabel();
        lblEstadoUsuario.setForeground(Color.WHITE);
        lblEstadoUsuario.setFont(new Font("Arial", Font.BOLD, 12));
    }
    
    private void configurarMenu() {
        // Menú Archivo
        JMenu menuArchivo = new JMenu("Archivo");
        estiloMenu(menuArchivo);
        
        JMenuItem itemActualizar = new JMenuItem("🔄 Actualizar");
        JMenuItem itemSalir = new JMenuItem("🚪 Salir");
        estiloItemMenu(itemActualizar);
        estiloItemMenu(itemSalir);
        
        itemSalir.addActionListener(e -> System.exit(0));
        
        menuArchivo.add(itemActualizar);
        menuArchivo.addSeparator();
        menuArchivo.add(itemSalir);
        
        // Menú Sistema
        JMenu menuSistema = new JMenu("Sistema");
        estiloMenu(menuSistema);
        
        JMenuItem itemCerrarSesion = new JMenuItem("🔒 Cerrar Sesión");
        estiloItemMenu(itemCerrarSesion);
        
        itemCerrarSesion.addActionListener(e -> Main.cerrarSesion());
        
        menuSistema.add(itemCerrarSesion);
        
        // Agregar menús a la barra
        barraMenu.add(menuArchivo);
        barraMenu.add(menuSistema);
        
        // Agregar etiqueta de usuario al final
        barraMenu.add(Box.createHorizontalGlue());
        barraMenu.add(lblEstadoUsuario);
    }
    
    private void configurarLayout() {
        setLayout(new BorderLayout());
        
        // Agregar barra de menú
        setJMenuBar(barraMenu);
        
        // Agregar pestañas
        panelPestanas.addTab("📦 Gestión de Productos", panelProductos);
        panelPestanas.addTab("🛒 Módulo de Ventas", panelVentas);
        panelPestanas.addTab("📊 Reportes y Análisis", panelReportes);
        panelPestanas.addTab("👥 Gestión de Usuarios", panelGestionUsuarios);
        
        add(panelPestanas, BorderLayout.CENTER);
        
        // Panel de estado inferior
        JPanel panelEstado = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelEstado.setBackground(new Color(30, 30, 30));
        panelEstado.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(90, 90, 90)));
        panelEstado.add(new JLabel("Sistema de Gestión Ferretería Carlín © 2024"));
        
        add(panelEstado, BorderLayout.SOUTH);
    }
    
    private void aplicarTemaOscuro() {
        // Aplicar colores oscuros a todos los componentes
        Color colorFondo = new Color(45, 45, 45);
        Color colorTexto = new Color(220, 220, 220);
        
        panelPestanas.setBackground(colorFondo);
        panelPestanas.setForeground(colorTexto);
        
        UIManager.put("TabbedPane.background", colorFondo);
        UIManager.put("TabbedPane.foreground", colorTexto);
        UIManager.put("TabbedPane.contentAreaColor", colorFondo);
    }
    
    private void estiloMenu(JMenu menu) {
        menu.setForeground(Color.WHITE);
        menu.setBackground(new Color(60, 60, 60));
        menu.setOpaque(true);
    }
    
    private void estiloItemMenu(JMenuItem item) {
        item.setForeground(Color.WHITE);
        item.setBackground(new Color(60, 60, 60));
        item.setOpaque(true);
    }
    
    // Métodos públicos para controlar la interfaz
    public void aplicarPermisos(List<String> permisos) {
        System.out.println("Aplicando permisos: " + permisos);
        
        // Habilitar/deshabilitar pestañas según permisos
        if (permisos == null || permisos.isEmpty()) {
            habilitarTodasLasPestanas();
            return;
        }
        
        // Gestión de Productos
        boolean puedeGestionarProductos = permisos.contains("puedeGestionarProductos") || 
                                         permisos.contains("admin");
        panelPestanas.setEnabledAt(0, puedeGestionarProductos);
        
        // Módulo de Ventas
        boolean puedeVender = permisos.contains("puedeVender") || 
                            permisos.contains("admin");
        panelPestanas.setEnabledAt(1, puedeVender);
        
        // Reportes
        boolean puedeVerReportes = permisos.contains("puedeVerReportes") || 
                                  permisos.contains("admin");
        panelPestanas.setEnabledAt(2, puedeVerReportes);
        
        // Gestión de Usuarios
        boolean puedeGestionarUsuarios = permisos.contains("puedeGestionarUsuarios") || 
                                        permisos.contains("admin");
        panelPestanas.setEnabledAt(3, puedeGestionarUsuarios);
        
        System.out.println("Permisos aplicados:");
        System.out.println("  Gestión Productos: " + puedeGestionarProductos);
        System.out.println("  Ventas: " + puedeVender);
        System.out.println("  Reportes: " + puedeVerReportes);
        System.out.println("  Gestión Usuarios: " + puedeGestionarUsuarios);
    }
    
    public void habilitarTodasLasPestanas() {
        for (int i = 0; i < panelPestanas.getTabCount(); i++) {
            panelPestanas.setEnabledAt(i, true);
        }
        System.out.println("Todas las pestañas habilitadas");
    }
    
    public void setTitle(String title) {
        super.setTitle(title);
    }
    
    public void setUsuarioActual(String usuario, String rol) {
        lblEstadoUsuario.setText("👤 " + usuario + " | 🎭 " + rol);
    }
    
    // Método para obtener paneles (para el controlador)
    public PanelVentas getPanelVentas() {
        return panelVentas;
    }
    
    public PanelProductos getPanelProductos() {
        return panelProductos;
    }
    
    public PanelReportes getPanelReportes() {
        return panelReportes;
    }
    
    public PanelGestionUsuarios getPanelGestionUsuarios() {
        return panelGestionUsuarios;
    }
}