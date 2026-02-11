package util;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class PasswordHasherFullTest {

    @Test
    public void testHashPasswordReturnsNonNull() {
        String hash = PasswordHasher.hashPassword("miPassword123");
        assertNotNull(hash);
    }

    @Test
    public void testHashPasswordReturnsBCryptFormat() {
        String hash = PasswordHasher.hashPassword("test");
        assertTrue(hash.startsWith("$2a$") || hash.startsWith("$2b$") || hash.startsWith("$2y$"));
    }

    @Test
    public void testHashPasswordNull() {
        String hash = PasswordHasher.hashPassword(null);
        assertNull(hash);
    }

    @Test
    public void testHashPasswordEmptyString() {
        String hash = PasswordHasher.hashPassword("");
        assertNotNull(hash);
    }

    @Test
    public void testHashPasswordDifferentHashesForSamePassword() {
        String hash1 = PasswordHasher.hashPassword("same");
        String hash2 = PasswordHasher.hashPassword("same");
        assertNotEquals(hash1, hash2); // BCrypt uses random salt
    }

    @Test
    public void testCheckPasswordValid() {
        String password = "mySecret123";
        String hash = PasswordHasher.hashPassword(password);
        assertTrue(PasswordHasher.checkPassword(password, hash));
    }

    @Test
    public void testCheckPasswordInvalid() {
        String hash = PasswordHasher.hashPassword("correct");
        assertFalse(PasswordHasher.checkPassword("wrong", hash));
    }

    @Test
    public void testCheckPasswordNullPassword() {
        assertFalse(PasswordHasher.checkPassword(null, "$2a$10$abc"));
    }

    @Test
    public void testCheckPasswordNullHash() {
        assertFalse(PasswordHasher.checkPassword("pass", null));
    }

    @Test
    public void testCheckPasswordEmptyHash() {
        assertFalse(PasswordHasher.checkPassword("pass", ""));
    }

    @Test
    public void testCheckPasswordBothNull() {
        assertFalse(PasswordHasher.checkPassword(null, null));
    }

    @Test
    public void testCheckPasswordInvalidHashFormat() {
        assertFalse(PasswordHasher.checkPassword("pass", "nothash"));
    }

    @Test
    public void testIsHashedTrue2a() {
        assertTrue(PasswordHasher.isHashed("$2a$10$abcdefghij"));
    }

    @Test
    public void testIsHashedTrue2b() {
        assertTrue(PasswordHasher.isHashed("$2b$10$abcdefghij"));
    }

    @Test
    public void testIsHashedTrue2y() {
        assertTrue(PasswordHasher.isHashed("$2y$10$abcdefghij"));
    }

    @Test
    public void testIsHashedFalsePlainText() {
        assertFalse(PasswordHasher.isHashed("plaintext"));
    }

    @Test
    public void testIsHashedFalseNull() {
        assertFalse(PasswordHasher.isHashed(null));
    }

    @Test
    public void testIsHashedFalseEmpty() {
        assertFalse(PasswordHasher.isHashed(""));
    }

    @Test
    public void testIsHashedFalsePartialPrefix() {
        assertFalse(PasswordHasher.isHashed("$2c$10$abc"));
    }

    @Test
    public void testHashAndVerifyLongPassword() {
        String longPassword = "a".repeat(100);
        String hash = PasswordHasher.hashPassword(longPassword);
        assertTrue(PasswordHasher.checkPassword(longPassword, hash));
    }

    @Test
    public void testHashAndVerifySpecialChars() {
        String special = "p@$$w0rd!#%^&*()";
        String hash = PasswordHasher.hashPassword(special);
        assertTrue(PasswordHasher.checkPassword(special, hash));
    }

    @Test
    public void testIsHashedRealHash() {
        String hash = PasswordHasher.hashPassword("test");
        assertTrue(PasswordHasher.isHashed(hash));
    }
}
