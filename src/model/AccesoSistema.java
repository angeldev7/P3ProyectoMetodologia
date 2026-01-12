package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AccesoSistema {
    private String usuario;
    private String rol;
    private String fechaHora;
    private String tipoAcceso; // "EXITOSO" o "FALLIDO"
    private String ip; // Para futuras implementaciones
    private String mensaje;

    public AccesoSistema(String usuario, String rol, String tipoAcceso, String mensaje) {
        this.usuario = usuario;
        this.rol = rol;
        this.tipoAcceso = tipoAcceso;
        this.mensaje = mensaje;
        this.fechaHora = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        this.ip = "localhost"; // Por ahora estático
    }

    // Constructor completo para cargar desde base de datos
    public AccesoSistema(String usuario, String rol, String fechaHora, String tipoAcceso, String ip, String mensaje) {
        this.usuario = usuario;
        this.rol = rol;
        this.fechaHora = fechaHora;
        this.tipoAcceso = tipoAcceso;
        this.ip = ip;
        this.mensaje = mensaje;
    }

    // Getters y Setters
    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    public String getFechaHora() { return fechaHora; }
    public void setFechaHora(String fechaHora) { this.fechaHora = fechaHora; }

    public String getTipoAcceso() { return tipoAcceso; }
    public void setTipoAcceso(String tipoAcceso) { this.tipoAcceso = tipoAcceso; }

    public String getIp() { return ip; }
    public void setIp(String ip) { this.ip = ip; }

    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }

    @Override
    public String toString() {
        return String.format("Usuario: %s | Rol: %s | Fecha: %s | Tipo: %s | Mensaje: %s",
            usuario, rol, fechaHora, tipoAcceso, mensaje);
    }
}