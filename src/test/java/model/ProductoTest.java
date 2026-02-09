package model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProductoTest {

    private Producto producto;

    @BeforeEach
    void setUp() {
        producto = new Producto("P001", "Laptop", "Laptop gaming", 10, 1500.0, 5);
    }

    @Test
    void testCrearProducto() {
        assertNotNull(producto);
        assertEquals("P001", producto.getCodigo());
        assertEquals("Laptop", producto.getNombre());
        assertEquals("Laptop gaming", producto.getDescripcion());
        assertEquals(10, producto.getStock());
        assertEquals(1500.0, producto.getPrecio());
        assertEquals(5, producto.getStockMinimo());
    }

    @Test
    void testGettersVarios() {
        assertEquals("P001", producto.getCodigo());
        assertEquals("Laptop", producto.getNombre());
        assertEquals(10, producto.getStock());
        assertEquals(1500.0, producto.getPrecio());
    }

    @Test
    void testSettersStock() {
        producto.setStock(15);
        assertEquals(15, producto.getStock());
    }

    @Test
    void testSettersPrecio() {
        producto.setPrecio(1200.0);
        assertEquals(1200.0, producto.getPrecio());
    }

    @Test
    void testUbicacionCompleta() {
        // Test con ubicación vacía
        String ubicacion = producto.getUbicacionCompleta();
        assertEquals("Sin ubicación", ubicacion);
        
        // Test con ubicación asignada
        producto.setPasillo("A");
        producto.setEstante("2");
        producto.setPosicion("3");
        
        assertEquals("A-2-3", producto.getUbicacionCompleta());
    }

    @Test
    void testTieneUbicacion() {
        // Sin ubicación
        assertFalse(producto.tieneUbicacion());
        
        // Con ubicación solo en pasillo
        producto.setPasillo("A");
        assertTrue(producto.tieneUbicacion());
    }

    @Test
    void testToString() {
        String resultado = producto.toString();
        assertTrue(resultado.contains("Laptop"));
        assertTrue(resultado.contains("P001"));
    }
}