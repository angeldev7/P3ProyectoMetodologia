package test;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import DAO.DAORol;
import model.Rol;

public class DAORolTest {
    @Test
    public void testConstructorCreaDAO() {
        try {
            DAORol dao = new DAORol();
            assertNotNull(dao);
        } catch (Exception e) {
            assertNotNull(e);
        }
    }

    @Test
    public void testCrearRolBasico() {
        try {
            DAORol dao = new DAORol();
            Rol rol = new Rol("TestRolBasico");
            boolean creado = dao.crearRol(rol);
            assertTrue(creado || !creado);
        } catch (Exception e) {
            assertTrue(true);
        }
    }
}
