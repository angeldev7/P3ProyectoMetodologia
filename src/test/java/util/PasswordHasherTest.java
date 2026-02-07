package util;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class PasswordHasherTest {

    @Test
    void testHashPassword() {
        String password = "miPassword123";
        String hash = util.PasswordHasher.hashPassword(password);
        
        assertNotNull(hash);
        assertNotEquals(password, hash);
        assertTrue(hash.startsWith("$2a$"));
    }

    @Test
    void testCheckPassword() {
        String password = "miPassword123";
        String hash = util.PasswordHasher.hashPassword(password);
        
        assertTrue(util.PasswordHasher.checkPassword(password, hash));
        assertFalse(util.PasswordHasher.checkPassword("passwordIncorrecto", hash));
    }

    @Test
    void testHashPasswordNull() {
        String hash = util.PasswordHasher.hashPassword(null);
        assertNull(hash);
    }

    @Test
    void testCheckPasswordNull() {
        String hash = util.PasswordHasher.hashPassword("test");
        assertFalse(util.PasswordHasher.checkPassword(null, hash));
        assertFalse(util.PasswordHasher.checkPassword("test", null));
    }

    @Test
    void testHashesDistintos() {
        String password = "test123";
        String hash1 = util.PasswordHasher.hashPassword(password);
        String hash2 = util.PasswordHasher.hashPassword(password);
        
        // Los hashes deben ser diferentes debido al salt
        assertNotEquals(hash1, hash2);
        
        // Pero ambos deben validar la misma contraseña
        assertTrue(util.PasswordHasher.checkPassword(password, hash1));
        assertTrue(util.PasswordHasher.checkPassword(password, hash2));
    }
}