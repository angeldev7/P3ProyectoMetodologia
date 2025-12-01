package Repositorio.impl;

import Modelo.RolPersonalizado;
import Repositorio.RolRepository;
import Persistencia.IdGenerator;
import Persistencia.MongoConnection;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Implementación MongoDB para gestión de roles personalizados
 */
public class RolRepositoryMongo implements RolRepository {
    private final MongoCollection<Document> collection;
    private final IdGenerator idGenerator;
    
    public RolRepositoryMongo() {
        MongoDatabase db = MongoConnection.getInstancia().getDatabase();
        this.collection = db.getCollection("roles_personalizados");
        this.idGenerator = new IdGenerator(db);
    }
    
    @Override
    public void save(RolPersonalizado rol) {
        if (rol.getIdRol() == null || rol.getIdRol().isEmpty()) {
            rol.setIdRol(idGenerator.nextRolId());
        }
        
        Document doc = new Document("_id", rol.getIdRol())
            .append("nombreRol", rol.getNombreRol())
            .append("permisos", new ArrayList<>(rol.getPermisos()))
            .append("creadoPor", rol.getCreadoPor())
            .append("fechaCreacion", rol.getFechaCreacion());
        
        collection.insertOne(doc);
    }
    
    @Override
    public RolPersonalizado findByNombre(String nombreRol) {
        Document doc = collection.find(new Document("nombreRol", nombreRol)).first();
        return doc != null ? documentToRol(doc) : null;
    }
    
    @Override
    public List<RolPersonalizado> findAll() {
        List<RolPersonalizado> roles = new ArrayList<>();
        for (Document doc : collection.find()) {
            roles.add(documentToRol(doc));
        }
        return roles;
    }
    
    @Override
    public boolean deleteById(String idRol) {
        return collection.deleteOne(new Document("_id", idRol)).getDeletedCount() > 0;
    }
    
    @Override
    public boolean existsByNombre(String nombreRol) {
        return collection.countDocuments(new Document("nombreRol", nombreRol)) > 0;
    }
    
    private RolPersonalizado documentToRol(Document doc) {
        RolPersonalizado rol = new RolPersonalizado();
        rol.setIdRol(doc.getString("_id"));
        rol.setNombreRol(doc.getString("nombreRol"));
        
        List<?> permisosList = doc.getList("permisos", String.class);
        Set<String> permisos = new HashSet<>();
        if (permisosList != null) {
            for (Object p : permisosList) {
                permisos.add(p.toString());
            }
        }
        rol.setPermisos(permisos);
        
        rol.setCreadoPor(doc.getString("creadoPor"));
        rol.setFechaCreacion(doc.getString("fechaCreacion"));
        
        return rol;
    }
}
