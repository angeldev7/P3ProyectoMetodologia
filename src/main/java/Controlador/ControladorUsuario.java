package Controlador;

import Modelo.*;
import Repositorio.UsuarioRepository;
import Repositorio.RolRepository;
import Repositorio.impl.UsuarioRepositoryMongo;
import Repositorio.impl.RolRepositoryMongo;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

/**
 * Controlador para gestión de usuarios, autenticación y roles personalizados (MongoDB exclusivo)
 * Persistencia 100% en MongoDB mediante UsuarioRepositoryMongo y RolRepositoryMongo.
 */
public class ControladorUsuario {
    private static ControladorUsuario instancia;
    private UsuarioRepository usuarioRepository; // Mongo
    private RolRepository rolRepository; // Mongo
    private Usuario usuarioActual;
    
    private ControladorUsuario() {
        try {
            usuarioRepository = new UsuarioRepositoryMongo();
            rolRepository = new RolRepositoryMongo();
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
        if (usuarioActual == null || !Usuario.ROL_ADMIN_MASTER.equals(usuarioActual.getRol())) {
            return false;
        }
        boolean eliminado = usuarioRepository.deleteById(idUsuario);
        return eliminado;
    }

    /**
     * Actualiza datos de usuario (nombre completo, contraseña, rol)
     * Si se cambia el rol, también actualiza los permisos correspondientes
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
            fNombre.setAccessible(true); 
            fContrasena.setAccessible(true); 
            fRol.setAccessible(true);
            
            // Actualizar campos
            if (nuevoNombre != null && !nuevoNombre.isBlank()) {
                fNombre.set(existente, nuevoNombre);
            }
            if (nuevaContrasena != null && !nuevaContrasena.isBlank()) {
                fContrasena.set(existente, nuevaContrasena);
            }
            
            // Si se cambia el rol, actualizar permisos
            if (nuevoRol != null && !nuevoRol.isBlank() && !nuevoRol.equals(existente.getRol())) {
                fRol.set(existente, nuevoRol);
                
                // Reconfigurar permisos según el nuevo rol
                boolean esRolPredefinido = Usuario.ROL_ADMIN_MASTER.equals(nuevoRol) ||
                                          Usuario.ROL_JEFATURA_FINANCIERA.equals(nuevoRol) ||
                                          Usuario.ROL_ASISTENTE_CONTABLE.equals(nuevoRol);
                
                if (esRolPredefinido) {
                    // Rol predeterminado: limpiar permisos personalizados para que use los por defecto
                    existente.setPermisos(new java.util.HashSet<>());
                } else {
                    // Rol personalizado: cargar permisos desde MongoDB
                    RolPersonalizado rolPersonalizado = rolRepository.findByNombre(nuevoRol);
                    if (rolPersonalizado != null) {
                        existente.setPermisos(rolPersonalizado.getPermisos());
                    }
                }
            }
            
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
    
    // ============= GESTIÓN DE ROLES PERSONALIZADOS =============
    
    /**
     * Crea un nuevo rol personalizado y lo guarda en MongoDB
     * @param nombreRol Nombre del rol
     * @param permisos Set de permisos asignados
     * @return true si se creó exitosamente, false si ya existe
     */
    public boolean crearRolPersonalizado(String nombreRol, Set<String> permisos) {
        if (usuarioActual == null || !Usuario.ROL_ADMIN_MASTER.equals(usuarioActual.getRol())) {
            return false; // Solo Admin Master puede crear roles
        }
        
        if (rolRepository.existsByNombre(nombreRol)) {
            return false; // El rol ya existe
        }
        
        String fechaCreacion = LocalDateTime.now().format(
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        );
        
        RolPersonalizado nuevoRol = new RolPersonalizado(
            null, // El ID se genera automáticamente
            nombreRol,
            permisos,
            usuarioActual.getNombreUsuario(),
            fechaCreacion
        );
        
        rolRepository.save(nuevoRol);
        return true;
    }
    
    /**
     * Obtiene todos los roles personalizados del sistema
     */
    public List<RolPersonalizado> getRolesPersonalizados() {
        return rolRepository.findAll();
    }
    
    /**
     * Obtiene un rol personalizado por su nombre
     */
    public RolPersonalizado getRolPorNombre(String nombreRol) {
        return rolRepository.findByNombre(nombreRol);
    }
    
    /**
     * Elimina un rol personalizado (solo Admin Master)
     */
    public boolean eliminarRol(String idRol) {
        if (usuarioActual == null || !Usuario.ROL_ADMIN_MASTER.equals(usuarioActual.getRol())) {
            return false;
        }
        return rolRepository.deleteById(idRol);
    }
    
    /**
     * Obtiene todos los nombres de roles disponibles (predeterminados + personalizados)
     */
    public List<String> getTodosLosRolesDisponibles() {
        List<String> roles = new java.util.ArrayList<>();
        // Roles predeterminados (excepto Admin Master que no se debe asignar manualmente)
        roles.add(Usuario.ROL_JEFATURA_FINANCIERA);
        roles.add(Usuario.ROL_ASISTENTE_CONTABLE);
        
        // Roles personalizados
        for (RolPersonalizado rol : rolRepository.findAll()) {
            roles.add(rol.getNombreRol());
        }
        
        return roles;
    }
    
}
