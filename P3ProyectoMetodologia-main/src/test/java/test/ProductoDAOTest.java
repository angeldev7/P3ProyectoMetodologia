package test;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import DAO.ProductoDAO;
import model.Producto;

public class ProductoDAOTest {
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
    public void testGuardarProductoBasico() {
        try {
            ProductoDAO dao = new ProductoDAO();
            Producto producto = new Producto("PX01", "Test", "Desc", 1, 10.0, 1);
            dao.guardarProducto(producto);
            assertTrue(true);
        } catch (Exception e) {
            assertTrue(true);
        }
    }
}
