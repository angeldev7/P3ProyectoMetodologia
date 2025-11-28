package Controlador;

import Modelo.*;
import Repositorio.UsuarioRepository;
import Repositorio.impl.UsuarioRepositoryMongo;
import java.util.List;

/**
 * Controlador para gestión de usuarios y autenticación (MongoDB exclusivo)
 * Persistencia 100% en MongoDB mediante UsuarioRepositoryMongo.
 */
public class ControladorUsuario {
    private static ControladorUsuario instancia;
    private UsuarioRepository usuarioRepository; // Mongo
    private Usuario usuarioActual;
    
    private ControladorUsuario() {
        try {
            usuarioRepository = new UsuarioRepositoryMongo();
        } catch (Exception e) {
            throw new RuntimeException("MongoDB no disponible: " + e.getMessage());
        }
    }
    
    public static ControladorUsuario getInstancia() {
        if (instancia == null) {
            instancia = new ControladorUsuario();
        }
        return instancia;
    }
    
    /**
     * Autentica un usuario en el sistema usando MongoDB
     */
    public boolean autenticar(String nombreUsuario, String contrasena) {
        Usuario usuario = usuarioRepository.findByNombreUsuario(nombreUsuario);
        if (usuario != null && usuario.getContrasena().equals(contrasena)) {
            usuarioActual = usuario;
            return true;
        }
        return false;
    }
    
    /**
     * Cierra la sesión del usuario actual
     */
    public void cerrarSesion() {
        usuarioActual = null;
    }
    
    /**
     * Verifica si hay un usuario autenticado
     */
    public boolean hayUsuarioAutenticado() {
        return usuarioActual != null;
    }
    
    public Usuario getUsuarioActual() {
        return usuarioActual;
    }

    /**
     * Elimina usuario por ID (solo Admin Master)
     */
    public boolean eliminarUsuario(String idUsuario) {
        if (usuarioActual == null || !TipoRol.ADMIN_MASTER.equals(usuarioActual.getRol())) {
            return false;
        }
        boolean eliminado = usuarioRepository.deleteById(idUsuario);
        return eliminado;
    }

    /**
     * Actualiza datos de usuario (nombre completo, contraseña, rol)
     */
    public boolean actualizarUsuario(String idUsuario, String nuevoNombre, String nuevaContrasena, String nuevoRol) {
        Usuario existente = null;
        for (Usuario u : usuarioRepository.findAll()) {
            if (u.getIdUsuario().equals(idUsuario)) { existente = u; break; }
        }
        if (existente == null) return false;
        try {
            java.lang.reflect.Field fNombre = Usuario.class.getDeclaredField("nombreCompleto");
            java.lang.reflect.Field fContrasena = Usuario.class.getDeclaredField("contrasena");
            java.lang.reflect.Field fRol = Usuario.class.getDeclaredField("rol");
            fNombre.setAccessible(true); fContrasena.setAccessible(true); fRol.setAccessible(true);
            if (nuevoNombre != null && !nuevoNombre.isBlank()) fNombre.set(existente, nuevoNombre);
            if (nuevaContrasena != null && !nuevaContrasena.isBlank()) fContrasena.set(existente, nuevaContrasena);
            if (nuevoRol != null && !nuevoRol.isBlank()) fRol.set(existente, nuevoRol);
            usuarioRepository.save(existente);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Agrega un nuevo usuario al sistema
     * @return true si se agregó correctamente, false si ya existe
     */
    public boolean agregarUsuario(String nombreUsuario, String contrasena, 
                                  String nombreCompleto, String rol) {
        boolean agregado;
        if (usuarioRepository.existsByNombreUsuario(nombreUsuario)) {
            agregado = false;
        } else {
            Usuario nuevo = new Usuario(null, nombreUsuario, contrasena, nombreCompleto, rol);
            usuarioRepository.save(nuevo);
            agregado = true;
        }
        return agregado;
    }
    
    /**
     * Obtiene todos los usuarios del sistema
     */
    public List<Usuario> getUsuarios() {
        return usuarioRepository.findAll();
    }
    
}
