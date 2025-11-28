package Modelo;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Clase abstracta que representa una transacción contable
 * Fase: Implementación - Metodología Cascada
 */
public abstract class Transaccion implements Serializable {
    private static final long serialVersionUID = 1L;
    
    protected String idTransaccion; // ID legible (TRX-000001)
    protected LocalDate fecha;
    protected String tipoDocumento;
    protected String proveedorCliente;
    protected double monto;
    protected String cuentaContable;
    protected String numeroDocumento;
    protected String estado;
    protected Usuario usuarioRegistro;
    
    // Constructor
    public Transaccion(String idTransaccion, LocalDate fecha, String tipoDocumento,
                      String proveedorCliente, double monto, String cuentaContable,
                      String numeroDocumento, Usuario usuarioRegistro) {
        this.idTransaccion = idTransaccion;
        this.fecha = fecha;
        this.tipoDocumento = tipoDocumento;
        this.proveedorCliente = proveedorCliente;
        this.monto = monto;
        this.cuentaContable = cuentaContable;
        this.numeroDocumento = numeroDocumento;
        this.estado = EstadoTransaccion.REGISTRADO;
        this.usuarioRegistro = usuarioRegistro;
    }
    
    /**
     * Método abstracto para calcular el IVA
     */
    public abstract double calcularIVA();
    
    /**
     * Valida que la transacción tenga datos correctos
     */
    public boolean validar() {
        if (proveedorCliente == null || proveedorCliente.trim().isEmpty()) {
            return false;
        }
        if (monto < Constantes.MONTO_MINIMO_TRANSACCION) {
            return false;
        }
        if (fecha.isAfter(LocalDate.now())) {
            return false;
        }
        return true;
    }
    
    // Getters y Setters
    public String getIdTransaccion() { return idTransaccion; }
    public LocalDate getFecha() { return fecha; }
    public String getTipoDocumento() { return tipoDocumento; }
    public String getProveedorCliente() { return proveedorCliente; }
    public double getMonto() { return monto; }
    public String getCuentaContable() { return cuentaContable; }
    public String getNumeroDocumento() { return numeroDocumento; }
    public String getEstado() { return estado; }
    public Usuario getUsuarioRegistro() { return usuarioRegistro; }
    
    public void setEstado(String estado) { this.estado = estado; }
    
    @Override
    public String toString() {
        return String.format("%s - %s - $%.2f - %s", 
            fecha, proveedorCliente, monto, estado);
    }
}
