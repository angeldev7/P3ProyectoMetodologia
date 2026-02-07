// model/ItemCarrito.java
package model;

public class ItemCarrito {
    private Producto producto;
    private int cantidad;

    public ItemCarrito(Producto producto, int cantidad) {
        if (producto == null) {
            throw new IllegalArgumentException("El producto no puede ser null");
        }
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a 0");
        }
        this.producto = producto;
        this.cantidad = cantidad;
    }

    // Getters y Setters
    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { 
        if (producto == null) {
            throw new IllegalArgumentException("El producto no puede ser null");
        }
        this.producto = producto; 
    }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { 
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a 0");
        }
        this.cantidad = cantidad; 
    }

    public double getSubtotal() {
        if (producto == null) {
            return 0.0;
        }
        return producto.getPrecio() * cantidad;
    }

    public String getCodigoProducto() {
        return producto != null ? producto.getCodigo() : null;
    }

    public String getNombreProducto() {
        return producto != null ? producto.getNombre() : null;
    }

    public double getPrecioUnitario() {
        return producto != null ? producto.getPrecio() : 0.0;
    }
    
    @Override
    public String toString() {
        if (producto == null) {
            return "ItemCarrito{producto=null, cantidad=" + cantidad + "}";
        }
        return String.format("ItemCarrito{producto=%s, cantidad=%d, subtotal=%.2f}", 
                           producto.getNombre(), cantidad, getSubtotal());
    }
}