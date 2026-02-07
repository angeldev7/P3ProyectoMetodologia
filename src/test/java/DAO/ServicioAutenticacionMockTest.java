package DAO;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import model.Usuario;
import model.Rol;
import model.AccesoSistema;

import java.util.List;

@ExtendWith(MockitoExtension.class)
public class ServicioAutenticacionMockTest {

    @Mock
    private DAOUsuario mockDaoUsuario;

    @Mock
    private DAORol mockDaoRol;

    @Mock
    private AccesoSistemaDAO mockAccesoDAO;

    private ServicioAutenticacion servicio;

    @BeforeEach
    public void setUp() {
        servicio = new ServicioAutenticacion(mockDaoUsuario, mockDaoRol, mockAccesoDAO);
    }

    @Test
    public void testAutenticarUsuarioNoEncontrado() {
        when(mockDaoUsuario.buscarUsuarioPorNombre("noexiste")).thenReturn(null);
        boolean result = servicio.autenticar("noexiste", "pass");
        assertFalse(result);
        verify(mockAccesoDAO).registrarAcceso(any(AccesoSistema.class));
    }

    @Test
    public void testAutenticarUsuarioBloqueado() {
        Usuario user = new Usuario("bloqueado", "hash", "Blocked User", "Vendedor");
        user.setBloqueado(true);
        when(mockDaoUsuario.buscarUsuarioPorNombre("bloqueado")).thenReturn(user);

        boolean result = servicio.autenticar("bloqueado", "pass");
        assertFalse(result);
        verify(mockAccesoDAO).registrarAcceso(any(AccesoSistema.class));
    }

    @Test
    public void testAutenticarContrasenaIncorrecta() {
        Usuario user = new Usuario("user1", "hash", "User", "Vendedor");
        when(mockDaoUsuario.buscarUsuarioPorNombre("user1")).thenReturn(user);
        when(mockDaoUsuario.autenticar("user1", "wrong")).thenReturn(false);

        boolean result = servicio.autenticar("user1", "wrong");
        assertFalse(result);
        verify(mockAccesoDAO, atLeastOnce()).registrarAcceso(any(AccesoSistema.class));
    }

    @Test
    public void testAutenticarExitosoConRol() {
        Usuario user = new Usuario("admin", "hash", "Admin User", "Administrador");
        Rol rol = new Rol("Administrador");
        rol.agregarPermiso("puedeGestionarProductos");

        when(mockDaoUsuario.buscarUsuarioPorNombre("admin")).thenReturn(user);
        when(mockDaoUsuario.autenticar("admin", "adminpass")).thenReturn(true);
        when(mockDaoRol.buscarRolPorNombre("Administrador")).thenReturn(rol);

        boolean result = servicio.autenticar("admin", "adminpass");
        assertTrue(result);
        assertEquals("admin", servicio.getUsuarioActual());
        assertEquals("Administrador", servicio.getRolActual());
    }

    @Test
    public void testAutenticarExitosoSinRol() {
        Usuario user = new Usuario("user1", "hash", "User One", "NuevoRol");

        when(mockDaoUsuario.buscarUsuarioPorNombre("user1")).thenReturn(user);
        when(mockDaoUsuario.autenticar("user1", "pass")).thenReturn(true);
        when(mockDaoRol.buscarRolPorNombre("NuevoRol")).thenReturn(null);
        // crearRolBasico will call daoRol.crearRol and then buscarRolPorNombre again

        boolean result = servicio.autenticar("user1", "pass");
        assertTrue(result);
        verify(mockDaoRol).crearRol(any(Rol.class));
    }

    @Test
    public void testTienePermisoAdmin() {
        Usuario user = new Usuario("admin", "hash", "Admin", "Administrador");
        when(mockDaoUsuario.buscarUsuarioPorNombre("admin")).thenReturn(user);
        when(mockDaoUsuario.autenticar("admin", "pass")).thenReturn(true);
        when(mockDaoRol.buscarRolPorNombre("Administrador")).thenReturn(new Rol("Administrador"));

        servicio.autenticar("admin", "pass");
        assertTrue(servicio.tienePermiso("cualquierPermiso"));
    }

