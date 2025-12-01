package Modelo;

import java.io.Serializable;
import java.util.*;

/**
 * Clase que representa un usuario del sistema contable
 */
public class Usuario implements Serializable {
    private static final long serialVersionUID = 1L;
    
    // Roles predefinidos
    public static final String ROL_ADMIN_MASTER = "Admin Master";
    public static final String ROL_JEFATURA_FINANCIERA = "Jefatura Financiera";
    public static final String ROL_ASISTENTE_CONTABLE = "Asistente Contable";
    
    // Permisos disponibles
    public static final String PERMISO_REGISTRAR_TRANSACCION = "REGISTRAR_TRANSACCION";
    public static final String PERMISO_APROBAR_TRANSACCION = "APROBAR_TRANSACCION";
    public static final String PERMISO_RECHAZAR_TRANSACCION = "RECHAZAR_TRANSACCION";
    public static final String PERMISO_ELIMINAR_TRANSACCION = "ELIMINAR_TRANSACCION";
    public static final String PERMISO_VER_BITACORA = "VER_BITACORA";
    public static final String PERMISO_CALCULAR_IVA = "CALCULAR_IVA";
    public static final String PERMISO_GENERAR_RESPALDO = "GENERAR_RESPALDO";
    public static final String PERMISO_GESTIONAR_USUARIOS = "GESTIONAR_USUARIOS";
    public static final String PERMISO_CREAR_ROLES = "CREAR_ROLES";
    
    private String idUsuario; // ID legible (ADM-001, JEF-001, ASIS-001)
    private String nombreUsuario;
    private String contrasena;  // Ahora en texto plano
    private String nombreCompleto;
    private String rol;
    private boolean activo;
    private Set<String> permisosPersonalizados; // Permisos específicos del usuario
    
    // Constructor
    public Usuario(String idUsuario, String nombreUsuario, String contrasena, 
                   String nombreCompleto, String rol) {
        this.idUsuario = idUsuario;
        this.nombreUsuario = nombreUsuario;
        this.contrasena = contrasena;
        this.nombreCompleto = nombreCompleto;
        this.rol = rol;
        this.activo = true;
        this.permisosPersonalizados = new HashSet<>();
        configurarPermisosPorDefecto();
    }
    
    /**
     * Verifica si la contraseña proporcionada es correcta
     */
    public boolean autenticar(String contrasenaIngresada) {
        return this.contrasena.equals(contrasenaIngresada);
    }
    
    /**
     * Configura permisos por defecto según el rol
     * Para roles personalizados, los permisos se cargan desde MongoDB usando setPermisos()
     */
    private void configurarPermisosPorDefecto() {
        permisosPersonalizados.clear();
        
        switch (rol) {
            case ROL_ADMIN_MASTER:
                // Admin Master tiene todos los permisos
                permisosPersonalizados.add(PERMISO_REGISTRAR_TRANSACCION);
                permisosPersonalizados.add(PERMISO_APROBAR_TRANSACCION);
                permisosPersonalizados.add(PERMISO_RECHAZAR_TRANSACCION);
                permisosPersonalizados.add(PERMISO_ELIMINAR_TRANSACCION);
                permisosPersonalizados.add(PERMISO_VER_BITACORA);
                permisosPersonalizados.add(PERMISO_CALCULAR_IVA);
                permisosPersonalizados.add(PERMISO_GENERAR_RESPALDO);
                permisosPersonalizados.add(PERMISO_GESTIONAR_USUARIOS);
                permisosPersonalizados.add(PERMISO_CREAR_ROLES);
                break;
            case ROL_JEFATURA_FINANCIERA:
                permisosPersonalizados.add(PERMISO_REGISTRAR_TRANSACCION);
                permisosPersonalizados.add(PERMISO_APROBAR_TRANSACCION);
                permisosPersonalizados.add(PERMISO_RECHAZAR_TRANSACCION);
                permisosPersonalizados.add(PERMISO_VER_BITACORA);
                permisosPersonalizados.add(PERMISO_CALCULAR_IVA);
                permisosPersonalizados.add(PERMISO_GENERAR_RESPALDO);
                break;
            case ROL_ASISTENTE_CONTABLE:
                permisosPersonalizados.add(PERMISO_REGISTRAR_TRANSACCION);
                permisosPersonalizados.add(PERMISO_VER_BITACORA);
                break;
            default:
                // Para roles personalizados, los permisos deben cargarse desde MongoDB
                // El repositorio debe llamar setPermisos() después de crear el objeto
                break;
        }
    }
    
    /**
     * Verifica si el usuario tiene un permiso específico
     */
    public boolean tienePermiso(String permiso) {
        return permisosPersonalizados.contains(permiso);
    }
    
    /**
     * Agrega un permiso personalizado
     */
    public void agregarPermiso(String permiso) {
        permisosPersonalizados.add(permiso);
    }
    
    /**
     * Remueve un permiso
     */
    public void removerPermiso(String permiso) {
        permisosPersonalizados.remove(permiso);
    }
    
    /**
     * Obtiene todos los permisos del usuario
     */
    public Set<String> getPermisos() {
        return new HashSet<>(permisosPersonalizados);
    }
    
    /**
     * Establece permisos personalizados
     */
    public void setPermisos(Set<String> permisos) {
        this.permisosPersonalizados = new HashSet<>(permisos);
    }
    
    // Getters y Setters
    public String getIdUsuario() { return idUsuario; }
    public String getNombreUsuario() { return nombreUsuario; }
    public String getNombreCompleto() { return nombreCompleto; }
    public String getRol() { return rol; }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
    public String getContrasena() { return contrasena; }
    
    /**
     * Obtiene lista de todos los permisos disponibles
     */
    public static List<String> getTodosLosPermisos() {
        return Arrays.asList(
            PERMISO_REGISTRAR_TRANSACCION,
            PERMISO_APROBAR_TRANSACCION,
            PERMISO_RECHAZAR_TRANSACCION,
            PERMISO_ELIMINAR_TRANSACCION,
            PERMISO_VER_BITACORA,
            PERMISO_CALCULAR_IVA,
            PERMISO_GENERAR_RESPALDO,
            PERMISO_GESTIONAR_USUARIOS,
            PERMISO_CREAR_ROLES
        );
    }
    
    @Override
    public String toString() {
        return nombreCompleto + " (" + rol + ")";
    }
}
