// model/ItemCarrito.java
package model;

public class ItemCarrito {
    private Producto producto;
    private int cantidad;

    public ItemCarrito(Producto producto, int cantidad) {
        this.producto = producto;
        this.cantidad = cantidad;
    }

    // Getters y Setters
    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { 
        this.cantidad = cantidad; 
    }

    public double getSubtotal() {
        return producto.getPrecio() * cantidad;
    }

    public String getCodigoProducto() {
        return producto.getCodigo();
    }

    public String getNombreProducto() {
        return producto.getNombre();
    }

    public double getPrecioUnitario() {
        return producto.getPrecio();
    }
}