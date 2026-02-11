package model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

class VentaTest {

    private Venta venta;

    @BeforeEach
    void setUp() {
        venta = new Venta("P001", "Laptop Gaming", 2, 1500.0, 3000.0);
    }

    @Test
    void testCrearVenta() {
        assertNotNull(venta);
        assertEquals("P001", venta.getCodigoProducto());
        assertEquals("Laptop Gaming", venta.getNombreProducto());
        assertEquals(2, venta.getCantidad());
        assertEquals(1500.0, venta.getPrecioUnitario());
        assertEquals(3000.0, venta.getTotal());
    }

    @Test
    void testFechaAutomatica() {
        String fechaEsperada = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        assertEquals(fechaEsperada, venta.getFecha());
    }

    @Test
    void testGetters() {
        assertEquals("P001", venta.getCodigoProducto());
        assertEquals("Laptop Gaming", venta.getNombreProducto());
        assertEquals(2, venta.getCantidad());
        assertEquals(1500.0, venta.getPrecioUnitario());
        assertEquals(3000.0, venta.getTotal());
    }

    @Test
    void testToString() {
        String resultado = venta.toString();
        assertTrue(resultado.contains("P001"));
        assertTrue(resultado.contains("Laptop Gaming"));
        assertTrue(resultado.contains("2"));
        assertTrue(resultado.contains("1500"));
        assertTrue(resultado.contains("3000"));
    }

    @Test
    void testVentaConDiferentesValores() {
        Venta otraVenta = new Venta("P002", "Mouse", 5, 25.0, 125.0);
        
        assertEquals("P002", otraVenta.getCodigoProducto());
        assertEquals("Mouse", otraVenta.getNombreProducto());
        assertEquals(5, otraVenta.getCantidad());
        assertEquals(25.0, otraVenta.getPrecioUnitario());
        assertEquals(125.0, otraVenta.getTotal());
    }
}