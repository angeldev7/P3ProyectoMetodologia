package model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;

public class InventarioFullTest {
    private Inventario inventario;

    @BeforeEach
    void setUp() {
        inventario = new Inventario();
    }

    @Test
    public void testConstructorInicializaDatosEjemplo() {
        assertTrue(inventario.obtenerCantidadProductos() > 0);
    }

    @Test
    public void testAgregarProducto() {
        int antes = inventario.obtenerCantidadProductos();
        inventario.agregarProducto(new Producto("PX01", "Test", "Desc", 10, 5.0, 2));
        assertEquals(antes + 1, inventario.obtenerCantidadProductos());
    }

    @Test
    public void testEliminarProductoExistente() {
        assertTrue(inventario.eliminarProducto("P001"));
    }

    @Test
    public void testEliminarProductoInexistente() {
        assertFalse(inventario.eliminarProducto("NOEXISTE"));
    }

    @Test
    public void testBuscarProductoPorCodigoExistente() {
        Producto p = inventario.buscarProductoPorCodigo("P001");
        assertNotNull(p);
        assertEquals("P001", p.getCodigo());
    }

    @Test
    public void testBuscarProductoPorCodigoInexistente() {
        assertNull(inventario.buscarProductoPorCodigo("XXXX"));
    }

    @Test
    public void testObtenerTodosProductos() {
        Producto[] productos = inventario.obtenerTodosProductos();
        assertNotNull(productos);
        assertTrue(productos.length > 0);
    }

    @Test
    public void testAgregarVenta() {
        Venta venta = new Venta("P001", "Martillo", 2, 12.50, 25.00);
        inventario.agregarVenta(venta);
        assertEquals(1, inventario.obtenerCantidadVentas());
    }

    @Test
    public void testObtenerTodasVentas() {
        inventario.agregarVenta(new Venta("P001", "Martillo", 1, 12.50, 12.50));
        Venta[] ventas = inventario.obtenerTodasVentas();
        assertNotNull(ventas);
        assertEquals(1, ventas.length);
    }

    @Test
    public void testObtenerTotalVentas() {
        inventario.agregarVenta(new Venta("P001", "Martillo", 2, 12.50, 25.00));
        inventario.agregarVenta(new Venta("P002", "Destornilladores", 1, 25.00, 25.00));
        assertEquals(50.00, inventario.obtenerTotalVentas(), 0.01);
    }

    @Test
    public void testExisteProductoTrue() {
        assertTrue(inventario.existeProducto("P001"));
    }

    @Test
    public void testExisteProductoFalse() {
        assertFalse(inventario.existeProducto("ZZZZ"));
    }

    @Test
    public void testObtenerCantidadProductos() {
        assertTrue(inventario.obtenerCantidadProductos() >= 4);
    }

    @Test
    public void testObtenerCantidadVentasInicial() {
        assertEquals(0, inventario.obtenerCantidadVentas());
    }

    @Test
    public void testObtenerProductosBajoStockMinimo() {
        inventario.agregarProducto(new Producto("BAJO1", "Bajo Stock", "Desc", 1, 5.0, 10));
        List<Producto> bajoStock = inventario.obtenerProductosBajoStockMinimo();
        assertNotNull(bajoStock);
        assertTrue(bajoStock.size() > 0);
    }

    @Test
    public void testActualizarStock() {
        inventario.actualizarStock("P001", 100);
        Producto p = inventario.buscarProductoPorCodigo("P001");
        assertEquals(100, p.getStock());
    }

    @Test
    public void testActualizarStockProductoInexistente() {
        inventario.actualizarStock("NOEXISTE", 50);
        // No debería lanzar excepción
    }

    @Test
    public void testIncrementarStock() {
        Producto p = inventario.buscarProductoPorCodigo("P001");
        int stockAntes = p.getStock();
        inventario.incrementarStock("P001", 5);
        assertEquals(stockAntes + 5, p.getStock());
    }

    @Test
    public void testIncrementarStockProductoInexistente() {
        inventario.incrementarStock("NOEXISTE", 5);
    }

    @Test
    public void testDecrementarStock() {
        Producto p = inventario.buscarProductoPorCodigo("P001");
        int stockAntes = p.getStock();
        inventario.decrementarStock("P001", 3);
        assertEquals(stockAntes - 3, p.getStock());
    }

    @Test
    public void testDecrementarStockInsuficiente() {
        Producto p = inventario.buscarProductoPorCodigo("P001");
        int stockAntes = p.getStock();
        inventario.decrementarStock("P001", 9999);
        assertEquals(stockAntes, p.getStock());
    }

    @Test
    public void testDecrementarStockProductoInexistente() {
        inventario.decrementarStock("NOEXISTE", 1);
    }

    @Test
    public void testObtenerVentasPorProducto() {
        inventario.agregarVenta(new Venta("P001", "Martillo", 1, 12.50, 12.50));
        inventario.agregarVenta(new Venta("P002", "Destornilladores", 1, 25.00, 25.00));
        inventario.agregarVenta(new Venta("P001", "Martillo", 2, 12.50, 25.00));
        List<Venta> ventasP001 = inventario.obtenerVentasPorProducto("P001");
        assertEquals(2, ventasP001.size());
    }

    @Test
    public void testObtenerVentasPorProductoInexistente() {
        List<Venta> ventas = inventario.obtenerVentasPorProducto("NOEXISTE");
        assertTrue(ventas.isEmpty());
    }

    @Test
    public void testCalcularValorTotalInventario() {
        double valor = inventario.calcularValorTotalInventario();
        assertTrue(valor > 0);
    }
}
