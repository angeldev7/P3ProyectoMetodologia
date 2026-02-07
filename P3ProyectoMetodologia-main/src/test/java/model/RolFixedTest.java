package model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RolFixedTest {

    private Rol rol;

    @BeforeEach
    void setUp() {
        rol = new Rol("VENDEDOR");
        rol.agregarPermiso("puedeVender");
        rol.agregarPermiso("puedeVerProductos");
    }

    @Test
    void testBasicProperties() {
        assertEquals("VENDEDOR", rol.getNombre());
        assertNotNull(rol);
    }

    @Test
    void testPermisos() {
        assertTrue(rol.tienePermiso("puedeVender"));
        assertFalse(rol.tienePermiso("permisoInexistente"));
    }

    @Test
    void testToString() {
        String str = rol.toString();
        assertNotNull(str);
        assertFalse(str.isEmpty());
    }
}