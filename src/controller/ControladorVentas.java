package controller;

import view.VentanaPrincipal;
import model.InventarioDAO;
import model.CarritoCompra;
import model.Producto;
import model.Venta;
import model.ItemCarrito;
import javax.swing.JOptionPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ControladorVentas {
    private static final Logger logger = LoggerFactory.getLogger(ControladorVentas.class);
    private VentanaPrincipal vista;
    private InventarioDAO modelo;
    private CarritoCompra carrito;
    private static final String PRODUCTO_NO_SELECCIONADO = "Producto No Seleccionado";
    private static final String CONFIRMAR_ELIMINACION = "Confirmar Eliminación";
    private static final String ELIMINACION_EXITOSA = "Eliminación Exitosa";

    public ControladorVentas(VentanaPrincipal vista, InventarioDAO modelo, CarritoCompra carrito) {
        this.vista = vista;
        this.modelo = modelo;
        this.carrito = carrito;

        // Registrar listeners específicos de ventas
        this.vista.panelVentas.btnAgregarAlCarrito.addActionListener(e -> agregarProductoAlCarrito());
        this.vista.panelVentas.btnEliminarDelCarrito.addActionListener(e -> eliminarProductoDelCarrito());
        this.vista.panelVentas.btnProcesarVenta.addActionListener(e -> procesarVentaCarrito());

        this.vista.panelVentas.setCarrito(carrito);
        
        // Cargar datos iniciales
        cargarProductosAlCombo();
        actualizarTablaVentas();
        actualizarCarritoEnVista();
    }

    private void agregarProductoAlCarrito() {
        if (vista == null || vista.panelVentas == null) {
            logger.error("⚠️ Componentes de ventas no inicializados");
            return;
        }

        String codigoProducto = obtenerProductoSeleccionadoSeguro();
        String cantidadStr = obtenerCantidadSegura();

        logger.info("Intentando agregar al carrito:");
        logger.info("  Código seleccionado: " + codigoProducto);
        logger.info("  Cantidad: " + cantidadStr);

        // Validación de producto seleccionado
        if (codigoProducto == null || codigoProducto.isEmpty()) {
            JOptionPane.showMessageDialog(vista, "Por favor seleccione un producto del listado.",
                    PRODUCTO_NO_SELECCIONADO, JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Validación de cantidad
        if (cantidadStr.isEmpty()) {
            JOptionPane.showMessageDialog(vista, "Por favor ingrese la cantidad deseada.", "Cantidad Requerida",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int cantidad = Integer.parseInt(cantidadStr);
            if (cantidad <= 0) {
                JOptionPane.showMessageDialog(vista, "La cantidad debe ser mayor a cero.", "Cantidad Inválida",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            Producto producto = modelo.buscarProductoPorCodigo(codigoProducto);
            if (producto == null) {
                JOptionPane.showMessageDialog(vista,
                        "Producto seleccionado no encontrado en el inventario.\nCódigo: " + codigoProducto,
                        "Error de Producto", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Verificar disponibilidad de stock (considerando lo que ya está en el carrito)
            int cantidadEnCarrito = obtenerCantidadEnCarrito(codigoProducto);
            int stockDisponible = producto.getStock() - cantidadEnCarrito;

            if (stockDisponible < cantidad) {
                JOptionPane.showMessageDialog(vista,
                        "Stock insuficiente.\nDisponible: " + producto.getStock() + "\nYa en carrito: "
                                + cantidadEnCarrito + "\nStock real disponible: " + stockDisponible,
                        "Error de Stock", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Agregar al carrito
            carrito.agregarProducto(producto, cantidad);
            actualizarCarritoEnVista();

            JOptionPane.showMessageDialog(vista,
                    "✅ Producto agregado al carrito:\n" + producto.getNombre() + " x " + cantidad + "\nSubtotal: $"
                            + String.format("%.2f", producto.getPrecio() * cantidad),
                    "Producto Agregado", JOptionPane.INFORMATION_MESSAGE);

            // Limpiar campo de cantidad después de agregar
            vista.panelVentas.txtCantidad.setText("");

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(vista, "Por favor ingrese un número válido para la cantidad.",
                    "Formato Inválido", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarProductoDelCarrito() {
        if (vista.panelVentas.tablaCarrito == null) {
            logger.error("Tabla del carrito no inicializada");
            return;
        }
        
        int filaSeleccionada = vista.panelVentas.tablaCarrito.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(vista, "Por favor seleccione un producto del carrito para eliminar.",
                    PRODUCTO_NO_SELECCIONADO, JOptionPane.WARNING_MESSAGE);
            return;
        }

        int filaModelo = vista.panelVentas.tablaCarrito.convertRowIndexToModel(filaSeleccionada);
        String codigoProducto = (String) vista.panelVentas.modeloCarrito.getValueAt(filaModelo, 0);
        String nombreProducto = sanitizarTexto((String) vista.panelVentas.modeloCarrito.getValueAt(filaModelo, 1));
        int cantidad = (Integer) vista.panelVentas.modeloCarrito.getValueAt(filaModelo, 3);

        int respuesta = JOptionPane.showConfirmDialog(vista, "¿Está seguro de que desea eliminar del carrito?\n"
                + "Producto: " + nombreProducto + "\n" + "Cantidad: " + cantidad, CONFIRMAR_ELIMINACION,
                JOptionPane.YES_NO_OPTION);

        if (respuesta == JOptionPane.YES_OPTION) {
            carrito.eliminarProducto(codigoProducto);
            actualizarCarritoEnVista();
            JOptionPane.showMessageDialog(vista, "Producto eliminado del carrito.", ELIMINACION_EXITOSA,
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void procesarVentaCarrito() {
        if (carrito.estaVacio()) {
            JOptionPane.showMessageDialog(vista, 
                "El carrito está vacío.\nAgregue productos antes de procesar la venta.", 
                "Carrito Vacío", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Verificar stock para todos los productos del carrito
        StringBuilder erroresStock = new StringBuilder();
        for (ItemCarrito item : carrito.getItems()) {
            Producto producto = modelo.buscarProductoPorCodigo(item.getCodigoProducto());
            if (producto == null) {
                erroresStock.append("❌ El producto '").append(item.getNombreProducto())
                           .append("' ya no está disponible.\n");
            } else if (producto.getStock() < item.getCantidad()) {
                erroresStock.append("❌ Stock insuficiente para '").append(item.getNombreProducto())
                           .append("'\n  Disponible: ").append(producto.getStock())
                           .append(" | Solicitado: ").append(item.getCantidad()).append("\n");
            }
        }

        if (erroresStock.length() > 0) {
            JOptionPane.showMessageDialog(vista, 
                "No se puede procesar la venta:\n\n" + erroresStock.toString(), 
                "Error de Stock", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Confirmar venta con resumen
        StringBuilder resumen = new StringBuilder();
        resumen.append("🎯 RESUMEN DE VENTA\n\n");
        resumen.append("Productos a vender:\n");
        
        for (ItemCarrito item : carrito.getItems()) {
            resumen.append(String.format("• %s: %d x $%.2f = $%.2f\n", 
                item.getNombreProducto(), 
                item.getCantidad(), 
                item.getPrecioUnitario(), 
                item.getSubtotal()));
        }
        
        resumen.append("\n────────────────────────────\n");
        resumen.append(String.format("TOTAL: $%.2f\n\n", carrito.getTotal()));
        resumen.append("¿Confirmar esta venta?");
        
        int respuesta = JOptionPane.showConfirmDialog(vista, 
            resumen.toString(), 
            "Confirmar Venta", 
            JOptionPane.YES_NO_OPTION, 
            JOptionPane.QUESTION_MESSAGE);

        if (respuesta == JOptionPane.YES_OPTION) {
            // Procesar cada venta individualmente
            for (ItemCarrito item : carrito.getItems()) {
                Producto producto = modelo.buscarProductoPorCodigo(item.getCodigoProducto());
                
                // Crear venta individual
                Venta venta = new Venta(
                    item.getCodigoProducto(),
                    item.getNombreProducto(),
                    item.getCantidad(),
                    item.getPrecioUnitario(),
                    item.getSubtotal()
                );
                modelo.agregarVenta(venta);
                
                producto.setStock(producto.getStock() - item.getCantidad());
            }
            
            JOptionPane.showMessageDialog(vista, 
                "✅ ¡VENTA PROCESADA EXITOSAMENTE!\n\n" +
                "📦 Productos vendidos: " + carrito.getCantidadItems() + "\n" +
                "💰 Total: $" + String.format("%.2f", carrito.getTotal()) + "\n" +
                "📝 Stock actualizado correctamente", 
                "Venta Completada", 
                JOptionPane.INFORMATION_MESSAGE);
           
            // Limpiar y actualizar
            carrito.limpiarCarrito();
            actualizarCarritoEnVista();
            actualizarTablaVentas();
            cargarProductosAlCombo();
            vista.panelVentas.txtCantidad.setText("");
        }
    }

    private int obtenerCantidadEnCarrito(String codigoProducto) {
        for (ItemCarrito item : carrito.getItems()) {
            if (item.getCodigoProducto().equals(codigoProducto)) {
                return item.getCantidad();
            }
        }
        return 0;
    }
    
    private String obtenerProductoSeleccionadoSeguro() {
        if (vista.panelVentas.cmbProductos == null) {
            return "";
        }
        Object selected = vista.panelVentas.cmbProductos.getSelectedItem();
        return (selected != null) ? selected.toString() : "";
    }

    private String obtenerCantidadSegura() {
        if (vista.panelVentas.txtCantidad == null) {
            return "";
        }
        String texto = vista.panelVentas.txtCantidad.getText();
        return (texto != null) ? texto.trim() : "";
    }
    
    public void actualizarCarritoEnVista() {
        logger.info("Actualizando carrito en vista...");
        if (vista.panelVentas != null) {
            vista.panelVentas.actualizarCarritoEnTabla();
            vista.panelVentas.revalidate();
            vista.panelVentas.repaint();
        }
    }
    
    public void actualizarTablaVentas() {
        if (vista.panelVentas == null || vista.panelVentas.modeloTabla == null) {
            logger.error("Componentes de vista de ventas no inicializados");
            return;
        }
        
        vista.panelVentas.modeloTabla.setRowCount(0);
        
        // Crear array reutilizable FUERA del loop
        Object[] datosFila = new Object[6];
        
        Venta[] ventas = modelo.obtenerTodasVentas();
        if (ventas != null) {
            for (Venta venta : ventas) {
                datosFila[0] = venta.getCodigoProducto();
                datosFila[1] = venta.getNombreProducto();
                datosFila[2] = venta.getCantidad();
                datosFila[3] = String.format("$%.2f", venta.getPrecioUnitario());
                datosFila[4] = String.format("$%.2f", venta.getTotal());
                datosFila[5] = venta.getFecha();
                
                vista.panelVentas.modeloTabla.addRow(datosFila);
            }
        }
    }
    
    public void cargarProductosAlCombo() {
        logger.info("Cargando productos al ComboBox...");
        
        if (vista.panelVentas == null || vista.panelVentas.cmbProductos == null) {
            logger.error("Componentes de vista de ventas no inicializados");
            return;
        }
        
        vista.panelVentas.cmbProductos.removeAllItems();
        
        // Obtener todos los productos
        Producto[] productos = modelo.obtenerTodosProductos();
        int productosConStock = 0;
        
        if (productos != null) {
            for (Producto producto : productos) {
                if (producto.getStock() > 0) {
                    vista.panelVentas.cmbProductos.addItem(producto.getCodigo());
                    productosConStock++;
                    logger.info("  Agregado: " + producto.getCodigo() + " - " + producto.getNombre());
                }
            }
        }
        
        logger.info("Total productos con stock agregados: " + productosConStock);
        
        if (productosConStock == 0) {
            logger.info("Advertencia: No hay productos con stock disponible");
            JOptionPane.showMessageDialog(vista, 
                "No hay productos con stock disponible en la base de datos.\n" +
                "Por favor agregue productos primero.", 
                "Sin Stock", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private String sanitizarTexto(String texto) {
        if (texto == null) return "";
        
        // Remover caracteres peligrosos para HTML/JavaScript
        return texto.replaceAll("[<>\"'&;]", "");
    }
}