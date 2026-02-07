package util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Test Simple de Cobertura")
public class TestSimple {
    
    @Test
    @DisplayName("Test básico que siempre pasa")
    void testBasico() {
        int suma = 2 + 2;
        assertEquals(4, suma, "2 + 2 debe ser 4");
    }
    
    @Test
    @DisplayName("Test de string")
    void testString() {
        String mensaje = "Hola Mundo";
        assertNotNull(mensaje);
        assertTrue(mensaje.contains("Mundo"));
    }
    
    @Test
    @DisplayName("Test de boolean")
    void testBoolean() {
        boolean verdadero = true;
        boolean falso = false;
        
        assertTrue(verdadero);
        assertFalse(falso);
        assertNotEquals(verdadero, falso);
    }
}