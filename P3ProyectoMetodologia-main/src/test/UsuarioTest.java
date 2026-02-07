package test;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import model.Usuario;

public class UsuarioTest {
    
    @Test
    public void testConstructor() {
        Usuario usuario = new Usuario("admin", "pass123", "Admin User", "Admin");
        assertNotNull(usuario);
        assertEquals("admin", usuario.getUsuario());
        assertEquals("pass123", usuario.getContrasena());
        assertEquals("Admin User", usuario.getNombreCompleto());
        assertEquals("Admin", usuario.getRol());
        assertEquals("Activo", usuario.getEstado());
        assertNotNull(usuario.getFechaCreacion());
        assertFalse(usuario.isBloqueado());
    }
    
    @Test
    public void testSetUsuario() {
        Usuario usuario = new Usuario("user1", "pass", "User One", "User");
        usuario.setUsuario("user2");
        assertEquals("user2", usuario.getUsuario());
    }
    
    @Test
    public void testSetContrasena() {
        Usuario usuario = new Usuario("user", "oldpass", "User", "User");
        usuario.setContrasena("newpass");
        assertEquals("newpass", usuario.getContrasena());
    }
    
    @Test
    public void testSetNombreCompleto() {
        Usuario usuario = new Usuario("user", "pass", "Old Name", "User");
        usuario.setNombreCompleto("New Name");
        assertEquals("New Name", usuario.getNombreCompleto());
    }
    
    @Test
    public void testSetRol() {
        Usuario usuario = new Usuario("user", "pass", "User", "User");
        usuario.setRol("Admin");
        assertEquals("Admin", usuario.getRol());
    }
    
    @Test
    public void testSetEstado() {
        Usuario usuario = new Usuario("user", "pass", "User", "User");
        assertEquals("Activo", usuario.getEstado());
        usuario.setEstado("Inactivo");
        assertEquals("Inactivo", usuario.getEstado());
    }
    
    @Test
    public void testBloquearUsuario() {
        Usuario usuario = new Usuario("user", "pass", "User", "User");
        assertFalse(usuario.isBloqueado());
        usuario.setBloqueado(true);
        assertTrue(usuario.isBloqueado());
    }
    
    @Test
    public void testDesbloquearUsuario() {
        Usuario usuario = new Usuario("user", "pass", "User", "User");
        usuario.setBloqueado(true);
        assertTrue(usuario.isBloqueado());
        usuario.setBloqueado(false);
        assertFalse(usuario.isBloqueado());
    }
    
    @Test
    public void testFechaBloqueo() {
        Usuario usuario = new Usuario("user", "pass", "User", "User");
        assertNull(usuario.getFechaBloqueo());
        usuario.setFechaBloqueo("2026-02-07");
        assertEquals("2026-02-07", usuario.getFechaBloqueo());
    }
    
    @Test
    public void testSetFechaCreacion() {
        Usuario usuario = new Usuario("user", "pass", "User", "User");
        String fechaOriginal = usuario.getFechaCreacion();
        assertNotNull(fechaOriginal);
        usuario.setFechaCreacion("2025-01-01");
        assertEquals("2025-01-01", usuario.getFechaCreacion());
    }
    
    @Test
    public void testUsuarioConDatosCompletos() {
        Usuario usuario = new Usuario("vendedor1", "secure123", "Juan Pérez", "Vendedor");
        assertEquals("vendedor1", usuario.getUsuario());
        assertEquals("secure123", usuario.getContrasena());
        assertEquals("Juan Pérez", usuario.getNombreCompleto());
        assertEquals("Vendedor", usuario.getRol());
        assertEquals("Activo", usuario.getEstado());
        assertFalse(usuario.isBloqueado());
        assertNull(usuario.getFechaBloqueo());
    }
    
    @Test
    public void testUsuarioConNombreVacio() {
        Usuario usuario = new Usuario("", "pass", "", "");
        assertEquals("", usuario.getUsuario());
        assertEquals("", usuario.getNombreCompleto());
        assertEquals("", usuario.getRol());
    }
    
    @Test
    public void testCambioEstadoMultiple() {
        Usuario usuario = new Usuario("user", "pass", "User", "User");
        usuario.setEstado("Inactivo");
        assertEquals("Inactivo", usuario.getEstado());
        usuario.setEstado("Suspendido");
        assertEquals("Suspendido", usuario.getEstado());
        usuario.setEstado("Activo");
        assertEquals("Activo", usuario.getEstado());
    }
    
    @Test
    public void testBloqueoConFecha() {
        Usuario usuario = new Usuario("user", "pass", "User", "User");
        usuario.setBloqueado(true);
        usuario.setFechaBloqueo("2026-02-07");
        assertTrue(usuario.isBloqueado());
        assertEquals("2026-02-07", usuario.getFechaBloqueo());
    }
    
    @Test
    public void testDesbloqueoLimpiaFecha() {
        Usuario usuario = new Usuario("user", "pass", "User", "User");
        usuario.setBloqueado(true);
        usuario.setFechaBloqueo("2026-02-07");
        usuario.setBloqueado(false);
        usuario.setFechaBloqueo(null);
        assertFalse(usuario.isBloqueado());
        assertNull(usuario.getFechaBloqueo());
    }
    
    @Test
    public void testMultiplesCambiosContrasena() {
        Usuario usuario = new Usuario("user", "pass1", "User", "User");
        usuario.setContrasena("pass2");
        assertEquals("pass2", usuario.getContrasena());
        usuario.setContrasena("pass3");
        assertEquals("pass3", usuario.getContrasena());
    }
    
    @Test
    public void testRolesVariados() {
        Usuario u1 = new Usuario("u1", "p", "User 1", "Admin");
        Usuario u2 = new Usuario("u2", "p", "User 2", "Vendedor");
        Usuario u3 = new Usuario("u3", "p", "User 3", "Cliente");
        
        assertEquals("Admin", u1.getRol());
        assertEquals("Vendedor", u2.getRol());
        assertEquals("Cliente", u3.getRol());
    }
    
    @Test
    public void testEstadoInicial() {
        Usuario usuario = new Usuario("new", "pass", "New User", "User");
        assertEquals("Activo", usuario.getEstado());
        assertFalse(usuario.isBloqueado());
        assertNull(usuario.getFechaBloqueo());
        assertNotNull(usuario.getFechaCreacion());
    }
}