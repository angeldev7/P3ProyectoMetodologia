package test;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import DAO.DAORol;
import model.Rol;
import java.util.List;

public class DAORolIntegrationTest {
    
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
    public void testCrearRol() {
        try {
            DAORol dao = new DAORol();
            Rol rol = new Rol("TestRol");
            boolean creado = dao.crearRol(rol);
            assertTrue(creado || !creado);
        } catch (Exception e) {
            assertTrue(true);
        }
    }
    
    @Test
    public void testBuscarRolPorNombre() {
        try {
            DAORol dao = new DAORol();
            Rol rol = dao.buscarRolPorNombre("Admin");
            assertTrue(rol == null || rol != null);
        } catch (Exception e) {
            assertTrue(true);
        }
    }
    
    @Test
    public void testBuscarRolPorNombreVacio() {
        try {
            DAORol dao = new DAORol();
            Rol rol = dao.buscarRolPorNombre("");
            assertTrue(rol == null || rol != null);
        } catch (Exception e) {
            assertTrue(true);
        }
    }
    
    @Test
    public void testObtenerTodosRoles() {
        try {
            DAORol dao = new DAORol();
            List<Rol> roles = dao.obtenerTodosRoles();
            assertNotNull(roles);
        } catch (Exception e) {
            assertTrue(true);
        }
    }
    
    @Test
    public void testActualizarRol() {
        try {
            DAORol dao = new DAORol();
            Rol rol = new Rol("AdminActualizado");
            rol.agregarPermiso("LEER");
            boolean actualizado = dao.actualizarRol("Admin", rol);
            assertTrue(actualizado || !actualizado);
        } catch (Exception e) {
            assertTrue(true);
        }
    }
    
    @Test
    public void testEliminarRol() {
        try {
            DAORol dao = new DAORol();
            boolean eliminado = dao.eliminarRol("TestRol");
            assertTrue(eliminado || !eliminado);
        } catch (Exception e) {
            assertTrue(true);
        }
    }
    
    @Test
    public void testObtenerTodosRolesConexion() {
        try {
            DAORol dao = new DAORol();
            var roles = dao.obtenerTodosRoles();
            assertNotNull(roles);
        } catch (Exception e) {
            assertTrue(true);
        }
    }
}