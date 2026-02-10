// App/Main.java
package App;
import ch.qos.logback.classic.util.ContextInitializer;
import java.util.List;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import controller.ControladorInventario;
import controller.ControladorGestionUsuarios;
import Database.ConexionBaseDatos;
import DAO.ServicioAutenticacion;
import model.InventarioDAO;  // CAMBIADO: De Inventario a InventarioDAO
import util.MigradorContrasenas;
import view.VentanaPrincipal;
import view.VentanaLogin;
public class Main {
    private static ServicioAutenticacion servicioAuth;
    private static VentanaPrincipal ventanaPrincipal;
    private static VentanaLogin ventanaLogin;
    private static InventarioDAO inventario;
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    
    static {
        // Forzar a Logback a usar archivo de configuración
        System.setProperty(ContextInitializer.CONFIG_FILE_PROPERTY, "logback.xml");
        
        // Registrar hook de shutdown
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Cerrando conexiones del sistema...");
            ConexionBaseDatos.cerrar();
        }));
    }
    
    

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
                } catch (Exception e) {
                    try {
                        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                    } catch (Exception ex) {
                        logger.error("Error al configurar el Look and Feel: " + ex.getMessage());
                    }
                }
                
                ConexionBaseDatos.conectar();
                
                // Inicializar servicios
                servicioAuth = new ServicioAutenticacion();
                inventario = new InventarioDAO();
                
                logger.info("Sistema inicializado con " + inventario.obtenerCantidadProductos() + " productos");
                
                mostrarVentanaLogin();
            }
        });
    }
    
    public static ServicioAutenticacion getServicioAutenticacion() {
        return servicioAuth;
    }
    
    // NUEVO: Método para obtener el InventarioDAO
    public static InventarioDAO getInventario() {
        return inventario;
    }

    private static void mostrarVentanaLogin() {
        ventanaLogin = new VentanaLogin();
        ventanaLogin.setLoginListener(new VentanaLogin.ListenerLogin() {
            @Override
            public void onLoginExitoso(String usuario, String rol) {
               logger.error("Login exitoso - Usuario: " + usuario + " | Rol: " + rol);
                ventanaLogin.setVisible(false);
                mostrarVentanaPrincipal();
            }

            @Override
            public void onLoginFallido(String mensaje) {
                logger.error("Login fallido: " + mensaje);
            }
        });
        ventanaLogin.setVisible(true);
    }

    private static void mostrarVentanaPrincipal() {
        ventanaPrincipal = new VentanaPrincipal();
        
        // Configurar controladores
        logger.info("Creando controladores...");
        new ControladorInventario(ventanaPrincipal, inventario);
        new ControladorGestionUsuarios(ventanaPrincipal.panelGestionUsuarios);
        
        // Obtener información del usuario actual
        String usuario = servicioAuth.getUsuarioActual();
        String rol = servicioAuth.getRolActual();
        List<String> permisos = servicioAuth.getPermisosActuales();
        
        logger.info("Usuario: " + usuario + " | Rol: " + rol);
        logger.info("Permisos: " + permisos);
        
        // Configurar título
        ventanaPrincipal.setTitle("Ferretería Carlín - Sistema de Gestión (Usuario: " + usuario + " | Rol: " + rol + ")");
        
        // Aplicar permisos según el rol
        if (permisos != null && !permisos.isEmpty()) {
            ventanaPrincipal.aplicarPermisos(permisos);
            logger.info("Permisos aplicados correctamente");
        } else {
            ventanaPrincipal.habilitarTodasLasPestanas();
            logger.info("Advertencia: No se encontraron permisos específicos");
        }
        
        ventanaPrincipal.setVisible(true);
    }

    public static void cerrarSesion() {
        if (ventanaPrincipal != null) {
            ventanaPrincipal.setVisible(false);
            ventanaPrincipal.dispose();
        }
        servicioAuth.cerrarSesion();
        ventanaLogin.limpiarFormulario();
        ventanaLogin.setVisible(true);
    }
}