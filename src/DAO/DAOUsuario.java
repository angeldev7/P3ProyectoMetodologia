package DAO;

import Database.ConexionBaseDatos;
import model.Usuario;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;

import org.bson.Document;
import org.bson.conversions.Bson;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import util.PasswordHasher;

public class DAOUsuario {
    private MongoCollection<Document> coleccion;
    
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
            System.err.println("Error obteniendo usuarios: " + e.getMessage());
        }
        return usuarios;
    }
    public boolean autenticar(String usuario, String contrasena) {
        try {
            Usuario user = buscarUsuarioPorNombre(usuario);
            if (user != null) {
                if (user.isBloqueado()) {
                    System.out.println("Usuario bloqueado: " + usuario);
                    return false;
                }
                
                System.out.println("Usuario encontrado: " + user.getUsuario());
                boolean passwordValido = PasswordHasher.checkPassword(contrasena, user.getContrasena());
                System.out.println("Verificación de contraseña: " + (passwordValido ? "válida" : "inválida"));
                return passwordValido;
            } else {
                System.out.println("Usuario no encontrado: " + usuario);
            }
        } catch (Exception e) {
            System.err.println("Error en autenticación: " + e.getMessage());
        }
        return false;
    }
    
    public boolean crearUsuario(Usuario usuario) {
        try {
            String contrasenaEncriptada = PasswordHasher.hashPassword(usuario.getContrasena());
            if (contrasenaEncriptada == null) {
                System.err.println("Error al encriptar contraseña");
                return false;
            }
            
            Document doc = new Document("usuario", usuario.getUsuario())
                .append("contrasena", contrasenaEncriptada)
                .append("nombreCompleto", usuario.getNombreCompleto())
                .append("rol", usuario.getRol())
                .append("estado", usuario.getEstado())
                .append("fechaCreacion", usuario.getFechaCreacion());
            
            coleccion.insertOne(doc);
            System.out.println("Usuario creado con contraseña encriptada: " + usuario.getUsuario());
            return true;
        } catch (Exception e) {
            System.err.println("Error creando usuario: " + e.getMessage());
            return false;
        }
    }
    
    public boolean cambiarContrasena(String usuario, String nuevaContrasena) {
        try {
            String contrasenaEncriptada = PasswordHasher.hashPassword(nuevaContrasena);
            if (contrasenaEncriptada == null) {
                System.err.println("Error al encriptar nueva contraseña");
                return false;
            }
            
            coleccion.updateOne(
                Filters.eq("usuario", usuario), 
                Updates.set("contrasena", contrasenaEncriptada)
            );
            System.out.println("Contraseña actualizada para: " + usuario);
            return true;
        } catch (Exception e) {
            System.err.println("Error cambiando contraseña: " + e.getMessage());
            return false;
        }
    }
    


    public boolean bloquearUsuario(String usuario) {
        try {
            Document doc = coleccion.find(Filters.eq("usuario", usuario)).first();
            if (doc == null) {
                System.err.println("Usuario no encontrado: " + usuario);
                return false;
            }
             
            String fechaBloqueoStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
             
            Bson actualizaciones = Updates.combine(
                Updates.set("bloqueado", true),
                Updates.set("estado", "Bloqueado"),
                Updates.set("fechaBloqueo", fechaBloqueoStr)
            );
             
            coleccion.updateOne(Filters.eq("usuario", usuario), actualizaciones);
            System.out.println("Usuario bloqueado: " + usuario);
            return true;
             
        } catch (Exception e) {
            System.err.println("Error bloqueando usuario: " + e.getMessage());
            return false;
        }
    }
    
    public Usuario buscarUsuarioPorNombre(String usuario) {
        try {
            Document doc = coleccion.find(Filters.eq("usuario", usuario)).first();
            if (doc != null) {
                return documentoAUsuario(doc);
            }
        } catch (Exception e) {
            System.err.println("Error buscando usuario: " + e.getMessage());
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
            System.err.println("❌ Error actualizando usuario: " + e.getMessage());
            return false;
        }
    }
    
    public boolean eliminarUsuario(String usuario) {
        try {
            coleccion.deleteOne(Filters.eq("usuario", usuario));
            return true;
        } catch (Exception e) {
            System.err.println("❌ Error eliminando usuario: " + e.getMessage());
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
            System.err.println("❌ Error verificando estado de bloqueo: " + e.getMessage());
            return false;
        }
    }
    private Usuario documentoAUsuario(Document doc) {
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
                    System.err.println("⚠️  Error obteniendo fechaBloqueo para usuario " + usuario.getUsuario() + ": " + e.getMessage());
                    usuario.setFechaBloqueo(null);
                }
            }
            
            return usuario;
        } catch (Exception e) {
            System.err.println("❌ Error grave convirtiendo documento a Usuario: " + e.getMessage());
            System.err.println("📄 Documento: " + doc.toJson());
            e.printStackTrace();
            return null;
        }
    }
    public boolean desbloquearUsuario(String usuario) {
        try {
            Document doc = coleccion.find(Filters.eq("usuario", usuario)).first();
            if (doc == null) {
                System.err.println("❌ Usuario no encontrado: " + usuario);
                return false;
            }
            
            Bson actualizaciones = Updates.combine(
                Updates.set("bloqueado", false),
                Updates.set("estado", "Activo"),
                Updates.unset("fechaBloqueo")
            );
            
            // ELIMINAR todo el debugging de UpdateResult
            coleccion.updateOne(Filters.eq("usuario", usuario), actualizaciones);
            System.out.println("Usuario desbloqueado: " + usuario);
            return true;
            
        } catch (Exception e) {
            System.err.println("❌ Error desbloqueando usuario: " + e.getMessage());
            return false;
        }
    }

    

}