// model/InventarioDAO.java
package model;

import DAO.ProductoDAO;
import java.util.ArrayList;
import java.util.List;

public class InventarioDAO {
    private List<Producto> productos;
    private List<Venta> ventas;
    private ProductoDAO productoDAO;
    
    public InventarioDAO() {
        this.productos = new ArrayList<>();
        this.ventas = new ArrayList<>();
        this.productoDAO = new ProductoDAO();
        cargarProductosDesdeBD();
    }
    
    private void cargarProductosDesdeBD() {
        try {
            this.productos = productoDAO.obtenerTodosProductos();
            System.out.println("Productos cargados: " + productos.size());
            
            if (productos.isEmpty()) {
                System.out.println("No hay productos en la BD, agregando datos de ejemplo");
                inicializarDatosEjemplo();
            }
        } catch (Exception e) {
            System.err.println("Error al cargar productos: " + e.getMessage());
            inicializarDatosEjemplo();
        }
    }
    
    private void inicializarDatosEjemplo() {
        Producto[] productosEjemplo = {
            new Producto("P001", "Martillo", "Martillo de construcción de acero", 15, 12.50, 5),
            new Producto("P002", "Juego de Destornilladores", "Juego de 6 destornilladores", 8, 25.00, 3),
            new Producto("P003", "Clavos 2 pulgadas", "Caja de 100 clavos de 2 pulgadas", 20, 8.75, 10),
            new Producto("P004", "Brocha de Pintura", "Brocha de pintura de 3 pulgadas", 12, 15.30, 4)
        };
        
        for (Producto producto : productosEjemplo) {
            agregarProducto(producto);
        }
    }
    
    // Métodos modificados para sincronizar con MongoDB
    
    public void agregarProducto(Producto producto) {
        productos.add(producto);
        productoDAO.guardarProducto(producto);
    }
    
    public boolean eliminarProducto(String codigo) {
        boolean eliminadoLocal = productos.removeIf(producto -> producto.getCodigo().equals(codigo));
        
        if (eliminadoLocal) {
            productoDAO.eliminarProducto(codigo);
            return true;
        }
        return false;
    }
    
    public Producto buscarProductoPorCodigo(String codigo) {
        Producto producto = productoDAO.buscarProductoPorCodigo(codigo);
        
        if (producto == null) {
            producto = productos.stream()
                    .filter(p -> p.getCodigo().equals(codigo))
                    .findFirst()
                    .orElse(null);
        }
        
        return producto;
    }
    
    public Producto[] obtenerTodosProductos() {
        productos = productoDAO.obtenerTodosProductos();
        return productos.toArray(new Producto[0]);
    }
    
    // Métodos para ventas (sin cambios)
    public void agregarVenta(Venta venta) {
        ventas.add(venta);
    }
    
    public Venta[] obtenerTodasVentas() {
        return ventas.toArray(new Venta[0]);
    }
    
    // Métodos modificados para stock
    
    public void actualizarStock(String codigoProducto, int nuevaCantidad) {
        Producto producto = buscarProductoPorCodigo(codigoProducto);
        if (producto != null) {
            producto.setStock(nuevaCantidad);
            // Actualizar en MongoDB
            productoDAO.actualizarStock(codigoProducto, nuevaCantidad);
        }
    }
    
    public void incrementarStock(String codigoProducto, int cantidad) {
        Producto producto = buscarProductoPorCodigo(codigoProducto);
        if (producto != null) {
            int nuevoStock = producto.getStock() + cantidad;
            producto.setStock(nuevoStock);
            // Actualizar en MongoDB
            productoDAO.actualizarStock(codigoProducto, nuevoStock);
        }
    }
    
    public void decrementarStock(String codigoProducto, int cantidad) {
        Producto producto = buscarProductoPorCodigo(codigoProducto);
        if (producto != null && producto.getStock() >= cantidad) {
            int nuevoStock = producto.getStock() - cantidad;
            producto.setStock(nuevoStock);
            // Actualizar en MongoDB
            productoDAO.actualizarStock(codigoProducto, nuevoStock);
        }
    }
    
    // Métodos adicionales (sin cambios en la lógica)
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
    
    public double obtenerTotalVentas() {
        return ventas.stream().mapToDouble(Venta::getTotal).sum();
    }
    
    // Nuevo método para obtener productos con stock para ventas
    public List<Producto> obtenerProductosConStockParaVenta() {
        return productoDAO.obtenerProductosConStock();
    }
}