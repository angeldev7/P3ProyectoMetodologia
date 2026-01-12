// model/CarritoCompra.java
package model;

import java.util.ArrayList;
import java.util.List;

public class CarritoCompra {
    private List<ItemCarrito> items;
    private double total;

    public CarritoCompra() {
        this.items = new ArrayList<>();
        this.total = 0.0;
    }

    public void agregarProducto(Producto producto, int cantidad) {
        // Verificar si el producto ya está en el carrito
        for (ItemCarrito item : items) {
            if (item.getProducto().getCodigo().equals(producto.getCodigo())) {
                item.setCantidad(item.getCantidad() + cantidad);
                calcularTotal();
                return;
            }
        }
        
        // Si no está, agregarlo como nuevo item
        items.add(new ItemCarrito(producto, cantidad));
        calcularTotal();
    }

    public void eliminarProducto(String codigoProducto) {
        items.removeIf(item -> item.getProducto().getCodigo().equals(codigoProducto));
        calcularTotal();
    }

    public void actualizarCantidad(String codigoProducto, int nuevaCantidad) {
        for (ItemCarrito item : items) {
            if (item.getProducto().getCodigo().equals(codigoProducto)) {
                if (nuevaCantidad <= 0) {
                    eliminarProducto(codigoProducto);
                } else {
                    item.setCantidad(nuevaCantidad);
                }
                break;
            }
        }
        calcularTotal();
    }

    public void limpiarCarrito() {
        items.clear();
        total = 0.0;
    }

    private void calcularTotal() {
        total = 0.0;
        for (ItemCarrito item : items) {
            total += item.getSubtotal();
        }
    }

    // Getters
    public List<ItemCarrito> getItems() { return items; }
    public double getTotal() { return total; }
    public int getCantidadItems() { return items.size(); }
    public boolean estaVacio() { return items.isEmpty(); }
}