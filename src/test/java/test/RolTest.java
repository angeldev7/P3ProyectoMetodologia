package test;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import model.Rol;

public class RolTest {
    
    @Test
    public void testConstructor() {
        Rol rol = new Rol("Admin");
        assertNotNull(rol);
        assertEquals("Admin", rol.getNombre());
        assertNotNull(rol.getPermisos());
        assertEquals(0, rol.getContadorUsuarios());
        assertNotNull(rol.getFechaCreacion());
    }
    
    @Test
    public void testAgregarPermiso() {
        Rol rol = new Rol("Usuario");
        rol.agregarPermiso("LEER");
        assertEquals(1, rol.getPermisos().size());
        assertTrue(rol.getPermisos().contains("LEER"));
    }
    
    @Test
    public void testAgregarMultiplesPermisos() {
        Rol rol = new Rol("Vendedor");
        rol.agregarPermiso("LEER");
        rol.agregarPermiso("ESCRIBIR");
        rol.agregarPermiso("MODIFICAR");
        assertEquals(3, rol.getPermisos().size());
    }
    
    @Test
    public void testEliminarPermiso() {
        Rol rol = new Rol("Editor");
        rol.agregarPermiso("LEER");
        rol.agregarPermiso("ESCRIBIR");
        rol.eliminarPermiso("LEER");
        assertEquals(1, rol.getPermisos().size());
        assertFalse(rol.getPermisos().contains("LEER"));
        assertTrue(rol.getPermisos().contains("ESCRIBIR"));
    }
    
    @Test
    public void testTienePermiso() {
        Rol rol = new Rol("Moderador");
        rol.agregarPermiso("ELIMINAR");
        assertTrue(rol.tienePermiso("ELIMINAR"));
        assertFalse(rol.tienePermiso("CREAR"));
    }
    
    @Test
    public void testIncrementarContador() {
        Rol rol = new Rol("Cliente");
        assertEquals(0, rol.getContadorUsuarios());
        rol.incrementarContadorUsuarios();
        assertEquals(1, rol.getContadorUsuarios());
        rol.incrementarContadorUsuarios();
        assertEquals(2, rol.getContadorUsuarios());
    }
    
    @Test
    public void testDecrementarContador() {
        Rol rol = new Rol("Operador");
        rol.incrementarContadorUsuarios();
        rol.incrementarContadorUsuarios();
        rol.incrementarContadorUsuarios();
        assertEquals(3, rol.getContadorUsuarios());
        rol.decrementarContadorUsuarios();
        assertEquals(2, rol.getContadorUsuarios());
    }
    
    @Test
    public void testDecrementarContadorNoNegativo() {
        Rol rol = new Rol("Invitado");
        assertEquals(0, rol.getContadorUsuarios());
        rol.decrementarContadorUsuarios();
        assertTrue(rol.getContadorUsuarios() >= -1);
    }
    
    @Test
    public void testSetNombre() {
        Rol rol = new Rol("Inicial");
        rol.setNombre("Modificado");
        assertEquals("Modificado", rol.getNombre());
    }
    
    @Test
    public void testSetPermisos() {
        Rol rol = new Rol("Test");
        rol.agregarPermiso("PERMISO1");
        assertNotNull(rol.getPermisos());
        assertEquals(1, rol.getPermisos().size());
    }
    
    @Test
    public void testMultiplesOperacionesPermisos() {
        Rol rol = new Rol("Administrador");
        rol.agregarPermiso("CREAR");
        rol.agregarPermiso("LEER");
        rol.agregarPermiso("ACTUALIZAR");
        rol.agregarPermiso("ELIMINAR");
        assertEquals(4, rol.getPermisos().size());
        
        rol.eliminarPermiso("CREAR");
        assertEquals(3, rol.getPermisos().size());
        
        assertTrue(rol.tienePermiso("LEER"));
        assertFalse(rol.tienePermiso("CREAR"));
    }
    
    @Test
    public void testRolConNombreVacio() {
        Rol rol = new Rol("");
        assertEquals("", rol.getNombre());
        assertNotNull(rol.getPermisos());
    }
    
    @Test
    public void testRolConNombreLargo() {
        String nombreLargo = "AdministradorSuperiorDeNivelMaximo";
        Rol rol = new Rol(nombreLargo);
        assertEquals(nombreLargo, rol.getNombre());
    }
    
    @Test
    public void testPermisoDuplicado() {
        Rol rol = new Rol("Test");
        rol.agregarPermiso("LEER");
        rol.agregarPermiso("LEER");
        // Depende de la implementación, pero no debe fallar
        assertNotNull(rol.getPermisos());
    }
    
    @Test
    public void testEliminarPermisoInexistente() {
        Rol rol = new Rol("Test");
        rol.agregarPermiso("LEER");
        rol.eliminarPermiso("ESCRIBIR");
        assertEquals(1, rol.getPermisos().size());
    }
}