package test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import model.InventarioDAO;
import model.Producto;
import model.Rol;

/**
 * Tests para la Historia de Usuario 35:
 * "Como Gerente quiero monitorear niveles de stock y rotación para optimizar inversión en inventario"
 */
@DisplayName("Monitoreo de Inventario - Tests de Aceptación")
public class MonitoreoInventarioTest {
    
    private InventarioDAO inventario;
    private Rol rolGerente;
    private Rol rolVendedor;
    
    // Productos de prueba
    private Producto productoStockNormal;
    private Producto productoBajoStock;
    private Producto productoSinStock;
    
    @BeforeEach
    public void setUp() {
        // Inicializar inventario
        inventario = new InventarioDAO();
        
        // Crear roles
        rolGerente = new Rol("Gerente");
        rolGerente.agregarPermiso("puedeVerReportes");
        rolGerente.agregarPermiso("puedeGestionarProductos");
        
        rolVendedor = new Rol("Vendedor");
        rolVendedor.agregarPermiso("puedeVender");
        
        // Limpiar inventario y agregar productos de prueba
        Producto[] productosExistentes = inventario.obtenerTodosProductos();
        for (Producto p : productosExistentes) {
            inventario.eliminarProducto(p.getCodigo());
        }
        
        // Crear productos de prueba con diferentes niveles de stock
        productoStockNormal = new Producto("PN001", "Martillo Grande", "Martillo de acero inoxidable", 25, 35.50, 10);
        productoBajoStock = new Producto("PB002", "Destornillador Phillips", "Destornillador punta estrella", 3, 12.00, 5);
        productoSinStock = new Producto("PS003", "Cinta Métrica", "Cinta métrica 5 metros", 0, 18.75, 8);
        
        inventario.agregarProducto(productoStockNormal);
        inventario.agregarProducto(productoBajoStock);
        inventario.agregarProducto(productoSinStock);
    }
    
    /**
     * Escenario 1: Visualización del reporte de inventario
     * Dado que soy un gerente autenticado en el sistema,
     * Cuando accedo a la sección de reportes de inventario,
     * Entonces el sistema me muestra un reporte con los niveles actuales de stock 
     * y la rotación mensual por producto.
     */
    @Test
    @DisplayName("Escenario 1a: Gerente puede ver todos los productos del inventario")
    public void testGerenteVisualizaReporteInventario() {
        // Given (Dado que) - soy un gerente autenticado
        assertTrue(rolGerente.tienePermiso("puedeVerReportes"), 
                "El gerente debe tener permiso para ver reportes");
        
        // When (Cuando) - accedo a la sección de reportes de inventario
        Producto[] productos = inventario.obtenerTodosProductos();
        
        // Then (Entonces) - el sistema me muestra un reporte con los niveles actuales
        assertNotNull(productos, "El reporte de productos no debe ser nulo");
        assertEquals(3, productos.length, "Debe haber 3 productos en el reporte");
        
        // Verificar que cada producto tiene información de stock
        for (Producto producto : productos) {
            assertNotNull(producto, "El producto no debe ser nulo");
            assertNotNull(producto.getCodigo(), "El código del producto no debe ser nulo");
            assertNotNull(producto.getNombre(), "El nombre del producto no debe ser nulo");
            assertTrue(producto.getStock() >= 0, "El stock debe ser un valor válido");
            assertTrue(producto.getStockMinimo() >= 0, "El stock mínimo debe ser un valor válido");
        }
    }
    
    @Test
    @DisplayName("Escenario 1b: Reporte muestra niveles de stock correctos")
    public void testReporteMuestraNivelesStockCorrectos() {
        // Given (Dado que) - soy un gerente autenticado con acceso al reporte
        assertTrue(rolGerente.tienePermiso("puedeVerReportes"));
        
        // When (Cuando) - consulto el reporte
        Producto productoConsultado1 = inventario.buscarProductoPorCodigo("PN001");
        Producto productoConsultado2 = inventario.buscarProductoPorCodigo("PB002");
        Producto productoConsultado3 = inventario.buscarProductoPorCodigo("PS003");
        
        // Then (Entonces) - los niveles de stock son correctos
        assertEquals(25, productoConsultado1.getStock(), "Stock normal debe ser 25");
        assertEquals(3, productoConsultado2.getStock(), "Stock bajo debe ser 3");
        assertEquals(0, productoConsultado3.getStock(), "Stock debe ser 0");
    }
    
    @Test
    @DisplayName("Escenario 1c: Reporte incluye información de stock mínimo")
    public void testReporteIncluyeStockMinimo() {
        // Given (Dado que) - soy un gerente con acceso a reportes
        assertTrue(rolGerente.tienePermiso("puedeVerReportes"));
        
        // When (Cuando) - visualizo el reporte
        Producto[] productos = inventario.obtenerTodosProductos();
        
        // Then (Entonces) - cada producto muestra su stock mínimo definido
        for (Producto producto : productos) {
            assertTrue(producto.getStockMinimo() > 0, 
                    "Cada producto debe tener un stock mínimo definido");
        }
        
        assertEquals(10, productoStockNormal.getStockMinimo(), "Stock mínimo debe ser 10");
        assertEquals(5, productoBajoStock.getStockMinimo(), "Stock mínimo debe ser 5");
        assertEquals(8, productoSinStock.getStockMinimo(), "Stock mínimo debe ser 8");
    }
    
