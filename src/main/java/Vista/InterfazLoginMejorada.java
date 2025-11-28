package Vista;

import Controlador.ControladorUsuario;
import Modelo.TipoRol;
import Modelo.Usuario;
import javax.swing.*;
import java.awt.*;

public class InterfazLoginMejorada extends JFrame {
    // Paleta central (basada en versión previa azul corporativo)
    private static final Color COLOR_HEADER_START = new Color(41, 128, 185); // #2980B9
    private static final Color COLOR_HEADER_END = new Color(52, 152, 219);   // #3498DB
    private static final Color COLOR_SUCCESS = new Color(46, 204, 113);      // Verde éxito
    private static final Color COLOR_DANGER = new Color(231, 76, 60);        // Rojo peligro
    private static final Color COLOR_INFO_BG = new Color(236, 240, 241);     // Gris claro panel info
    private static final Color COLOR_FIELD_BORDER = new Color(189, 195, 199);

    private JTextField txtUsuario;
    private JPasswordField txtContrasena;
    private JButton btnIngresar;
    private JButton btnCancelar;
    
    private ControladorUsuario controladorUsuario;
    
    public InterfazLoginMejorada() {
        super("Sistema de Gestion Contable - Inicio de Sesion");
        controladorUsuario = ControladorUsuario.getInstancia();
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(750, 680);
        setLocationRelativeTo(null);
        setResizable(false);
        
        inicializarComponentes();
    }
    
    private void inicializarComponentes() {
        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        panelPrincipal.setBackground(COLOR_INFO_BG);

        // Panel Superior - Logo y Titulo
        JPanel panelTitulo = crearPanelTitulo();

        // Panel Central - Login
        JPanel panelLogin = crearPanelLogin();

        // Panel de informacion
        JPanel panelInfo = crearPanelInformacion();

        // Ensamblar todo
        JPanel panelCentral = new JPanel(new BorderLayout(5, 5));
        panelCentral.setOpaque(false);
        panelCentral.add(panelLogin, BorderLayout.NORTH);

        JPanel panelSur = new JPanel(new BorderLayout(0, 5));
        panelSur.setOpaque(false);
        panelSur.add(panelInfo, BorderLayout.NORTH);

        panelPrincipal.add(panelTitulo, BorderLayout.NORTH);
        panelPrincipal.add(panelCentral, BorderLayout.CENTER);
        panelPrincipal.add(panelSur, BorderLayout.SOUTH);

        getContentPane().add(panelPrincipal);
    }
    
    private JPanel crearPanelTitulo() {
        JPanel panel = new GradientPanel(COLOR_HEADER_START, COLOR_HEADER_END);
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(25, 20, 25, 20));
        
        JPanel panelTextos = new JPanel(new GridLayout(4, 1, 0, 8));
        panelTextos.setOpaque(false);
        
