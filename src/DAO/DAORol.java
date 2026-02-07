// DAO/DAORol.java
package DAO;

import Database.ConexionBaseDatos;
import model.Rol;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class DAORol {
    private MongoCollection<Document> coleccion;
    private static final Logger logger = LoggerFactory.getLogger(DAORol.class);
    
    public DAORol() {
        this.coleccion = ConexionBaseDatos.getColeccion("roles");
    }
    
    public boolean crearRol(Rol rol) {
        try {
            Document doc = new Document("nombre", rol.getNombre())
                .append("permisos", rol.getPermisos())
                .append("contadorUsuarios", rol.getContadorUsuarios())
                .append("fechaCreacion", rol.getFechaCreacion());
            
            coleccion.insertOne(doc);
            return true;
        } catch (Exception e) {
            logger.error("Error creando rol: " + e.getMessage());
            return false;
        }
    }
    
    public Rol buscarRolPorNombre(String nombre) {
        Document doc = coleccion.find(Filters.eq("nombre", nombre)).first();
        if (doc != null) {
            return documentoARol(doc);
        }
        return null;
    }
    
    public List<Rol> obtenerTodosRoles() {
        List<Rol> roles = new ArrayList<>();
        for (Document doc : coleccion.find()) {
            roles.add(documentoARol(doc));
        }
        return roles;
    }
    
    public boolean actualizarRol(String nombre, Rol rol) {
        try {
            Bson actualizaciones = Updates.combine(
                Updates.set("permisos", rol.getPermisos()),
                Updates.set("contadorUsuarios", rol.getContadorUsuarios())
            );
            
            coleccion.updateOne(Filters.eq("nombre", nombre), actualizaciones);
            return true;
        } catch (Exception e) {
            logger.error("Error actualizando rol: " + e.getMessage());
            return false;
        }
    }
    
    public boolean eliminarRol(String nombre) {
        try {
            coleccion.deleteOne(Filters.eq("nombre", nombre));
            return true;
        } catch (Exception e) {
            logger.error("Error eliminando rol: " + e.getMessage());
            return false;
        }
    }
    
    public boolean actualizarContadorUsuarios(String nombreRol, int cambio) {
        try {
            Rol rol = buscarRolPorNombre(nombreRol);
            if (rol != null) {
                int nuevoContador = rol.getContadorUsuarios() + cambio;
                coleccion.updateOne(
                    Filters.eq("nombre", nombreRol),
                    Updates.set("contadorUsuarios", nuevoContador)
                );
                return true;
            }
            return false;
        } catch (Exception e) {
            logger.error("Error actualizando contador de usuarios: " + e.getMessage());
            return false;
        }
    }
    
    private Rol documentoARol(Document doc) {
        Rol rol = new Rol(doc.getString("nombre"));
        @SuppressWarnings("unchecked")
        List<String> permisos = (List<String>) doc.get("permisos");
        if (permisos != null) {
            rol.setPermisos(permisos);
        }
        rol.setContadorUsuarios(doc.getInteger("contadorUsuarios", 0));
        rol.setFechaCreacion(doc.getString("fechaCreacion"));
        return rol;
    }
}