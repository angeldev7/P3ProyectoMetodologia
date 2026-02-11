package model;

import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

@DisplayName("Test de la clase Usuario")
class UsuarioTest {

    private Usuario usuario;
    private static final String USUARIO = "jperez";
    private static final String CONTRASENA = "password123";
    private static final String NOMBRE_COMPLETO = "Juan Pérez";
    private static final String ROL = "empleado";

    @BeforeEach
    void setUp() {
        usuario = new Usuario(USUARIO, CONTRASENA, NOMBRE_COMPLETO, ROL);
    }

    @Test
    @DisplayName("Constructor debe inicializar correctamente")
    void testConstructor() {
        assertThat(usuario.getUsuario()).isEqualTo(USUARIO);
        assertThat(usuario.getContrasena()).isEqualTo(CONTRASENA);
        assertThat(usuario.getNombreCompleto()).isEqualTo(NOMBRE_COMPLETO);
        assertThat(usuario.getRol()).isEqualTo(ROL);
    }

    @Test
    @DisplayName("Estado activo debe ser por defecto")
    void testEstadoPorDefecto() {
        assertThat(usuario.getEstado()).isEqualTo("Activo");
        assertThat(usuario.isBloqueado()).isFalse();
    }

    @Test
    @DisplayName("Setters deben actualizar los valores correctamente")
    void testSetters() {
        usuario.setUsuario("mgonzalez");
        usuario.setContrasena("newpassword");
        usuario.setNombreCompleto("María González");
        usuario.setRol("administrador");
        usuario.setBloqueado(true);

        assertThat(usuario.getUsuario()).isEqualTo("mgonzalez");
        assertThat(usuario.getContrasena()).isEqualTo("newpassword");
        assertThat(usuario.getNombreCompleto()).isEqualTo("María González");
        assertThat(usuario.getRol()).isEqualTo("administrador");
        assertThat(usuario.getEstado()).isEqualTo("Bloqueado");
        assertThat(usuario.isBloqueado()).isTrue();
    }

    @Test
    @DisplayName("toString debe incluir información del usuario")
    void testToString() {
        String resultado = usuario.toString();
        assertThat(resultado).isNotNull();
        assertThat(resultado).isNotEmpty();
    }
}