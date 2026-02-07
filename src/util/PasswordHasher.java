package util;

import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PasswordHasher {
	private static final Logger logger = LoggerFactory.getLogger(PasswordHasher.class);
    
    // Generar hash de contraseña
    public static String hashPassword(String password) {
        try {
            return BCrypt.hashpw(password, BCrypt.gensalt());
        } catch (Exception e) {
            logger.error("❌ Error hashing password: " + e.getMessage());
            return null;
        }
    }
    
    // Verificar contraseña
    public static boolean checkPassword(String password, String hashedPassword) {
        try {
            if (hashedPassword == null || hashedPassword.isEmpty()) {
                return false;
            }
            return BCrypt.checkpw(password, hashedPassword);
        } catch (Exception e) {
            logger.error("❌ Error verificando contraseña: " + e.getMessage());
            logger.error("🔍 Hash proporcionado: " + hashedPassword);
            return false;
        }
    }
    
    // Verificar si un string ya está hasheado (para migración)
    public static boolean isHashed(String password) {
        return password != null && 
               (password.startsWith("$2a$") || password.startsWith("$2b$") || password.startsWith("$2y$"));
    }
}