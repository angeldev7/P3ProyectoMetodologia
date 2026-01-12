// Database/ConexionBaseDatos.java
package Database;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import org.bson.Document;
import java.util.concurrent.TimeUnit;

public class ConexionBaseDatos {
    private static MongoClient clienteMongo;
    private static MongoDatabase baseDatos;
    
    private static final String CADENA_CONEXION = "mongodb+srv://asrodriguez12_db_user:angelitus7@proyectometodologia.byuxkac.mongodb.net/?appName=ProyectoMetodologia";
    private static final String NOMBRE_BASE_DATOS = "ferreteria_carlin";
    
    public static void conectar() {
        try {
            clienteMongo = MongoClients.create(CADENA_CONEXION);
            baseDatos = clienteMongo.getDatabase(NOMBRE_BASE_DATOS);
            baseDatos.runCommand(new Document("ping", 1));
            System.out.println("Conectado a la base de datos: " + NOMBRE_BASE_DATOS);
        } catch (Exception e) {
            System.err.println("Error al conectar a la base de datos: " + e.getMessage());
            e.printStackTrace();
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
            System.out.println("Conexión cerrada");
        }
    }
}