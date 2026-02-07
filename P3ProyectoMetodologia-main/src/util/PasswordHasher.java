package util;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordHasher {
    
    // Generar hash de contraseña
    public static String hashPassword(String password) {
        // Manejar caso null
        if (password == null) {
            return null;
        }
        
        try {
            return BCrypt.hashpw(password, BCrypt.gensalt());
        } catch (Exception e) {
            System.err.println("Error hashing password: " + e.getMessage());
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
            System.err.println("Error verificando contraseña: " + e.getMessage());
            System.err.println("Hash proporcionado: " + hashedPassword);
            return false;
        }
    }
    
    // Verificar si un string ya está hasheado (para migración)
    public static boolean isHashed(String password) {
        return password != null && 
               (password.startsWith("$2a$") || password.startsWith("$2b$") || password.startsWith("$2y$"));
    }
}