// model/Venta.java
package model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Venta {
    private String codigoProducto;
    private String nombreProducto;
    private int cantidad;
    private double precioUnitario;
    private double total;
    private String fecha;

    public Venta(String codigoProducto, String nombreProducto, int cantidad, double precioUnitario, double total) {
        this.codigoProducto = codigoProducto;
        this.nombreProducto = nombreProducto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.total = total;
        this.fecha = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    // Getters
    public String getCodigoProducto() { return codigoProducto; }
    public String getNombreProducto() { return nombreProducto; }
    public int getCantidad() { return cantidad; }
    public double getPrecioUnitario() { return precioUnitario; }
    public double getTotal() { return total; }
    public String getFecha() { return fecha; }

    @Override
    public String toString() {
        return "Venta{codigo=" + codigoProducto + ", producto=" + nombreProducto + 
               ", cantidad=" + cantidad + ", precioUnitario=" + precioUnitario + 
               ", total=" + total + ", fecha=" + fecha + "}";
    }
}