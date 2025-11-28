package Modelo;

import java.time.LocalDate;

/**
 * Clase que representa una Factura de venta
 * Especialización de Transacción
 */
public class Factura extends Transaccion {
    private static final long serialVersionUID = 1L;
    
    private String numeroFactura;
    private double subtotal;
    private double iva;
    
    public Factura(String idTransaccion, LocalDate fecha, String proveedorCliente,
                  double subtotal, String cuentaContable, String numeroFactura,
                  Usuario usuarioRegistro) {
        super(idTransaccion, fecha, "Factura", proveedorCliente, 
              subtotal, cuentaContable, numeroFactura, usuarioRegistro);
        this.numeroFactura = numeroFactura;
        this.subtotal = subtotal;
        this.iva = calcularIVA();
        this.monto = calcularTotal();
    }
    
    @Override
    public double calcularIVA() {
        return subtotal * Constantes.IVA_ECUADOR;
    }
    
    /**
     * Calcula el total de la factura (subtotal + IVA)
     */
    public double calcularTotal() {
        return subtotal + calcularIVA();
    }
    
    // Getters
    public String getNumeroFactura() { return numeroFactura; }
    public double getSubtotal() { return subtotal; }
    public double getIva() { return iva; }
    
    @Override
    public String toString() {
        return String.format("Factura %s - %s - Subtotal: $%.2f - IVA: $%.2f - Total: $%.2f", 
            numeroFactura, proveedorCliente, subtotal, iva, monto);
    }
}