    @Test
    public void testTienePermisoConRol() {
        Usuario user = new Usuario("vendedor", "hash", "Vendedor", "Vendedor");
        Rol rol = new Rol("Vendedor");
        rol.agregarPermiso("puedeVender");

        when(mockDaoUsuario.buscarUsuarioPorNombre("vendedor")).thenReturn(user);
        when(mockDaoUsuario.autenticar("vendedor", "pass")).thenReturn(true);
        when(mockDaoRol.buscarRolPorNombre("Vendedor")).thenReturn(rol);

        servicio.autenticar("vendedor", "pass");
        assertTrue(servicio.tienePermiso("puedeVender"));
        assertFalse(servicio.tienePermiso("puedeGestionarUsuarios"));
    }

    @Test
    public void testTienePermisoSinRol() {
        assertFalse(servicio.tienePermiso("cualquier"));
    }

    @Test
    public void testGetUsuarioActualNull() {
        assertNull(servicio.getUsuarioActual());
    }

    @Test
    public void testGetRolActualNull() {
        assertNull(servicio.getRolActual());
    }

    @Test
    public void testGetUsuarioActualObj() {
        assertNull(servicio.getUsuarioActualObj());
    }

    @Test
    public void testCerrarSesion() {
        Usuario user = new Usuario("admin", "hash", "Admin", "Admin");
        Rol rol = new Rol("Admin");
        when(mockDaoUsuario.buscarUsuarioPorNombre("admin")).thenReturn(user);
        when(mockDaoUsuario.autenticar("admin", "pass")).thenReturn(true);
        when(mockDaoRol.buscarRolPorNombre("Admin")).thenReturn(rol);

        servicio.autenticar("admin", "pass");
        assertNotNull(servicio.getUsuarioActual());

        servicio.cerrarSesion();
        assertNull(servicio.getUsuarioActual());
        assertNull(servicio.getRolActual());
    }

    @Test
    public void testGetPermisosActualesConRol() {
        Usuario user = new Usuario("user1", "hash", "User", "Vendedor");
        Rol rol = new Rol("Vendedor");
        rol.agregarPermiso("perm1");
        rol.agregarPermiso("perm2");

        when(mockDaoUsuario.buscarUsuarioPorNombre("user1")).thenReturn(user);
        when(mockDaoUsuario.autenticar("user1", "pass")).thenReturn(true);
        when(mockDaoRol.buscarRolPorNombre("Vendedor")).thenReturn(rol);

        servicio.autenticar("user1", "pass");
        List<String> permisos = servicio.getPermisosActuales();
        assertNotNull(permisos);
        assertEquals(2, permisos.size());
    }

    @Test
    public void testGetPermisosActualesSinLogin() {
        assertNull(servicio.getPermisosActuales());
    }

    @Test
    public void testGetUsuarioActualObjDespuesDeLogin() {
        Usuario user = new Usuario("user1", "hash", "User One", "Vendedor");
        Rol rol = new Rol("Vendedor");

        when(mockDaoUsuario.buscarUsuarioPorNombre("user1")).thenReturn(user);
        when(mockDaoUsuario.autenticar("user1", "pass")).thenReturn(true);
        when(mockDaoRol.buscarRolPorNombre("Vendedor")).thenReturn(rol);

        servicio.autenticar("user1", "pass");
        Usuario actual = servicio.getUsuarioActualObj();
        assertNotNull(actual);
        assertEquals("user1", actual.getUsuario());
    }

    @Test
    public void testMostrarEstadisticasAccesos() {
        when(mockAccesoDAO.contarAccesosTotales()).thenReturn(100L);
        when(mockAccesoDAO.contarAccesosExitosos()).thenReturn(80L);
        when(mockAccesoDAO.contarAccesosFallidos()).thenReturn(20L);

        assertDoesNotThrow(() -> servicio.mostrarEstadisticasAccesos());
    }

    @Test
    public void testMostrarEstadisticasAccesosSinDatos() {
        when(mockAccesoDAO.contarAccesosTotales()).thenReturn(0L);
        when(mockAccesoDAO.contarAccesosExitosos()).thenReturn(0L);
        when(mockAccesoDAO.contarAccesosFallidos()).thenReturn(0L);

        assertDoesNotThrow(() -> servicio.mostrarEstadisticasAccesos());
    }

    @Test
    public void testMostrarEstadisticasAccesosError() {
        when(mockAccesoDAO.contarAccesosTotales()).thenThrow(new RuntimeException("Error"));
        assertDoesNotThrow(() -> servicio.mostrarEstadisticasAccesos());
    }
}
