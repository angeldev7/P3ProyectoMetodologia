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
import model.Rol;

import java.util.Arrays;
import java.util.List;

@SuppressWarnings("unchecked")
@ExtendWith(MockitoExtension.class)
public class DAORolMockTest {

    @Mock
    private MongoCollection<Document> mockColeccion;

    @Mock
    private FindIterable<Document> mockFindIterable;

    @Mock
    private MongoCursor<Document> mockCursor;

    private DAORol dao;

    @BeforeEach
    public void setUp() {
        dao = new DAORol(mockColeccion);
    }

    private Document crearDocumentoRol(String nombre, List<String> permisos, int contadorUsuarios) {
        return new Document("nombre", nombre)
            .append("permisos", permisos)
            .append("contadorUsuarios", contadorUsuarios)
            .append("fechaCreacion", "2025-01-01");
    }

    @Test
    public void testCrearRolExitoso() {
        Rol rol = new Rol("Vendedor");
        rol.agregarPermiso("puedeVender");
        boolean result = dao.crearRol(rol);
        assertTrue(result);
        verify(mockColeccion).insertOne(any(Document.class));
    }

    @Test
    public void testCrearRolError() {
        doThrow(new RuntimeException("Error")).when(mockColeccion).insertOne(any(Document.class));
        boolean result = dao.crearRol(new Rol("Test"));
        assertFalse(result);
    }

    @Test
    public void testBuscarRolPorNombreEncontrado() {
        Document doc = crearDocumentoRol("Admin", List.of("puedeGestionarProductos", "puedeVender"), 3);
        when(mockColeccion.find(any(Bson.class))).thenReturn(mockFindIterable);
        when(mockFindIterable.first()).thenReturn(doc);

        Rol rol = dao.buscarRolPorNombre("Admin");
        assertNotNull(rol);
        assertEquals("Admin", rol.getNombre());
        assertEquals(3, rol.getContadorUsuarios());
        assertTrue(rol.tienePermiso("puedeVender"));
    }

    @Test
    public void testBuscarRolPorNombreNoEncontrado() {
        when(mockColeccion.find(any(Bson.class))).thenReturn(mockFindIterable);
        when(mockFindIterable.first()).thenReturn(null);

        Rol rol = dao.buscarRolPorNombre("NoExiste");
        assertNull(rol);
    }

    @Test
    public void testObtenerTodosRoles() {
        Document doc1 = crearDocumentoRol("Admin", List.of("puedeGestionarProductos"), 2);
        Document doc2 = crearDocumentoRol("Vendedor", List.of("puedeVender"), 5);

        when(mockColeccion.find()).thenReturn(mockFindIterable);
        when(mockFindIterable.iterator()).thenReturn(mockCursor);
        when(mockCursor.hasNext()).thenReturn(true, true, false);
        when(mockCursor.next()).thenReturn(doc1, doc2);

        List<Rol> roles = dao.obtenerTodosRoles();
        assertEquals(2, roles.size());
        assertEquals("Admin", roles.get(0).getNombre());
        assertEquals("Vendedor", roles.get(1).getNombre());
    }

    @Test
    public void testActualizarRolExitoso() {
        Rol rol = new Rol("Admin");
        rol.agregarPermiso("puedeVender");
        boolean result = dao.actualizarRol("Admin", rol);
        assertTrue(result);
        verify(mockColeccion).updateOne(any(Bson.class), any(Bson.class));
    }

    @Test
    public void testActualizarRolError() {
        doThrow(new RuntimeException("Error")).when(mockColeccion).updateOne(any(Bson.class), any(Bson.class));
        boolean result = dao.actualizarRol("Admin", new Rol("Admin"));
        assertFalse(result);
    }

    @Test
    public void testEliminarRolExitoso() {
        boolean result = dao.eliminarRol("Test");
        assertTrue(result);
        verify(mockColeccion).deleteOne(any(Bson.class));
    }

    @Test
    public void testEliminarRolError() {
        doThrow(new RuntimeException("Error")).when(mockColeccion).deleteOne(any(Bson.class));
        boolean result = dao.eliminarRol("Test");
        assertFalse(result);
    }

    @Test
    public void testActualizarContadorUsuariosRolEncontrado() {
        Document doc = crearDocumentoRol("Admin", List.of("perm1"), 3);
        when(mockColeccion.find(any(Bson.class))).thenReturn(mockFindIterable);
        when(mockFindIterable.first()).thenReturn(doc);

        boolean result = dao.actualizarContadorUsuarios("Admin", 1);
        assertTrue(result);
        verify(mockColeccion).updateOne(any(Bson.class), any(Bson.class));
    }

    @Test
    public void testActualizarContadorUsuariosRolNoEncontrado() {
        when(mockColeccion.find(any(Bson.class))).thenReturn(mockFindIterable);
        when(mockFindIterable.first()).thenReturn(null);

        boolean result = dao.actualizarContadorUsuarios("NoExiste", 1);
        assertFalse(result);
    }

    @Test
    public void testActualizarContadorUsuariosError() {
        when(mockColeccion.find(any(Bson.class))).thenThrow(new RuntimeException("Error"));
        boolean result = dao.actualizarContadorUsuarios("Admin", 1);
        assertFalse(result);
    }

    @Test
    public void testDocumentoARolSinPermisos() {
        Document doc = crearDocumentoRol("Basic", null, 0);
        when(mockColeccion.find(any(Bson.class))).thenReturn(mockFindIterable);
        when(mockFindIterable.first()).thenReturn(doc);

        Rol rol = dao.buscarRolPorNombre("Basic");
        assertNotNull(rol);
        assertEquals("Basic", rol.getNombre());
    }
}
