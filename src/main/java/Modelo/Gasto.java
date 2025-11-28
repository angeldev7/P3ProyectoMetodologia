package Modelo;

import java.time.LocalDate;

/**
 * Clase que representa un Gasto o compra
 * Especialización de Transacción
 */
public class Gasto extends Transaccion {
    private static final long serialVersionUID = 1L;
    
    private String numeroComprobante;
    private boolean deducible;
    private double ivaCompra;
    
    public Gasto(String idTransaccion, LocalDate fecha, String proveedorCliente,
                double monto, String cuentaContable, String numeroComprobante,
                boolean deducible, Usuario usuarioRegistro) {
        super(idTransaccion, fecha, "Gasto", proveedorCliente, 
              monto, cuentaContable, numeroComprobante, usuarioRegistro);
        this.numeroComprobante = numeroComprobante;
        this.deducible = deducible;
        this.ivaCompra = calcularIVA();
    }
    
    @Override
    public double calcularIVA() {
        // El IVA en compras se calcula desde el total
        // Monto = Base + IVA, por lo tanto Base = Monto / (1 + IVA)
        double base = monto / (1 + Constantes.IVA_ECUADOR);
        return monto - base;
    }
    
    // Getters
    public String getNumeroComprobante() { return numeroComprobante; }
    public boolean isDeducible() { return deducible; }
    public double getIvaCompra() { return ivaCompra; }
    
    @Override
    public String toString() {
        return String.format("Gasto %s - %s - Total: $%.2f - IVA Compra: $%.2f %s", 
            numeroComprobante, proveedorCliente, monto, ivaCompra,
            deducible ? "(Deducible)" : "");
    }
}
