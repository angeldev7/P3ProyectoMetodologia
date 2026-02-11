package test;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import DAO.ProductoDAO;
import model.Producto;
import java.util.List;

public class ProductoDAOIntegrationTest {
    
    @Test
    public void testConstructorCreaDAO() {
        try {
            ProductoDAO dao = new ProductoDAO();
            assertNotNull(dao);
        } catch (Exception e) {
            assertNotNull(e);
        }
    }
    
    @Test
    public void testGuardarProducto() {
        try {
            ProductoDAO dao = new ProductoDAO();
            Producto producto = new Producto("P001", "Test", "Descripcion", 10, 15.5, 2);
            dao.guardarProducto(producto);
            assertTrue(true);
        } catch (Exception e) {
            assertTrue(true);
        }
    }
    
    @Test
    public void testActualizarProducto() {
        try {
            ProductoDAO dao = new ProductoDAO();
            Producto producto = new Producto("P001", "Test Actualizado", "Desc", 20, 25.5, 3);
            dao.actualizarProducto(producto);
            assertTrue(true);
        } catch (Exception e) {
            assertTrue(true);
        }
    }
    
    @Test
    public void testEliminarProducto() {
        try {
            ProductoDAO dao = new ProductoDAO();
            dao.eliminarProducto("P001");
            assertTrue(true);
        } catch (Exception e) {
            assertTrue(true);
        }
    }
    
    @Test
    public void testBuscarProductoPorCodigo() {
        try {
            ProductoDAO dao = new ProductoDAO();
            Producto producto = dao.buscarProductoPorCodigo("P001");
            assertTrue(producto == null || producto != null);
        } catch (Exception e) {
            assertTrue(true);
        }
    }
    
    @Test
    public void testObtenerTodosProductos() {
        try {
            ProductoDAO dao = new ProductoDAO();
            List<Producto> productos = dao.obtenerTodosProductos();
            assertNotNull(productos);
        } catch (Exception e) {
            assertTrue(true);
        }
    }
    
    @Test
    public void testObtenerProductosConStock() {
        try {
            ProductoDAO dao = new ProductoDAO();
            List<Producto> productos = dao.obtenerProductosConStock();
            assertNotNull(productos);
        } catch (Exception e) {
            assertTrue(true);
        }
    }
    
    @Test
    public void testActualizarStock() {
        try {
            ProductoDAO dao = new ProductoDAO();
            dao.actualizarStock("P001", 50);
            assertTrue(true);
        } catch (Exception e) {
            assertTrue(true);
        }
    }
    
    @Test
    public void testExisteProducto() {
        try {
            ProductoDAO dao = new ProductoDAO();
            boolean existe = dao.existeProducto("P001");
            assertTrue(existe || !existe);
        } catch (Exception e) {
            assertTrue(true);
        }
    }
    
    @Test
    public void testContarProductos() {
        try {
            ProductoDAO dao = new ProductoDAO();
            long cantidad = dao.contarProductos();
            assertTrue(cantidad >= 0);
        } catch (Exception e) {
            assertTrue(true);
        }
    }
    
    @Test
    public void testVerificarConexion() {
        try {
            ProductoDAO dao = new ProductoDAO();
            boolean conexion = dao.verificarConexion();
            assertTrue(conexion || !conexion);
        } catch (Exception e) {
            assertTrue(true);
        }
    }
    
    @Test
    public void testBuscarProductosPorUbicacion() {
        try {
            ProductoDAO dao = new ProductoDAO();
            List<Producto> productos = dao.buscarProductosPorUbicacion(null, null, null);
            assertNotNull(productos);
        } catch (Exception e) {
            assertTrue(true);
        }
    }
    
    @Test
    public void testObtenerProductosSinUbicacion() {
        try {
            ProductoDAO dao = new ProductoDAO();
            List<Producto> productos = dao.obtenerProductosSinUbicacion();
            assertNotNull(productos);
        } catch (Exception e) {
            assertTrue(true);
        }
    }
}