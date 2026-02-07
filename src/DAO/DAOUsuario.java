package DAO;

import Database.ConexionBaseDatos;
import model.Usuario;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import util.PasswordHasher;

public class DAOUsuario {
    private MongoCollection<Document> coleccion;
    private static final Logger logger = LoggerFactory.getLogger(DAOUsuario.class);
    
    public DAOUsuario() {
        this.coleccion = ConexionBaseDatos.getColeccion("usuarios");
    }
    
    public List<Usuario> obtenerTodosUsuarios() {
        List<Usuario> usuarios = new ArrayList<>();
        try {
            for (Document doc : coleccion.find()) {
                Usuario usuario = documentoAUsuario(doc);
                if (usuario != null) {
                    usuarios.add(usuario);
                }
            }
        } catch (Exception e) {
            logger.error("Error obteniendo usuarios: " + e.getMessage());
        }
        return usuarios;
    }
    public boolean autenticar(String usuario, String contrasena) {
        try {
            Usuario user = buscarUsuarioPorNombre(usuario);
            if (user != null) {
                if (user.isBloqueado()) {
                    logger.info("Usuario bloqueado: " + usuario);
                    return false;
                }
                
                logger.info("Usuario encontrado: " + user.getUsuario());
                boolean passwordValido = PasswordHasher.checkPassword(contrasena, user.getContrasena());
                logger.info("Verificación de contraseña: " + (passwordValido ? "válida" : "inválida"));
                return passwordValido;
            } else {
                logger.info("Usuario no encontrado: " + usuario);
            }
        } catch (Exception e) {
            logger.error("Error en autenticación: " + e.getMessage());
        }
        return false;
    }
    
    public boolean crearUsuario(Usuario usuario) {
        try {
            String contrasenaEncriptada = PasswordHasher.hashPassword(usuario.getContrasena());
            if (contrasenaEncriptada == null) {
                logger.error("Error al encriptar contraseña");
                return false;
            }
            
            Document doc = new Document("usuario", usuario.getUsuario())
                .append("contrasena", contrasenaEncriptada)
                .append("nombreCompleto", usuario.getNombreCompleto())
                .append("rol", usuario.getRol())
                .append("estado", usuario.getEstado())
                .append("fechaCreacion", usuario.getFechaCreacion());
            
            coleccion.insertOne(doc);
            logger.info("Usuario creado con contraseña encriptada: " + usuario.getUsuario());
            return true;
        } catch (Exception e) {
            logger.error("Error creando usuario: " + e.getMessage());
            return false;
        }
    }
    
    public boolean cambiarContrasena(String usuario, String nuevaContrasena) {
        try {
            String contrasenaEncriptada = PasswordHasher.hashPassword(nuevaContrasena);
            if (contrasenaEncriptada == null) {
                logger.error("Error al encriptar nueva contraseña");
                return false;
            }
            
            coleccion.updateOne(
                Filters.eq("usuario", usuario), 
                Updates.set("contrasena", contrasenaEncriptada)
            );
            logger.info("Contraseña actualizada para: " + usuario);
            return true;
        } catch (Exception e) {
            logger.error("Error cambiando contraseña: " + e.getMessage());
            return false;
        }
    }
    


    public boolean bloquearUsuario(String usuario) {
        try {
            Document doc = coleccion.find(Filters.eq("usuario", usuario)).first();
            if (doc == null) {
                logger.error("Usuario no encontrado: " + usuario);
                return false;
            }
             
            String fechaBloqueoStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
             
            Bson actualizaciones = Updates.combine(
                Updates.set("bloqueado", true),
                Updates.set("estado", "Bloqueado"),
                Updates.set("fechaBloqueo", fechaBloqueoStr)
            );
             
            coleccion.updateOne(Filters.eq("usuario", usuario), actualizaciones);
            logger.info("Usuario bloqueado: " + usuario);
            return true;
             
        } catch (Exception e) {
            logger.error("Error bloqueando usuario: " + e.getMessage());
            return false;
        }
    }
    
    public Usuario buscarUsuarioPorNombre(String usuario) {
    	if (usuario == null || usuario.trim().isEmpty()) {
    		logger.error("⚠️ Nombre de usuario nulo o vacío");
    		return null;
    	}
    	
    	try {
            Document doc = coleccion.find(Filters.eq("usuario", usuario)).first();
            if (doc != null) {
                return documentoAUsuario(doc);
            }
        } catch (Exception e) {
            logger.error("Error buscando usuario: " + e.getMessage());
        }
        return null;
    }
    
   
    
