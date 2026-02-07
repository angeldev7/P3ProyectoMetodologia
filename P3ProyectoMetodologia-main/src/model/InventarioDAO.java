// model/InventarioDAO.java
package model;

import DAO.ProductoDAO;
import java.util.ArrayList;
import java.util.List;

public class InventarioDAO {
    private List<Producto> productos;
    private List<Venta> ventas;
    private ProductoDAO productoDAO;
    
    // Constructor para testing con inyección de dependencias
    public InventarioDAO(ProductoDAO productoDAO) {
        this.productos = new ArrayList<>();
        this.ventas = new ArrayList<>();
        this.productoDAO = productoDAO;
        cargarProductosDesdeBD();
    }
    
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
            new Producto("P001", "Martillo", "Martillo de construcción de acero", 15, 12.50, 5, "A", "1", "2"),
            new Producto("P002", "Juego de Destornilladores", "Juego de 6 destornilladores", 8, 25.00, 3, "A", "2", "1"),
            new Producto("P003", "Clavos 2 pulgadas", "Caja de 100 clavos de 2 pulgadas", 20, 8.75, 10, "B", "1", "3"),
            new Producto("P004", "Brocha de Pintura", "Brocha de pintura de 3 pulgadas", 12, 15.30, 4, "B", "2", "1")
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
    
    // NUEVO: Métodos para búsqueda por ubicación
    public List<Producto> buscarProductosPorUbicacion(String pasillo, String estante, String posicion) {
        List<Producto> productosEncontrados = new ArrayList<>();
        
        for (Producto producto : productos) {
            boolean coincidenPasillo = pasillo == null || pasillo.trim().isEmpty() || 
                producto.getPasillo().toLowerCase().contains(pasillo.trim().toLowerCase());
            boolean coincidenEstante = estante == null || estante.trim().isEmpty() || 
                producto.getEstante().toLowerCase().contains(estante.trim().toLowerCase());
            boolean coincidenPosicion = posicion == null || posicion.trim().isEmpty() || 
                producto.getPosicion().toLowerCase().contains(posicion.trim().toLowerCase());
                
            if (coincidenPasillo && coincidenEstante && coincidenPosicion) {
                productosEncontrados.add(producto);
            }
        }
        
        return productosEncontrados;
    }
    
    // NUEVO: Método para obtener productos sin ubicación asignada
    public List<Producto> obtenerProductosSinUbicacion() {
        List<Producto> productosSinUbicacion = new ArrayList<>();
        for (Producto producto : productos) {
            if (!producto.tieneUbicacion()) {
                productosSinUbicacion.add(producto);
            }
        }
        return productosSinUbicacion;
    }
    
    // NUEVO: Método para obtener todas las ubicaciones únicas
    public List<String> obtenerUbicacionesUnicas() {
        List<String> ubicaciones = new ArrayList<>();
        for (Producto producto : productos) {
            if (producto.tieneUbicacion()) {
                String ubicacion = producto.getUbicacionCompleta();
                if (!ubicaciones.contains(ubicacion)) {
                    ubicaciones.add(ubicacion);
                }
            }
        }
        return ubicaciones;
    }
}