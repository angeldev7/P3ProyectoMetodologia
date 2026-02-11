// Controller/ControladorInventario.java
package controller;

import model.Inventario;
import DAO.AccesoSistemaDAO;
import model.AccesoSistema;
import model.InventarioDAO;
import model.Producto;
import model.Venta;
import model.CarritoCompra;
import model.ItemCarrito;
import view.VentanaPrincipal;
import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.List;
import java.util.regex.Pattern;

@SuppressWarnings("unused")
public class ControladorInventario implements ActionListener, ListSelectionListener, DocumentListener {
    private VentanaPrincipal vista;
    private InventarioDAO modelo;
    private CarritoCompra carrito;
    private AccesoSistemaDAO accesoDAO;

    // Constructor para testing con inyección de dependencias
    public ControladorInventario(VentanaPrincipal vista, InventarioDAO modelo, CarritoCompra carrito, AccesoSistemaDAO accesoDAO) {
        this.vista = vista;
        this.modelo = modelo;
        this.carrito = carrito;
        this.accesoDAO = accesoDAO;
    }

    public ControladorInventario(VentanaPrincipal vista, InventarioDAO  modelo) {
        this.vista = vista;
        this.modelo = modelo;
        this.carrito = new CarritoCompra();
        this.accesoDAO = new AccesoSistemaDAO(); 
        
        // Registrar listeners
        this.vista.panelProductos.btnGuardarProducto.addActionListener(this);
        this.vista.panelProductos.btnNuevoProducto.addActionListener(this);
        this.vista.panelProductos.btnEliminarProducto.addActionListener(this);
        this.vista.panelProductos.btnBuscarPorUbicacion.addActionListener(this); // NUEVO
        this.vista.panelVentas.btnAgregarAlCarrito.addActionListener(this);
        this.vista.panelVentas.btnEliminarDelCarrito.addActionListener(this);
        this.vista.panelVentas.btnProcesarVenta.addActionListener(this);
        this.vista.panelReportes.btnGenerarReporte.addActionListener(this);
        this.vista.panelProductos.tablaProductos.getSelectionModel().addListSelectionListener(this);
        this.vista.panelProductos.txtBuscar.getDocument().addDocumentListener(this);
        
        this.vista.panelVentas.setCarrito(carrito);
        
        System.out.println("ControladorInventario inicializado");
        System.out.println("Productos en modelo: " + modelo.obtenerCantidadProductos());
        
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
        } else if (fuente == vista.panelProductos.btnBuscarPorUbicacion) { // NUEVO
            buscarPorUbicacion();
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

    // Getters para testing
    public CarritoCompra getCarrito() { return carrito; }
    public InventarioDAO getModelo() { return modelo; }

    private void guardarProducto() {
    	String codigo = vista.panelProductos.txtCodigo.getText().trim();
        String nombre = vista.panelProductos.txtNombre.getText().trim();
        String descripcion = vista.panelProductos.txtDescripcion.getText().trim();
        String stockStr = vista.panelProductos.txtStock.getText().trim();
        String precioStr = vista.panelProductos.txtPrecio.getText().trim(); 
        String stockMinimoStr = vista.panelProductos.txtStockMinimo.getText().trim();
        // NUEVO: Obtener campos de ubicación
        String pasillo = vista.panelProductos.txtPasillo.getText().trim();
        String estante = vista.panelProductos.txtEstante.getText().trim();
        String posicion = vista.panelProductos.txtPosicion.getText().trim();

        // Validación de campos vacíos
        if (codigo.isEmpty() || nombre.isEmpty() || stockStr.isEmpty() || precioStr.isEmpty() || stockMinimoStr.isEmpty()) {
            JOptionPane.showMessageDialog(vista, "Por favor complete todos los campos requeridos.", "Campos Incompletos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Limpiar el símbolo de dólar si está presente
        String precioLimpioStr = precioStr.startsWith("$") ? precioStr.substring(1) : precioStr;
        
        // Validación de formato numérico para el precio
        if (!Pattern.matches("^\\d+(\\.\\d+)?$", precioLimpioStr)) {
            JOptionPane.showMessageDialog(vista, "Por favor ingrese un valor numérico válido para el precio (ej: 10.50 o $10.50).", "Formato de Precio Inválido", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int stock = Integer.parseInt(stockStr);
            double precio = Double.parseDouble(precioLimpioStr); 
            int stockMinimo = Integer.parseInt(stockMinimoStr);

            if (stock < 0 || precio < 0 || stockMinimo < 0) {
                JOptionPane.showMessageDialog(vista, "El stock, precio y stock mínimo deben ser valores positivos.", "Valores Inválidos", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Producto productoExistente = modelo.buscarProductoPorCodigo(codigo);
            
            if (productoExistente != null && vista.panelProductos.txtCodigo.isEditable()) {
                JOptionPane.showMessageDialog(vista, "Ya existe un producto con ese código.", "Código Duplicado", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (productoExistente != null && !vista.panelProductos.txtCodigo.isEditable()) {
                // Actualizar producto existente - MongoDB se actualiza automáticamente
                productoExistente.setNombre(nombre);
                productoExistente.setDescripcion(descripcion);
                productoExistente.setStock(stock);
                productoExistente.setPrecio(precio);
                productoExistente.setStockMinimo(stockMinimo);
                // NUEVO: Actualizar campos de ubicación
                productoExistente.setPasillo(pasillo);
                productoExistente.setEstante(estante);
                productoExistente.setPosicion(posicion);
                
                // Nota: El InventarioDAO ya actualiza MongoDB automáticamente
                JOptionPane.showMessageDialog(vista, 
                    "Producto actualizado exitosamente.\nUbicación: " + productoExistente.getUbicacionCompleta(), 
                    "Actualización Exitosa", JOptionPane.INFORMATION_MESSAGE);
            } else {
                // Crear nuevo producto con ubicación - MongoDB se guarda automáticamente
                Producto nuevoProducto = new Producto(codigo, nombre, descripcion, stock, precio, stockMinimo, pasillo, estante, posicion);
                modelo.agregarProducto(nuevoProducto);
                JOptionPane.showMessageDialog(vista, 
                    "Producto guardado exitosamente en la base de datos.\nUbicación asignada: " + nuevoProducto.getUbicacionCompleta(), 
                    "Guardado Exitoso", JOptionPane.INFORMATION_MESSAGE);
            }
            
            actualizarTablaProductos();
            cargarProductosAlCombo();
            limpiarFormularioProducto();
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(vista, "Por favor ingrese valores numéricos válidos.", "Formato Inválido", JOptionPane.ERROR_MESSAGE);
        }
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
                    "Producto eliminado exitosamente de la base de datos.", 
                    "Eliminación Exitosa", JOptionPane.INFORMATION_MESSAGE);
                actualizarTablaProductos();
                cargarProductosAlCombo();
                limpiarFormularioProducto();
            } else {
                JOptionPane.showMessageDialog(vista, 
                    "No se pudo eliminar el producto.", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    private void agregarProductoAlCarrito() {
        String codigoProducto = (String) vista.panelVentas.cmbProductos.getSelectedItem();
        String cantidadStr = vista.panelVentas.txtCantidad.getText().trim();

        System.out.println("Intentando agregar al carrito:");
        System.out.println("  Código seleccionado: " + codigoProducto);
        System.out.println("  Cantidad: " + cantidadStr);

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
                "Producto agregado al carrito:\n" + 
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
                erroresStock.append("El producto '").append(item.getNombreProducto())
                           .append("' ya no está disponible.\n");
            } else if (producto.getStock() < item.getCantidad()) {
                erroresStock.append("Stock insuficiente para '").append(item.getNombreProducto())
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
        resumen.append("RESUMEN DE VENTA\n\n");
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
                "¡VENTA PROCESADA EXITOSAMENTE!\n\n" +
                "Productos vendidos: " + carrito.getCantidadItems() + "\n" +
                "Total: $" + String.format("%.2f", carrito.getTotal()) + "\n" +
                "Stock actualizado correctamente", 
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
        System.out.println("Actualizando carrito en vista...");
        vista.panelVentas.actualizarCarritoEnTabla();
        vista.panelVentas.revalidate();
        vista.panelVentas.repaint();
    }

    private void generarReporte() {
        String tipoReporte = (String) vista.panelReportes.cmbTipoReporte.getSelectedItem();
        StringBuilder reporte = new StringBuilder();
        
        switch (tipoReporte) {
            case "Reporte de Ventas":
                reporte.append("=== REPORTE DE VENTAS ===\n");
                reporte.append("Fecha: ").append(LocalDate.now()).append("\n\n");
                double totalVentas = 0;
                for (Venta venta : modelo.obtenerTodasVentas()) {
                    reporte.append(String.format("Producto: %s | Cant: %d | Total: $%.2f | Fecha: %s\n", 
                        venta.getNombreProducto(), venta.getCantidad(), venta.getTotal(), venta.getFecha()));
                    totalVentas += venta.getTotal();
                }
                reporte.append(String.format("\nTOTAL VENTAS: $%.2f", totalVentas));
                break;
                
            case "Alerta de Stock Bajo":
                reporte.append("=== ALERTA DE STOCK BAJO ===\n");
                reporte.append("Fecha: ").append(LocalDate.now()).append("\n\n");
                boolean tieneStockBajo = false;
                for (Producto producto : modelo.obtenerTodosProductos()) {
                    if (producto.getStock() <= producto.getStockMinimo()) {
                        reporte.append(String.format("Producto: %s | Stock Actual: %d | Mínimo: %d\n", 
                            producto.getNombre(), producto.getStock(), producto.getStockMinimo()));
                        tieneStockBajo = true;
                    }
                }
                if (!tieneStockBajo) {
                    reporte.append("No hay productos con stock bajo.");
                }
                break;
                
            case "Catálogo de Productos":
                reporte.append("=== CATÁLOGO DE PRODUCTOS ===\n");
                reporte.append("Fecha: ").append(LocalDate.now()).append("\n\n");
                for (Producto producto : modelo.obtenerTodosProductos()) {
                    reporte.append(String.format("Código: %s | Nombre: %s | Stock: %d | Precio: $%.2f\n", 
                        producto.getCodigo(), producto.getNombre(), producto.getStock(), producto.getPrecio()));
                }
                break;
                
            // NUEVO CASO: HISTORIAL DE ACCESOS
            case "Historial de Accesos":
                reporte.append("=== HISTORIAL DE ACCESOS AL SISTEMA ===\n");
                reporte.append("Fecha del reporte: ").append(LocalDate.now()).append("\n");
                reporte.append("Generado por: ").append(App.Main.getServicioAutenticacion().getUsuarioActual()).append("\n\n");
                
                List<AccesoSistema> accesos = accesoDAO.obtenerTodosAccesos();
                
                // Estadísticas
                long totalAccesos = accesoDAO.contarAccesosTotales();
                long exitosos = accesoDAO.contarAccesosExitosos();
                long fallidos = accesoDAO.contarAccesosFallidos();
                
                reporte.append("ESTADÍSTICAS GENERALES:\n");
                reporte.append(String.format("• Total de intentos de acceso: %d\n", totalAccesos));
                reporte.append(String.format("• Accesos exitosos: %d\n", exitosos));
                reporte.append(String.format("• Accesos fallidos: %d\n", fallidos));
                reporte.append(String.format("• Tasa de éxito: %.2f%%\n\n", 
                    totalAccesos > 0 ? (exitosos * 100.0 / totalAccesos) : 0));
                
                reporte.append("DETALLE DE ACCESOS (más recientes primero):\n");
                reporte.append("----------------------------------------------------------------------------------------\n");
                
                int contador = 0;
                for (AccesoSistema acceso : accesos) {
                    contador++;
                    String icono = acceso.getTipoAcceso().equals("EXITOSO") ? "[OK]" : "[ERROR]";
                    reporte.append(String.format("%s %d. [%s] Usuario: %-15s | Rol: %-15s\n", 
                        icono, contador, acceso.getFechaHora(), acceso.getUsuario(), acceso.getRol()));
                    reporte.append(String.format("   Mensaje: %s\n", acceso.getMensaje()));
                    
                    // Mostrar solo los últimos 50 accesos para no saturar el reporte
                    if (contador >= 50) {
                        reporte.append("\nMostrando solo los 50 accesos más recientes\n");
                        break;
                    }
                }
                
                if (accesos.isEmpty()) {
                    reporte.append("No hay registros de acceso en el sistema.\n");
                }
                break;
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
                producto.getUbicacionCompleta(), // NUEVO: Columna de ubicación
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
        System.out.println("Cargando productos al ComboBox...");
        
        vista.panelVentas.cmbProductos.removeAllItems();
        
        // Como modelo ya es InventarioDAO, no necesitamos verificación
        int productosConStock = 0;
        
        // Obtener todos los productos
        Producto[] productos = modelo.obtenerTodosProductos();
        
        for (Producto producto : productos) {
            if (producto.getStock() > 0) {
                vista.panelVentas.cmbProductos.addItem(producto.getCodigo());
                productosConStock++;
                System.out.println("  Agregado: " + producto.getCodigo() + " - " + producto.getNombre());
            }
        }
        
        System.out.println("Total productos con stock agregados: " + productosConStock);
        
        if (productosConStock == 0) {
            System.out.println("Advertencia: No hay productos con stock disponible");
            JOptionPane.showMessageDialog(vista, 
                "No hay productos con stock disponible en la base de datos.\n" +
                "Por favor agregue productos primero.", 
                "Sin Stock", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void limpiarFormularioProducto() {
        vista.panelProductos.txtCodigo.setText("");
        vista.panelProductos.txtNombre.setText("");
        vista.panelProductos.txtDescripcion.setText("");
        vista.panelProductos.txtStock.setText("");
        vista.panelProductos.txtPrecio.setText("");
        vista.panelProductos.txtStockMinimo.setText("");
        // NUEVO: Limpiar campos de ubicación
        vista.panelProductos.txtPasillo.setText("");
        vista.panelProductos.txtEstante.setText("");
        vista.panelProductos.txtPosicion.setText("");
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
        // NUEVO: Llenar campos de ubicación
        vista.panelProductos.txtPasillo.setText(producto.getPasillo());
        vista.panelProductos.txtEstante.setText(producto.getEstante());
        vista.panelProductos.txtPosicion.setText(producto.getPosicion());
        vista.panelProductos.txtCodigo.setEditable(false);
    }

    private void filtrarProductos() {
        String consulta = vista.panelProductos.txtBuscar.getText();
        vista.panelProductos.filtrarTabla(consulta);
    }
    
    // NUEVO: Método para buscar por ubicación
    private void buscarPorUbicacion() {
        String pasillo = JOptionPane.showInputDialog(vista, 
            "Ingrese el pasillo a buscar (deje vacío para cualquier pasillo):", 
            "Búsqueda por Ubicación", 
            JOptionPane.QUESTION_MESSAGE);
        
        // Si el usuario cancela, pasillo será null
        if (pasillo == null) {
            return;
        }
        
        String estante = JOptionPane.showInputDialog(vista, 
            "Ingrese el estante a buscar (deje vacío para cualquier estante):", 
            "Búsqueda por Ubicación", 
            JOptionPane.QUESTION_MESSAGE);
            
        if (estante == null) {
            return;
        }
        
        String posicion = JOptionPane.showInputDialog(vista, 
            "Ingrese la posición a buscar (deje vacío para cualquier posición):", 
            "Búsqueda por Ubicación", 
            JOptionPane.QUESTION_MESSAGE);
            
        if (posicion == null) {
            return;
        }
        
        // Buscar productos que coincidan
        java.util.List<Producto> productosEncontrados = new java.util.ArrayList<>();
        
        for (Producto producto : modelo.obtenerTodosProductos()) {
            boolean coincidenPasillo = pasillo.trim().isEmpty() || 
                producto.getPasillo().toLowerCase().contains(pasillo.trim().toLowerCase());
            boolean coincidenEstante = estante.trim().isEmpty() || 
                producto.getEstante().toLowerCase().contains(estante.trim().toLowerCase());
            boolean coincidenPosicion = posicion.trim().isEmpty() || 
                producto.getPosicion().toLowerCase().contains(posicion.trim().toLowerCase());
                
            if (coincidenPasillo && coincidenEstante && coincidenPosicion) {
                productosEncontrados.add(producto);
            }
        }
        
        // Mostrar resultados
        if (productosEncontrados.isEmpty()) {
            JOptionPane.showMessageDialog(vista, 
                "No se encontraron productos en la ubicación especificada.\n" +
                "Pasillo: " + (pasillo.trim().isEmpty() ? "[Cualquiera]" : pasillo) + "\n" +
                "Estante: " + (estante.trim().isEmpty() ? "[Cualquiera]" : estante) + "\n" +
                "Posición: " + (posicion.trim().isEmpty() ? "[Cualquiera]" : posicion),
                "Sin Resultados", 
                JOptionPane.INFORMATION_MESSAGE);
        } else {
            // Filtrar la tabla para mostrar solo los productos encontrados
            vista.panelProductos.modeloTabla.setRowCount(0);
            
            for (Producto producto : productosEncontrados) {
                Object[] datosFila = {
                    producto.getCodigo(),
                    producto.getNombre(),
                    producto.getDescripcion(),
                    producto.getStock(),
                    String.format("$%.2f", producto.getPrecio()),
                    producto.getStockMinimo(),
                    producto.getUbicacionCompleta(),
                    producto.getStock() <= producto.getStockMinimo() ? "STOCK BAJO" : "OK"
                };
                vista.panelProductos.modeloTabla.addRow(datosFila);
            }
            
            JOptionPane.showMessageDialog(vista, 
                "Se encontraron " + productosEncontrados.size() + " producto(s) en la ubicación especificada.\n" +
                "Pasillo: " + (pasillo.trim().isEmpty() ? "[Cualquiera]" : pasillo) + "\n" +
                "Estante: " + (estante.trim().isEmpty() ? "[Cualquiera]" : estante) + "\n" +
                "Posición: " + (posicion.trim().isEmpty() ? "[Cualquiera]" : posicion) + "\n\n" +
                "Para ver todos los productos nuevamente, use el botón 'Nuevo Producto'.",
                "Productos Encontrados", 
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
}