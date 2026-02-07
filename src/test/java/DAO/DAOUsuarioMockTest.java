package DAO;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import model.Usuario;

import java.util.Arrays;
import java.util.List;

@SuppressWarnings("unchecked")
@ExtendWith(MockitoExtension.class)
public class DAOUsuarioMockTest {

    @Mock
    private MongoCollection<Document> mockColeccion;

    @Mock
    private FindIterable<Document> mockFindIterable;

    @Mock
    private MongoCursor<Document> mockCursor;

    private DAOUsuario dao;

    @BeforeEach
    public void setUp() {
        dao = new DAOUsuario(mockColeccion);
    }

    private Document crearDocumentoUsuario(String usuario, String contrasena, String nombre, String rol) {
        return new Document("usuario", usuario)
            .append("contrasena", contrasena)
            .append("nombreCompleto", nombre)
            .append("rol", rol)
            .append("estado", "Activo")
            .append("fechaCreacion", "2025-01-01");
    }

    @Test
    public void testObtenerTodosUsuarios() {
        Document doc1 = crearDocumentoUsuario("user1", "pass1", "User One", "Admin");
        Document doc2 = crearDocumentoUsuario("user2", "pass2", "User Two", "Vendedor");

        when(mockColeccion.find()).thenReturn(mockFindIterable);
        when(mockFindIterable.iterator()).thenReturn(mockCursor);
        when(mockCursor.hasNext()).thenReturn(true, true, false);
        when(mockCursor.next()).thenReturn(doc1, doc2);

        List<Usuario> usuarios = dao.obtenerTodosUsuarios();
        assertEquals(2, usuarios.size());
        assertEquals("user1", usuarios.get(0).getUsuario());
        assertEquals("user2", usuarios.get(1).getUsuario());
    }

    @Test
    public void testObtenerTodosUsuariosError() {
        when(mockColeccion.find()).thenThrow(new RuntimeException("DB Error"));
        List<Usuario> usuarios = dao.obtenerTodosUsuarios();
        assertTrue(usuarios.isEmpty());
    }

    @Test
    public void testBuscarUsuarioPorNombreEncontrado() {
        Document doc = crearDocumentoUsuario("admin", "hash", "Admin User", "Admin");
        when(mockColeccion.find(any(Bson.class))).thenReturn(mockFindIterable);
        when(mockFindIterable.first()).thenReturn(doc);

        Usuario user = dao.buscarUsuarioPorNombre("admin");
        assertNotNull(user);
        assertEquals("admin", user.getUsuario());
        assertEquals("Admin", user.getRol());
    }

    @Test
    public void testBuscarUsuarioPorNombreNoEncontrado() {
        when(mockColeccion.find(any(Bson.class))).thenReturn(mockFindIterable);
        when(mockFindIterable.first()).thenReturn(null);

        Usuario user = dao.buscarUsuarioPorNombre("noexiste");
        assertNull(user);
    }

    @Test
    public void testBuscarUsuarioPorNombreError() {
        when(mockColeccion.find(any(Bson.class))).thenThrow(new RuntimeException("DB Error"));
        Usuario user = dao.buscarUsuarioPorNombre("admin");
        assertNull(user);
    }

    @Test
    public void testCrearUsuarioExitoso() {
        Usuario user = new Usuario("nuevo", "pass123", "Nuevo User", "Vendedor");
        boolean result = dao.crearUsuario(user);
        assertTrue(result);
        verify(mockColeccion).insertOne(any(Document.class));
    }

    @Test
    public void testCrearUsuarioError() {
        doThrow(new RuntimeException("Insert error")).when(mockColeccion).insertOne(any(Document.class));
        Usuario user = new Usuario("nuevo", "pass123", "Nuevo User", "Vendedor");
        boolean result = dao.crearUsuario(user);
        assertFalse(result);
    }

    @Test
    public void testCrearUsuarioContrasenaNull() {
        Usuario user = new Usuario("nuevo", null, "Nuevo User", "Vendedor");
        boolean result = dao.crearUsuario(user);
        assertFalse(result);
    }

