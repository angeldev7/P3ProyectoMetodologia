package exception;

public class ConexionBaseDatosException extends RuntimeException {
    public ConexionBaseDatosException(String message) {
        super(message);
    }
    
    public ConexionBaseDatosException(String message, Throwable cause) {
        super(message, cause);
    }
}