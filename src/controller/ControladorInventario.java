// Controller/ControladorInventario.java
package controller;

import model.Inventario;
import DAO.AccesoSistemaDAO;
import exception.ValidacionException;
import model.AccesoSistema;
import model.InventarioDAO;
import model.Producto;
import model.Venta;
import model.CarritoCompra;
import model.ItemCarrito;
import view.VentanaPrincipal;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.List;
import java.util.regex.Pattern;

@SuppressWarnings("unused")
public class ControladorInventario implements ActionListener, ListSelectionListener, DocumentListener {
	private static final Logger logger = LoggerFactory.getLogger(ControladorInventario.class);
    private VentanaPrincipal vista;
    private InventarioDAO modelo;
    private CarritoCompra carrito;
    private AccesoSistemaDAO accesoDAO;

    public ControladorInventario(VentanaPrincipal vista, InventarioDAO  modelo) {
    	if (vista == null) {
            throw new IllegalArgumentException("La vista no puede ser nula");
        }
        if (modelo == null) {
            throw new IllegalArgumentException("El modelo no puede ser nulo");
        }
    	this.vista = vista;
        this.modelo = modelo;
        this.carrito = new CarritoCompra();
        this.accesoDAO = new AccesoSistemaDAO(); 
        
        // Registrar listeners validandolos
        if(vista.panelProductos != null) {
        	this.vista.panelProductos.btnGuardarProducto.addActionListener(this);
            this.vista.panelProductos.btnNuevoProducto.addActionListener(this);
            this.vista.panelProductos.btnEliminarProducto.addActionListener(this);
        }
        if(vista.panelVentas != null) {
        	this.vista.panelVentas.btnAgregarAlCarrito.addActionListener(this);
            this.vista.panelVentas.btnEliminarDelCarrito.addActionListener(this);
            this.vista.panelVentas.btnProcesarVenta.addActionListener(this);
        }
        if(vista.panelReportes != null) {
        	this.vista.panelReportes.btnGenerarReporte.addActionListener(this);
        }
        
        this.vista.panelProductos.tablaProductos.getSelectionModel().addListSelectionListener(this);
        this.vista.panelProductos.txtBuscar.getDocument().addDocumentListener(this);
        
        this.vista.panelVentas.setCarrito(carrito);
        
       logger.info("ControladorInventario inicializado");
       logger.info("Productos en modelo: " + modelo.obtenerCantidadProductos());
        
        cargarProductosAlCombo();
        actualizarTablaProductos();
        actualizarTablaVentas();
        actualizarCarritoEnVista();
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        Object fuente = e.getSource();
        if (fuente == vista.panelProductos.btnGuardarProducto) {
            guardarProducto();
        } else if (fuente == vista.panelProductos.btnNuevoProducto) {
            limpiarFormularioProducto();
        } else if (fuente == vista.panelProductos.btnEliminarProducto) {
            eliminarProducto();
        } else if (fuente == vista.panelVentas.btnAgregarAlCarrito) {
            agregarProductoAlCarrito();
        } else if (fuente == vista.panelVentas.btnEliminarDelCarrito) {
            eliminarProductoDelCarrito();
        } else if (fuente == vista.panelVentas.btnProcesarVenta) {
            procesarVentaCarrito();
        } else if (fuente == vista.panelReportes.btnGenerarReporte) {
            generarReporte();
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting() && e.getSource() == vista.panelProductos.tablaProductos.getSelectionModel()) {
            int filaSeleccionada = vista.panelProductos.tablaProductos.getSelectedRow();
            if (filaSeleccionada != -1) {
                int filaModelo = vista.panelProductos.tablaProductos.convertRowIndexToModel(filaSeleccionada);
                String codigo = (String) vista.panelProductos.modeloTabla.getValueAt(filaModelo, 0);
                Producto producto = modelo.buscarProductoPorCodigo(codigo);
                if (producto != null) {
                    llenarFormularioProducto(producto);
                }
            }
        }
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        filtrarProductos();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        filtrarProductos();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        filtrarProductos();
    }
    
