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
import model.AccesoSistema;

import java.util.List;

@SuppressWarnings("unchecked")
@ExtendWith(MockitoExtension.class)
public class AccesoSistemaDAOMockTest {

    @Mock
    private MongoCollection<Document> mockColeccion;

    @Mock
    private FindIterable<Document> mockFindIterable;

    @Mock
    private FindIterable<Document> mockSortedIterable;

    @Mock
    private MongoCursor<Document> mockCursor;

    private AccesoSistemaDAO dao;

    @BeforeEach
    public void setUp() {
        dao = new AccesoSistemaDAO(mockColeccion);
    }

    private Document crearDocAcceso(String usuario, String rol, String fechaHora, String tipo, String ip, String mensaje) {
        return new Document("usuario", usuario)
            .append("rol", rol)
            .append("fechaHora", fechaHora)
            .append("tipoAcceso", tipo)
            .append("ip", ip)
            .append("mensaje", mensaje);
    }

    @Test
    public void testRegistrarAcceso() {
        AccesoSistema acceso = new AccesoSistema("admin", "Admin", "EXITOSO", "Login ok");
        dao.registrarAcceso(acceso);
        verify(mockColeccion).insertOne(any(Document.class));
    }

    @Test
    public void testRegistrarAccesoError() {
        doThrow(new RuntimeException("Error")).when(mockColeccion).insertOne(any(Document.class));
        AccesoSistema acceso = new AccesoSistema("admin", "Admin", "EXITOSO", "Login ok");
        // Should not throw
        assertDoesNotThrow(() -> dao.registrarAcceso(acceso));
    }

    @Test
    public void testObtenerTodosAccesos() {
        Document doc1 = crearDocAcceso("admin", "Admin", "2025-01-01 10:00", "EXITOSO", "127.0.0.1", "OK");
        Document doc2 = crearDocAcceso("user1", "Vendedor", "2025-01-01 11:00", "FALLIDO", "127.0.0.1", "Bad pass");

        when(mockColeccion.find()).thenReturn(mockFindIterable);
        when(mockFindIterable.sort(any(Bson.class))).thenReturn(mockSortedIterable);
        when(mockSortedIterable.iterator()).thenReturn(mockCursor);
        when(mockCursor.hasNext()).thenReturn(true, true, false);
        when(mockCursor.next()).thenReturn(doc1, doc2);

        List<AccesoSistema> accesos = dao.obtenerTodosAccesos();
        assertEquals(2, accesos.size());
        assertEquals("admin", accesos.get(0).getUsuario());
        assertEquals("user1", accesos.get(1).getUsuario());
    }

    @Test
    public void testObtenerTodosAccesosError() {
        when(mockColeccion.find()).thenThrow(new RuntimeException("Error"));
        List<AccesoSistema> accesos = dao.obtenerTodosAccesos();
        assertTrue(accesos.isEmpty());
    }

    @Test
    public void testObtenerAccesosPorUsuario() {
        Document doc = crearDocAcceso("admin", "Admin", "2025-01-01 10:00", "EXITOSO", "127.0.0.1", "OK");

        when(mockColeccion.find(any(Bson.class))).thenReturn(mockFindIterable);
        when(mockFindIterable.sort(any(Bson.class))).thenReturn(mockSortedIterable);
        when(mockSortedIterable.iterator()).thenReturn(mockCursor);
        when(mockCursor.hasNext()).thenReturn(true, false);
        when(mockCursor.next()).thenReturn(doc);

        List<AccesoSistema> accesos = dao.obtenerAccesosPorUsuario("admin");
        assertEquals(1, accesos.size());
        assertEquals("admin", accesos.get(0).getUsuario());
    }

    @Test
    public void testObtenerAccesosPorUsuarioError() {
        when(mockColeccion.find(any(Bson.class))).thenThrow(new RuntimeException("Error"));
        List<AccesoSistema> accesos = dao.obtenerAccesosPorUsuario("admin");
        assertTrue(accesos.isEmpty());
    }

    @Test
    public void testObtenerAccesosExitosos() {
        Document doc = crearDocAcceso("admin", "Admin", "2025-01-01 10:00", "EXITOSO", "127.0.0.1", "OK");

        when(mockColeccion.find(any(Bson.class))).thenReturn(mockFindIterable);
        when(mockFindIterable.sort(any(Bson.class))).thenReturn(mockSortedIterable);
        when(mockSortedIterable.iterator()).thenReturn(mockCursor);
        when(mockCursor.hasNext()).thenReturn(true, false);
        when(mockCursor.next()).thenReturn(doc);

        List<AccesoSistema> accesos = dao.obtenerAccesosExitosos();
        assertEquals(1, accesos.size());
        assertEquals("EXITOSO", accesos.get(0).getTipoAcceso());
    }

    @Test
    public void testObtenerAccesosExitososError() {
        when(mockColeccion.find(any(Bson.class))).thenThrow(new RuntimeException("Error"));
        List<AccesoSistema> accesos = dao.obtenerAccesosExitosos();
        assertTrue(accesos.isEmpty());
    }

