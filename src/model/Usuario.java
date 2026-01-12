// model/Usuario.java
package model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Usuario {
    private String usuario;
    private String contrasena;
    private String nombreCompleto;
    private String rol;
    private String estado;
    private String fechaCreacion;
    private boolean bloqueado;  
    private String fechaBloqueo; 

    public Usuario(String usuario, String contrasena, String nombreCompleto, String rol) {
        this.usuario = usuario;
        this.contrasena = contrasena;
        this.nombreCompleto = nombreCompleto;
        this.rol = rol;
        this.estado = "Activo";
        this.fechaCreacion = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        this.bloqueado = false; // Por defecto no está bloqueado
        this.fechaBloqueo = null;
    }

    // Getters y Setters
    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }

    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }

    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(String fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    // NUEVOS: Getters y Setters para bloqueo
    public boolean isBloqueado() { return bloqueado; }
    public void setBloqueado(boolean bloqueado) { 
        this.bloqueado = bloqueado;
        if (bloqueado) {
            // Usar solo fecha, no hora, para evitar problemas
            this.fechaBloqueo = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            this.estado = "Bloqueado";
        } else {
            this.fechaBloqueo = null;
            this.estado = "Activo";
        }
    }

    public String getFechaBloqueo() { return fechaBloqueo; }
    public void setFechaBloqueo(String fechaBloqueo) { this.fechaBloqueo = fechaBloqueo; }
}