    /**
     * Escenario 2: Identificación de productos con bajo stock
     * Dado que estoy visualizando el reporte,
     * Cuando un producto tiene un nivel de stock por debajo del mínimo definido,
     * Entonces el sistema lo resalta visualmente para facilitar su identificación.
     */
    @Test
    @DisplayName("Escenario 2a: Sistema identifica productos con bajo stock")
    public void testSistemaIdentificaProductosBajoStock() {
        assertTrue(rolGerente.tienePermiso("puedeVerReportes"));
        List<Producto> productosBajoStock = inventario.obtenerProductosBajoStockMinimo();
        assertNotNull(productosBajoStock, "La lista de productos con bajo stock no debe ser nula");
        assertEquals(2, productosBajoStock.size(), 
                "Debe haber 2 productos con stock por debajo del mínimo");
        boolean tieneProductoBajoStock = productosBajoStock.stream()
                .anyMatch(p -> p.getCodigo().equals("PB002"));
        boolean tieneProductoSinStock = productosBajoStock.stream()
                .anyMatch(p -> p.getCodigo().equals("PS003"));
        assertTrue(tieneProductoBajoStock, "Debe incluir el producto con bajo stock (3 <= 5)");
        assertTrue(tieneProductoSinStock, "Debe incluir el producto sin stock (0 <= 8)");
    }
    
    @Test
    @DisplayName("Escenario 2b: Productos con stock normal no aparecen como bajo stock")
    public void testProductosStockNormalNoAparecenComoBajoStock() {
        // Given (Dado que) - hay productos con stock adecuado
        assertTrue(productoStockNormal.getStock() > productoStockNormal.getStockMinimo());
        
        // When (Cuando) - consulto productos con bajo stock
        List<Producto> productosBajoStock = inventario.obtenerProductosBajoStockMinimo();
        
        // Then (Entonces) - los productos con stock normal NO aparecen
        boolean contieneProductoNormal = productosBajoStock.stream()
                .anyMatch(p -> p.getCodigo().equals("PN001"));
        
        assertFalse(contieneProductoNormal, 
                "Productos con stock normal no deben aparecer en la lista de bajo stock");
    }
    
    @Test
    @DisplayName("Escenario 2c: Criterio de bajo stock es stock <= stock mínimo")
    public void testCriterioBajoStockEsStockMenorOIgualMinimo() {
        // Given (Dado que) - tengo productos con diferentes niveles
        Producto productoEnElLimite = new Producto("PL004", "Alicate", "Alicate universal", 5, 22.00, 5);
        inventario.agregarProducto(productoEnElLimite);
        
        // When (Cuando) - consulto bajo stock
        List<Producto> productosBajoStock = inventario.obtenerProductosBajoStockMinimo();
        
        // Then (Entonces) - producto con stock == mínimo está incluido
        boolean contieneProductoEnLimite = productosBajoStock.stream()
                .anyMatch(p -> p.getCodigo().equals("PL004"));
        
        assertTrue(contieneProductoEnLimite, 
                "Un producto con stock igual al mínimo debe considerarse de bajo stock");
    }
    
    @Test
    @DisplayName("Escenario 2d: Reporte destaca productos críticos (sin stock)")
    public void testReporteDestacaProductosCriticos() {
        // Given (Dado que) - hay productos sin stock (críticos)
        List<Producto> productosBajoStock = inventario.obtenerProductosBajoStockMinimo();
        
        // When (Cuando) - filtro los productos con stock = 0
        List<Producto> productosCriticos = productosBajoStock.stream()
                .filter(p -> p.getStock() == 0)
                .toList();
        
        // Then (Entonces) - los productos sin stock son identificables
        assertEquals(1, productosCriticos.size(), "Debe haber 1 producto sin stock");
        assertEquals("PS003", productosCriticos.get(0).getCodigo(), 
                "El producto sin stock debe ser PS003");
        assertEquals(0, productosCriticos.get(0).getStock(), 
                "El stock debe ser exactamente 0");
    }
    
