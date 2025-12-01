package Modelo;

import java.util.HashSet;
import java.util.Set;

/**
 * Representa un rol personalizado creado por Admin Master
 * Se persiste en MongoDB para gestión dinámica de roles
 */
public class RolPersonalizado {
    private String idRol;
    private String nombreRol;
    private Set<String> permisos;
    private String creadoPor; // Usuario Admin Master que lo creó
    private String fechaCreacion;
    
    public RolPersonalizado() {
        this.permisos = new HashSet<>();
    }
    
    public RolPersonalizado(String idRol, String nombreRol, Set<String> permisos, 
                           String creadoPor, String fechaCreacion) {
        this.idRol = idRol;
        this.nombreRol = nombreRol;
        this.permisos = permisos != null ? permisos : new HashSet<>();
        this.creadoPor = creadoPor;
        this.fechaCreacion = fechaCreacion;
    }
    
    // Getters y Setters
    public String getIdRol() {
        return idRol;
    }
    
    public void setIdRol(String idRol) {
        this.idRol = idRol;
    }
    
    public String getNombreRol() {
        return nombreRol;
    }
    
    public void setNombreRol(String nombreRol) {
        this.nombreRol = nombreRol;
    }
    
    public Set<String> getPermisos() {
        return new HashSet<>(permisos);
    }
    
    public void setPermisos(Set<String> permisos) {
        this.permisos = permisos != null ? new HashSet<>(permisos) : new HashSet<>();
    }
    
    public String getCreadoPor() {
        return creadoPor;
    }
    
    public void setCreadoPor(String creadoPor) {
        this.creadoPor = creadoPor;
    }
    
    public String getFechaCreacion() {
        return fechaCreacion;
    }
    
    public void setFechaCreacion(String fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
    
    @Override
    public String toString() {
        return nombreRol;
    }
}
