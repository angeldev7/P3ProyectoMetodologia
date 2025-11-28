package Persistencia;

import com.mongodb.MongoClientSettings;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

import java.util.Collections;

/**
 * Singleton para manejar la conexión a MongoDB.
 * Coloca los JAR del driver en la carpeta lib/ si no usas Maven.
 */
public class MongoConnection {
    private static MongoConnection instancia;
    private MongoClient client;
    private MongoDatabase database;

    private String host = "localhost";
    private int port = 27017;
    private String databaseName = "sistema_contable";

    private MongoConnection() {
        inicializar();
    }

    public static synchronized MongoConnection getInstancia() {
        if (instancia == null) {
            instancia = new MongoConnection();
        }
        return instancia;
    }

    private void inicializar() {
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyToClusterSettings(b -> b.hosts(Collections.singletonList(new ServerAddress(host, port))))
                .build();
        client = MongoClients.create(settings);
        database = client.getDatabase(databaseName);
    }

    public MongoDatabase getDatabase() {
        return database;
    }
}
