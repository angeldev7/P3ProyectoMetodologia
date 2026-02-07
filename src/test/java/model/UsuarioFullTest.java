package model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class UsuarioFullTest {

    @Test
    public void testConstructor() {
        Usuario u = new Usuario("admin", "pass", "Admin User", "Admin");
        assertEquals("admin", u.getUsuario());
        assertEquals("pass", u.getContrasena());
        assertEquals("Admin User", u.getNombreCompleto());
        assertEquals("Admin", u.getRol());
        assertEquals("Activo", u.getEstado());
        assertFalse(u.isBloqueado());
        assertNull(u.getFechaBloqueo());
        assertNotNull(u.getFechaCreacion());
    }

    @Test
    public void testSetUsuario() {
        Usuario u = new Usuario("a", "p", "N", "R");
        u.setUsuario("b");
        assertEquals("b", u.getUsuario());
    }

    @Test
    public void testSetContrasena() {
        Usuario u = new Usuario("a", "old", "N", "R");
        u.setContrasena("new");
        assertEquals("new", u.getContrasena());
    }

    @Test
    public void testSetNombreCompleto() {
        Usuario u = new Usuario("a", "p", "Old", "R");
        u.setNombreCompleto("New");
        assertEquals("New", u.getNombreCompleto());
    }

    @Test
    public void testSetRol() {
        Usuario u = new Usuario("a", "p", "N", "Old");
        u.setRol("New");
        assertEquals("New", u.getRol());
    }

    @Test
    public void testSetEstado() {
        Usuario u = new Usuario("a", "p", "N", "R");
        u.setEstado("Inactivo");
        assertEquals("Inactivo", u.getEstado());
    }

    @Test
    public void testSetFechaCreacion() {
        Usuario u = new Usuario("a", "p", "N", "R");
        u.setFechaCreacion("2025-01-01");
        assertEquals("2025-01-01", u.getFechaCreacion());
    }

    @Test
    public void testSetBloqueadoTrue() {
        Usuario u = new Usuario("a", "p", "N", "R");
        u.setBloqueado(true);
        assertTrue(u.isBloqueado());
        assertEquals("Bloqueado", u.getEstado());
        assertNotNull(u.getFechaBloqueo());
    }

    @Test
    public void testSetBloqueadoFalse() {
        Usuario u = new Usuario("a", "p", "N", "R");
        u.setBloqueado(true);
        u.setBloqueado(false);
        assertFalse(u.isBloqueado());
        assertEquals("Activo", u.getEstado());
        assertNull(u.getFechaBloqueo());
    }

    @Test
    public void testSetFechaBloqueo() {
        Usuario u = new Usuario("a", "p", "N", "R");
        u.setFechaBloqueo("2026-02-07");
        assertEquals("2026-02-07", u.getFechaBloqueo());
    }

    @Test
    public void testSetFechaBloqueoNull() {
        Usuario u = new Usuario("a", "p", "N", "R");
        u.setFechaBloqueo("2026-02-07");
        u.setFechaBloqueo(null);
        assertNull(u.getFechaBloqueo());
    }

    @Test
    public void testBloqueoDesbloqueoCompleto() {
        Usuario u = new Usuario("user1", "pass", "User One", "Vendedor");
        assertFalse(u.isBloqueado());
        assertEquals("Activo", u.getEstado());

        u.setBloqueado(true);
        assertTrue(u.isBloqueado());
        assertEquals("Bloqueado", u.getEstado());
        assertNotNull(u.getFechaBloqueo());

        u.setBloqueado(false);
        assertFalse(u.isBloqueado());
        assertEquals("Activo", u.getEstado());
        assertNull(u.getFechaBloqueo());
    }

    @Test
    public void testFechaCreacionFormatoCorrecto() {
        Usuario u = new Usuario("a", "p", "N", "R");
        assertTrue(u.getFechaCreacion().matches("\\d{4}-\\d{2}-\\d{2}"));
    }
}
