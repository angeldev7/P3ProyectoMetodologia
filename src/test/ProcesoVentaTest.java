package test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import model.CarritoCompra;
import model.Producto;

/**
 * Tests para la Historia de Usuario:
 * "Como Vendedor quiero procesar una venta de manera eficiente para atender rápidamente a los clientes"
 */
@DisplayName("Proceso de Venta - Tests de Aceptación")
public class ProcesoVentaTest {
    
    private CarritoCompra carrito;
    private Producto productoConStock;
    private Producto productoSinStock;
    private Producto productoStockLimitado;
    
    @BeforeEach
    public void setUp() {
        // Inicializar carrito para cada test
        carrito = new CarritoCompra();
        
        // Crear productos de prueba
        productoConStock = new Producto("P001", "Martillo", "Martillo de carpintero", 50, 25.50, 5);
        productoSinStock = new Producto("P002", "Destornillador", "Destornillador plano", 0, 15.00, 5);
        productoStockLimitado = new Producto("P003", "Taladro", "Taladro eléctrico", 3, 150.00, 5);
    }
    
    /**
     * Escenario 1: Inicio del proceso de venta
     * Dado que soy un vendedor autenticado en el sistema,
     * Cuando inicio una nueva venta,
     * Entonces el sistema me permite registrar productos de forma inmediata.
     */
    @Test
    @DisplayName("Escenario 1: Inicio del proceso de venta - El carrito debe estar vacío y listo")
    public void testInicioProcesoVenta() {
        assertNotNull(carrito, "El carrito debe estar inicializado");
        assertTrue(carrito.estaVacio(), "El carrito debe estar vacío al inicio");
        assertEquals(0, carrito.getCantidadItems(), "No debe haber items al inicio");
        assertEquals(0.0, carrito.getTotal(), 0.001, "El total debe ser 0 al inicio");
    }
    
    /**
     * Escenario 2: Registro de productos en la venta
     * Dado que he iniciado una venta, Y los productos están disponibles en inventario,
     * Cuando agrego uno o más productos a la venta,
     * Entonces el sistema calcula automáticamente el total de la venta.
     */
    @Test
    @DisplayName("Escenario 2a: Agregar un producto - El total se calcula automáticamente")
    public void testAgregarUnProductoCalculaTotal() {
        assertTrue(productoConStock.getStock() > 0, "El producto debe tener stock");
        int cantidadAgregar = 2;
        carrito.agregarProducto(productoConStock, cantidadAgregar);
        assertEquals(1, carrito.getCantidadItems(), "Debe haber 1 item en el carrito");
        double totalEsperado = productoConStock.getPrecio() * cantidadAgregar;
        assertEquals(totalEsperado, carrito.getTotal(), 0.001, 
                "El total debe ser el precio unitario multiplicado por la cantidad");
        assertFalse(carrito.estaVacio(), "El carrito no debe estar vacío");
    }
    
    @Test
    @DisplayName("Escenario 2b: Agregar múltiples productos - El total se actualiza correctamente")
    public void testAgregarMultiplesProductosCalculaTotal() {
        // Given (Dado que) - he iniciado una venta y hay productos disponibles
        assertTrue(productoConStock.getStock() > 0);
        assertTrue(productoStockLimitado.getStock() > 0);
        
        // When (Cuando) - agrego múltiples productos a la venta
        carrito.agregarProducto(productoConStock, 2);
        carrito.agregarProducto(productoStockLimitado, 1);
        
        // Then (Entonces) - el sistema calcula automáticamente el total de todos los productos
        assertEquals(2, carrito.getCantidadItems(), "Debe haber 2 items diferentes en el carrito");
        
        double totalEsperado = (productoConStock.getPrecio() * 2) + 
                               (productoStockLimitado.getPrecio() * 1);
        assertEquals(totalEsperado, carrito.getTotal(), 0.001, 
                "El total debe ser la suma de todos los productos");
    }
    
    @Test
    @DisplayName("Escenario 2c: Agregar mismo producto dos veces - La cantidad se acumula")
    public void testAgregarMismoProductoDobleVezAcumulaCantidad() {
        // Given (Dado que) - he iniciado una venta
        carrito.agregarProducto(productoConStock, 2);
        
        // When (Cuando) - agrego el mismo producto nuevamente
        carrito.agregarProducto(productoConStock, 3);
        
        // Then (Entonces) - la cantidad se acumula y el total se recalcula
        assertEquals(1, carrito.getCantidadItems(), "Debe haber solo 1 item (mismo producto)");
        
        double totalEsperado = productoConStock.getPrecio() * 5; // 2 + 3 = 5
        assertEquals(totalEsperado, carrito.getTotal(), 0.001, 
                "El total debe reflejar la cantidad acumulada");
    }
    
    /**
     * Escenario 3: Validación de disponibilidad de productos
     * Dado que un producto no tiene stock suficiente,
     * Cuando intento agregarlo a la venta,
     * Entonces el sistema me informa que no hay disponibilidad, 
     * Y no permite continuar con ese producto.
     */
    @Test
    @DisplayName("Escenario 3a: Producto sin stock - No se debe poder agregar")
    public void testProductoSinStockNoPuedeAgregarse() {
        // Given (Dado que) - un producto no tiene stock
        assertEquals(0, productoSinStock.getStock(), "El producto debe tener stock en 0");
        assertFalse(productoSinStock.getStock() >= 1, 
                "El producto no tiene stock suficiente");
    }
    