    private void guardarProducto() {
    	 try {
    	        // 1. Obtener datos del formulario
    	        DatosProducto datos = obtenerDatosDelFormulario();
    	        
    	        // 2. Validar datos básicos
    	        validarDatosBasicos(datos);
    	        
    	        // 3. Validar formato numérico
    	        validarFormatosNumericos(datos);
    	        
    	        // 4. Convertir datos numéricos
    	        Producto producto = convertirDatosAProducto(datos);
    	        
    	        // 5. Validar valores numéricos
    	        validarValoresNumericos(producto);
    	        
    	        // 6. Verificar existencia del producto
    	        Producto productoExistente = modelo.buscarProductoPorCodigo(datos.codigo);
    	        
    	        // 7. Guardar o actualizar producto
    	        if (esActualizacion(productoExistente)) {
    	            actualizarProductoExistente(productoExistente, producto);
    	        } else {
    	            crearNuevoProducto(producto, datos.codigo);
    	        }
    	        
    	        // 8. Actualizar interfaz
    	        actualizarInterfazDespuesDeGuardar();
    	        
    	    } catch (ValidacionException e) {
    	        // Mostrar mensaje de error al usuario
    	        JOptionPane.showMessageDialog(vista, e.getMessage(), "Error de Validación", JOptionPane.ERROR_MESSAGE);
    	    } catch (NumberFormatException e) {
    	        JOptionPane.showMessageDialog(vista, "Por favor ingrese valores numéricos válidos.", "Formato Inválido", JOptionPane.ERROR_MESSAGE);
    	    }
    }
    
    private static class DatosProducto{
    	String codigo, nombre, descripcion, stockStr, precioStr, stockMinimoStr;

		public DatosProducto(String codigo, String nombre, String descripcion, String stockStr, String precioStr,
				String stockMinimoStr) {
			super();
			this.codigo = codigo;
			this.nombre = nombre;
			this.descripcion = descripcion;
			this.stockStr = stockStr;
			this.precioStr = precioStr;
			this.stockMinimoStr = stockMinimoStr;
		}
    }
    
    private DatosProducto obtenerDatosDelFormulario() {
        return new DatosProducto(
            obtenerTextoSeguro(vista.panelProductos.txtCodigo),
            obtenerTextoSeguro(vista.panelProductos.txtNombre),
            vista.panelProductos.txtDescripcion.getText().trim(),
            obtenerTextoSeguro(vista.panelProductos.txtStock),
            obtenerTextoSeguro(vista.panelProductos.txtPrecio),
            obtenerTextoSeguro(vista.panelProductos.txtStockMinimo)
        );
    }

    // Método auxiliar para obtener texto seguro
    private String obtenerTextoSeguro(JTextField campo) {
        return (campo != null && campo.getText() != null) ? campo.getText().trim() : "";
    }
    
    private void validarDatosBasicos(DatosProducto datos) {
        if (datos.codigo.isEmpty() || datos.nombre.isEmpty() || 
            datos.stockStr.isEmpty() || datos.precioStr.isEmpty() || 
            datos.stockMinimoStr.isEmpty()) {
            throw new ValidacionException("Por favor complete todos los campos requeridos.");
        }
    }
    
    private void validarFormatosNumericos(DatosProducto datos) {
        // Limpiar el símbolo de dólar si está presente
        String precioLimpioStr = datos.precioStr.startsWith("$") ? 
                                datos.precioStr.substring(1) : datos.precioStr;
        
        if (!Pattern.matches("^\\d+(\\.\\d+)?$", precioLimpioStr)) {
            throw new ValidacionException("Por favor ingrese un valor numérico válido para el precio (ej: 10.50 o $10.50).");
        }
    }
    
    private Producto convertirDatosAProducto(DatosProducto datos) {
        try {
            String precioLimpioStr = datos.precioStr.startsWith("$") ? 
                                    datos.precioStr.substring(1) : datos.precioStr;
            
            int stock = Integer.parseInt(datos.stockStr);
            double precio = Double.parseDouble(precioLimpioStr);
            int stockMinimo = Integer.parseInt(datos.stockMinimoStr);
            
            return new Producto(datos.codigo, datos.nombre, datos.descripcion, 
                               stock, precio, stockMinimo);
            
        } catch (NumberFormatException e) {
            throw new ValidacionException("Por favor ingrese valores numéricos válidos.");
        }
    }
    
    private void validarValoresNumericos(Producto producto) {
        if (producto.getStock() < 0 || producto.getPrecio() < 0 || 
            producto.getStockMinimo() < 0) {
            throw new ValidacionException("El stock, precio y stock mínimo deben ser valores positivos.");
        }
    }
    
