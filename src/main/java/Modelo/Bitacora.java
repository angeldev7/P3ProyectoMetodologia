package Modelo;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Clase que representa un registro en la bitácora de auditoría
 */
public class Bitacora implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String idRegistro; // legible BIT-000001
    private Usuario usuario;
    private LocalDateTime fechaHora;
    private String accion;
    private String descripcion;
    
    public Bitacora(String idRegistro, Usuario usuario, String accion, String descripcion) {
        this.idRegistro = idRegistro;
        this.usuario = usuario;
        this.fechaHora = LocalDateTime.now();
        this.accion = accion;
        this.descripcion = descripcion;
    }
    
    // Getters
    public String getIdRegistro() { return idRegistro; }
    public Usuario getUsuario() { return usuario; }
    public LocalDateTime getFechaHora() { return fechaHora; }
    public String getAccion() { return accion; }
    public String getDescripcion() { return descripcion; }
    
    public String getFechaHoraFormateada() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        return fechaHora.format(formatter);
    }
    
    @Override
    public String toString() {
        return String.format("[%s] %s - %s: %s", 
            getFechaHoraFormateada(), 
            usuario.getNombreUsuario(), 
            accion, 
            descripcion);
    }
}