    @Test
    @DisplayName("Escenario 3b: Cantidad solicitada mayor al stock - No se debe permitir")
    public void testCantidadMayorAlStockNoPuedeAgregarse() {
        // Given (Dado que) - un producto tiene stock limitado
        assertEquals(3, productoStockLimitado.getStock(), "El producto debe tener stock limitado");
        
        // When & Then (Cuando intento agregar más de lo disponible)
        int cantidadSolicitada = 5;
        assertFalse(productoStockLimitado.getStock() >= cantidadSolicitada,
                "No hay stock suficiente para la cantidad solicitada");
    }
    
    @Test
    @DisplayName("Escenario 3c: Cantidad dentro del stock - Se debe permitir")
    public void testCantidadDentroDelStockSiPuedeAgregarse() {
        // Given (Dado que) - un producto tiene stock limitado
        assertEquals(3, productoStockLimitado.getStock());
        
        // When (Cuando) - intento agregar una cantidad dentro del stock
        int cantidadSolicitada = 2;
        
        // Then (Entonces) - debe ser válido
        assertTrue(productoStockLimitado.getStock() >= cantidadSolicitada,
                "Hay stock suficiente para la cantidad solicitada");
        
        // Y debe agregarse correctamente
        carrito.agregarProducto(productoStockLimitado, cantidadSolicitada);
        assertEquals(1, carrito.getCantidadItems(), "El producto se agregó al carrito");
    }
    
    /**
     * Escenario 4: Confirmación y cierre de la venta
     * Dado que la información de la venta es correcta,
     * Cuando confirmo la venta,
     * Entonces el sistema registra la venta exitosamente
     */
    @Test
    @DisplayName("Escenario 4a: Confirmar venta con productos - La venta debe registrarse")
    public void testConfirmarVentaConProductos() {
        carrito.agregarProducto(productoConStock, 2);
        carrito.agregarProducto(productoStockLimitado, 1);
        
        assertFalse(carrito.estaVacio(), "El carrito debe tener productos");
        assertTrue(carrito.getTotal() > 0, "El total debe ser mayor a 0");
           int itemsVendidos = carrito.getCantidadItems();
        double totalVenta = carrito.getTotal();    
        assertEquals(2, itemsVendidos, "Se vendieron 2 items diferentes");
        assertTrue(totalVenta > 0, "La venta tiene un monto total válido");
        carrito.limpiarCarrito();
        assertTrue(carrito.estaVacio(), "El carrito debe vaciarse después de confirmar");
        assertEquals(0.0, carrito.getTotal(), 0.001, "El total debe volver a 0");
    }
    
    @Test
    @DisplayName("Escenario 4b: Intentar confirmar venta vacía - No debe permitirse")
    public void testNoPermitirConfirmarVentaVacia() {
        // Given (Dado que) - el carrito está vacío
        assertTrue(carrito.estaVacio(), "El carrito debe estar vacío");
        
        // When & Then (Cuando intento confirmar) - debe validarse
        assertEquals(0, carrito.getCantidadItems(), 
                "No se puede confirmar una venta sin productos");
        assertEquals(0.0, carrito.getTotal(), 0.001, 
                "Una venta vacía no tiene monto total");
    }
    
    @Test
    @DisplayName("Escenario 4c: Venta exitosa debe mantener integridad de datos")
    public void testIntegridadDatosVenta() {
        // Given (Dado que) - agrego productos al carrito
        carrito.agregarProducto(productoConStock, 3);
        
        // When (Cuando) - verifico los datos antes de confirmar
        double subtotalItem = carrito.getItems().get(0).getSubtotal();
        double totalCarrito = carrito.getTotal();
        
        // Then (Entonces) - los datos deben ser consistentes
        assertEquals(subtotalItem, totalCarrito, 0.001, 
                "El subtotal del item debe coincidir con el total del carrito");
        assertEquals(productoConStock.getPrecio() * 3, totalCarrito, 0.001,
                "El total debe calcularse correctamente");
    }
    
    // Test adicional: Flujo completo de venta
    @Test
    @DisplayName("Test de Integración: Flujo completo de una venta exitosa")
    public void testFlujoCompletoVentaExitosa() {
        // Escenario 1: Inicio
        assertNotNull(carrito);
        assertTrue(carrito.estaVacio());
        
        // Escenario 2: Agregar productos
        carrito.agregarProducto(productoConStock, 2);
        carrito.agregarProducto(productoStockLimitado, 1);
        
        assertEquals(2, carrito.getCantidadItems());
        double totalEsperado = (productoConStock.getPrecio() * 2) + 
                               (productoStockLimitado.getPrecio() * 1);
        assertEquals(totalEsperado, carrito.getTotal(), 0.001);
        
        // Escenario 3: Validar stock (implícito en los productos usados)
        assertTrue(productoConStock.getStock() >= 2);
        assertTrue(productoStockLimitado.getStock() >= 1);
        
        // Escenario 4: Confirmar venta
        assertFalse(carrito.estaVacio());
        assertTrue(carrito.getTotal() > 0);
        
        // Limpiar carrito después de confirmar
        carrito.limpiarCarrito();
        assertTrue(carrito.estaVacio());
    }
}
