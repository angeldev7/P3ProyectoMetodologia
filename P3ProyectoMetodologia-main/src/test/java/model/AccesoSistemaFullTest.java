package model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class AccesoSistemaFullTest {

    @Test
    public void testConstructorBasico() {
        AccesoSistema a = new AccesoSistema("admin", "Admin", "EXITOSO", "Login OK");
        assertEquals("admin", a.getUsuario());
        assertEquals("Admin", a.getRol());
        assertEquals("EXITOSO", a.getTipoAcceso());
        assertEquals("Login OK", a.getMensaje());
        assertNotNull(a.getFechaHora());
        assertEquals("localhost", a.getIp());
    }

    @Test
    public void testConstructorCompleto() {
        AccesoSistema a = new AccesoSistema("user", "Vendedor", "2026-02-07 10:00:00", "FALLIDO", "192.168.1.1", "Pass incorrecta");
        assertEquals("user", a.getUsuario());
        assertEquals("Vendedor", a.getRol());
        assertEquals("2026-02-07 10:00:00", a.getFechaHora());
        assertEquals("FALLIDO", a.getTipoAcceso());
        assertEquals("192.168.1.1", a.getIp());
        assertEquals("Pass incorrecta", a.getMensaje());
    }

    @Test
    public void testSetUsuario() {
        AccesoSistema a = new AccesoSistema("u1", "R", "EXITOSO", "M");
        a.setUsuario("u2");
        assertEquals("u2", a.getUsuario());
    }

    @Test
    public void testSetRol() {
        AccesoSistema a = new AccesoSistema("u", "Rol1", "EXITOSO", "M");
        a.setRol("Rol2");
        assertEquals("Rol2", a.getRol());
    }

    @Test
    public void testSetFechaHora() {
        AccesoSistema a = new AccesoSistema("u", "R", "EXITOSO", "M");
        a.setFechaHora("2025-01-01 00:00:00");
        assertEquals("2025-01-01 00:00:00", a.getFechaHora());
    }

    @Test
    public void testSetTipoAcceso() {
        AccesoSistema a = new AccesoSistema("u", "R", "EXITOSO", "M");
        a.setTipoAcceso("FALLIDO");
        assertEquals("FALLIDO", a.getTipoAcceso());
    }

    @Test
    public void testSetIp() {
        AccesoSistema a = new AccesoSistema("u", "R", "EXITOSO", "M");
        a.setIp("10.0.0.1");
        assertEquals("10.0.0.1", a.getIp());
    }

    @Test
    public void testSetMensaje() {
        AccesoSistema a = new AccesoSistema("u", "R", "EXITOSO", "Original");
        a.setMensaje("Nuevo");
        assertEquals("Nuevo", a.getMensaje());
    }

    @Test
    public void testToString() {
        AccesoSistema a = new AccesoSistema("admin", "Admin", "EXITOSO", "OK");
        String str = a.toString();
        assertTrue(str.contains("admin"));
        assertTrue(str.contains("Admin"));
        assertTrue(str.contains("EXITOSO"));
        assertTrue(str.contains("OK"));
    }
}
