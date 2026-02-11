// controller/GestorInventario.java (Clase coordinadora)
package controller;

import view.VentanaPrincipal;
import model.InventarioDAO;
import model.CarritoCompra;
import DAO.AccesoSistemaDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GestorInventario {
    private static final Logger logger = LoggerFactory.getLogger(GestorInventario.class);
    private VentanaPrincipal vista;
    private InventarioDAO modelo;
    private CarritoCompra carrito;
    private AccesoSistemaDAO accesoDAO;
    
    private ControladorProductos controladorProductos;
    private ControladorVentas controladorVentas;
    private ControladorReportes controladorReportes;
    
    public GestorInventario(VentanaPrincipal vista, InventarioDAO modelo) {
        this.vista = vista;
        this.modelo = modelo;
        this.carrito = new CarritoCompra();
        this.accesoDAO = new AccesoSistemaDAO();
        
        // Verificar que los componentes necesarios estén inicializados
        if (vista == null) {
            logger.error("La vista es nula en GestorInventario");
            throw new IllegalArgumentException("La vista no puede ser nula");
        }
        
        if (modelo == null) {
            logger.error("El modelo es nulo en GestorInventario");
            throw new IllegalArgumentException("El modelo no puede ser nulo");
        }
        
        logger.info("Inicializando GestorInventario...");
        
        // Inicializar controladores específicos
        try {
            this.controladorProductos = new ControladorProductos(vista, modelo);
            logger.info("ControladorProductos inicializado");
        } catch (Exception e) {
            logger.error("Error al inicializar ControladorProductos: " + e.getMessage());
            this.controladorProductos = null;
        }
        
        try {
            this.controladorVentas = new ControladorVentas(vista, modelo, carrito);
            logger.info("ControladorVentas inicializado");
        } catch (Exception e) {
            logger.error("Error al inicializar ControladorVentas: " + e.getMessage());
            this.controladorVentas = null;
        }
        
        try {
            this.controladorReportes = new ControladorReportes(vista, modelo, accesoDAO);
            logger.info("ControladorReportes inicializado");
        } catch (Exception e) {
            logger.error("Error al inicializar ControladorReportes: " + e.getMessage());
            this.controladorReportes = null;
        }
        
        // Cargar datos iniciales
        try {
            cargarDatosIniciales();
            logger.info("Datos iniciales cargados exitosamente");
        } catch (Exception e) {
            logger.error("Error al cargar datos iniciales: " + e.getMessage());
        }
        
        logger.info("GestorInventario inicializado completamente");
        logger.info("Productos en modelo: " + modelo.obtenerCantidadProductos());
    }
    
    private void cargarDatosIniciales() {
        logger.info("Cargando datos iniciales...");
        
        // Cargar productos
        if (controladorProductos != null) {
            try {
                controladorProductos.actualizarTablaProductos();
                logger.info("Tabla de productos actualizada");
            } catch (Exception e) {
                logger.error("Error al actualizar tabla de productos: " + e.getMessage());
            }
            
            try {
                controladorProductos.cargarProductosAlCombo();
                logger.info("Productos cargados al combo");
            } catch (Exception e) {
                logger.error("Error al cargar productos al combo: " + e.getMessage());
            }
        } else {
            logger.warn("ControladorProductos no está inicializado");
        }
        
        // Cargar ventas
        if (controladorVentas != null) {
            try {
                controladorVentas.actualizarTablaVentas();
                logger.info("Tabla de ventas actualizada");
            } catch (Exception e) {
                logger.error("Error al actualizar tabla de ventas: " + e.getMessage());
            }
            
            try {
                controladorVentas.cargarProductosAlCombo();
                logger.info("Productos cargados al combo de ventas");
            } catch (Exception e) {
                logger.error("Error al cargar productos al combo de ventas: " + e.getMessage());
            }
            
            try {
                controladorVentas.actualizarCarritoEnVista();
                logger.info("Carrito actualizado en vista");
            } catch (Exception e) {
                logger.error("Error al actualizar carrito: " + e.getMessage());
            }
        } else {
            logger.warn("ControladorVentas no está inicializado");
        }
        
        logger.info("Carga de datos iniciales completada");
    }
    
    // Métodos para acceso externo si es necesario
    public ControladorProductos getControladorProductos() {
        return controladorProductos;
    }
    
    public ControladorVentas getControladorVentas() {
        return controladorVentas;
    }
    
    public ControladorReportes getControladorReportes() {
        return controladorReportes;
    }
    
    public CarritoCompra getCarrito() {
        return carrito;
    }
    
    public InventarioDAO getModelo() {
        return modelo;
    }
    
    public VentanaPrincipal getVista() {
        return vista;
    }
    
    public void actualizarTodo() {
        logger.info("Actualizando todos los componentes...");
        
        // Actualizar productos
        if (controladorProductos != null) {
            try {
                controladorProductos.actualizarTablaProductos();
            } catch (Exception e) {
                logger.error("Error al actualizar tabla de productos: " + e.getMessage());
            }
        }
        
        // Actualizar ventas
        if (controladorVentas != null) {
            try {
                controladorVentas.actualizarTablaVentas();
            } catch (Exception e) {
                logger.error("Error al actualizar tabla de ventas: " + e.getMessage());
            }
            
            try {
                controladorVentas.cargarProductosAlCombo();
            } catch (Exception e) {
                logger.error("Error al cargar productos al combo: " + e.getMessage());
            }
            
            try {
                controladorVentas.actualizarCarritoEnVista();
            } catch (Exception e) {
                logger.error("Error al actualizar carrito: " + e.getMessage());
            }
        }
        
        logger.info("Actualización completa");
    }
    
    // Método para verificar el estado de los controladores
    public String obtenerEstadoControladores() {
        StringBuilder estado = new StringBuilder();
        estado.append("=== ESTADO DE CONTROLADORES ===\n");
        estado.append("ControladorProductos: ").append(controladorProductos != null ? "✅ Activo" : "❌ Inactivo").append("\n");
        estado.append("ControladorVentas: ").append(controladorVentas != null ? "✅ Activo" : "❌ Inactivo").append("\n");
        estado.append("ControladorReportes: ").append(controladorReportes != null ? "✅ Activo" : "❌ Inactivo").append("\n");
        estado.append("Carrito: ").append(carrito != null ? "✅ Inicializado" : "❌ No inicializado").append("\n");
        estado.append("Productos en modelo: ").append(modelo.obtenerCantidadProductos()).append("\n");
        return estado.toString();
    }
}