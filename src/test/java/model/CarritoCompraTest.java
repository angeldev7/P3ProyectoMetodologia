package model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CarritoCompraTest {

    private CarritoCompra carrito;
    private Producto producto1;
    private Producto producto2;

    @BeforeEach
    void setUp() {
        carrito = new CarritoCompra();
        producto1 = new Producto("P001", "Laptop", "Laptop gaming", 10, 1500.0, 5);
        producto2 = new Producto("P002", "Mouse", "Mouse óptico", 50, 25.0, 10);
    }

    @Test
    void testCarritoVacio() {
        assertTrue(carrito.estaVacio());
        assertEquals(0, carrito.getCantidadItems());
        assertEquals(0.0, carrito.getTotal());
    }

    @Test
    void testAgregarProducto() {
        carrito.agregarProducto(producto1, 2);
        
        assertFalse(carrito.estaVacio());
        assertEquals(1, carrito.getCantidadItems());
        assertEquals(3000.0, carrito.getTotal()); // 1500 * 2
    }

    @Test
    void testAgregarVariosProductos() {
        carrito.agregarProducto(producto1, 1);
        carrito.agregarProducto(producto2, 2);
        
        assertEquals(2, carrito.getCantidadItems());
        assertEquals(1550.0, carrito.getTotal()); // 1500 + (25 * 2)
    }

    @Test
    void testAgregarMismoProducto() {
        carrito.agregarProducto(producto1, 1);
        carrito.agregarProducto(producto1, 2); // Debe sumar cantidades
        
        assertEquals(1, carrito.getCantidadItems()); // Solo un item
        assertEquals(4500.0, carrito.getTotal()); // 1500 * 3
    }

    @Test
    void testEliminarProducto() {
        carrito.agregarProducto(producto1, 2);
        carrito.agregarProducto(producto2, 1);
        
        carrito.eliminarProducto("P001");
        
        assertEquals(1, carrito.getCantidadItems());
        assertEquals(25.0, carrito.getTotal());
    }

    @Test
    void testActualizarCantidad() {
        carrito.agregarProducto(producto1, 2);
        carrito.actualizarCantidad("P001", 5);
        
        assertEquals(7500.0, carrito.getTotal()); // 1500 * 5
    }

    @Test
    void testActualizarCantidadACero() {
        carrito.agregarProducto(producto1, 2);
        carrito.actualizarCantidad("P001", 0);
        
        assertTrue(carrito.estaVacio());
        assertEquals(0.0, carrito.getTotal());
    }

    @Test
    void testLimpiarCarrito() {
        carrito.agregarProducto(producto1, 2);
        carrito.agregarProducto(producto2, 1);
        
        carrito.limpiarCarrito();
        
        assertTrue(carrito.estaVacio());
        assertEquals(0, carrito.getCantidadItems());
        assertEquals(0.0, carrito.getTotal());
    }
}