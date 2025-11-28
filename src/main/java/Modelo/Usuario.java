package Modelo;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * Clase que representa un usuario del sistema contable
 * Fase: Implementación - Metodología Cascada
 */
public class Usuario implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String idUsuario; // ID legible (ADM-001, JEF-001, ASIS-001)
    private String nombreUsuario;
    private String contrasena;  // Ahora en texto plano
    private String nombreCompleto;
    private String rol;
    private boolean activo;
    
    // Constructor
    public Usuario(String idUsuario, String nombreUsuario, String contrasena, 
                   String nombreCompleto, String rol) {
        this.idUsuario = idUsuario;
        this.nombreUsuario = nombreUsuario;
        this.contrasena = contrasena;  // Ya no se encripta
        this.nombreCompleto = nombreCompleto;
        this.rol = rol;
        this.activo = true;
    }
    
    // Constructor para cargar desde JSON (con contraseña en texto plano)
    public Usuario(String idUsuario, String nombreUsuario, String contrasena, 
                   String nombreCompleto, String rol, boolean usarDirecto) {
        this.idUsuario = idUsuario;
        this.nombreUsuario = nombreUsuario;
        this.contrasena = contrasena;  // Texto plano
        this.nombreCompleto = nombreCompleto;
        this.rol = rol;
        this.activo = true;
    }
    
    /**
     * Verifica si la contraseña proporcionada es correcta
     * Ahora compara directamente sin encriptar
     */
    public boolean autenticar(String contrasenaIngresada) {
        return this.contrasena.equals(contrasenaIngresada);
    }
    
    /**
     * Verifica si el usuario tiene permiso para realizar una acción
     */
    public boolean tienePermiso(String accion) {
        List<String> permisosAdminMaster = Arrays.asList(
            "GESTIONAR_USUARIOS", "GESTIONAR_ROLES", "VER_BITACORA_COMPLETA",
            "GENERAR_RESPALDO", "APROBAR_TRANSACCION", "CALCULAR_IVA",
            "REGISTRAR_TRANSACCION", "ELIMINAR_FACTURA"
        );
        List<String> permisosJefatura = Arrays.asList(
            "APROBAR_TRANSACCION", "GENERAR_RESPALDO", "VER_BITACORA_COMPLETA", 
            "CALCULAR_IVA", "REGISTRAR_TRANSACCION", "ELIMINAR_FACTURA"
        );
        List<String> permisosAsistente = Arrays.asList(
            "REGISTRAR_TRANSACCION", "ELIMINAR_FACTURA"
        );
        if (rol.equals(TipoRol.ADMIN_MASTER)) {
            return permisosAdminMaster.contains(accion);
        } else if (rol.equals(TipoRol.JEFATURA_FINANCIERA)) {
            return permisosJefatura.contains(accion);
        }
        return permisosAsistente.contains(accion);
    }
    
    // Getters y Setters
    public String getIdUsuario() { return idUsuario; }
    public String getNombreUsuario() { return nombreUsuario; }
    public String getNombreCompleto() { return nombreCompleto; }
    public String getRol() { return rol; }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
    public String getContrasena() { return contrasena; }  // Retorna contraseña en texto plano
    
    @Override
    public String toString() {
        return nombreCompleto + " (" + rol + ")";
    }
}
