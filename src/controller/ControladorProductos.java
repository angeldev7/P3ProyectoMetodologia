package controller;

import view.VentanaPrincipal;
import model.InventarioDAO;
import model.Producto;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import exception.ValidacionException;

public class ControladorProductos implements ListSelectionListener, DocumentListener {
    private static final Logger logger = LoggerFactory.getLogger(ControladorProductos.class);
    private VentanaPrincipal vista;
    private InventarioDAO modelo;
    private static final String PRODUCTO_NO_SELECCIONADO = "Producto No Seleccionado";
    private static final String CONFIRMAR_ELIMINACION = "Confirmar Eliminación";
    private static final String ELIMINACION_EXITOSA = "Eliminación Exitosa";
    
    public ControladorProductos(VentanaPrincipal vista, InventarioDAO modelo) {
        this.vista = vista;
        this.modelo = modelo;
        
        // Registrar listeners específicos de productos
        this.vista.panelProductos.btnGuardarProducto.addActionListener(e -> guardarProducto());
        this.vista.panelProductos.btnNuevoProducto.addActionListener(e -> limpiarFormularioProducto());
        this.vista.panelProductos.btnEliminarProducto.addActionListener(e -> eliminarProducto());
        
        this.vista.panelProductos.tablaProductos.getSelectionModel().addListSelectionListener(this);
        this.vista.panelProductos.txtBuscar.getDocument().addDocumentListener(this);
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
    
    private DatosProducto obtenerDatosDelFormulario() {
        return new DatosProducto(
            vista.panelProductos.txtCodigo.getText().trim(),
            vista.panelProductos.txtNombre.getText().trim(),
            vista.panelProductos.txtDescripcion.getText().trim(),
            vista.panelProductos.txtStock.getText().trim(),
            vista.panelProductos.txtPrecio.getText().trim(),
            vista.panelProductos.txtStockMinimo.getText().trim()
        );
    }
    
    private void validarDatosBasicos(DatosProducto datos) throws ValidacionException {
        if (datos.codigo.isEmpty() || datos.nombre.isEmpty() || 
            datos.stockStr.isEmpty() || datos.precioStr.isEmpty() || 
            datos.stockMinimoStr.isEmpty()) {
            throw new ValidacionException("Por favor complete todos los campos requeridos.");
        }
    }
    
    private void validarFormatosNumericos(DatosProducto datos) throws ValidacionException {
        // Limpiar el símbolo de dólar si está presente
        String precioLimpioStr = datos.precioStr.startsWith("$") ? 
                                datos.precioStr.substring(1) : datos.precioStr;
        
        if (!precioLimpioStr.matches("^\\d+(\\.\\d+)?$")) {
            throw new ValidacionException("Por favor ingrese un valor numérico válido para el precio (ej: 10.50 o $10.50).");
        }
    }
    
    private Producto convertirDatosAProducto(DatosProducto datos) throws NumberFormatException {
        String precioLimpioStr = datos.precioStr.startsWith("$") ? 
                                datos.precioStr.substring(1) : datos.precioStr;
        
        int stock = Integer.parseInt(datos.stockStr);
        double precio = Double.parseDouble(precioLimpioStr);
        int stockMinimo = Integer.parseInt(datos.stockMinimoStr);
        
        return new Producto(datos.codigo, datos.nombre, datos.descripcion, 
                           stock, precio, stockMinimo);
    }
    
    private void validarValoresNumericos(Producto producto) throws ValidacionException {
        if (producto.getStock() < 0 || producto.getPrecio() < 0 || 
            producto.getStockMinimo() < 0) {
            throw new ValidacionException("El stock, precio y stock mínimo deben ser valores positivos.");
        }
    }
    
    private boolean esActualizacion(Producto productoExistente) {
        return productoExistente != null && !vista.panelProductos.txtCodigo.isEditable();
    }
    
    private void crearNuevoProducto(Producto producto, String codigo) throws ValidacionException {
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
    
    public void cargarProductosAlCombo() {
        // Este método se implementará según necesidad
        logger.info("Cargando productos al combo...");
    }
    
    public void actualizarTablaProductos() {
        vista.panelProductos.modeloTabla.setRowCount(0);
        
        // Crear array reutilizable FUERA del loop
        Object[] datosFila = new Object[7];
        
        for (Producto producto : modelo.obtenerTodosProductos()) {
            datosFila[0] = producto.getCodigo();
            datosFila[1] = producto.getNombre();
            datosFila[2] = producto.getDescripcion();
            datosFila[3] = producto.getStock();
            datosFila[4] = String.format("$%.2f", producto.getPrecio());
            datosFila[5] = producto.getStockMinimo();
            datosFila[6] = producto.getStock() <= producto.getStockMinimo() ? "STOCK BAJO" : "OK";
            
            vista.panelProductos.modeloTabla.addRow(datosFila);
        }
    }
    
    private void eliminarProducto() {
        int filaSeleccionada = vista.panelProductos.tablaProductos.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(vista, "Por favor seleccione un producto para eliminar.", PRODUCTO_NO_SELECCIONADO, JOptionPane.WARNING_MESSAGE);
            return;
        }

        int filaModelo = vista.panelProductos.tablaProductos.convertRowIndexToModel(filaSeleccionada);
        String codigo = (String) vista.panelProductos.modeloTabla.getValueAt(filaModelo, 0);
        String nombre = (String) vista.panelProductos.modeloTabla.getValueAt(filaModelo, 1);
        // Sanitizar para prevenir inyección de caracteres especiales
        nombre = sanitizarTexto(nombre);
        
        int respuesta = JOptionPane.showConfirmDialog(vista, 
                "¿Está seguro de que desea eliminar el producto '" + sanitizarTexto(nombre) + "'?\n\n" +
                "Esta acción eliminará el producto de la base de datos MongoDB.", 
                CONFIRMAR_ELIMINACION, JOptionPane.YES_NO_OPTION);

        if (respuesta == JOptionPane.YES_OPTION) {
            boolean eliminado = modelo.eliminarProducto(codigo);
            if (eliminado) {
                JOptionPane.showMessageDialog(vista, 
                    "✅ Producto eliminado exitosamente de la base de datos.", 
                    ELIMINACION_EXITOSA, JOptionPane.INFORMATION_MESSAGE);
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
    
    private String sanitizarTexto(String texto) {
        if (texto == null) return "";
        
        // Remover caracteres peligrosos para HTML/JavaScript
        return texto.replaceAll("[<>\"'&;]", "");
    }
    
    // Clase interna DatosProducto
    public static class DatosProducto {
        public String codigo, nombre, descripcion, stockStr, precioStr, stockMinimoStr;

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
}