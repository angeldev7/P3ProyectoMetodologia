package model;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import DAO.ProductoDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class InventarioDAOMockTest {

    @Mock
    private ProductoDAO mockProductoDAO;

    private InventarioDAO dao;

    @BeforeEach
    public void setUp() {
        // Mock obtenerTodosProductos to return empty → triggers inicializarDatosEjemplo
        when(mockProductoDAO.obtenerTodosProductos()).thenReturn(new ArrayList<>());
        dao = new InventarioDAO(mockProductoDAO);
    }

    @Test
    public void testConstructorInicializaDatosEjemplo() {
        // After constructor, productoDAO should save 4 example products
        verify(mockProductoDAO, times(4)).guardarProducto(any(Producto.class));
    }

    @Test
    public void testAgregarProducto() {
        reset(mockProductoDAO);
        Producto p = new Producto("P010", "Nuevo", "Desc", 5, 10.0, 2);
        dao.agregarProducto(p);
        verify(mockProductoDAO).guardarProducto(p);
    }

    @Test
    public void testEliminarProductoExistente() {
        // The local list has P001-P004 from inicializarDatosEjemplo
        boolean result = dao.eliminarProducto("P001");
        assertTrue(result);
        verify(mockProductoDAO).eliminarProducto("P001");
    }

    @Test
    public void testEliminarProductoInexistente() {
        boolean result = dao.eliminarProducto("NOEXISTE");
        assertFalse(result);
    }

    @Test
    public void testBuscarProductoPorCodigoEnMock() {
        // Mock returns a product
        Producto p = new Producto("P099", "MockProd", "Desc", 1, 1.0, 1);
        when(mockProductoDAO.buscarProductoPorCodigo("P099")).thenReturn(p);
        Producto found = dao.buscarProductoPorCodigo("P099");
        assertNotNull(found);
        assertEquals("MockProd", found.getNombre());
    }

    @Test
    public void testBuscarProductoPorCodigoEnListaLocal() {
        // Mock returns null → falls back to local list
        when(mockProductoDAO.buscarProductoPorCodigo("P001")).thenReturn(null);
        Producto found = dao.buscarProductoPorCodigo("P001");
        assertNotNull(found);
        assertEquals("Martillo", found.getNombre());
    }

    @Test
    public void testBuscarProductoInexistente() {
        when(mockProductoDAO.buscarProductoPorCodigo("NOEXISTE")).thenReturn(null);
        Producto found = dao.buscarProductoPorCodigo("NOEXISTE");
        assertNull(found);
    }

    @Test
    public void testObtenerTodosProductos() {
        List<Producto> mockList = new ArrayList<>();
        mockList.add(new Producto("M1", "Mock1", "D", 1, 1.0, 1));
        when(mockProductoDAO.obtenerTodosProductos()).thenReturn(mockList);
        Producto[] products = dao.obtenerTodosProductos();
        assertEquals(1, products.length);
        assertEquals("Mock1", products[0].getNombre());
    }

    @Test
    public void testAgregarVenta() {
        Venta v = new Venta("P001", "Martillo", 2, 12.50, 25.0);
        dao.agregarVenta(v);
        assertEquals(1, dao.obtenerCantidadVentas());
    }

    @Test
    public void testObtenerTodasVentas() {
        dao.agregarVenta(new Venta("P001", "Martillo", 1, 12.50, 12.50));
        dao.agregarVenta(new Venta("P002", "Clavos", 2, 5.0, 10.0));
        Venta[] ventas = dao.obtenerTodasVentas();
        assertEquals(2, ventas.length);
    }

    @Test
    public void testActualizarStock() {
        when(mockProductoDAO.buscarProductoPorCodigo("P001")).thenReturn(null);
        Producto p = dao.buscarProductoPorCodigo("P001"); // uses local
        assertNotNull(p);
        int oldStock = p.getStock();
        dao.actualizarStock("P001", 99);
        assertEquals(99, p.getStock());
        verify(mockProductoDAO).actualizarStock("P001", 99);
    }

    @Test
    public void testActualizarStockProductoInexistente() {
        when(mockProductoDAO.buscarProductoPorCodigo("NOEXISTE")).thenReturn(null);
        dao.actualizarStock("NOEXISTE", 99);
        verify(mockProductoDAO, never()).actualizarStock(eq("NOEXISTE"), anyInt());
    }

    @Test
    public void testIncrementarStock() {
        when(mockProductoDAO.buscarProductoPorCodigo("P001")).thenReturn(null);
        Producto p = dao.buscarProductoPorCodigo("P001");
        int original = p.getStock();
        dao.incrementarStock("P001", 10);
        assertEquals(original + 10, p.getStock());
        verify(mockProductoDAO).actualizarStock("P001", original + 10);
    }

    @Test
    public void testIncrementarStockInexistente() {
        when(mockProductoDAO.buscarProductoPorCodigo("NOEXISTE")).thenReturn(null);
        dao.incrementarStock("NOEXISTE", 10);
        verify(mockProductoDAO, never()).actualizarStock(eq("NOEXISTE"), anyInt());
    }

    @Test
    public void testDecrementarStock() {
        when(mockProductoDAO.buscarProductoPorCodigo("P001")).thenReturn(null);
        Producto p = dao.buscarProductoPorCodigo("P001");
        int original = p.getStock();
        dao.decrementarStock("P001", 5);
        assertEquals(original - 5, p.getStock());
    }

    @Test
    public void testDecrementarStockInsuficiente() {
        when(mockProductoDAO.buscarProductoPorCodigo("P001")).thenReturn(null);
        Producto p = dao.buscarProductoPorCodigo("P001");
        int original = p.getStock();
        dao.decrementarStock("P001", 9999);
        assertEquals(original, p.getStock()); // no change
    }

    @Test
    public void testDecrementarStockInexistente() {
        when(mockProductoDAO.buscarProductoPorCodigo("NOEXISTE")).thenReturn(null);
        dao.decrementarStock("NOEXISTE", 5);
    }

    @Test
    public void testExisteProductoTrue() {
        when(mockProductoDAO.buscarProductoPorCodigo("P001")).thenReturn(null);
        // Falls back to local → existe in local
        assertTrue(dao.existeProducto("P001"));
    }

    @Test
    public void testExisteProductoFalse() {
        when(mockProductoDAO.buscarProductoPorCodigo("NOEXISTE")).thenReturn(null);
        assertFalse(dao.existeProducto("NOEXISTE"));
    }

    @Test
    public void testObtenerCantidadProductos() {
        // Initially 4 from inicializarDatosEjemplo
        assertTrue(dao.obtenerCantidadProductos() >= 4);
    }

    @Test
    public void testObtenerCantidadVentas() {
        assertEquals(0, dao.obtenerCantidadVentas());
        dao.agregarVenta(new Venta("P001", "M", 1, 1.0, 1.0));
        assertEquals(1, dao.obtenerCantidadVentas());
    }

    @Test
    public void testObtenerProductosBajoStockMinimo() {
        // P003 has stock=20, stockMinimo=10 → NOT bajo
        // Need to set one low
        when(mockProductoDAO.buscarProductoPorCodigo("P001")).thenReturn(null);
        Producto p = dao.buscarProductoPorCodigo("P001");
        p.setStock(p.getStockMinimo()); // equal → should be included (<=)
        List<Producto> bajoStock = dao.obtenerProductosBajoStockMinimo();
        assertTrue(bajoStock.stream().anyMatch(pr -> pr.getCodigo().equals("P001")));
    }

    @Test
    public void testObtenerVentasPorProducto() {
        dao.agregarVenta(new Venta("P001", "M", 1, 1.0, 1.0));
        dao.agregarVenta(new Venta("P002", "C", 1, 1.0, 1.0));
        dao.agregarVenta(new Venta("P001", "M", 2, 1.0, 2.0));
        List<Venta> ventasP001 = dao.obtenerVentasPorProducto("P001");
        assertEquals(2, ventasP001.size());
    }

    @Test
    public void testCalcularValorTotalInventario() {
        double valor = dao.calcularValorTotalInventario();
        assertTrue(valor > 0);
    }

    @Test
    public void testObtenerTotalVentas() {
        assertEquals(0.0, dao.obtenerTotalVentas(), 0.001);
        dao.agregarVenta(new Venta("P001", "M", 2, 10.0, 20.0));
        dao.agregarVenta(new Venta("P002", "C", 1, 5.0, 5.0));
        assertEquals(25.0, dao.obtenerTotalVentas(), 0.001);
    }

    @Test
    public void testObtenerProductosConStockParaVenta() {
        List<Producto> mockList = new ArrayList<>();
        mockList.add(new Producto("S1", "Stock1", "D", 5, 1.0, 1));
        when(mockProductoDAO.obtenerProductosConStock()).thenReturn(mockList);
        List<Producto> result = dao.obtenerProductosConStockParaVenta();
        assertEquals(1, result.size());
    }

    @Test
    public void testBuscarProductosPorUbicacionTodos() {
        List<Producto> results = dao.buscarProductosPorUbicacion(null, null, null);
        assertTrue(results.size() >= 4);
    }

    @Test
    public void testBuscarProductosPorUbicacionPasillo() {
        List<Producto> results = dao.buscarProductosPorUbicacion("A", null, null);
        assertTrue(results.size() >= 2); // P001 and P002 are in pasillo A
    }

    @Test
    public void testBuscarProductosPorUbicacionSinResultado() {
        List<Producto> results = dao.buscarProductosPorUbicacion("Z", "Z", "Z");
        assertEquals(0, results.size());
    }

    @Test
    public void testObtenerProductosSinUbicacion() {
        dao.agregarProducto(new Producto("PNOUB", "SinUbicacion", "D", 1, 1.0, 1));
        List<Producto> sinUb = dao.obtenerProductosSinUbicacion();
        assertTrue(sinUb.stream().anyMatch(p -> p.getCodigo().equals("PNOUB")));
    }

    @Test
    public void testObtenerUbicacionesUnicas() {
        List<String> ubicaciones = dao.obtenerUbicacionesUnicas();
        assertFalse(ubicaciones.isEmpty());
    }

    @Test
    public void testObtenerUbicacionesUnicasNoDuplica() {
        List<String> ubicaciones = dao.obtenerUbicacionesUnicas();
        long uniqueCount = ubicaciones.stream().distinct().count();
        assertEquals(ubicaciones.size(), uniqueCount);
    }
}
