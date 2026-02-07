package model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InventarioTest {

    private Inventario inventario;
    private Producto producto1;
    private Producto producto2;

    @BeforeEach
    void setUp() {
        inventario = new Inventario();
        producto1 = new Producto("P001", "Martillo", "Martillo de construcción", 10, 25.50, 5);
        producto2 = new Producto("P002", "Destornillador", "Destornillador Phillips", 15, 12.75, 3);
    }

    @Test
    void testConstructor() {
        assertNotNull(inventario);
        assertTrue(inventario.obtenerCantidadProductos() > 0); // Debe tener datos de ejemplo
        assertEquals(0, inventario.obtenerCantidadVentas());
    }

    @Test
    void testAgregarProducto() {
        int cantidadInicial = inventario.obtenerCantidadProductos();
        
        inventario.agregarProducto(producto1);
        
        assertEquals(cantidadInicial + 1, inventario.obtenerCantidadProductos());
        assertTrue(inventario.existeProducto("P001"));
    }

    @Test
    void testAgregarProductoDuplicado() {
        inventario.agregarProducto(producto1);
        int cantidadTrasUno = inventario.obtenerCantidadProductos();
        
        inventario.agregarProducto(producto1); // Producto duplicado
        
        assertEquals(cantidadTrasUno + 1, inventario.obtenerCantidadProductos()); // La lista permite duplicados
    }

    @Test
    void testEliminarProducto() {
        inventario.agregarProducto(producto1);
        assertTrue(inventario.existeProducto("P001"));
        
        boolean eliminado = inventario.eliminarProducto("P001");
        
        assertTrue(eliminado);
        assertFalse(inventario.existeProducto("P001"));
    }

    @Test
    void testEliminarProductoInexistente() {
        boolean eliminado = inventario.eliminarProducto("INEXISTENTE");
        
        assertFalse(eliminado);
    }

    @Test
    void testBuscarProductoPorCodigo() {
        inventario.agregarProducto(producto1);
        
        Producto encontrado = inventario.buscarProductoPorCodigo("P001");
        
        assertNotNull(encontrado);
        assertEquals("P001", encontrado.getCodigo());
        assertEquals("Martillo", encontrado.getNombre());
    }

    @Test
    void testBuscarProductoInexistente() {
        Producto encontrado = inventario.buscarProductoPorCodigo("INEXISTENTE");
        
        assertNull(encontrado);
    }

    @Test
    void testObtenerTodosProductos() {
        inventario.agregarProducto(producto1);
        inventario.agregarProducto(producto2);
        
        Producto[] productos = inventario.obtenerTodosProductos();
        
        assertNotNull(productos);
        assertTrue(productos.length >= 2); // Al menos los 2 que agregamos
    }

    @Test
    void testObtenerCantidadVentasInicial() {
        assertEquals(0, inventario.obtenerCantidadVentas());
    }

    @Test
    void testObtenerTodasVentasInicial() {
        Venta[] ventas = inventario.obtenerTodasVentas();
        
        assertNotNull(ventas);
        assertEquals(0, ventas.length);
    }

    @Test
    void testExisteProducto() {
        inventario.agregarProducto(producto1);
        
        assertTrue(inventario.existeProducto("P001"));
        assertFalse(inventario.existeProducto("INEXISTENTE"));
    }

    @Test
    void testObtenerCantidadProductos() {
        int cantidadInicial = inventario.obtenerCantidadProductos();
        
        inventario.agregarProducto(producto1);
        
        assertEquals(cantidadInicial + 1, inventario.obtenerCantidadProductos());
    }

    @Test
    void testObtenerCantidadVentas() {
        assertEquals(0, inventario.obtenerCantidadVentas());
    }

    @Test
    void testBuscarProductoBasico() {
        inventario.agregarProducto(producto1);
        inventario.agregarProducto(producto2);
        
        Producto encontrado = inventario.buscarProductoPorCodigo("P001");
        
        assertNotNull(encontrado);
        assertEquals("Martillo", encontrado.getNombre());
    }
}