    @Test
    public void testCambiarContrasenaExitoso() {
        boolean result = dao.cambiarContrasena("admin", "newpass");
        assertTrue(result);
        verify(mockColeccion).updateOne(any(Bson.class), any(Bson.class));
    }

    @Test
    public void testCambiarContrasenaError() {
        doThrow(new RuntimeException("Update error")).when(mockColeccion).updateOne(any(Bson.class), any(Bson.class));
        boolean result = dao.cambiarContrasena("admin", "newpass");
        assertFalse(result);
    }

    @Test
    public void testActualizarUsuarioExitoso() {
        Usuario user = new Usuario("admin", "pass", "Updated Name", "Admin");
        boolean result = dao.actualizarUsuario("admin", user);
        assertTrue(result);
        verify(mockColeccion).updateOne(any(Bson.class), any(Bson.class));
    }

    @Test
    public void testActualizarUsuarioError() {
        doThrow(new RuntimeException("Update error")).when(mockColeccion).updateOne(any(Bson.class), any(Bson.class));
        boolean result = dao.actualizarUsuario("admin", new Usuario("a", "p", "n", "r"));
        assertFalse(result);
    }

    @Test
    public void testEliminarUsuarioExitoso() {
        boolean result = dao.eliminarUsuario("user1");
        assertTrue(result);
        verify(mockColeccion).deleteOne(any(Bson.class));
    }

    @Test
    public void testEliminarUsuarioError() {
        doThrow(new RuntimeException("Delete error")).when(mockColeccion).deleteOne(any(Bson.class));
        boolean result = dao.eliminarUsuario("user1");
        assertFalse(result);
    }

    @Test
    public void testBloquearUsuarioExitoso() {
        Document doc = crearDocumentoUsuario("user1", "pass", "User One", "Vendedor");
        when(mockColeccion.find(any(Bson.class))).thenReturn(mockFindIterable);
        when(mockFindIterable.first()).thenReturn(doc);

        boolean result = dao.bloquearUsuario("user1");
        assertTrue(result);
        verify(mockColeccion).updateOne(any(Bson.class), any(Bson.class));
    }

    @Test
    public void testBloquearUsuarioNoEncontrado() {
        when(mockColeccion.find(any(Bson.class))).thenReturn(mockFindIterable);
        when(mockFindIterable.first()).thenReturn(null);

        boolean result = dao.bloquearUsuario("noexiste");
        assertFalse(result);
    }

    @Test
    public void testBloquearUsuarioError() {
        when(mockColeccion.find(any(Bson.class))).thenThrow(new RuntimeException("Error"));
        boolean result = dao.bloquearUsuario("user1");
        assertFalse(result);
    }

    @Test
    public void testDesbloquearUsuarioExitoso() {
        Document doc = crearDocumentoUsuario("user1", "pass", "User One", "Vendedor");
        when(mockColeccion.find(any(Bson.class))).thenReturn(mockFindIterable);
        when(mockFindIterable.first()).thenReturn(doc);

        boolean result = dao.desbloquearUsuario("user1");
        assertTrue(result);
        verify(mockColeccion).updateOne(any(Bson.class), any(Bson.class));
    }

    @Test
    public void testDesbloquearUsuarioNoEncontrado() {
        when(mockColeccion.find(any(Bson.class))).thenReturn(mockFindIterable);
        when(mockFindIterable.first()).thenReturn(null);

        boolean result = dao.desbloquearUsuario("noexiste");
        assertFalse(result);
    }

    @Test
    public void testDesbloquearUsuarioError() {
        when(mockColeccion.find(any(Bson.class))).thenThrow(new RuntimeException("Error"));
        boolean result = dao.desbloquearUsuario("user1");
        assertFalse(result);
    }

    @Test
    public void testPuedeSerBloqueadoAdmin() {
        assertFalse(dao.puedeSerBloqueado("admin"));
    }

