package model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class ProductoFullTest {

    @Test
    public void testConstructorBasico() {
        Producto p = new Producto("C1", "Nombre", "Desc", 10, 5.0, 2);
        assertEquals("C1", p.getCodigo());
        assertEquals("Nombre", p.getNombre());
        assertEquals("Desc", p.getDescripcion());
        assertEquals(10, p.getStock());
        assertEquals(5.0, p.getPrecio());
        assertEquals(2, p.getStockMinimo());
        assertEquals("", p.getPasillo());
        assertEquals("", p.getEstante());
        assertEquals("", p.getPosicion());
    }

    @Test
    public void testConstructorConUbicacion() {
        Producto p = new Producto("C2", "N", "D", 5, 10.0, 1, "A", "1", "3");
        assertEquals("A", p.getPasillo());
        assertEquals("1", p.getEstante());
        assertEquals("3", p.getPosicion());
    }

    @Test
    public void testConstructorConUbicacionNulos() {
        Producto p = new Producto("C3", "N", "D", 5, 10.0, 1, null, null, null);
        assertEquals("", p.getPasillo());
        assertEquals("", p.getEstante());
        assertEquals("", p.getPosicion());
    }

    @Test
    public void testSetCodigo() {
        Producto p = new Producto("C1", "N", "D", 1, 1.0, 1);
        p.setCodigo("C2");
        assertEquals("C2", p.getCodigo());
    }

    @Test
    public void testSetNombre() {
        Producto p = new Producto("C1", "N", "D", 1, 1.0, 1);
        p.setNombre("Nuevo");
        assertEquals("Nuevo", p.getNombre());
    }

    @Test
    public void testSetDescripcion() {
        Producto p = new Producto("C1", "N", "D", 1, 1.0, 1);
        p.setDescripcion("Nueva desc");
        assertEquals("Nueva desc", p.getDescripcion());
    }

    @Test
    public void testSetStock() {
        Producto p = new Producto("C1", "N", "D", 1, 1.0, 1);
        p.setStock(99);
        assertEquals(99, p.getStock());
    }

    @Test
    public void testSetPrecio() {
        Producto p = new Producto("C1", "N", "D", 1, 1.0, 1);
        p.setPrecio(99.99);
        assertEquals(99.99, p.getPrecio());
    }

    @Test
    public void testSetStockMinimo() {
        Producto p = new Producto("C1", "N", "D", 1, 1.0, 1);
        p.setStockMinimo(5);
        assertEquals(5, p.getStockMinimo());
    }

    @Test
    public void testSetPasillo() {
        Producto p = new Producto("C1", "N", "D", 1, 1.0, 1);
        p.setPasillo("B");
        assertEquals("B", p.getPasillo());
    }

    @Test
    public void testSetPasilloNull() {
        Producto p = new Producto("C1", "N", "D", 1, 1.0, 1);
        p.setPasillo(null);
        assertEquals("", p.getPasillo());
    }

    @Test
    public void testSetEstante() {
        Producto p = new Producto("C1", "N", "D", 1, 1.0, 1);
        p.setEstante("3");
        assertEquals("3", p.getEstante());
    }

    @Test
    public void testSetEstanteNull() {
        Producto p = new Producto("C1", "N", "D", 1, 1.0, 1);
        p.setEstante(null);
        assertEquals("", p.getEstante());
    }

    @Test
    public void testSetPosicion() {
        Producto p = new Producto("C1", "N", "D", 1, 1.0, 1);
        p.setPosicion("5");
        assertEquals("5", p.getPosicion());
    }

    @Test
    public void testSetPosicionNull() {
        Producto p = new Producto("C1", "N", "D", 1, 1.0, 1);
        p.setPosicion(null);
        assertEquals("", p.getPosicion());
    }

    @Test
    public void testGetUbicacionCompletaConUbicacion() {
        Producto p = new Producto("C1", "N", "D", 1, 1.0, 1, "A", "2", "3");
        assertEquals("A-2-3", p.getUbicacionCompleta());
    }

    @Test
    public void testGetUbicacionCompletaSinUbicacion() {
        Producto p = new Producto("C1", "N", "D", 1, 1.0, 1);
        assertEquals("Sin ubicación", p.getUbicacionCompleta());
    }

    @Test
    public void testGetUbicacionCompletaParcial() {
        Producto p = new Producto("C1", "N", "D", 1, 1.0, 1, "A", "", "");
        assertEquals("A-?-?", p.getUbicacionCompleta());
    }

    @Test
    public void testGetUbicacionCompletaParcialEstante() {
        Producto p = new Producto("C1", "N", "D", 1, 1.0, 1, "", "2", "");
        assertEquals("?-2-?", p.getUbicacionCompleta());
    }

    @Test
    public void testGetUbicacionCompletaParcialPosicion() {
        Producto p = new Producto("C1", "N", "D", 1, 1.0, 1, "", "", "5");
        assertEquals("?-?-5", p.getUbicacionCompleta());
    }

    @Test
    public void testTieneUbicacionTrue() {
        Producto p = new Producto("C1", "N", "D", 1, 1.0, 1, "A", "", "");
        assertTrue(p.tieneUbicacion());
    }

    @Test
    public void testTieneUbicacionFalse() {
        Producto p = new Producto("C1", "N", "D", 1, 1.0, 1);
        assertFalse(p.tieneUbicacion());
    }

    @Test
    public void testToStringConUbicacion() {
        Producto p = new Producto("C1", "Laptop", "D", 1, 1.0, 1, "A", "1", "2");
        String str = p.toString();
        assertTrue(str.contains("Laptop"));
        assertTrue(str.contains("C1"));
    }

    @Test
    public void testToStringSinUbicacion() {
        Producto p = new Producto("C1", "Laptop", "D", 1, 1.0, 1);
        String str = p.toString();
        assertTrue(str.contains("Sin ubicación"));
    }
}