    /**
     * Escenario 3: Acceso no autorizado al reporte
     * Dado que soy un usuario sin permisos de gerente,
     * Cuando intento acceder al reporte de niveles de stock,
     * Entonces el sistema deniega el acceso
     */
    @Test
    @DisplayName("Escenario 3a: Vendedor NO tiene permiso para ver reportes")
    public void testVendedorNoTienePermisoReportes() {
        assertEquals("Vendedor", rolVendedor.getNombre());
        boolean tienePermisoReportes = rolVendedor.tienePermiso("puedeVerReportes");
         assertFalse(tienePermisoReportes, 
                "Un vendedor NO debe tener permiso para ver reportes");
    }
    @Test
    @DisplayName("Escenario 3b: Solo roles con permiso puedeVerReportes acceden al reporte")
    public void testSoloRolesAutorizadosAccedenReportes() {
        // Given (Dado que) - tengo diferentes roles
        Rol rolAdmin = new Rol("Administrador");
        rolAdmin.agregarPermiso("puedeVerReportes");
        rolAdmin.agregarPermiso("puedeGestionarProductos");
        
        Rol rolCajero = new Rol("Cajero");
        rolCajero.agregarPermiso("puedeVender");
        
        // When & Then (Cuando verifico permisos)
        assertTrue(rolGerente.tienePermiso("puedeVerReportes"), 
                "Gerente DEBE tener acceso a reportes");
        assertTrue(rolAdmin.tienePermiso("puedeVerReportes"), 
                "Administrador DEBE tener acceso a reportes");
        assertFalse(rolVendedor.tienePermiso("puedeVerReportes"), 
                "Vendedor NO debe tener acceso a reportes");
        assertFalse(rolCajero.tienePermiso("puedeVerReportes"), 
                "Cajero NO debe tener acceso a reportes");
    }
    
    @Test
    @DisplayName("Escenario 3c: Acceso denegado se valida antes de mostrar datos")
    public void testAccesoDenegadoValidaAntesDeVerDatos() {
        // Given (Dado que) - soy un usuario sin permisos
        assertFalse(rolVendedor.tienePermiso("puedeVerReportes"));
        
        // When (Cuando) - intento acceder al reporte
        boolean puedeAcceder = rolVendedor.tienePermiso("puedeVerReportes");
        
        // Then (Entonces) - el acceso es denegado antes de obtener datos
        if (!puedeAcceder) {
            // Simular que no se obtienen datos si no hay permiso
            assertFalse(puedeAcceder, 
                    "El sistema debe denegar el acceso antes de mostrar datos");
        } else {
            fail("El vendedor no debería tener acceso a los reportes");
        }
    }
    
    // Tests adicionales para funcionalidad de reportes
    
    @Test
    @DisplayName("Test adicional: Cálculo del valor total del inventario")
    public void testCalculoValorTotalInventario() {
        // Given (Dado que) - soy gerente con acceso a reportes financieros
        assertTrue(rolGerente.tienePermiso("puedeVerReportes"));
        
        // When (Cuando) - calculo el valor total del inventario
        double valorTotal = inventario.calcularValorTotalInventario();
        
        // Then (Entonces) - el valor es la suma de stock * precio de cada producto
        double valorEsperado = (productoStockNormal.getStock() * productoStockNormal.getPrecio()) +
                               (productoBajoStock.getStock() * productoBajoStock.getPrecio()) +
                               (productoSinStock.getStock() * productoSinStock.getPrecio());
        
        assertEquals(valorEsperado, valorTotal, 0.01, 
                "El valor total del inventario debe calcularse correctamente");
        assertTrue(valorTotal > 0, "El inventario debe tener un valor positivo");
    }
    
    @Test
    @DisplayName("Test adicional: Reporte muestra cantidad de productos")
    public void testReporteMuestraCantidadProductos() {
        // Given (Dado que) - soy gerente
        assertTrue(rolGerente.tienePermiso("puedeVerReportes"));
        
        // When (Cuando) - consulto la cantidad de productos
        int cantidadProductos = inventario.obtenerCantidadProductos();
        
        // Then (Entonces) - la cantidad es correcta
        assertEquals(3, cantidadProductos, 
                "El reporte debe mostrar la cantidad correcta de productos");
    }
    
    @Test
    @DisplayName("Test de integración: Flujo completo de monitoreo de inventario")
    public void testFlujoCompletoMonitoreoInventario() {
        // Escenario 1: Acceso autorizado
        assertTrue(rolGerente.tienePermiso("puedeVerReportes"), 
                "El gerente debe tener acceso");
        
        // Escenario 1: Visualizar reporte
        Producto[] todosProductos = inventario.obtenerTodosProductos();
        assertEquals(3, todosProductos.length, "Debe haber 3 productos");
        
        // Escenario 2: Identificar bajo stock
        List<Producto> bajoStock = inventario.obtenerProductosBajoStockMinimo();
        assertEquals(2, bajoStock.size(), "Debe haber 2 productos con bajo stock");
        
        // Verificar productos críticos
        long productosCriticos = bajoStock.stream()
                .filter(p -> p.getStock() == 0)
                .count();
        assertEquals(1, productosCriticos, "Debe haber 1 producto crítico");
        
        // Escenario 3: Validar acceso denegado para otros roles
        assertFalse(rolVendedor.tienePermiso("puedeVerReportes"), 
                "El vendedor no debe tener acceso");
        
        // Calcular métricas adicionales
        double valorTotal = inventario.calcularValorTotalInventario();
        assertTrue(valorTotal > 0, "El inventario debe tener valor");
    }
}
