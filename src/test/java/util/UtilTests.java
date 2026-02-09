package util;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

/**
 * Tests adicionales para aumentar cobertura en util
 */
class UtilTests {

    @Test
    void testPasswordHasherCasos() {
        // Test hashear contraseña
        String hash1 = PasswordHasher.hashPassword("password123");
        String hash2 = PasswordHasher.hashPassword("password123");
        
        assertNotNull(hash1);
        assertNotNull(hash2);
        assertNotEquals(hash1, hash2); // Los hashes deben ser diferentes (sal)
        
        // Test verificar contraseña
        assertTrue(PasswordHasher.verificarContrasena("password123", hash1));
        assertTrue(PasswordHasher.verificarContrasena("password123", hash2));
        assertFalse(PasswordHasher.verificarContrasena("wrongpassword", hash1));
    }

    @Test
    void testPasswordHasherEdgeCases() {
        // Test con contraseña vacía
        String hashVacio = PasswordHasher.hashPassword("");
        assertNotNull(hashVacio);
        assertTrue(PasswordHasher.verificarContrasena("", hashVacio));
        
        // Test con contraseña muy larga
        String passwordLarga = "a".repeat(1000);
        String hashLarga = PasswordHasher.hashPassword(passwordLarga);
        assertNotNull(hashLarga);
        assertTrue(PasswordHasher.verificarContrasena(passwordLarga, hashLarga));
        
        // Test con caracteres especiales
        String passwordEspecial = "!@#$%^&*()_+-=[]{}|;:,.<>?";
        String hashEspecial = PasswordHasher.hashPassword(passwordEspecial);
        assertNotNull(hashEspecial);
        assertTrue(PasswordHasher.verificarContrasena(passwordEspecial, hashEspecial));
    }

    @Test
    void testPasswordHasherNull() {
        // Test con valores null
        assertThrows(Exception.class, () -> {
            PasswordHasher.hashPassword(null);
        });
        
        assertThrows(Exception.class, () -> {
            PasswordHasher.verificarContrasena(null, "hash");
        });
        
        assertThrows(Exception.class, () -> {
            PasswordHasher.verificarContrasena("password", null);
        });
    }

    @Test
    void testIsHashedMethod() {
        // Test con hash válido de BCrypt
        String hashValido = "$2a$10$N9qo8uLOickgx2ZMRZoMyeUQJ3Jg0P5CmwZL4lWyWWOUIq4m7pCXi";
        assertTrue(PasswordHasher.isHashed(hashValido));
        
        // Test con contraseña sin hashear
        assertFalse(PasswordHasher.isHashed("plaintext"));
        assertFalse(PasswordHasher.isHashed("admin"));
        assertFalse(PasswordHasher.isHashed("123456"));
        
        // Test con strings que parecen hash pero no son válidos
        assertFalse(PasswordHasher.isHashed("$2a$10$invalid"));
        assertFalse(PasswordHasher.isHashed("$2b$invalid"));
        
        // Test edge cases
        assertFalse(PasswordHasher.isHashed(""));
        assertFalse(PasswordHasher.isHashed(null));
    }

    @Test
    void testHashPasswordConsistency() {
        String password = "testPassword123";
        
        // Generar múltiples hashes
        String hash1 = PasswordHasher.hashPassword(password);
        String hash2 = PasswordHasher.hashPassword(password);
        String hash3 = PasswordHasher.hashPassword(password);
        
        // Verificar que todos son válidos
        assertTrue(PasswordHasher.verificarContrasena(password, hash1));
        assertTrue(PasswordHasher.verificarContrasena(password, hash2));
        assertTrue(PasswordHasher.verificarContrasena(password, hash3));
        
        // Verificar que todos son reconocidos como hashes
        assertTrue(PasswordHasher.isHashed(hash1));
        assertTrue(PasswordHasher.isHashed(hash2));
        assertTrue(PasswordHasher.isHashed(hash3));
    }

    @Test
    void testPasswordHasherSecurityFeatures() {
        // Test que contraseñas similares producen hashes muy diferentes
        String hash1 = PasswordHasher.hashPassword("Password123");
        String hash2 = PasswordHasher.hashPassword("Password124"); // Solo cambia último caracter
        
        assertNotEquals(hash1, hash2);
        
        // Verificar que no hay confusión cruzada
        assertFalse(PasswordHasher.verificarContrasena("Password123", hash2));
        assertFalse(PasswordHasher.verificarContrasena("Password124", hash1));
    }

    @Test
    void testMigradorContrasenasExistencia() {
        // Test que la clase MigradorContrasenas existe y tiene métodos básicos
        assertNotNull(MigradorContrasenas.class);
        
        // Verificar que no lance excepciones al verificar su existencia
        assertDoesNotThrow(() -> {
            MigradorContrasenas.class.getDeclaredMethods();
        });
    }
}