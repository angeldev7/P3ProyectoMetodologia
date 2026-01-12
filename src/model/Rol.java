// model/Rol.java
package model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Rol {
    private String nombre;
    private List<String> permisos;
    private int contadorUsuarios;
    private String fechaCreacion;

    public Rol(String nombre) {
        this.nombre = nombre;
        this.permisos = new ArrayList<>();
        this.contadorUsuarios = 0;
        this.fechaCreacion = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    // Getters y Setters
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public List<String> getPermisos() { return permisos; }
    public void setPermisos(List<String> permisos) { this.permisos = permisos; }
    
    public void agregarPermiso(String permiso) { 
        if (!permisos.contains(permiso)) {
            permisos.add(permiso); 
        }
    }
    
    public void eliminarPermiso(String permiso) { 
        permisos.remove(permiso); 
    }

    public int getContadorUsuarios() { return contadorUsuarios; }
    public void setContadorUsuarios(int contadorUsuarios) { this.contadorUsuarios = contadorUsuarios; }
    
    public void incrementarContadorUsuarios() { this.contadorUsuarios++; }
    public void decrementarContadorUsuarios() { this.contadorUsuarios--; }

    public String getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(String fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    // Métodos auxiliares
    public boolean tienePermiso(String permiso) {
        return permisos.contains(permiso);
    }

    public String getPermisosComoString() {
        return String.join(",", permisos);
    }
}