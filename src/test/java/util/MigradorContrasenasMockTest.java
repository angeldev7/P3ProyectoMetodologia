package util;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.junit.jupiter.MockitoExtension;

import DAO.DAOUsuario;
import model.Usuario;

import java.util.Arrays;
import java.util.Collections;

@ExtendWith(MockitoExtension.class)
class MigradorContrasenasMockTest {

    @Test
    void testMigrarUsuariosConContrasenasSinHash() {
        try (MockedConstruction<DAOUsuario> mocked = mockConstruction(DAOUsuario.class, (mock, context) -> {
            Usuario u1 = new Usuario("user1", "plaintext", "User 1", "Vendedor");
            Usuario u2 = new Usuario("user2", "$2a$10$hashedpassword", "User 2", "Admin");
            Usuario u3 = new Usuario("user3", "otroPlain", "User 3", "Vendedor");

            when(mock.obtenerTodosUsuarios()).thenReturn(Arrays.asList(u1, u2, u3));
            when(mock.cambiarContrasena(anyString(), anyString())).thenReturn(true);
        })) {
            assertDoesNotThrow(() -> MigradorContrasenas.migrarUsuariosExistentes());
        }
    }

    @Test
    void testMigrarUsuariosSinUsuarios() {
        try (MockedConstruction<DAOUsuario> mocked = mockConstruction(DAOUsuario.class, (mock, context) -> {
            when(mock.obtenerTodosUsuarios()).thenReturn(Collections.emptyList());
        })) {
            assertDoesNotThrow(() -> MigradorContrasenas.migrarUsuariosExistentes());
        }
    }

    @Test
    void testMigrarUsuariosCambioContrasenaFalla() {
        try (MockedConstruction<DAOUsuario> mocked = mockConstruction(DAOUsuario.class, (mock, context) -> {
            Usuario u1 = new Usuario("user1", "plaintext", "User 1", "Vendedor");
            when(mock.obtenerTodosUsuarios()).thenReturn(Arrays.asList(u1));
            when(mock.cambiarContrasena(anyString(), anyString())).thenReturn(false);
        })) {
            assertDoesNotThrow(() -> MigradorContrasenas.migrarUsuariosExistentes());
        }
    }

    @Test
    void testMigrarTodosYaHasheados() {
        try (MockedConstruction<DAOUsuario> mocked = mockConstruction(DAOUsuario.class, (mock, context) -> {
            Usuario u1 = new Usuario("user1", "$2a$10$abc123", "User 1", "Vendedor");
            Usuario u2 = new Usuario("user2", "$2b$10$xyz456", "User 2", "Admin");
            when(mock.obtenerTodosUsuarios()).thenReturn(Arrays.asList(u1, u2));
        })) {
            assertDoesNotThrow(() -> MigradorContrasenas.migrarUsuariosExistentes());
        }
    }
}
