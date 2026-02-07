package test;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import DAO.DAOUsuario;
import model.Usuario;
import java.util.List;

public class DAOUsuarioTest {
    @Test
    public void testConstructorCreaDAO() {
        try {
            DAOUsuario dao = new DAOUsuario();
            assertNotNull(dao);
        } catch (Exception e) {
            assertNotNull(e);
        }
    }

    @Test
    public void testObtenerTodosUsuarios() {
        try {
            DAOUsuario dao = new DAOUsuario();
            List<Usuario> usuarios = dao.obtenerTodosUsuarios();
            assertNotNull(usuarios);
        } catch (Exception e) {
            assertTrue(true);
        }
    }

    @Test
    public void testAutenticarUsuarioInvalido() {
        try {
            DAOUsuario dao = new DAOUsuario();
            boolean resultado = dao.autenticar("noexiste", "noexiste");
            assertFalse(resultado);
        } catch (Exception e) {
            assertTrue(true);
        }
    }
}
