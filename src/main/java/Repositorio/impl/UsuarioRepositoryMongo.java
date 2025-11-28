package Repositorio.impl;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;

import Modelo.Usuario;
import Persistencia.IdGenerator;
import Persistencia.MongoConnection;
import Repositorio.UsuarioRepository;

import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.List;

public class UsuarioRepositoryMongo implements UsuarioRepository {
    private final MongoCollection<Document> collection;
    private final IdGenerator idGenerator;

    public UsuarioRepositoryMongo() {
        MongoDatabase database = MongoConnection.getInstancia().getDatabase();
        this.collection = database.getCollection("usuarios");
        this.idGenerator = new IdGenerator(database);
    }

    @Override
    public Usuario findByNombreUsuario(String nombreUsuario) {
        Document doc = collection.find(Filters.eq("nombreUsuario", nombreUsuario)).first();
        return doc != null ? documentToUsuario(doc) : null;
    }

    @Override
    public boolean existsByNombreUsuario(String nombreUsuario) {
        return collection.countDocuments(Filters.eq("nombreUsuario", nombreUsuario)) > 0;
    }

    @Override
    public List<Usuario> findAll() {
        List<Usuario> usuarios = new ArrayList<>();
        for (Document doc : collection.find()) {
            usuarios.add(documentToUsuario(doc));
        }
        return usuarios;
    }

    @Override
    public Usuario save(Usuario usuario) {
        // Generar ID si es nuevo
        if (usuario.getIdUsuario() == null || usuario.getIdUsuario().isEmpty()) {
            String nuevoId = idGenerator.nextUsuarioId(usuario.getRol());
            Document doc = new Document("idUsuario", nuevoId)
                    .append("nombreUsuario", usuario.getNombreUsuario())
                    .append("contrasena", usuario.getContrasena())
                    .append("nombreCompleto", usuario.getNombreCompleto())
                    .append("rol", usuario.getRol())
                    .append("activo", usuario.isActivo());
            collection.insertOne(doc);
            return new Usuario(nuevoId, usuario.getNombreUsuario(), usuario.getContrasena(),
                    usuario.getNombreCompleto(), usuario.getRol());
        } else {
            // Actualizar existente
            Bson filter = Filters.eq("idUsuario", usuario.getIdUsuario());
            Bson updates = Updates.combine(
                    Updates.set("nombreUsuario", usuario.getNombreUsuario()),
                    Updates.set("contrasena", usuario.getContrasena()),
                    Updates.set("nombreCompleto", usuario.getNombreCompleto()),
                    Updates.set("rol", usuario.getRol()),
                    Updates.set("activo", usuario.isActivo())
            );
            collection.updateOne(filter, updates);
            return usuario;
        }
    }

    @Override
    public boolean deleteById(String idUsuario) {
        return collection.deleteOne(Filters.eq("idUsuario", idUsuario)).getDeletedCount() > 0;
    }

    @Override
    public long count() {
        return collection.countDocuments();
    }

    private Usuario documentToUsuario(Document doc) {
        return new Usuario(
                doc.getString("idUsuario"),
                doc.getString("nombreUsuario"),
                doc.getString("contrasena"),
                doc.getString("nombreCompleto"),
                doc.getString("rol"),
                true // usarDirecto
        );
    }
}