    @Test
    public void testObtenerAccesosFallidos() {
        Document doc = crearDocAcceso("user1", "Vendedor", "2025-01-01 11:00", "FALLIDO", "127.0.0.1", "Bad pass");

        when(mockColeccion.find(any(Bson.class))).thenReturn(mockFindIterable);
        when(mockFindIterable.sort(any(Bson.class))).thenReturn(mockSortedIterable);
        when(mockSortedIterable.iterator()).thenReturn(mockCursor);
        when(mockCursor.hasNext()).thenReturn(true, false);
        when(mockCursor.next()).thenReturn(doc);

        List<AccesoSistema> accesos = dao.obtenerAccesosFallidos();
        assertEquals(1, accesos.size());
        assertEquals("FALLIDO", accesos.get(0).getTipoAcceso());
    }

    @Test
    public void testObtenerAccesosFallidosError() {
        when(mockColeccion.find(any(Bson.class))).thenThrow(new RuntimeException("Error"));
        List<AccesoSistema> accesos = dao.obtenerAccesosFallidos();
        assertTrue(accesos.isEmpty());
    }

    @Test
    public void testObtenerAccesosPorFecha() {
        Document doc = crearDocAcceso("admin", "Admin", "2025-01-01 10:00", "EXITOSO", "127.0.0.1", "OK");

        when(mockColeccion.find(any(Bson.class))).thenReturn(mockFindIterable);
        when(mockFindIterable.sort(any(Bson.class))).thenReturn(mockSortedIterable);
        when(mockSortedIterable.iterator()).thenReturn(mockCursor);
        when(mockCursor.hasNext()).thenReturn(true, false);
        when(mockCursor.next()).thenReturn(doc);

        List<AccesoSistema> accesos = dao.obtenerAccesosPorFecha("2025-01-01");
        assertEquals(1, accesos.size());
    }

    @Test
    public void testObtenerAccesosPorFechaError() {
        when(mockColeccion.find(any(Bson.class))).thenThrow(new RuntimeException("Error"));
        List<AccesoSistema> accesos = dao.obtenerAccesosPorFecha("2025-01-01");
        assertTrue(accesos.isEmpty());
    }

    @Test
    public void testContarAccesosTotales() {
        when(mockColeccion.countDocuments()).thenReturn(42L);
        assertEquals(42, dao.contarAccesosTotales());
    }

    @Test
    public void testContarAccesosTotalesError() {
        when(mockColeccion.countDocuments()).thenThrow(new RuntimeException("Error"));
        assertEquals(0, dao.contarAccesosTotales());
    }

    @Test
    public void testContarAccesosExitosos() {
        when(mockColeccion.countDocuments(any(Bson.class))).thenReturn(30L);
        assertEquals(30, dao.contarAccesosExitosos());
    }

    @Test
    public void testContarAccesosExitososError() {
        when(mockColeccion.countDocuments(any(Bson.class))).thenThrow(new RuntimeException("Error"));
        assertEquals(0, dao.contarAccesosExitosos());
    }

    @Test
    public void testContarAccesosFallidos() {
        when(mockColeccion.countDocuments(any(Bson.class))).thenReturn(12L);
        assertEquals(12, dao.contarAccesosFallidos());
    }

    @Test
    public void testContarAccesosFallidosError() {
        when(mockColeccion.countDocuments(any(Bson.class))).thenThrow(new RuntimeException("Error"));
        assertEquals(0, dao.contarAccesosFallidos());
    }

    @Test
    public void testEliminarRegistrosAntiguos() {
        assertTrue(dao.eliminarRegistrosAntiguos(30));
    }

    // --- Tests para sanitizarTexto y fecha inválida ---

    @Test
    public void testRegistrarAccesoConTextoNull() {
        AccesoSistema acceso = new AccesoSistema(null, null, "EXITOSO", null);
        assertDoesNotThrow(() -> dao.registrarAcceso(acceso));
        verify(mockColeccion).insertOne(any(Document.class));
    }

    @Test
    public void testRegistrarAccesoConCaracteresEspeciales() {
        AccesoSistema acceso = new AccesoSistema("user<script>", "Admin\"", "EXITOSO", "msg&;test");
        assertDoesNotThrow(() -> dao.registrarAcceso(acceso));
        verify(mockColeccion).insertOne(any(Document.class));
    }

    @Test
    public void testObtenerAccesosPorFechaNull() {
        List<AccesoSistema> accesos = dao.obtenerAccesosPorFecha(null);
        assertTrue(accesos.isEmpty());
    }

    @Test
    public void testObtenerAccesosPorFechaFormatoInvalido() {
        List<AccesoSistema> accesos = dao.obtenerAccesosPorFecha("invalid-date");
        assertTrue(accesos.isEmpty());
    }

    @Test
    public void testObtenerAccesosPorFechaFormatoIncompleto() {
        List<AccesoSistema> accesos = dao.obtenerAccesosPorFecha("2025-01");
        assertTrue(accesos.isEmpty());
    }
}