    public boolean actualizarUsuario(String usuario, Usuario user) {
        try {
            Bson actualizaciones = Updates.combine(
                Updates.set("nombreCompleto", user.getNombreCompleto()),
                Updates.set("rol", user.getRol()),
                Updates.set("estado", user.getEstado())
            );
            
            coleccion.updateOne(Filters.eq("usuario", usuario), actualizaciones);
            return true;
        } catch (Exception e) {
            logger.error("❌ Error actualizando usuario: " + e.getMessage());
            return false;
        }
    }
    
    public boolean eliminarUsuario(String usuario) {
        try {
            coleccion.deleteOne(Filters.eq("usuario", usuario));
            return true;
        } catch (Exception e) {
            logger.error("❌ Error eliminando usuario: " + e.getMessage());
            return false;
        }
    }
    public boolean puedeSerBloqueado(String usuario) {
        try {
            // El usuario admin no puede ser bloqueado
            if ("admin".equals(usuario)) {
                return false;
            }
            
            Document doc = coleccion.find(Filters.eq("usuario", usuario)).first();
            if (doc != null) {
                // Verificar si ya está bloqueado
                boolean yaBloqueado = doc.getBoolean("bloqueado", false);
                return !yaBloqueado;
            }
            return false;
        } catch (Exception e) {
            logger.error("❌ Error verificando estado de bloqueo: " + e.getMessage());
            return false;
        }
    }
    private Usuario documentoAUsuario(Document doc) {
    	if (doc == null) {
            logger.error("⚠️ Documento nulo en documentoAUsuario");
            return null;
        }
    	
    	try {
            // Crear usuario con datos básicos
            Usuario usuario = new Usuario(
                doc.getString("usuario"),
                doc.getString("contrasena"),
                doc.getString("nombreCompleto"),
                doc.getString("rol")
            );
            
            // Establecer estado
            if (doc.containsKey("estado")) {
                usuario.setEstado(doc.getString("estado"));
            } else {
                usuario.setEstado("Activo");
            }
            
            // Establecer fecha de creación
            if (doc.containsKey("fechaCreacion")) {
                usuario.setFechaCreacion(doc.getString("fechaCreacion"));
            }
            
            // Manejar el campo bloqueado
            boolean bloqueado = false;
            if (doc.containsKey("bloqueado")) {
                try {
                    bloqueado = doc.getBoolean("bloqueado");
                } catch (Exception e) {
                    // Si no es booleano, intentar como string
                    String bloqueadoStr = doc.getString("bloqueado");
                    bloqueado = "true".equalsIgnoreCase(bloqueadoStr) || "1".equals(bloqueadoStr);
                }
            }
            usuario.setBloqueado(bloqueado);
            
            // Manejar fecha de bloqueo - SOLUCIÓN AL ERROR
            if (doc.containsKey("fechaBloqueo")) {
                try {
                    String fechaBloqueo = doc.getString("fechaBloqueo");
                    usuario.setFechaBloqueo(fechaBloqueo);
                } catch (Exception e) {
                    logger.error("⚠️  Error obteniendo fechaBloqueo para usuario " + usuario.getUsuario() + ": " + e.getMessage());
                    usuario.setFechaBloqueo(null);
                }
            }
            
            return usuario;
        } catch (Exception e) {
            logger.error("❌ Error grave convirtiendo documento a Usuario: " + e.getMessage());
            logger.error("📄 Documento: " + doc.toJson());
            e.printStackTrace();
            return null;
        }
    }
    public boolean desbloquearUsuario(String usuario) {
        try {
            Document doc = coleccion.find(Filters.eq("usuario", usuario)).first();
            if (doc == null) {
                logger.error("❌ Usuario no encontrado: " + usuario);
                return false;
            }
            
            Bson actualizaciones = Updates.combine(
                Updates.set("bloqueado", false),
                Updates.set("estado", "Activo"),
                Updates.unset("fechaBloqueo")
            );
            
            // ELIMINAR todo el debugging de UpdateResult
            coleccion.updateOne(Filters.eq("usuario", usuario), actualizaciones);
            logger.info("Usuario desbloqueado: " + usuario);
            return true;
            
        } catch (Exception e) {
            logger.error("❌ Error desbloqueando usuario: " + e.getMessage());
            return false;
        }
    }

    

}