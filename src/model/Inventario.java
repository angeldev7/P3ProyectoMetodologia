// Model/Inventario.java
package model;

import java.util.ArrayList;
import java.util.List;

public class Inventario {
    private List<Producto> productos;
    private List<Venta> ventas;

    public Inventario() {
        this.productos = new ArrayList<>();
        this.ventas = new ArrayList<>();
        // Agregar algunos datos de ejemplo para pruebas
        inicializarDatosEjemplo();
    }

    private void inicializarDatosEjemplo() {
        productos.add(new Producto("P001", "Martillo", "Martillo de construcción de acero", 15, 12.50, 5));
        productos.add(new Producto("P002", "Juego de Destornilladores", "Juego de 6 destornilladores", 8, 25.00, 3));
        productos.add(new Producto("P003", "Clavos 2 pulgadas", "Caja de 100 clavos de 2 pulgadas", 20, 8.75, 10));
        productos.add(new Producto("P004", "Brocha de Pintura", "Brocha de pintura de 3 pulgadas", 12, 15.30, 4));
    }

    // Gestión de productos
    public void agregarProducto(Producto producto) {
        productos.add(producto);
    }

    public boolean eliminarProducto(String codigo) {
        return productos.removeIf(producto -> producto.getCodigo().equals(codigo));
    }

    public Producto buscarProductoPorCodigo(String codigo) {
        return productos.stream()
                .filter(producto -> producto.getCodigo().equals(codigo))
                .findFirst()
                .orElse(null);
    }

    public Producto[] obtenerTodosProductos() {
        return productos.toArray(new Producto[0]);
    }

    // Gestión de ventas
    public void agregarVenta(Venta venta) {
        ventas.add(venta);
    }

    public Venta[] obtenerTodasVentas() {
        return ventas.toArray(new Venta[0]);
    }

    public double obtenerTotalVentas() {
        return ventas.stream().mapToDouble(Venta::getTotal).sum();
    }

    // Métodos adicionales útiles
    public boolean existeProducto(String codigo) {
        return buscarProductoPorCodigo(codigo) != null;
    }

    public int obtenerCantidadProductos() {
        return productos.size();
    }

    public int obtenerCantidadVentas() {
        return ventas.size();
    }

    public List<Producto> obtenerProductosBajoStockMinimo() {
        List<Producto> productosBajoStock = new ArrayList<>();
        for (Producto producto : productos) {
            if (producto.getStock() <= producto.getStockMinimo()) {
                productosBajoStock.add(producto);
            }
        }
        return productosBajoStock;
    }

    public void actualizarStock(String codigoProducto, int nuevaCantidad) {
        Producto producto = buscarProductoPorCodigo(codigoProducto);
        if (producto != null) {
            producto.setStock(nuevaCantidad);
        }
    }

    public void incrementarStock(String codigoProducto, int cantidad) {
        Producto producto = buscarProductoPorCodigo(codigoProducto);
        if (producto != null) {
            producto.setStock(producto.getStock() + cantidad);
        }
    }

    public void decrementarStock(String codigoProducto, int cantidad) {
        Producto producto = buscarProductoPorCodigo(codigoProducto);
        if (producto != null && producto.getStock() >= cantidad) {
            producto.setStock(producto.getStock() - cantidad);
        }
    }

    public List<Venta> obtenerVentasPorProducto(String codigoProducto) {
        List<Venta> ventasProducto = new ArrayList<>();
        for (Venta venta : ventas) {
            if (venta.getCodigoProducto().equals(codigoProducto)) {
                ventasProducto.add(venta);
            }
        }
        return ventasProducto;
    }

    public double calcularValorTotalInventario() {
        double valorTotal = 0;
        for (Producto producto : productos) {
            valorTotal += producto.getStock() * producto.getPrecio();
        }
        return valorTotal;
    }
}