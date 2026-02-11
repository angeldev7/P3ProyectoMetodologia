// model/ItemCarrito.java
package model;

public class ItemCarrito {
    private Producto producto;
    private int cantidad;

    public ItemCarrito(Producto producto, int cantidad) {
        if (producto == null) {
            throw new IllegalArgumentException("El producto no puede ser nulo");
        }
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a cero");
        }
        this.producto = producto;
        this.cantidad = cantidad;
    }

    // Getters y Setters
    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) {
        if (producto == null) {
            throw new IllegalArgumentException("El producto no puede ser nulo");
        }
        this.producto = producto;
    }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a cero");
        }
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

    @Override
    public String toString() {
        return "ItemCarrito{producto=" + producto.getNombre() + ", cantidad=" + cantidad + ", subtotal=" + getSubtotal() + "}";
    }
}