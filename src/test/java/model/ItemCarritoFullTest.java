package model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class ItemCarritoFullTest {

    private Producto crearProducto() {
        return new Producto("P001", "Martillo", "Desc", 10, 12.50, 5);
    }

    @Test
    public void testConstructorValido() {
        Producto p = crearProducto();
        ItemCarrito item = new ItemCarrito(p, 3);
        assertEquals(p, item.getProducto());
        assertEquals(3, item.getCantidad());
    }

    @Test
    public void testConstructorProductoNull() {
        assertThrows(IllegalArgumentException.class, () -> new ItemCarrito(null, 3));
    }

    @Test
    public void testConstructorCantidadCero() {
        assertThrows(IllegalArgumentException.class, () -> new ItemCarrito(crearProducto(), 0));
    }

    @Test
    public void testConstructorCantidadNegativa() {
        assertThrows(IllegalArgumentException.class, () -> new ItemCarrito(crearProducto(), -1));
    }

    @Test
    public void testSetProductoValido() {
        ItemCarrito item = new ItemCarrito(crearProducto(), 1);
        Producto nuevo = new Producto("P002", "Destornillador", "Desc", 5, 8.0, 2);
        item.setProducto(nuevo);
        assertEquals(nuevo, item.getProducto());
    }

    @Test
    public void testSetProductoNull() {
        ItemCarrito item = new ItemCarrito(crearProducto(), 1);
        assertThrows(IllegalArgumentException.class, () -> item.setProducto(null));
    }

    @Test
    public void testSetCantidadValida() {
        ItemCarrito item = new ItemCarrito(crearProducto(), 1);
        item.setCantidad(10);
        assertEquals(10, item.getCantidad());
    }

    @Test
    public void testSetCantidadCero() {
        ItemCarrito item = new ItemCarrito(crearProducto(), 1);
        assertThrows(IllegalArgumentException.class, () -> item.setCantidad(0));
    }

    @Test
    public void testSetCantidadNegativa() {
        ItemCarrito item = new ItemCarrito(crearProducto(), 1);
        assertThrows(IllegalArgumentException.class, () -> item.setCantidad(-5));
    }

    @Test
    public void testGetSubtotal() {
        Producto p = new Producto("P001", "Martillo", "Desc", 10, 12.50, 5);
        ItemCarrito item = new ItemCarrito(p, 3);
        assertEquals(37.50, item.getSubtotal(), 0.001);
    }

    @Test
    public void testGetSubtotalCantidadUno() {
        Producto p = new Producto("P001", "Martillo", "Desc", 10, 25.0, 5);
        ItemCarrito item = new ItemCarrito(p, 1);
        assertEquals(25.0, item.getSubtotal(), 0.001);
    }

    @Test
    public void testGetCodigoProducto() {
        Producto p = new Producto("ABC123", "Martillo", "Desc", 10, 12.50, 5);
        ItemCarrito item = new ItemCarrito(p, 1);
        assertEquals("ABC123", item.getCodigoProducto());
    }

    @Test
    public void testGetNombreProducto() {
        Producto p = new Producto("P001", "Destornillador", "Desc", 10, 12.50, 5);
        ItemCarrito item = new ItemCarrito(p, 1);
        assertEquals("Destornillador", item.getNombreProducto());
    }

    @Test
    public void testGetPrecioUnitario() {
        Producto p = new Producto("P001", "Martillo", "Desc", 10, 99.99, 5);
        ItemCarrito item = new ItemCarrito(p, 1);
        assertEquals(99.99, item.getPrecioUnitario(), 0.001);
    }

    @Test
    public void testToString() {
        Producto p = new Producto("P001", "Martillo", "Desc", 10, 12.50, 5);
        ItemCarrito item = new ItemCarrito(p, 3);
        String str = item.toString();
        assertTrue(str.contains("Martillo"));
        assertTrue(str.contains("3"));
    }

    @Test
    public void testSubtotalCambiaCantidad() {
        Producto p = new Producto("P001", "Martillo", "Desc", 10, 10.0, 5);
        ItemCarrito item = new ItemCarrito(p, 2);
        assertEquals(20.0, item.getSubtotal(), 0.001);
        item.setCantidad(5);
        assertEquals(50.0, item.getSubtotal(), 0.001);
    }

    @Test
    public void testSubtotalCambiaProducto() {
        Producto p1 = new Producto("P001", "Martillo", "Desc", 10, 10.0, 5);
        Producto p2 = new Producto("P002", "Clavos", "Desc", 20, 5.0, 10);
        ItemCarrito item = new ItemCarrito(p1, 3);
        assertEquals(30.0, item.getSubtotal(), 0.001);
        item.setProducto(p2);
        assertEquals(15.0, item.getSubtotal(), 0.001);
    }
}
