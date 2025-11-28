package Ejecutable;

import Vista.InterfazLoginMejorada;
import Persistencia.MongoConnection;
import Repositorio.impl.UsuarioRepositoryMongo;
import Modelo.Usuario;
import Modelo.TipoRol;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Clase principal ejecutable del Sistema Contable
 * Comercial el mejor vendedor S.A.
 * Versión 2.0 - Persistencia MongoDB exclusiva
 * 
 * @author WildBär Systems
 * @version 2.0
 */
public class SistemaContable {
    
    public static void main(String[] args) {
        // Configurar Look and Feel del sistema
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                System.err.println("No se pudo configurar el Look and Feel");
            }
        }
        
        // Inicializar conexión Mongo y sembrar datos base
        MongoConnection.getInstancia();
        sembrarDatosBase();

        // Iniciar la aplicación
        SwingUtilities.invokeLater(() -> {
            InterfazLoginMejorada login = new InterfazLoginMejorada();
            login.setVisible(true);
        });
    }

    private static void sembrarDatosBase() {
        try {
            UsuarioRepositoryMongo repo = new UsuarioRepositoryMongo();
            if (repo.findAll().isEmpty()) {
                repo.save(new Usuario(null, "adminmaster", "adminmaster123", "Administrador Superior", TipoRol.ADMIN_MASTER));
                repo.save(new Usuario(null, "admin", "admin123", "Jefatura Principal", TipoRol.JEFATURA_FINANCIERA));
                repo.save(new Usuario(null, "asistente", "asistente123", "Asistente Contable", TipoRol.ASISTENTE_CONTABLE));
            }
        } catch (Exception e) {
            javax.swing.JOptionPane.showMessageDialog(null, 
                "Error conectando a MongoDB: " + e.getMessage() + "\n\nVerifique que MongoDB esté corriendo en localhost:27017",
                "Error de Conexión", javax.swing.JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }
}
