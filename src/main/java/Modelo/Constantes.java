package Modelo;

public final class Constantes {
    
    // Constructor privado para evitar instanciacion
    private Constantes() {
        throw new UnsupportedOperationException("Clase de constantes no instanciable");
    }
    
    //CONSTANTES FISCALES
    public static final double IVA_ECUADOR = 0.15; // 15% IVA
    
    //VALIDACIONES
    public static final double MONTO_MINIMO_TRANSACCION = 0.01;
}