    @Test
    public void testPuedeSerBloqueadoUsuarioNoBloqueado() {
        Document doc = crearDocumentoUsuario("user1", "pass", "User", "Vendedor");
        doc.append("bloqueado", false);
        when(mockColeccion.find(any(Bson.class))).thenReturn(mockFindIterable);
        when(mockFindIterable.first()).thenReturn(doc);

        assertTrue(dao.puedeSerBloqueado("user1"));
    }

    @Test
    public void testPuedeSerBloqueadoUsuarioYaBloqueado() {
        Document doc = crearDocumentoUsuario("user1", "pass", "User", "Vendedor");
        doc.append("bloqueado", true);
        when(mockColeccion.find(any(Bson.class))).thenReturn(mockFindIterable);
        when(mockFindIterable.first()).thenReturn(doc);

        assertFalse(dao.puedeSerBloqueado("user1"));
    }

    @Test
    public void testPuedeSerBloqueadoUsuarioNoEncontrado() {
        when(mockColeccion.find(any(Bson.class))).thenReturn(mockFindIterable);
        when(mockFindIterable.first()).thenReturn(null);

        assertFalse(dao.puedeSerBloqueado("noexiste"));
    }

    @Test
    public void testPuedeSerBloqueadoError() {
        when(mockColeccion.find(any(Bson.class))).thenThrow(new RuntimeException("Error"));
        assertFalse(dao.puedeSerBloqueado("user1"));
    }

    @Test
    public void testAutenticarUsuarioNoEncontrado() {
        when(mockColeccion.find(any(Bson.class))).thenReturn(mockFindIterable);
        when(mockFindIterable.first()).thenReturn(null);

        assertFalse(dao.autenticar("noexiste", "pass"));
    }

    @Test
    public void testAutenticarUsuarioBloqueado() {
        Document doc = crearDocumentoUsuario("user1", "hash", "User", "Vendedor");
        doc.append("bloqueado", true);
        when(mockColeccion.find(any(Bson.class))).thenReturn(mockFindIterable);
        when(mockFindIterable.first()).thenReturn(doc);

        assertFalse(dao.autenticar("user1", "pass"));
    }

    @Test
    public void testAutenticarError() {
        when(mockColeccion.find(any(Bson.class))).thenThrow(new RuntimeException("Error"));
        assertFalse(dao.autenticar("user1", "pass"));
    }

    @Test
    public void testDocumentoAUsuarioConBloqueadoString() {
        Document doc = crearDocumentoUsuario("user1", "pass", "User One", "Admin");
        doc.put("bloqueado", "true"); // string instead of boolean
        
        when(mockColeccion.find(any(Bson.class))).thenReturn(mockFindIterable);
        when(mockFindIterable.first()).thenReturn(doc);
        
        Usuario user = dao.buscarUsuarioPorNombre("user1");
        assertNotNull(user);
        assertTrue(user.isBloqueado());
    }

    @Test
    public void testDocumentoAUsuarioConFechaBloqueo() {
        Document doc = crearDocumentoUsuario("user1", "pass", "User One", "Admin");
        doc.append("bloqueado", true);
        doc.append("fechaBloqueo", "2025-06-01");

        when(mockColeccion.find(any(Bson.class))).thenReturn(mockFindIterable);
        when(mockFindIterable.first()).thenReturn(doc);

        Usuario user = dao.buscarUsuarioPorNombre("user1");
        assertNotNull(user);
        assertEquals("2025-06-01", user.getFechaBloqueo());
    }

    @Test
    public void testDocumentoAUsuarioSinEstado() {
        Document doc = new Document("usuario", "user1")
            .append("contrasena", "pass")
            .append("nombreCompleto", "User One")
            .append("rol", "Admin");
        // No "estado" field

        when(mockColeccion.find(any(Bson.class))).thenReturn(mockFindIterable);
        when(mockFindIterable.first()).thenReturn(doc);

        Usuario user = dao.buscarUsuarioPorNombre("user1");
        assertNotNull(user);
        assertEquals("Activo", user.getEstado());
    }
}
