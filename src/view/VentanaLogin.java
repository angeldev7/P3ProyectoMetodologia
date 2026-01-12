// View/VentanaLogin.java
package view;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import DAO.ServicioAutenticacion;

public class VentanaLogin extends JFrame {
    private static final long serialVersionUID = 1L;
    public JTextField txtUsuario;
    public JPasswordField txtContrasena;
    public JButton btnLogin;
    private ListenerLogin listenerLogin;
    private ServicioAutenticacion servicioAuth;
    

    public interface ListenerLogin {
        void onLoginExitoso(String usuario, String rol);
        void onLoginFallido(String mensaje);
    }

    public VentanaLogin() {
        setTitle("🔧 Ferretería Carlín - Inicio de Sesión");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 450, 500);
        setResizable(false);
        setLocationRelativeTo(null);
        
        // ❌ NO crear nueva instancia aquí - usar la de Main
        // servicioAuth = new ServicioAutenticacion();
        
        inicializarComponentes();
        configurarLayout();
        configurarEventos();
    }

    public void setLoginListener(ListenerLogin listener) {
        this.listenerLogin = listener;
    }

    private void inicializarComponentes() {
        // Campos de texto
        txtUsuario = crearCampoTexto();
        txtContrasena = new JPasswordField();
        estiloCampoTexto(txtContrasena);
        
        // Botón
        btnLogin = crearBoton("🔑 Iniciar Sesión", new Color(0, 123, 255));
    }

    private void configurarEventos() {
        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                intentarLogin();
            }
        });

        // Login con Enter
        txtContrasena.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    intentarLogin();
                }
            }
        });

        txtUsuario.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    intentarLogin();
                }
            }
        });
    }

    private void intentarLogin() {
        String usuario = txtUsuario.getText().trim();
        String contrasena = new String(txtContrasena.getPassword());
        
        if (usuario.isEmpty() || contrasena.isEmpty()) {
            mostrarError("Por favor ingrese usuario y contraseña");
            return;
        }

        // Obtener la instancia única de Main
        ServicioAutenticacion servicioAuth = App.Main.getServicioAutenticacion();
        
        if (servicioAuth == null) {
            mostrarError("Error interno del sistema");
            return;
        }

        // Verificar credenciales contra la base de datos
        if (servicioAuth.autenticar(usuario, contrasena)) {
            if (listenerLogin != null) {
                String rol = servicioAuth.getRolActual();
                listenerLogin.onLoginExitoso(usuario, rol);
            }
        } else {
            mostrarError("Usuario o contraseña incorrectos");
            if (listenerLogin != null) {
                listenerLogin.onLoginFallido("Credenciales inválidas");
            }
        }
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error de Autenticación", JOptionPane.ERROR_MESSAGE);
        txtContrasena.setText("");
        txtContrasena.requestFocus();
    }

    public void limpiarFormulario() {
        txtUsuario.setText("");
        txtContrasena.setText("");
        txtUsuario.requestFocus();
    }

    private void configurarLayout() {
        JPanel panelPrincipal = new JPanel(new BorderLayout());
        panelPrincipal.setBackground(new Color(45, 45, 45));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        // Header
        JPanel panelHeader = crearPanelHeader();
        panelPrincipal.add(panelHeader, BorderLayout.NORTH);
        
        // Form panel
        JPanel panelFormulario = crearPanelFormulario();
        panelPrincipal.add(panelFormulario, BorderLayout.CENTER);
        
        // Footer
        JPanel panelFooter = crearPanelFooter();
        panelPrincipal.add(panelFooter, BorderLayout.SOUTH);
        
        setContentPane(panelPrincipal);
    }

    private JPanel crearPanelHeader() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(45, 45, 45));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        
        JLabel etiquetaIcono = new JLabel("🔧");
        etiquetaIcono.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 64));
        etiquetaIcono.setForeground(new Color(0, 123, 255));
        etiquetaIcono.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel etiquetaTitulo = new JLabel("Ferretería Carlín");
        etiquetaTitulo.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 24));
        etiquetaTitulo.setForeground(Color.WHITE);
        etiquetaTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel etiquetaSubtitulo = new JLabel("Sistema de Gestión de Inventario");
        etiquetaSubtitulo.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        etiquetaSubtitulo.setForeground(new Color(180, 180, 180));
        etiquetaSubtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        panel.add(etiquetaIcono);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(etiquetaTitulo);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(etiquetaSubtitulo);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        return panel;
    }

    private JPanel crearPanelFormulario() {
        // Panel contenedor con borde
        JPanel panelContenedor = new JPanel(new BorderLayout());
        panelContenedor.setBackground(new Color(55, 55, 55));
        panelContenedor.setBorder(new TitledBorder(new LineBorder(new Color(90, 90, 90)), " Inicio de Sesión ", 
                  TitledBorder.CENTER, TitledBorder.TOP, 
                  new Font(Font.SANS_SERIF, Font.BOLD, 14), new Color(220, 220, 220)));
        
        // Panel interno con los componentes
        JPanel panelInterno = new JPanel();
        panelInterno.setBackground(new Color(55, 55, 55));
        panelInterno.setBorder(BorderFactory.createEmptyBorder(25, 20, 20, 20));
        panelInterno.setLayout(new GridBagLayout());
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Usuario
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.weightx = 0;
        panelInterno.add(crearEtiqueta("Usuario:"), gbc);
        
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 1.0;
        gbc.ipadx = 200;
        panelInterno.add(txtUsuario, gbc);
        
        // Contraseña
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.weightx = 0;
        gbc.ipadx = 0;
        panelInterno.add(crearEtiqueta("Contraseña:"), gbc);
        
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 1.0;
        gbc.ipadx = 200;
        panelInterno.add(txtContrasena, gbc);
        
        // Botón login
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 0;
        gbc.ipadx = 0;
        gbc.insets = new Insets(20, 10, 10, 10);
        btnLogin.setPreferredSize(new Dimension(250, 40));
        panelInterno.add(btnLogin, gbc);

        // Credenciales de prueba
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 10, 10, 10);
        JLabel etiquetaDemo = new JLabel("Demo: admin / admin");
        etiquetaDemo.setFont(new Font(Font.SANS_SERIF, Font.ITALIC, 12));
        etiquetaDemo.setForeground(new Color(150, 150, 150));
        etiquetaDemo.setHorizontalAlignment(SwingConstants.CENTER);
        panelInterno.add(etiquetaDemo, gbc);
        
        panelContenedor.add(panelInterno, BorderLayout.CENTER);
        return panelContenedor;
    }

    private JPanel crearPanelFooter() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(45, 45, 45));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));
        
        JLabel etiquetaFooter = new JLabel("Sistema desarrollado para Ferretería Carlín © 2024");
        etiquetaFooter.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        etiquetaFooter.setForeground(new Color(150, 150, 150));
        
        panel.add(etiquetaFooter);
        return panel;
    }

    private JLabel crearEtiqueta(String texto) {
        JLabel etiqueta = new JLabel(texto);
        etiqueta.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        etiqueta.setForeground(new Color(220, 220, 220));
        return etiqueta;
    }

    private JTextField crearCampoTexto() {
        JTextField campoTexto = new JTextField();
        estiloCampoTexto(campoTexto);
        return campoTexto;
    }

    private void estiloCampoTexto(JTextField campoTexto) {
        campoTexto.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        campoTexto.setBackground(new Color(60, 60, 60));
        campoTexto.setForeground(new Color(220, 220, 220));
        campoTexto.setCaretColor(Color.WHITE);
        campoTexto.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(90, 90, 90), 2),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
    }

    private JButton crearBoton(String texto, Color colorFondo) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        boton.setBackground(colorFondo);
        boton.setForeground(Color.WHITE);
        boton.setFocusPainted(false);
        boton.setBorder(new LineBorder(colorFondo.darker(), 2));
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return boton;
    }
}