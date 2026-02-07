package DAO;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import model.Producto;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unchecked")
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ProductoDAOMockTest {

    @Mock
    private MongoCollection<Document> mockColeccion;

    @Mock
    private FindIterable<Document> mockFindIterable;

    @Mock
    private MongoCursor<Document> mockCursor;

    private ProductoDAO dao;

    @BeforeEach
    public void setUp() {
        dao = new ProductoDAO(mockColeccion);
    }

    private Document crearDocProducto(String codigo, String nombre, int stock, double precio, int stockMinimo) {
        return new Document("codigo", codigo)
            .append("nombre", nombre)
            .append("descripcion", "Desc " + nombre)
            .append("stock", stock)
            .append("precio", precio)
            .append("stockMinimo", stockMinimo)
            .append("pasillo", "A")
            .append("estante", "1")
            .append("posicion", "2");
    }

    @Test
    public void testGuardarProducto() {
        Producto p = new Producto("P001", "Martillo", "Desc", 10, 12.50, 5, "A", "1", "2");
        dao.guardarProducto(p);
        verify(mockColeccion).insertOne(any(Document.class));
    }

    @Test
    public void testGuardarProductoError() {
        doThrow(new RuntimeException("Error")).when(mockColeccion).insertOne(any(Document.class));
        Producto p = new Producto("P001", "Martillo", "Desc", 10, 12.50, 5);
        assertDoesNotThrow(() -> dao.guardarProducto(p));
    }

    @Test
    public void testActualizarProducto() {
        Producto p = new Producto("P001", "MartilloUpdated", "Desc", 20, 15.0, 5, "B", "2", "3");
        dao.actualizarProducto(p);
        verify(mockColeccion).updateOne(any(Bson.class), any(Document.class));
    }

    @Test
    public void testActualizarProductoError() {
        doThrow(new RuntimeException("Error")).when(mockColeccion).updateOne(any(Bson.class), any(Document.class));
        Producto p = new Producto("P001", "M", "D", 1, 1.0, 1);
        assertDoesNotThrow(() -> dao.actualizarProducto(p));
    }

    @Test
    public void testEliminarProducto() {
        dao.eliminarProducto("P001");
        verify(mockColeccion).deleteOne(any(Bson.class));
    }

    @Test
    public void testEliminarProductoError() {
        doThrow(new RuntimeException("Error")).when(mockColeccion).deleteOne(any(Bson.class));
        assertDoesNotThrow(() -> dao.eliminarProducto("P001"));
    }

    @Test
    public void testBuscarProductoPorCodigoEncontrado() {
        Document doc = crearDocProducto("P001", "Martillo", 10, 12.50, 5);
        when(mockColeccion.find(any(Bson.class))).thenReturn(mockFindIterable);
        when(mockFindIterable.first()).thenReturn(doc);

        Producto p = dao.buscarProductoPorCodigo("P001");
        assertNotNull(p);
        assertEquals("P001", p.getCodigo());
        assertEquals("Martillo", p.getNombre());
    }

    @Test
    public void testBuscarProductoPorCodigoNoEncontrado() {
        when(mockColeccion.find(any(Bson.class))).thenReturn(mockFindIterable);
        when(mockFindIterable.first()).thenReturn(null);

        Producto p = dao.buscarProductoPorCodigo("NOEXISTE");
        assertNull(p);
    }

    @Test
    public void testBuscarProductoPorCodigoError() {
        when(mockColeccion.find(any(Bson.class))).thenThrow(new RuntimeException("Error"));
        Producto p = dao.buscarProductoPorCodigo("P001");
        assertNull(p);
    }

    @Test
    public void testObtenerTodosProductos() {
        Document doc1 = crearDocProducto("P001", "Martillo", 10, 12.50, 5);
        Document doc2 = crearDocProducto("P002", "Clavos", 20, 5.0, 10);

        when(mockColeccion.find()).thenReturn(mockFindIterable);
        when(mockFindIterable.iterator()).thenReturn(mockCursor);
        when(mockCursor.hasNext()).thenReturn(true, true, false);
        when(mockCursor.next()).thenReturn(doc1, doc2);

        List<Producto> productos = dao.obtenerTodosProductos();
        assertEquals(2, productos.size());
    }

    @Test
    public void testObtenerTodosProductosError() {
        when(mockColeccion.find()).thenThrow(new RuntimeException("Error"));
        List<Producto> productos = dao.obtenerTodosProductos();
        assertTrue(productos.isEmpty());
    }

    @Test
    public void testObtenerProductosConStock() {
        Document doc = crearDocProducto("P001", "Martillo", 10, 12.50, 5);

        when(mockColeccion.find(any(Bson.class))).thenReturn(mockFindIterable);
        when(mockFindIterable.iterator()).thenReturn(mockCursor);
        when(mockCursor.hasNext()).thenReturn(true, false);
        when(mockCursor.next()).thenReturn(doc);

        List<Producto> productos = dao.obtenerProductosConStock();
        assertEquals(1, productos.size());
    }

    @Test
    public void testObtenerProductosConStockError() {
        when(mockColeccion.find(any(Bson.class))).thenThrow(new RuntimeException("Error"));
        List<Producto> productos = dao.obtenerProductosConStock();
        assertTrue(productos.isEmpty());
    }

    @Test
    public void testActualizarStock() {
        dao.actualizarStock("P001", 99);
        verify(mockColeccion).updateOne(any(Bson.class), any(Document.class));
    }

    @Test
    public void testActualizarStockError() {
        doThrow(new RuntimeException("Error")).when(mockColeccion).updateOne(any(Bson.class), any(Document.class));
        assertDoesNotThrow(() -> dao.actualizarStock("P001", 99));
    }

    @Test
    public void testExisteProductoTrue() {
        when(mockColeccion.countDocuments(any(Bson.class))).thenReturn(1L);
        assertTrue(dao.existeProducto("P001"));
    }

    @Test
    public void testExisteProductoFalse() {
        when(mockColeccion.countDocuments(any(Bson.class))).thenReturn(0L);
        assertFalse(dao.existeProducto("NOEXISTE"));
    }

    @Test
    public void testExisteProductoError() {
        when(mockColeccion.countDocuments(any(Bson.class))).thenThrow(new RuntimeException("Error"));
        assertFalse(dao.existeProducto("P001"));
    }

    @Test
    public void testContarProductos() {
        when(mockColeccion.countDocuments()).thenReturn(42L);
        assertEquals(42, dao.contarProductos());
    }

    @Test
    public void testContarProductosError() {
        when(mockColeccion.countDocuments()).thenThrow(new RuntimeException("Error"));
        assertEquals(0, dao.contarProductos());
    }

    @Test
    public void testVerificarConexionExitosa() {
        when(mockColeccion.countDocuments()).thenReturn(0L);
        assertTrue(dao.verificarConexion());
    }

    @Test
    public void testVerificarConexionFallida() {
        when(mockColeccion.countDocuments()).thenThrow(new RuntimeException("Error"));
        assertFalse(dao.verificarConexion());
    }

    @Test
    public void testBuscarProductosPorUbicacionTodos() {
        Document doc = crearDocProducto("P001", "Martillo", 10, 12.50, 5);

        when(mockColeccion.find(any(Document.class))).thenReturn(mockFindIterable);
        when(mockFindIterable.iterator()).thenReturn(mockCursor);
        when(mockCursor.hasNext()).thenReturn(true, false);
        when(mockCursor.next()).thenReturn(doc);

        List<Producto> result = dao.buscarProductosPorUbicacion(null, null, null);
        assertEquals(1, result.size());
    }

    @Test
    public void testBuscarProductosPorUbicacionConPasillo() {
        Document doc = crearDocProducto("P001", "Martillo", 10, 12.50, 5);

        when(mockColeccion.find(any(Bson.class))).thenReturn(mockFindIterable);
        when(mockFindIterable.iterator()).thenReturn(mockCursor);
        when(mockCursor.hasNext()).thenReturn(true, false);
        when(mockCursor.next()).thenReturn(doc);

        List<Producto> result = dao.buscarProductosPorUbicacion("A", null, null);
        assertEquals(1, result.size());
    }

    @Test
    public void testBuscarProductosPorUbicacionConTodosFiltros() {
        Document doc = crearDocProducto("P001", "Martillo", 10, 12.50, 5);

        when(mockColeccion.find(any(Bson.class))).thenReturn(mockFindIterable);
        when(mockFindIterable.iterator()).thenReturn(mockCursor);
        when(mockCursor.hasNext()).thenReturn(true, false);
        when(mockCursor.next()).thenReturn(doc);

        List<Producto> result = dao.buscarProductosPorUbicacion("A", "1", "2");
        assertEquals(1, result.size());
    }

    @Test
    public void testBuscarProductosPorUbicacionError() {
        when(mockColeccion.find(any(Document.class))).thenThrow(new RuntimeException("Error"));
        when(mockColeccion.find(any(Bson.class))).thenThrow(new RuntimeException("Error"));
        List<Producto> result = dao.buscarProductosPorUbicacion(null, null, null);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testObtenerProductosSinUbicacion() {
        Document doc = new Document("codigo", "P099")
            .append("nombre", "SinUb")
            .append("descripcion", "D")
            .append("stock", 1)
            .append("precio", 1.0)
            .append("stockMinimo", 1);

        when(mockColeccion.find(any(Bson.class))).thenReturn(mockFindIterable);
        when(mockFindIterable.iterator()).thenReturn(mockCursor);
        when(mockCursor.hasNext()).thenReturn(true, false);
        when(mockCursor.next()).thenReturn(doc);

        List<Producto> result = dao.obtenerProductosSinUbicacion();
        assertEquals(1, result.size());
    }

    @Test
    public void testObtenerProductosSinUbicacionError() {
        when(mockColeccion.find(any(Bson.class))).thenThrow(new RuntimeException("Error"));
        List<Producto> result = dao.obtenerProductosSinUbicacion();
        assertTrue(result.isEmpty());
    }

    @Test
    public void testConvertirDocumentoSinUbicacion() {
        Document doc = new Document("codigo", "P001")
            .append("nombre", "Test")
            .append("descripcion", "D")
            .append("stock", 1)
            .append("precio", 1.0)
            .append("stockMinimo", 1);
        // No pasillo/estante/posicion fields

        when(mockColeccion.find(any(Bson.class))).thenReturn(mockFindIterable);
        when(mockFindIterable.first()).thenReturn(doc);

        Producto p = dao.buscarProductoPorCodigo("P001");
        assertNotNull(p);
        assertEquals("", p.getPasillo());
        assertEquals("", p.getEstante());
        assertEquals("", p.getPosicion());
    }
}
