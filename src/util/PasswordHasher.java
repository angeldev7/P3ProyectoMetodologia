package util;

import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PasswordHasher {
	private static final Logger logger = LoggerFactory.getLogger(PasswordHasher.class);
    
    // Generar hash de contraseña
    public static String hashPassword(String password) {
        // Manejar caso null
        if (password == null) {
            return null;
        }
        
        try {
            return BCrypt.hashpw(password, BCrypt.gensalt());
        } catch (Exception e) {
<<<<<<< HEAD
            logger.error("❌ Error hashing password: " + e.getMessage());
=======
            System.err.println("Error hashing password: " + e.getMessage());
>>>>>>> origin/Test
            return null;
        }
    }
    
    // Verificar contraseña
    public static boolean checkPassword(String password, String hashedPassword) {
        // Manejar casos null
        if (password == null || hashedPassword == null || hashedPassword.isEmpty()) {
            return false;
        }
        
        try {
            return BCrypt.checkpw(password, hashedPassword);
        } catch (Exception e) {
<<<<<<< HEAD
            logger.error("❌ Error verificando contraseña: " + e.getMessage());
            logger.error("🔍 Hash proporcionado: " + hashedPassword);
=======
            System.err.println("Error verificando contraseña: " + e.getMessage());
            System.err.println("Hash proporcionado: " + hashedPassword);
>>>>>>> origin/Test
            return false;
        }
    }
    
    // Verificar si un string ya está hasheado (para migración)
    public static boolean isHashed(String password) {
        return password != null && 
               (password.startsWith("$2a$") || password.startsWith("$2b$") || password.startsWith("$2y$"));
    }
}