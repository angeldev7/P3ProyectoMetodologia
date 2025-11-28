package Repositorio.impl;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;

import Modelo.Bitacora;
import Modelo.Usuario;
import Persistencia.IdGenerator;
import Persistencia.MongoConnection;
import Repositorio.BitacoraRepository;

import org.bson.Document;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class BitacoraRepositoryMongo implements BitacoraRepository {
    private final MongoCollection<Document> collection;
    private final IdGenerator idGenerator;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public BitacoraRepositoryMongo() {
        MongoDatabase database = MongoConnection.getInstancia().getDatabase();
        this.collection = database.getCollection("bitacora");
        this.idGenerator = new IdGenerator(database);
    }

    @Override
    public Bitacora registrar(Bitacora bitacora) {
        String idRegistro = idGenerator.nextBitacoraId();
        
        Document doc = new Document("idRegistro", idRegistro)
                .append("idUsuario", bitacora.getUsuario().getIdUsuario())
                .append("nombreUsuario", bitacora.getUsuario().getNombreUsuario())
                .append("accion", bitacora.getAccion())
                .append("descripcion", bitacora.getDescripcion())
                .append("fechaHora", bitacora.getFechaHora().format(FORMATTER));

        collection.insertOne(doc);
        
        return new Bitacora(idRegistro, bitacora.getUsuario(), 
                bitacora.getAccion(), bitacora.getDescripcion());
    }

    @Override
    public List<Bitacora> findAll() {
        List<Bitacora> bitacoras = new ArrayList<>();
        for (Document doc : collection.find().sort(Sorts.descending("fechaHora"))) {
            bitacoras.add(documentToBitacora(doc));
        }
        return bitacoras;
    }

    @Override
    public List<Bitacora> findLast(int cantidad) {
        List<Bitacora> bitacoras = new ArrayList<>();
        for (Document doc : collection.find()
                .sort(Sorts.descending("fechaHora"))
                .limit(cantidad)) {
            bitacoras.add(documentToBitacora(doc));
        }
        return bitacoras;
    }

    @Override
    public List<Bitacora> findByUsuario(String idUsuario) {
        List<Bitacora> bitacoras = new ArrayList<>();
        for (Document doc : collection.find(Filters.eq("idUsuario", idUsuario))
                .sort(Sorts.descending("fechaHora"))) {
            bitacoras.add(documentToBitacora(doc));
        }
        return bitacoras;
    }

    @Override
    public long count() {
        return collection.countDocuments();
    }

    private Bitacora documentToBitacora(Document doc) {
        // Crear usuario simplificado desde datos almacenados
        String idUsuario = doc.getString("idUsuario");
        String nombreUsuario = doc.getString("nombreUsuario");
        
        Usuario usuario = new Usuario(idUsuario, nombreUsuario, 
                "", nombreUsuario, "");
        
        return new Bitacora(
                doc.getString("idRegistro"),
                usuario,
                doc.getString("accion"),
                doc.getString("descripcion")
        );
    }
}
