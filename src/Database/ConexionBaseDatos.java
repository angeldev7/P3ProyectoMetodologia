// Database/ConexionBaseDatos.java
package Database;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

import exception.ConexionBaseDatosException;

import com.mongodb.client.MongoCollection;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import org.bson.Document;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConexionBaseDatos {
    private static MongoClient clienteMongo;
    private static MongoDatabase baseDatos;
    private static final Logger logger = LoggerFactory.getLogger(ConexionBaseDatos.class);
    
    private static final String NOMBRE_BASE_DATOS = "ferreteria_carlin";
    
    // Método para cargar la configuración de forma segura
    private static String getConnectionString() {
        Properties prop = new Properties();
        // La ruta es relativa a la raíz del proyecto
        try (FileInputStream input = new FileInputStream("config.properties")) {
            prop.load(input);
            return prop.getProperty("db.connection_string");
        } catch (IOException ex) {
            logger.error("Error: No se pudo encontrar el archivo 'config.properties'. Asegúrate de que esté en la raíz del proyecto.");
            ex.printStackTrace();
            return null;
        }
    }
    
    public static void conectar() {
        try {
            String cadenaConexion = getConnectionString();
            if (cadenaConexion == null || cadenaConexion.isEmpty()) {
            	throw new ConexionBaseDatosException("La cadena de conexion esta vacia o no se pudo cargar desde config.properties");
            }
            
            clienteMongo = MongoClients.create(cadenaConexion);
            if (clienteMongo == null) {
                throw new ConexionBaseDatosException("No se pudo crear el cliente MongoDB");
            }
            
            baseDatos = clienteMongo.getDatabase(NOMBRE_BASE_DATOS);
            if (baseDatos == null) {
                throw new ConexionBaseDatosException("No se pudo obtener la base de datos: " + NOMBRE_BASE_DATOS);
            }
            
            baseDatos.runCommand(new Document("ping", 1));
            logger.info("Conectado a la base de datos: " + NOMBRE_BASE_DATOS);
        } catch (Exception e) {
            logger.error("Error al conectar a la base de datos: " + e.getMessage());
            throw new ConexionBaseDatosException("Error al conectar a MongoDB", e);
        }
    }
    
    public static MongoDatabase getBaseDatos() {
        if (baseDatos == null) {
            conectar();
        }
        return baseDatos;
    }
    
    public static MongoCollection<Document> getColeccion(String nombreColeccion) {
        return getBaseDatos().getCollection(nombreColeccion);
    }
    
    public static void cerrar() {
        if (clienteMongo != null) {
            clienteMongo.close();
            logger.info("Conexión cerrada");
        }
    }
}