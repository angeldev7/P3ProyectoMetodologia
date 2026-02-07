package model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CarritoCompraFullTest {

    private CarritoCompra carrito;
    private Producto p1, p2, p3;

    @BeforeEach
    public void setUp() {
        carrito = new CarritoCompra();
        p1 = new Producto("P001", "Martillo", "Desc", 10, 12.50, 5);
        p2 = new Producto("P002", "Clavos", "Desc", 20, 5.00, 10);
        p3 = new Producto("P003", "Brocha", "Desc", 15, 8.75, 4);
    }

    @Test
    public void testConstructor() {
        assertTrue(carrito.estaVacio());
        assertEquals(0, carrito.getCantidadItems());
        assertEquals(0.0, carrito.getTotal(), 0.001);
    }

    @Test
    public void testAgregarProductoNuevo() {
        carrito.agregarProducto(p1, 2);
        assertEquals(1, carrito.getCantidadItems());
        assertFalse(carrito.estaVacio());
        assertEquals(25.0, carrito.getTotal(), 0.001);
    }

    @Test
    public void testAgregarProductoExistenteSumaCantidad() {
        carrito.agregarProducto(p1, 2);
        carrito.agregarProducto(p1, 3);
        assertEquals(1, carrito.getCantidadItems());
        // 5 * 12.50 = 62.50
        assertEquals(62.50, carrito.getTotal(), 0.001);
        assertEquals(5, carrito.getItems().get(0).getCantidad());
    }

    @Test
    public void testAgregarMultiplesProductos() {
        carrito.agregarProducto(p1, 2);
        carrito.agregarProducto(p2, 3);
        carrito.agregarProducto(p3, 1);
        assertEquals(3, carrito.getCantidadItems());
        // 2*12.50 + 3*5.00 + 1*8.75 = 25 + 15 + 8.75 = 48.75
        assertEquals(48.75, carrito.getTotal(), 0.001);
    }

    @Test
    public void testEliminarProducto() {
        carrito.agregarProducto(p1, 2);
        carrito.agregarProducto(p2, 3);
        carrito.eliminarProducto("P001");
        assertEquals(1, carrito.getCantidadItems());
        assertEquals(15.0, carrito.getTotal(), 0.001);
    }

    @Test
    public void testEliminarProductoInexistente() {
        carrito.agregarProducto(p1, 2);
        carrito.eliminarProducto("INEXISTENTE");
        assertEquals(1, carrito.getCantidadItems());
        assertEquals(25.0, carrito.getTotal(), 0.001);
    }

    @Test
    public void testEliminarUltimoProducto() {
        carrito.agregarProducto(p1, 1);
        carrito.eliminarProducto("P001");
        assertTrue(carrito.estaVacio());
        assertEquals(0.0, carrito.getTotal(), 0.001);
    }

    @Test
    public void testActualizarCantidadValida() {
        carrito.agregarProducto(p1, 2);
        carrito.actualizarCantidad("P001", 5);
        assertEquals(1, carrito.getCantidadItems());
        assertEquals(62.50, carrito.getTotal(), 0.001);
    }

    @Test
    public void testActualizarCantidadCeroEliminaProducto() {
        carrito.agregarProducto(p1, 2);
        carrito.agregarProducto(p2, 1);
        carrito.actualizarCantidad("P001", 0);
        assertEquals(1, carrito.getCantidadItems());
        assertEquals(5.0, carrito.getTotal(), 0.001);
    }

    @Test
    public void testActualizarCantidadNegativaEliminaProducto() {
        carrito.agregarProducto(p1, 2);
        carrito.actualizarCantidad("P001", -1);
        assertTrue(carrito.estaVacio());
    }

    @Test
    public void testActualizarCantidadProductoInexistente() {
        carrito.agregarProducto(p1, 2);
        carrito.actualizarCantidad("NOEXISTE", 5);
        assertEquals(1, carrito.getCantidadItems());
        assertEquals(25.0, carrito.getTotal(), 0.001);
    }

    @Test
    public void testLimpiarCarrito() {
        carrito.agregarProducto(p1, 2);
        carrito.agregarProducto(p2, 3);
        carrito.limpiarCarrito();
        assertTrue(carrito.estaVacio());
        assertEquals(0, carrito.getCantidadItems());
        assertEquals(0.0, carrito.getTotal(), 0.001);
    }

    @Test
    public void testLimpiarCarritoVacio() {
        carrito.limpiarCarrito();
        assertTrue(carrito.estaVacio());
    }

    @Test
    public void testGetItems() {
        carrito.agregarProducto(p1, 2);
        carrito.agregarProducto(p2, 1);
        assertEquals(2, carrito.getItems().size());
    }

    @Test
    public void testTotalConProductosCeros() {
        Producto pGratis = new Producto("PG", "Gratis", "Desc", 10, 0.0, 1);
        carrito.agregarProducto(pGratis, 5);
        assertEquals(0.0, carrito.getTotal(), 0.001);
    }

    @Test
    public void testTotalConPreciosDecimales() {
        Producto p = new Producto("PD", "Decimal", "Desc", 10, 9.99, 1);
        carrito.agregarProducto(p, 3);
        assertEquals(29.97, carrito.getTotal(), 0.001);
    }

    @Test
    public void testAgregarMismoProductoMultiplesVeces() {
        carrito.agregarProducto(p1, 1);
        carrito.agregarProducto(p1, 1);
        carrito.agregarProducto(p1, 1);
        assertEquals(1, carrito.getCantidadItems());
        assertEquals(3, carrito.getItems().get(0).getCantidad());
        assertEquals(37.50, carrito.getTotal(), 0.001);
    }

    @Test
    public void testEstaVacioInicialmente() {
        assertTrue(carrito.estaVacio());
    }

    @Test
    public void testNoEstaVacioConProductos() {
        carrito.agregarProducto(p1, 1);
        assertFalse(carrito.estaVacio());
    }

    @Test
    public void testGetCantidadItems() {
        assertEquals(0, carrito.getCantidadItems());
        carrito.agregarProducto(p1, 1);
        assertEquals(1, carrito.getCantidadItems());
        carrito.agregarProducto(p2, 1);
        assertEquals(2, carrito.getCantidadItems());
        carrito.agregarProducto(p1, 1); // existente, no incrementa count
        assertEquals(2, carrito.getCantidadItems());
    }
}