    private boolean esActualizacion(Producto productoExistente) {
        return productoExistente != null && !vista.panelProductos.txtCodigo.isEditable();
    }
    
    private void crearNuevoProducto(Producto producto, String codigo) {
        Producto productoExistente = modelo.buscarProductoPorCodigo(codigo);
        
        if (productoExistente != null && vista.panelProductos.txtCodigo.isEditable()) {
            throw new ValidacionException("Ya existe un producto con ese código.");
        }
        
        modelo.agregarProducto(producto);
        JOptionPane.showMessageDialog(vista, 
            "Producto guardado exitosamente en la base de datos.", 
            "Guardado Exitoso", JOptionPane.INFORMATION_MESSAGE);
    }

    private void actualizarProductoExistente(Producto productoExistente, Producto nuevosDatos) {
        productoExistente.setNombre(nuevosDatos.getNombre());
        productoExistente.setDescripcion(nuevosDatos.getDescripcion());
        productoExistente.setStock(nuevosDatos.getStock());
        productoExistente.setPrecio(nuevosDatos.getPrecio());
        productoExistente.setStockMinimo(nuevosDatos.getStockMinimo());
        
        // Nota: El InventarioDAO ya actualiza MongoDB automáticamente
        JOptionPane.showMessageDialog(vista, 
            "Producto actualizado exitosamente.", 
            "Actualización Exitosa", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void actualizarInterfazDespuesDeGuardar() {
        actualizarTablaProductos();
        cargarProductosAlCombo();
        limpiarFormularioProducto();
    }

    private void eliminarProducto() {
        int filaSeleccionada = vista.panelProductos.tablaProductos.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(vista, "Por favor seleccione un producto para eliminar.", "Producto No Seleccionado", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int filaModelo = vista.panelProductos.tablaProductos.convertRowIndexToModel(filaSeleccionada);
        String codigo = (String) vista.panelProductos.modeloTabla.getValueAt(filaModelo, 0);
        String nombre = (String) vista.panelProductos.modeloTabla.getValueAt(filaModelo, 1);
        
        int respuesta = JOptionPane.showConfirmDialog(vista, 
            "¿Está seguro de que desea eliminar el producto '" + nombre + "'?\n\n" +
            "Esta acción eliminará el producto de la base de datos MongoDB.", 
            "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);

        if (respuesta == JOptionPane.YES_OPTION) {
            boolean eliminado = modelo.eliminarProducto(codigo);
            if (eliminado) {
                JOptionPane.showMessageDialog(vista, 
                    "✅ Producto eliminado exitosamente de la base de datos.", 
                    "Eliminación Exitosa", JOptionPane.INFORMATION_MESSAGE);
                actualizarTablaProductos();
                cargarProductosAlCombo();
                limpiarFormularioProducto();
            } else {
                JOptionPane.showMessageDialog(vista, 
                    "❌ No se pudo eliminar el producto.", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
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
            JOptionPane.showMessageDialog(vista, 
                "Por favor seleccione un producto del listado.", 
                "Producto No Seleccionado", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Validación de cantidad
        if (cantidadStr.isEmpty()) {
            JOptionPane.showMessageDialog(vista, 
                "Por favor ingrese la cantidad deseada.", 
                "Cantidad Requerida", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int cantidad = Integer.parseInt(cantidadStr);
            if (cantidad <= 0) {
                JOptionPane.showMessageDialog(vista, 
                    "La cantidad debe ser mayor a cero.", 
                    "Cantidad Inválida", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            Producto producto = modelo.buscarProductoPorCodigo(codigoProducto);
            if (producto == null) {
                JOptionPane.showMessageDialog(vista, 
                    "Producto seleccionado no encontrado en el inventario.\nCódigo: " + codigoProducto, 
                    "Error de Producto", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Verificar disponibilidad de stock (considerando lo que ya está en el carrito)
            int cantidadEnCarrito = obtenerCantidadEnCarrito(codigoProducto);
            int stockDisponible = producto.getStock() - cantidadEnCarrito;
            
            if (stockDisponible < cantidad) {
                JOptionPane.showMessageDialog(vista, 
                    "Stock insuficiente.\nDisponible: " + producto.getStock() + 
                    "\nYa en carrito: " + cantidadEnCarrito +
                    "\nStock real disponible: " + stockDisponible, 
                    "Error de Stock", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Agregar al carrito
            carrito.agregarProducto(producto, cantidad);
            actualizarCarritoEnVista();
            
            JOptionPane.showMessageDialog(vista, 
                "✅ Producto agregado al carrito:\n" + 
                producto.getNombre() + " x " + cantidad + 
                "\nSubtotal: $" + String.format("%.2f", producto.getPrecio() * cantidad), 
                "Producto Agregado", JOptionPane.INFORMATION_MESSAGE);
            
            // Limpiar campo de cantidad después de agregar
            vista.panelVentas.txtCantidad.setText("");
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(vista, 
                "Por favor ingrese un número válido para la cantidad.", 
                "Formato Inválido", JOptionPane.ERROR_MESSAGE);
        }
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

    private void eliminarProductoDelCarrito() {
        int filaSeleccionada = vista.panelVentas.tablaCarrito.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(vista, 
                "Por favor seleccione un producto del carrito para eliminar.", 
                "Producto No Seleccionado", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int filaModelo = vista.panelVentas.tablaCarrito.convertRowIndexToModel(filaSeleccionada);
        String codigoProducto = (String) vista.panelVentas.modeloCarrito.getValueAt(filaModelo, 0);
        String nombreProducto = (String) vista.panelVentas.modeloCarrito.getValueAt(filaModelo, 1);
        int cantidad = (Integer) vista.panelVentas.modeloCarrito.getValueAt(filaModelo, 3);

        int respuesta = JOptionPane.showConfirmDialog(vista, 
            "¿Está seguro de que desea eliminar del carrito?\n" +
            "Producto: " + nombreProducto + "\n" +
            "Cantidad: " + cantidad, 
            "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);

        if (respuesta == JOptionPane.YES_OPTION) {
            carrito.eliminarProducto(codigoProducto);
            actualizarCarritoEnVista();
            JOptionPane.showMessageDialog(vista, 
                "Producto eliminado del carrito.", 
                "Eliminación Exitosa", JOptionPane.INFORMATION_MESSAGE);
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
            actualizarTablaProductos();
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

    private void actualizarCarritoEnVista() {
        logger.info("Actualizando carrito en vista...");
        vista.panelVentas.actualizarCarritoEnTabla();
        vista.panelVentas.revalidate();
        vista.panelVentas.repaint();
    }

    private void generarReporte() {
    	if (vista == null || vista.panelReportes == null) {
            System.err.println("⚠️ Componentes de vista no inicializados para generar reporte");
            return;
        }
        
        try {
            String tipoReporte = obtenerTipoReporteSeleccionado();
            StringBuilder reporte = generarContenidoReporte(tipoReporte);
            
            if (reporte != null) {
                mostrarReporteEnVista(reporte);
            } else {
                vista.panelReportes.txtReporte.setText("Error al generar el reporte.");
            }
        } catch (Exception e) {
            logger.error("Error al generar reporte: " + e.getMessage());
            if (vista.panelReportes.txtReporte != null) {
                vista.panelReportes.txtReporte.setText("Error: " + e.getMessage());
            }
        }
    }
    
    private String obtenerTipoReporteSeleccionado() {
    	if (vista == null || vista.panelReportes == null || vista.panelReportes.cmbTipoReporte == null) {
            return "Catálogo de Productos"; // Valor por defecto
        }
        
        Object selected = vista.panelReportes.cmbTipoReporte.getSelectedItem();
        return (selected != null) ? selected.toString() : "Catálogo de Productos";
    }
    
    private StringBuilder generarContenidoReporte(String tipoReporte) {
        switch (tipoReporte) {
            case "Reporte de Ventas":
                return generarReporteVentas();
                
            case "Alerta de Stock Bajo":
                return generarReporteStockBajo();
                
            case "Catálogo de Productos":
                return generarReporteCatalogo();
                
            case "Historial de Accesos":
                return generarReporteHistorialAccesos();
                
            default:
                return generarReporteNoEncontrado(tipoReporte);
        }
    }
    
    private StringBuilder generarReporteVentas() {
        StringBuilder reporte = new StringBuilder();
        reporte.append("=== REPORTE DE VENTAS ===\n");
        reporte.append("Fecha: ").append(LocalDate.now()).append("\n\n");
        
        double totalVentas = agregarDetalleVentas(reporte);
        agregarTotalVentas(reporte, totalVentas);
        
        return reporte;
    }
    
    private double agregarDetalleVentas(StringBuilder reporte) {
    	if (reporte == null || modelo == null) return 0;
        
        Venta[] ventas = modelo.obtenerTodasVentas();
        if (ventas == null) {
            reporte.append("No hay datos de ventas disponibles.\n");
            return 0;
        }
        
        double totalVentas = 0;
        for (Venta venta : ventas) {
            if (venta == null) continue;
            
            String nombre = venta.getNombreProducto() != null ? venta.getNombreProducto() : "Producto desconocido";
            int cantidad = venta.getCantidad();
            double total = venta.getTotal();
            String fecha = venta.getFecha() != null ? venta.getFecha() : "Fecha desconocida";
            
            String detalleVenta = String.format(
                "Producto: %s | Cant: %d | Total: $%.2f | Fecha: %s\n",
                nombre, cantidad, total, fecha
            );
            reporte.append(detalleVenta);
            totalVentas += total;
        }
        return totalVentas;
    }

    private void agregarTotalVentas(StringBuilder reporte, double totalVentas) {
        reporte.append(String.format("\nTOTAL VENTAS: $%.2f", totalVentas));
    }
    
    private StringBuilder generarReporteStockBajo() {
        StringBuilder reporte = new StringBuilder();
        reporte.append("=== ALERTA DE STOCK BAJO ===\n");
        reporte.append("Fecha: ").append(LocalDate.now()).append("\n\n");
        
        boolean tieneStockBajo = agregarProductosStockBajo(reporte);
        
        if (!tieneStockBajo) {
            reporte.append("No hay productos con stock bajo.");
        }
        
        return reporte;
    }
    
    private boolean agregarProductosStockBajo(StringBuilder reporte) {
        boolean tieneStockBajo = false;
        for (Producto producto : modelo.obtenerTodosProductos()) {
            if (esStockBajo(producto)) {
                String detalleProducto = String.format(
                    "Producto: %s | Stock Actual: %d | Mínimo: %d\n",
                    producto.getNombre(), producto.getStock(), producto.getStockMinimo()
                );
                reporte.append(detalleProducto);
                tieneStockBajo = true;
            }
        }
        return tieneStockBajo;
    }
    
    private boolean esStockBajo(Producto producto) {
        return producto.getStock() <= producto.getStockMinimo();
    }

    private StringBuilder generarReporteCatalogo() {
        StringBuilder reporte = new StringBuilder();
        reporte.append("=== CATÁLOGO DE PRODUCTOS ===\n");
        reporte.append("Fecha: ").append(LocalDate.now()).append("\n\n");
        
        agregarProductosAlCatalogo(reporte);
        
        return reporte;
    }
    
    private void agregarProductosAlCatalogo(StringBuilder reporte) {
        for (Producto producto : modelo.obtenerTodosProductos()) {
            String detalleProducto = String.format(
                "Código: %s | Nombre: %s | Stock: %d | Precio: $%.2f\n",
                producto.getCodigo(), producto.getNombre(), 
                producto.getStock(), producto.getPrecio()
            );
            reporte.append(detalleProducto);
        }
    }
    
    private StringBuilder generarReporteHistorialAccesos() {
        StringBuilder reporte = new StringBuilder();
        
        agregarEncabezadoHistorialAccesos(reporte);
        agregarEstadisticasAccesos(reporte);
        agregarDetalleAccesos(reporte);
        
        return reporte;
    }
    
    private void agregarEncabezadoHistorialAccesos(StringBuilder reporte) {
    	 if (reporte == null) return;
    	    
    	    reporte.append("=== HISTORIAL DE ACCESOS AL SISTEMA ===\n");
    	    reporte.append("Fecha del reporte: ").append(LocalDate.now()).append("\n");
    	    
    	    String usuarioActual = "Desconocido";
    	    try {
    	        usuarioActual = App.Main.getServicioAutenticacion().getUsuarioActual();
    	        if (usuarioActual == null) {
    	            usuarioActual = "Desconocido";
    	        }
    	    } catch (Exception e) {
    	        logger.error("Error obteniendo usuario actual: " + e.getMessage());
    	    }
    	    
    	    reporte.append("Generado por: ").append(usuarioActual).append("\n\n");
    }
    
    private void agregarEstadisticasAccesos(StringBuilder reporte) {
    	if (reporte == null) return;
        if (accesoDAO == null) {
            reporte.append("⚠️ No se pudo acceder a las estadísticas de accesos (DAO no inicializado)\n\n");
            return;
        }
        
        try {
            long totalAccesos = accesoDAO.contarAccesosTotales();
            long exitosos = accesoDAO.contarAccesosExitosos();
            long fallidos = accesoDAO.contarAccesosFallidos();
            double tasaExito = calcularTasaExito(totalAccesos, exitosos);
            
            reporte.append("📊 ESTADÍSTICAS GENERALES:\n");
            reporte.append(String.format("• Total de intentos de acceso: %d\n", totalAccesos));
            reporte.append(String.format("• Accesos exitosos: %d\n", exitosos));
            reporte.append(String.format("• Accesos fallidos: %d\n", fallidos));
            reporte.append(String.format("• Tasa de éxito: %.2f%%\n\n", tasaExito));
        } catch (Exception e) {
            reporte.append("⚠️ Error al obtener estadísticas de accesos: ").append(e.getMessage()).append("\n\n");
        }
    }
    
    private double calcularTasaExito(long totalAccesos, long exitosos) {
        return totalAccesos > 0 ? (exitosos * 100.0 / totalAccesos) : 0;
    }

    private void agregarDetalleAccesos(StringBuilder reporte) {
    	if (reporte == null) return;
        if (accesoDAO == null) {
            reporte.append("⚠️ No se pudo acceder al historial de accesos\n");
            return;
        }
        
        try {
            List<AccesoSistema> accesos = accesoDAO.obtenerTodosAccesos();
            
            reporte.append("📋 DETALLE DE ACCESOS (más recientes primero):\n");
            reporte.append("----------------------------------------------------------------------------------------\n");
            
            if (accesos == null || accesos.isEmpty()) {
                reporte.append("No hay registros de acceso en el sistema.\n");
                return;
            }
            
            int contador = 0;
            for (AccesoSistema acceso : accesos) {
                if (acceso == null) continue; // ⚠️ Saltar accesos nulos
                
                contador++;
                agregarDetalleAcceso(reporte, acceso, contador);
                
                if (contador >= 50) {
                    reporte.append("\n⚠️ Mostrando solo los 50 accesos más recientes\n");
                    break;
                }
            }
        } catch (Exception e) {
            reporte.append("⚠️ Error al obtener detalle de accesos: ").append(e.getMessage()).append("\n");
        }
    }

    private void agregarDetalleAcceso(StringBuilder reporte, AccesoSistema acceso, int numero) {
    	if (reporte == null || acceso == null) return;
        
        String tipoAcceso = acceso.getTipoAcceso();
        String icono = obtenerIconoAcceso(tipoAcceso != null ? tipoAcceso : "DESCONOCIDO");
        
        String fechaHora = acceso.getFechaHora() != null ? acceso.getFechaHora() : "Fecha desconocida";
        String usuario = acceso.getUsuario() != null ? acceso.getUsuario() : "Usuario desconocido";
        String rol = acceso.getRol() != null ? acceso.getRol() : "Rol desconocido";
        String mensaje = acceso.getMensaje() != null ? acceso.getMensaje() : "Sin mensaje";
        
        String cabeceraAcceso = String.format(
            "%s %d. [%s] Usuario: %-15s | Rol: %-15s\n",
            icono, numero, fechaHora, usuario, rol
        );
        reporte.append(cabeceraAcceso);
        
        String mensajeAcceso = String.format("   Mensaje: %s\n", mensaje);
        reporte.append(mensajeAcceso);
    }

    private String obtenerIconoAcceso(String tipoAcceso) {
    	if (tipoAcceso == null) {
            return "❓";
        }
        return tipoAcceso.equals("EXITOSO") ? "✅" : "❌";
    }
    
    private StringBuilder generarReporteNoEncontrado(String tipoReporte) {
        StringBuilder reporte = new StringBuilder();
        reporte.append("=== TIPO DE REPORTE NO ENCONTRADO ===\n");
        reporte.append("El tipo de reporte '").append(tipoReporte).append("' no está disponible.\n");
        reporte.append("Tipos disponibles:\n");
        reporte.append("- Reporte de Ventas\n");
        reporte.append("- Alerta de Stock Bajo\n");
        reporte.append("- Catálogo de Productos\n");
        reporte.append("- Historial de Accesos\n");
        return reporte;
    }
    
    private void mostrarReporteEnVista(StringBuilder reporte) {
    	if (vista == null || vista.panelReportes == null || vista.panelReportes.txtReporte == null) {
            logger.error("⚠️ Componentes de vista no disponibles para mostrar reporte");
            return;
        }
        
        if (reporte == null) {
            vista.panelReportes.txtReporte.setText("No se pudo generar el reporte.");
            return;
        }
        
        vista.panelReportes.txtReporte.setText(reporte.toString());
    }

    private void actualizarTablaProductos() {
        vista.panelProductos.modeloTabla.setRowCount(0);
        for (Producto producto : modelo.obtenerTodosProductos()) {
            Object[] datosFila = {
                producto.getCodigo(),
                producto.getNombre(),
                producto.getDescripcion(),
                producto.getStock(),
                String.format("$%.2f", producto.getPrecio()),
                producto.getStockMinimo(),
                producto.getStock() <= producto.getStockMinimo() ? "STOCK BAJO" : "OK"
            };
            vista.panelProductos.modeloTabla.addRow(datosFila);
        }
    }

    private void actualizarTablaVentas() {
        vista.panelVentas.modeloTabla.setRowCount(0);
        for (Venta venta : modelo.obtenerTodasVentas()) {
            Object[] datosFila = {
                venta.getCodigoProducto(),
                venta.getNombreProducto(),
                venta.getCantidad(),
                String.format("$%.2f", venta.getPrecioUnitario()),
                String.format("$%.2f", venta.getTotal()),
                venta.getFecha()
            };
            vista.panelVentas.modeloTabla.addRow(datosFila);
        }
    }

    private void cargarProductosAlCombo() {
        logger.info("Cargando productos al ComboBox...");
        
        vista.panelVentas.cmbProductos.removeAllItems();
        
        // Verificar si modelo es InventarioDAO
        if (modelo instanceof InventarioDAO) {
            InventarioDAO inventarioDAO = (InventarioDAO) modelo;
            int productosConStock = 0;
            
            // Obtener todos los productos
            Producto[] productos = inventarioDAO.obtenerTodosProductos();
            
            for (Producto producto : productos) {
                if (producto.getStock() > 0) {
                    vista.panelVentas.cmbProductos.addItem(producto.getCodigo());
                    productosConStock++;
                    logger.info("  Agregado: " + producto.getCodigo() + " - " + producto.getNombre());
                }
            }
            
            System.out.println("Total productos con stock agregados: " + productosConStock);
            
            if (productosConStock == 0) {
                logger.info("Advertencia: No hay productos con stock disponible");
                JOptionPane.showMessageDialog(vista, 
                    "No hay productos con stock disponible en la base de datos.\n" +
                    "Por favor agregue productos primero.", 
                    "Sin Stock", JOptionPane.WARNING_MESSAGE);
            }
        } else {
            logger.error("❌ Error: El modelo no es InventarioDAO");
        }
    }

    private void limpiarFormularioProducto() {
        vista.panelProductos.txtCodigo.setText("");
        vista.panelProductos.txtNombre.setText("");
        vista.panelProductos.txtDescripcion.setText("");
        vista.panelProductos.txtStock.setText("");
        vista.panelProductos.txtPrecio.setText("");
        vista.panelProductos.txtStockMinimo.setText("");
        vista.panelProductos.tablaProductos.clearSelection();
        vista.panelProductos.txtCodigo.setEditable(true);
    }

    private void llenarFormularioProducto(Producto producto) {
        vista.panelProductos.txtCodigo.setText(producto.getCodigo());
        vista.panelProductos.txtNombre.setText(producto.getNombre());
        vista.panelProductos.txtDescripcion.setText(producto.getDescripcion());
        vista.panelProductos.txtStock.setText(String.valueOf(producto.getStock()));
        vista.panelProductos.txtPrecio.setText(String.format("$%.2f", producto.getPrecio())); 
        vista.panelProductos.txtStockMinimo.setText(String.valueOf(producto.getStockMinimo()));
        vista.panelProductos.txtCodigo.setEditable(false);
    }

    private void filtrarProductos() {
        String consulta = vista.panelProductos.txtBuscar.getText();
        vista.panelProductos.filtrarTabla(consulta);
    }
}