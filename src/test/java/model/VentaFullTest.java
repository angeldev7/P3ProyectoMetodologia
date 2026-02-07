package model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class VentaFullTest {

    @Test
    public void testConstructor() {
        Venta v = new Venta("P001", "Martillo", 3, 12.50, 37.50);
        assertEquals("P001", v.getCodigoProducto());
        assertEquals("Martillo", v.getNombreProducto());
        assertEquals(3, v.getCantidad());
        assertEquals(12.50, v.getPrecioUnitario());
        assertEquals(37.50, v.getTotal());
        assertNotNull(v.getFecha());
    }

    @Test
    public void testFechaFormato() {
        Venta v = new Venta("P001", "Test", 1, 1.0, 1.0);
        String fecha = v.getFecha();
        // formato yyyy-MM-dd
        assertTrue(fecha.matches("\\d{4}-\\d{2}-\\d{2}"));
    }

    @Test
    public void testToString() {
        Venta v = new Venta("P001", "Martillo", 2, 12.50, 25.00);
        String str = v.toString();
        assertTrue(str.contains("P001"));
        assertTrue(str.contains("Martillo"));
        assertTrue(str.contains("2"));
    }

    @Test
    public void testVentaConValoresCero() {
        Venta v = new Venta("P000", "Gratis", 0, 0.0, 0.0);
        assertEquals(0, v.getCantidad());
        assertEquals(0.0, v.getTotal());
    }

    @Test
    public void testVentaConPrecioAlto() {
        Venta v = new Venta("P999", "Costoso", 100, 999.99, 99999.00);
        assertEquals(99999.00, v.getTotal());
    }
}
