package Modelo;

/**
 * Clase con constantes de estados de transacción
 */
public class EstadoTransaccion {
    public static final String REGISTRADO = "Registrado";
    public static final String APROBADO = "Aprobado";
    public static final String RECHAZADO = "Rechazado";
    public static final String ELIMINADO = "Eliminado";
    
    // Constructor privado para evitar instanciación
    private EstadoTransaccion() {
    }
}