        JLabel lblTitulo = new JLabel("Sistema de Gestión Contable y Tributaria", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setForeground(Color.WHITE);
        
        JLabel lblSubtitulo = new JLabel("Comercial el mejor vendedor S.A.", SwingConstants.CENTER);
        lblSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblSubtitulo.setForeground(new Color(255, 250, 205));
        
        JLabel lblPropositoLinea1 = new JLabel("Control de Facturas de Venta y Gastos de Compra", SwingConstants.CENTER);
        lblPropositoLinea1.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        lblPropositoLinea1.setForeground(new Color(224, 255, 255));
        
        JLabel lblPropositoLinea2 = new JLabel("Cálculo de Retención de IVA (30%) - Bitácora de Auditoría", SwingConstants.CENTER);
        lblPropositoLinea2.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblPropositoLinea2.setForeground(new Color(224, 255, 255));
        
        panelTextos.add(lblTitulo);
        panelTextos.add(lblSubtitulo);
        panelTextos.add(lblPropositoLinea1);
        panelTextos.add(lblPropositoLinea2);
        
        panel.add(panelTextos, BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel crearPanelLogin() {
        JPanel panelLogin = new JPanel(new GridLayout(3, 2, 10, 10));
        panelLogin.setBorder(BorderFactory.createTitledBorder("INICIAR SESIÓN - Credenciales de Acceso"));
        panelLogin.setBackground(COLOR_INFO_BG);

        JLabel lblUsuario = new JLabel("Usuario:");
        txtUsuario = new JTextField();
        estilizarCampo(txtUsuario);

        JLabel lblContrasena = new JLabel("Contraseña:");
        txtContrasena = new JPasswordField();
        estilizarCampo(txtContrasena);

        btnIngresar = new JButton("Iniciar Sesión");
        styleButton(btnIngresar, COLOR_SUCCESS, 120);
        btnIngresar.addActionListener(e -> intentarLogin());

        btnCancelar = new JButton("Cancelar");
        styleButton(btnCancelar, COLOR_DANGER, 120);
        btnCancelar.addActionListener(e -> dispose());

        panelLogin.add(lblUsuario);
        panelLogin.add(txtUsuario);
        panelLogin.add(lblContrasena);
        panelLogin.add(txtContrasena);
        panelLogin.add(btnIngresar);
        panelLogin.add(btnCancelar);

        return panelLogin;
    }
    
    private JPanel crearPanelInformacion() {
        JPanel panel = new JPanel(new GridLayout(2, 1, 0, 5));
        panel.setBackground(COLOR_INFO_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(52, 152, 219)),
                "Informacion del Sistema"),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        
        JLabel lblQueHace = new JLabel("<html><b>QUE HACE:</b> Registra facturas de venta y gastos de compra, " +
                                       "calcula retencion de IVA (30% del IVA en compras), aprueba/rechaza transacciones segun roles, " +
                                       "genera respaldos anuales y mantiene bitacora de auditoria completa. " +
                           "<b>Persistencia MongoDB exclusiva.</b></html>");
        lblQueHace.setFont(new Font("Arial", Font.PLAIN, 10));
        lblQueHace.setForeground(new Color(44, 62, 80));
        
        JLabel lblQueNoHace = new JLabel("<html><b>QUE NO HACE:</b> No gestiona inventarios, no calcula nomina, " +
                                         "no genera estados financieros completos (solo retencion de IVA), " +
                                         "no maneja bancos ni conciliaciones.</html>");
        lblQueNoHace.setFont(new Font("Arial", Font.PLAIN, 10));
        lblQueNoHace.setForeground(new Color(44, 62, 80));
        
        panel.add(lblQueHace);
        panel.add(lblQueNoHace);
        
        return panel;
    }
    
    private void estilizarCampo(JTextField campo) {
        campo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_FIELD_BORDER),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)));
    }
    
    private void intentarLogin() {
        String usuario = txtUsuario.getText();
        String contrasena = new String(txtContrasena.getPassword());

        if (controladorUsuario.autenticar(usuario, contrasena)) {
            JOptionPane.showMessageDialog(this, "Inicio de sesión exitoso", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            
            // Obtener usuario actual y redirigir según rol
            Usuario usuarioActual = controladorUsuario.getUsuarioActual();
            
            if (usuarioActual != null) {
                String rol = usuarioActual.getRol();
                
                // Cerrar ventana de login
                this.dispose();
                
                // Abrir ventana según rol
                if (TipoRol.ADMIN_MASTER.equals(rol)) {
                    // Admin Master → Dashboard de administración
                    SwingUtilities.invokeLater(() -> {
                        AdminMasterDashboard dashboard = new AdminMasterDashboard();
                        dashboard.setVisible(true);
                    });
                } else if (TipoRol.JEFATURA_FINANCIERA.equals(rol) || TipoRol.ASISTENTE_CONTABLE.equals(rol)) {
                    // Jefatura o Asistente → Interfaz Principal
                    SwingUtilities.invokeLater(() -> {
                        InterfazPrincipal interfaz = new InterfazPrincipal();
                        interfaz.setVisible(true);
                    });
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Credenciales incorrectas", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            InterfazLoginMejorada login = new InterfazLoginMejorada();
            login.setVisible(true);
        });
    }

    // Helper para estilo uniforme de botones
    private void styleButton(JButton boton, Color fondo, int width) {
        boton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        boton.setBackground(fondo);
        boton.setForeground(Color.WHITE);
        boton.setFocusPainted(false);
        boton.setBorderPainted(false);
        boton.setPreferredSize(new Dimension(width, 40));
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    // Panel con gradiente vertical suave
    private static class GradientPanel extends JPanel {
        private final Color start;
        private final Color end;
        GradientPanel(Color start, Color end) {
            this.start = start;
            this.end = end;
            setOpaque(true);
        }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();
            GradientPaint gp = new GradientPaint(0, 0, start, 0, h, end);
            g2.setPaint(gp);
            g2.fillRect(0, 0, w, h);
            g2.dispose();
        }
    }
}
