package model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;

public class RolFullTest {

    @Test
    public void testConstructor() {
        Rol r = new Rol("Admin");
        assertEquals("Admin", r.getNombre());
        assertNotNull(r.getPermisos());
        assertEquals(0, r.getContadorUsuarios());
        assertNotNull(r.getFechaCreacion());
    }

    @Test
    public void testSetNombre() {
        Rol r = new Rol("A");
        r.setNombre("B");
        assertEquals("B", r.getNombre());
    }

    @Test
    public void testSetPermisos() {
        Rol r = new Rol("Test");
        List<String> permisos = new ArrayList<>();
        permisos.add("LEER");
        permisos.add("ESCRIBIR");
        r.setPermisos(permisos);
        assertEquals(2, r.getPermisos().size());
    }

    @Test
    public void testAgregarPermiso() {
        Rol r = new Rol("Test");
        r.agregarPermiso("LEER");
        assertTrue(r.getPermisos().contains("LEER"));
        assertEquals(1, r.getPermisos().size());
    }

    @Test
    public void testAgregarPermisoDuplicadoNoAgrega() {
        Rol r = new Rol("Test");
        r.agregarPermiso("LEER");
        r.agregarPermiso("LEER");
        assertEquals(1, r.getPermisos().size());
    }

    @Test
    public void testEliminarPermiso() {
        Rol r = new Rol("Test");
        r.agregarPermiso("LEER");
        r.agregarPermiso("ESCRIBIR");
        r.eliminarPermiso("LEER");
        assertEquals(1, r.getPermisos().size());
        assertFalse(r.getPermisos().contains("LEER"));
    }

    @Test
    public void testEliminarPermisoInexistente() {
        Rol r = new Rol("Test");
        r.agregarPermiso("LEER");
        r.eliminarPermiso("NOEXISTE");
        assertEquals(1, r.getPermisos().size());
    }

    @Test
    public void testSetContadorUsuarios() {
        Rol r = new Rol("Test");
        r.setContadorUsuarios(5);
        assertEquals(5, r.getContadorUsuarios());
    }

    @Test
    public void testIncrementarContadorUsuarios() {
        Rol r = new Rol("Test");
        r.incrementarContadorUsuarios();
        assertEquals(1, r.getContadorUsuarios());
        r.incrementarContadorUsuarios();
        assertEquals(2, r.getContadorUsuarios());
    }

    @Test
    public void testDecrementarContadorUsuarios() {
        Rol r = new Rol("Test");
        r.incrementarContadorUsuarios();
        r.incrementarContadorUsuarios();
        r.decrementarContadorUsuarios();
        assertEquals(1, r.getContadorUsuarios());
    }

    @Test
    public void testDecrementarContadorDesdeZero() {
        Rol r = new Rol("Test");
        r.decrementarContadorUsuarios();
        assertEquals(-1, r.getContadorUsuarios());
    }

    @Test
    public void testSetFechaCreacion() {
        Rol r = new Rol("Test");
        r.setFechaCreacion("2025-01-01");
        assertEquals("2025-01-01", r.getFechaCreacion());
    }

    @Test
    public void testTienePermisoTrue() {
        Rol r = new Rol("Test");
        r.agregarPermiso("ADMIN");
        assertTrue(r.tienePermiso("ADMIN"));
    }

    @Test
    public void testTienePermisoFalse() {
        Rol r = new Rol("Test");
        assertFalse(r.tienePermiso("ADMIN"));
    }

    @Test
    public void testGetPermisosComoString() {
        Rol r = new Rol("Test");
        r.agregarPermiso("LEER");
        r.agregarPermiso("ESCRIBIR");
        String s = r.getPermisosComoString();
        assertTrue(s.contains("LEER"));
        assertTrue(s.contains("ESCRIBIR"));
        assertTrue(s.contains(","));
    }

    @Test
    public void testGetPermisosComoStringVacio() {
        Rol r = new Rol("Test");
        assertEquals("", r.getPermisosComoString());
    }

    @Test
    public void testMultiplesPermisos() {
        Rol r = new Rol("SuperAdmin");
        r.agregarPermiso("puedeGestionarProductos");
        r.agregarPermiso("puedeVender");
        r.agregarPermiso("puedeVerReportes");
        r.agregarPermiso("puedeExportar");
        r.agregarPermiso("puedeGestionarUsuarios");
        assertEquals(5, r.getPermisos().size());
        assertTrue(r.tienePermiso("puedeGestionarProductos"));
        assertTrue(r.tienePermiso("puedeVender"));
        assertTrue(r.tienePermiso("puedeVerReportes"));
        assertTrue(r.tienePermiso("puedeExportar"));
        assertTrue(r.tienePermiso("puedeGestionarUsuarios"));
    }
}
