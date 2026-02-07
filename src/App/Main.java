// App/Main.java
package App;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import controller.ControladorInventario;
import controller.ControladorGestionUsuarios;
import Database.ConexionBaseDatos;
import DAO.ServicioAutenticacion;
import model.InventarioDAO;  // CAMBIADO: De Inventario a InventarioDAO
import util.MigradorContrasenas;
import view.VentanaPrincipal;
import view.VentanaLogin;

import java.util.List;

public class Main {
    private static ServicioAutenticacion servicioAuth; // DEBE ser estático y único
    private static VentanaPrincipal ventanaPrincipal;
    private static VentanaLogin ventanaLogin;
    private static InventarioDAO inventario;  // NUEVO: Variable para InventarioDAO

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
                        ex.printStackTrace();
                    }
                }
                
                ConexionBaseDatos.conectar();
                
                // Ejecutar migración de contraseñas si es necesario
                MigradorContrasenas.migrarUsuariosExistentes();
                
                // Inicializar servicios
                servicioAuth = new ServicioAutenticacion();
                inventario = new InventarioDAO();
                
                System.out.println("Sistema inicializado con " + inventario.obtenerCantidadProductos() + " productos");
                
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
                System.out.println("Login exitoso - Usuario: " + usuario + " | Rol: " + rol);
                ventanaLogin.setVisible(false);
                mostrarVentanaPrincipal();
            }

            @Override
            public void onLoginFallido(String mensaje) {
                System.err.println("Login fallido: " + mensaje);
            }
        });
        ventanaLogin.setVisible(true);
    }

    private static void mostrarVentanaPrincipal() {
        ventanaPrincipal = new VentanaPrincipal();
        
        // Configurar controladores
        System.out.println("Creando controladores...");
        new ControladorInventario(ventanaPrincipal, inventario);
        new ControladorGestionUsuarios(ventanaPrincipal.panelGestionUsuarios);
        
        // Obtener información del usuario actual
        String usuario = servicioAuth.getUsuarioActual();
        String rol = servicioAuth.getRolActual();
        List<String> permisos = servicioAuth.getPermisosActuales();
        
        System.out.println("Usuario: " + usuario + " | Rol: " + rol);
        System.out.println("Permisos: " + permisos);
        
        // Configurar título
        ventanaPrincipal.setTitle("Ferretería Carlin - Sistema de Gestión (Usuario: " + usuario + " | Rol: " + rol + ")");
        
        // Aplicar permisos según el rol
        if (permisos != null && !permisos.isEmpty()) {
            ventanaPrincipal.aplicarPermisos(permisos);
            System.out.println("Permisos aplicados correctamente");
        } else {
            ventanaPrincipal.habilitarTodasLasPestanas();
            System.out.println("Advertencia: No se encontraron permisos específicos");
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