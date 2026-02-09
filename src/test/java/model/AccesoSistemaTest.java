package model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

class AccesoSistemaTest {

    @Test
    void testConstructorSimple() {
        AccesoSistema acceso = new AccesoSistema("admin", "Administrador", "EXITOSO", "Login correcto");
        
        assertEquals("admin", acceso.getUsuario());
        assertEquals("Administrador", acceso.getRol());
        assertEquals("EXITOSO", acceso.getTipoAcceso());
        assertEquals("Login correcto", acceso.getMensaje());
        assertEquals("localhost", acceso.getIp());
        assertNotNull(acceso.getFechaHora());
        assertTrue(acceso.getFechaHora().contains(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
    }

    @Test
    void testConstructorCompleto() {
        AccesoSistema acceso = new AccesoSistema("vendedor", "Vendedor", "2024-01-15 10:30:45", 
                                                "FALLIDO", "192.168.1.100", "Contraseña incorrecta");
        
        assertEquals("vendedor", acceso.getUsuario());
        assertEquals("Vendedor", acceso.getRol());
        assertEquals("2024-01-15 10:30:45", acceso.getFechaHora());
        assertEquals("FALLIDO", acceso.getTipoAcceso());
        assertEquals("192.168.1.100", acceso.getIp());
        assertEquals("Contraseña incorrecta", acceso.getMensaje());
    }

    @Test
    void testSetters() {
        AccesoSistema acceso = new AccesoSistema("user", "User", "EXITOSO", "OK");
        
        acceso.setUsuario("admin");
        acceso.setRol("Administrador");
        acceso.setFechaHora("2024-02-01 14:20:30");
        acceso.setTipoAcceso("FALLIDO");
        acceso.setIp("10.0.0.1");
        acceso.setMensaje("Usuario bloqueado");

        assertEquals("admin", acceso.getUsuario());
        assertEquals("Administrador", acceso.getRol());
        assertEquals("2024-02-01 14:20:30", acceso.getFechaHora());
        assertEquals("FALLIDO", acceso.getTipoAcceso());
        assertEquals("10.0.0.1", acceso.getIp());
        assertEquals("Usuario bloqueado", acceso.getMensaje());
    }

    @Test
    void testTipoAccesoExitoso() {
        AccesoSistema accesoExitoso = new AccesoSistema("admin", "Admin", "EXITOSO", "OK");
        
        assertEquals("EXITOSO", accesoExitoso.getTipoAcceso());
    }

    @Test
    void testTipoAccesoFallido() {
        AccesoSistema accesoFallido = new AccesoSistema("admin", "Admin", "FALLIDO", "Error");
        
        assertEquals("FALLIDO", accesoFallido.getTipoAcceso());
    }

    @Test
    void testToString() {
        AccesoSistema acceso = new AccesoSistema("admin", "Administrador", "EXITOSO", "Login correcto");
        
        String resultado = acceso.toString();
        assertNotNull(resultado);
    }

    @Test 
    void testFechaHoraAutoGenerada() {
        String antesCreacion = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        
        AccesoSistema acceso = new AccesoSistema("user", "User", "EXITOSO", "OK");
        
        String despuesCreacion = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        
        assertTrue(acceso.getFechaHora().compareTo(antesCreacion) >= 0);
        assertTrue(acceso.getFechaHora().compareTo(despuesCreacion) <= 0);
    }
}