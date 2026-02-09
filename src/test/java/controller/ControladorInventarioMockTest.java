package controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

import model.*;
import DAO.AccesoSistemaDAO;
import DAO.ProductoDAO;
import view.VentanaPrincipal;
import view.PanelProductos;
import view.PanelVentas;
import view.PanelReportes;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ControladorInventarioMockTest {

    @Mock
    private InventarioDAO mockModelo;

    @Mock
    private AccesoSistemaDAO mockAccesoDAO;

    private VentanaPrincipal vista;
    private CarritoCompra carrito;
    private ControladorInventario controlador;

    @BeforeEach
    public void setUp() throws Exception {
        // Create real Swing components in headless-compatible mode
        vista = createMockVista();
        carrito = new CarritoCompra();
        controlador = new ControladorInventario(vista, mockModelo, carrito, mockAccesoDAO);
    }

    private VentanaPrincipal createMockVista() throws Exception {
        // We can't call new VentanaPrincipal() (it tries to set up a full JFrame)
        // Instead, we create a mock and set real panel instances
        VentanaPrincipal mockVista = mock(VentanaPrincipal.class);

        // Create real panels
        PanelProductos pp = new PanelProductos();
        PanelVentas pv = new PanelVentas();
        PanelReportes pr = new PanelReportes();

        // Set public fields via reflection since Mockito mocks don't support public field access
        Field ppField = VentanaPrincipal.class.getDeclaredField("panelProductos");
        ppField.setAccessible(true);
        ppField.set(mockVista, pp);

        Field pvField = VentanaPrincipal.class.getDeclaredField("panelVentas");
        pvField.setAccessible(true);
        pvField.set(mockVista, pv);

        Field prField = VentanaPrincipal.class.getDeclaredField("panelReportes");
        prField.setAccessible(true);
        prField.set(mockVista, pr);

        return mockVista;
    }

    // ====== Tests for guardarProducto (via actionPerformed) ======

    @Test
    public void testGuardarProductoNuevo() {
        try (MockedStatic<JOptionPane> mocked = mockStatic(JOptionPane.class)) {
            // Set form values
            vista.panelProductos.txtCodigo.setText("P099");
            vista.panelProductos.txtNombre.setText("TestProduct");
            vista.panelProductos.txtDescripcion.setText("Desc");
            vista.panelProductos.txtStock.setText("10");
            vista.panelProductos.txtPrecio.setText("25.50");
            vista.panelProductos.txtStockMinimo.setText("3");
            vista.panelProductos.txtPasillo.setText("A");
            vista.panelProductos.txtEstante.setText("1");
            vista.panelProductos.txtPosicion.setText("2");
            vista.panelProductos.txtCodigo.setEditable(true);

            when(mockModelo.buscarProductoPorCodigo("P099")).thenReturn(null);
            when(mockModelo.obtenerTodosProductos()).thenReturn(new Producto[0]);

            ActionEvent event = new ActionEvent(vista.panelProductos.btnGuardarProducto, ActionEvent.ACTION_PERFORMED, "");
            controlador.actionPerformed(event);

            verify(mockModelo).agregarProducto(any(Producto.class));
        }
    }

    @Test
    public void testGuardarProductoCamposVacios() {
        try (MockedStatic<JOptionPane> mocked = mockStatic(JOptionPane.class)) {
            vista.panelProductos.txtCodigo.setText("");
            vista.panelProductos.txtNombre.setText("");
            vista.panelProductos.txtStock.setText("");
            vista.panelProductos.txtPrecio.setText("");
            vista.panelProductos.txtStockMinimo.setText("");

            ActionEvent event = new ActionEvent(vista.panelProductos.btnGuardarProducto, ActionEvent.ACTION_PERFORMED, "");
            controlador.actionPerformed(event);

            verify(mockModelo, never()).agregarProducto(any(Producto.class));
        }
    }

    @Test
    public void testGuardarProductoPrecioConDolar() {
        try (MockedStatic<JOptionPane> mocked = mockStatic(JOptionPane.class)) {
            vista.panelProductos.txtCodigo.setText("P100");
            vista.panelProductos.txtNombre.setText("Test");
            vista.panelProductos.txtDescripcion.setText("Desc");
            vista.panelProductos.txtStock.setText("5");
            vista.panelProductos.txtPrecio.setText("$15.99");
            vista.panelProductos.txtStockMinimo.setText("2");
            vista.panelProductos.txtPasillo.setText("");
            vista.panelProductos.txtEstante.setText("");
            vista.panelProductos.txtPosicion.setText("");
            vista.panelProductos.txtCodigo.setEditable(true);

            when(mockModelo.buscarProductoPorCodigo("P100")).thenReturn(null);
            when(mockModelo.obtenerTodosProductos()).thenReturn(new Producto[0]);

            ActionEvent event = new ActionEvent(vista.panelProductos.btnGuardarProducto, ActionEvent.ACTION_PERFORMED, "");
            controlador.actionPerformed(event);

            verify(mockModelo).agregarProducto(any(Producto.class));
        }
    }

    @Test
    public void testGuardarProductoPrecioInvalido() {
        try (MockedStatic<JOptionPane> mocked = mockStatic(JOptionPane.class)) {
            vista.panelProductos.txtCodigo.setText("P100");
            vista.panelProductos.txtNombre.setText("Test");
            vista.panelProductos.txtDescripcion.setText("Desc");
            vista.panelProductos.txtStock.setText("5");
            vista.panelProductos.txtPrecio.setText("abc");
            vista.panelProductos.txtStockMinimo.setText("2");

            ActionEvent event = new ActionEvent(vista.panelProductos.btnGuardarProducto, ActionEvent.ACTION_PERFORMED, "");
            controlador.actionPerformed(event);

            verify(mockModelo, never()).agregarProducto(any(Producto.class));
        }
    }

    @Test
    public void testGuardarProductoValoresNegativos() {
        try (MockedStatic<JOptionPane> mocked = mockStatic(JOptionPane.class)) {
            vista.panelProductos.txtCodigo.setText("P100");
            vista.panelProductos.txtNombre.setText("Test");
            vista.panelProductos.txtDescripcion.setText("Desc");
            vista.panelProductos.txtStock.setText("-1");
            vista.panelProductos.txtPrecio.setText("10.0");
            vista.panelProductos.txtStockMinimo.setText("2");

            ActionEvent event = new ActionEvent(vista.panelProductos.btnGuardarProducto, ActionEvent.ACTION_PERFORMED, "");
            controlador.actionPerformed(event);

            verify(mockModelo, never()).agregarProducto(any(Producto.class));
        }
    }

    @Test
    public void testGuardarProductoDuplicado() {
        try (MockedStatic<JOptionPane> mocked = mockStatic(JOptionPane.class)) {
            vista.panelProductos.txtCodigo.setText("P001");
            vista.panelProductos.txtNombre.setText("Existente");
            vista.panelProductos.txtDescripcion.setText("Desc");
            vista.panelProductos.txtStock.setText("5");
            vista.panelProductos.txtPrecio.setText("10.0");
            vista.panelProductos.txtStockMinimo.setText("2");
            vista.panelProductos.txtPasillo.setText("");
            vista.panelProductos.txtEstante.setText("");
            vista.panelProductos.txtPosicion.setText("");
            vista.panelProductos.txtCodigo.setEditable(true);

            when(mockModelo.buscarProductoPorCodigo("P001")).thenReturn(new Producto("P001", "Old", "D", 1, 1.0, 1));

            ActionEvent event = new ActionEvent(vista.panelProductos.btnGuardarProducto, ActionEvent.ACTION_PERFORMED, "");
            controlador.actionPerformed(event);

            verify(mockModelo, never()).agregarProducto(any(Producto.class));
        }
    }

    @Test
    public void testActualizarProductoExistente() {
        try (MockedStatic<JOptionPane> mocked = mockStatic(JOptionPane.class)) {
            vista.panelProductos.txtCodigo.setText("P001");
            vista.panelProductos.txtNombre.setText("Updated");
            vista.panelProductos.txtDescripcion.setText("Updated Desc");
            vista.panelProductos.txtStock.setText("20");
            vista.panelProductos.txtPrecio.setText("15.0");
            vista.panelProductos.txtStockMinimo.setText("5");
            vista.panelProductos.txtPasillo.setText("B");
            vista.panelProductos.txtEstante.setText("3");
            vista.panelProductos.txtPosicion.setText("1");
            vista.panelProductos.txtCodigo.setEditable(false); // editing existing

            Producto existente = new Producto("P001", "Old", "D", 1, 1.0, 1, "A", "1", "1");
            when(mockModelo.buscarProductoPorCodigo("P001")).thenReturn(existente);
            when(mockModelo.obtenerTodosProductos()).thenReturn(new Producto[0]);

            ActionEvent event = new ActionEvent(vista.panelProductos.btnGuardarProducto, ActionEvent.ACTION_PERFORMED, "");
            controlador.actionPerformed(event);

            assertEquals("Updated", existente.getNombre());
            assertEquals(20, existente.getStock());
        }
    }

    // ====== Tests for eliminarProducto ======

    @Test
    public void testEliminarProductoNoSeleccionado() {
        try (MockedStatic<JOptionPane> mocked = mockStatic(JOptionPane.class)) {
            vista.panelProductos.tablaProductos.clearSelection();

            ActionEvent event = new ActionEvent(vista.panelProductos.btnEliminarProducto, ActionEvent.ACTION_PERFORMED, "");
            controlador.actionPerformed(event);

            verify(mockModelo, never()).eliminarProducto(anyString());
        }
    }

    // ====== Tests for limpiarFormulario ======

    @Test
    public void testNuevoProductoLimpiaFormulario() {
        try (MockedStatic<JOptionPane> mocked = mockStatic(JOptionPane.class)) {
            vista.panelProductos.txtCodigo.setText("P001");
            vista.panelProductos.txtNombre.setText("Test");

            ActionEvent event = new ActionEvent(vista.panelProductos.btnNuevoProducto, ActionEvent.ACTION_PERFORMED, "");
            controlador.actionPerformed(event);

            assertEquals("", vista.panelProductos.txtCodigo.getText());
            assertEquals("", vista.panelProductos.txtNombre.getText());
            assertTrue(vista.panelProductos.txtCodigo.isEditable());
        }
    }

    // ====== Tests for agregarProductoAlCarrito ======

    @Test
    public void testAgregarAlCarritoProductoNoSeleccionado() {
        try (MockedStatic<JOptionPane> mocked = mockStatic(JOptionPane.class)) {
            vista.panelVentas.cmbProductos.removeAllItems();
            vista.panelVentas.txtCantidad.setText("5");

            ActionEvent event = new ActionEvent(vista.panelVentas.btnAgregarAlCarrito, ActionEvent.ACTION_PERFORMED, "");
            controlador.actionPerformed(event);

            assertTrue(carrito.estaVacio());
        }
    }

    @Test
    public void testAgregarAlCarritoCantidadVacia() {
        try (MockedStatic<JOptionPane> mocked = mockStatic(JOptionPane.class)) {
            vista.panelVentas.cmbProductos.addItem("P001");
            vista.panelVentas.txtCantidad.setText("");

            ActionEvent event = new ActionEvent(vista.panelVentas.btnAgregarAlCarrito, ActionEvent.ACTION_PERFORMED, "");
            controlador.actionPerformed(event);

            assertTrue(carrito.estaVacio());
        }
    }

    @Test
    public void testAgregarAlCarritoCantidadNegativa() {
        try (MockedStatic<JOptionPane> mocked = mockStatic(JOptionPane.class)) {
            vista.panelVentas.cmbProductos.addItem("P001");
            vista.panelVentas.txtCantidad.setText("-1");

            Producto p = new Producto("P001", "Martillo", "D", 10, 12.50, 5);
            when(mockModelo.buscarProductoPorCodigo("P001")).thenReturn(p);

            ActionEvent event = new ActionEvent(vista.panelVentas.btnAgregarAlCarrito, ActionEvent.ACTION_PERFORMED, "");
            controlador.actionPerformed(event);

            assertTrue(carrito.estaVacio());
        }
    }

    @Test
    public void testAgregarAlCarritoCantidadInvalida() {
        try (MockedStatic<JOptionPane> mocked = mockStatic(JOptionPane.class)) {
            vista.panelVentas.cmbProductos.addItem("P001");
            vista.panelVentas.txtCantidad.setText("abc");

            ActionEvent event = new ActionEvent(vista.panelVentas.btnAgregarAlCarrito, ActionEvent.ACTION_PERFORMED, "");
            controlador.actionPerformed(event);

            assertTrue(carrito.estaVacio());
        }
    }

    @Test
    public void testAgregarAlCarritoProductoNoEncontrado() {
        try (MockedStatic<JOptionPane> mocked = mockStatic(JOptionPane.class)) {
            vista.panelVentas.cmbProductos.addItem("P999");
            vista.panelVentas.txtCantidad.setText("1");

            when(mockModelo.buscarProductoPorCodigo("P999")).thenReturn(null);

            ActionEvent event = new ActionEvent(vista.panelVentas.btnAgregarAlCarrito, ActionEvent.ACTION_PERFORMED, "");
            controlador.actionPerformed(event);

            assertTrue(carrito.estaVacio());
        }
    }

    @Test
    public void testAgregarAlCarritoStockInsuficiente() {
        try (MockedStatic<JOptionPane> mocked = mockStatic(JOptionPane.class)) {
            vista.panelVentas.cmbProductos.addItem("P001");
            vista.panelVentas.txtCantidad.setText("999");

            Producto p = new Producto("P001", "Martillo", "D", 5, 12.50, 2);
            when(mockModelo.buscarProductoPorCodigo("P001")).thenReturn(p);

            ActionEvent event = new ActionEvent(vista.panelVentas.btnAgregarAlCarrito, ActionEvent.ACTION_PERFORMED, "");
            controlador.actionPerformed(event);

            assertTrue(carrito.estaVacio());
        }
    }

    @Test
    public void testAgregarAlCarritoExitoso() {
        try (MockedStatic<JOptionPane> mocked = mockStatic(JOptionPane.class)) {
            vista.panelVentas.cmbProductos.addItem("P001");
            vista.panelVentas.txtCantidad.setText("2");

            Producto p = new Producto("P001", "Martillo", "D", 10, 12.50, 5);
            when(mockModelo.buscarProductoPorCodigo("P001")).thenReturn(p);

            ActionEvent event = new ActionEvent(vista.panelVentas.btnAgregarAlCarrito, ActionEvent.ACTION_PERFORMED, "");
            controlador.actionPerformed(event);

            assertFalse(carrito.estaVacio());
            assertEquals(1, carrito.getCantidadItems());
        }
    }

    // ====== Tests for procesarVentaCarrito ======

    @Test
    public void testProcesarVentaCarritoVacio() {
        try (MockedStatic<JOptionPane> mocked = mockStatic(JOptionPane.class)) {
            ActionEvent event = new ActionEvent(vista.panelVentas.btnProcesarVenta, ActionEvent.ACTION_PERFORMED, "");
            controlador.actionPerformed(event);

            verify(mockModelo, never()).agregarVenta(any(Venta.class));
        }
    }

    @Test
    public void testProcesarVentaProductoNoDisponible() {
        try (MockedStatic<JOptionPane> mocked = mockStatic(JOptionPane.class)) {
            // Add item to cart manually
            Producto p = new Producto("P001", "Martillo", "D", 10, 12.50, 5);
            carrito.agregarProducto(p, 2);

            // Product now returns null (not found)
            when(mockModelo.buscarProductoPorCodigo("P001")).thenReturn(null);
            when(mockModelo.obtenerTodosProductos()).thenReturn(new Producto[0]);

            ActionEvent event = new ActionEvent(vista.panelVentas.btnProcesarVenta, ActionEvent.ACTION_PERFORMED, "");
            controlador.actionPerformed(event);

            verify(mockModelo, never()).agregarVenta(any(Venta.class));
        }
    }

    @Test
    public void testProcesarVentaStockInsuficiente() {
        try (MockedStatic<JOptionPane> mocked = mockStatic(JOptionPane.class)) {
            Producto p = new Producto("P001", "Martillo", "D", 1, 12.50, 5);
            carrito.agregarProducto(p, 2);

            // Stock available is 1, but cart has 2
            Producto inDB = new Producto("P001", "Martillo", "D", 1, 12.50, 5);
            when(mockModelo.buscarProductoPorCodigo("P001")).thenReturn(inDB);

            ActionEvent event = new ActionEvent(vista.panelVentas.btnProcesarVenta, ActionEvent.ACTION_PERFORMED, "");
            controlador.actionPerformed(event);

            verify(mockModelo, never()).agregarVenta(any(Venta.class));
        }
    }

    @Test
    public void testProcesarVentaExitosa() {
        try (MockedStatic<JOptionPane> mocked = mockStatic(JOptionPane.class)) {
            mocked.when(() -> JOptionPane.showConfirmDialog(any(), any(), anyString(), anyInt(), anyInt()))
                  .thenReturn(JOptionPane.YES_OPTION);

            Producto p = new Producto("P001", "Martillo", "D", 10, 12.50, 5);
            carrito.agregarProducto(p, 2);

            when(mockModelo.buscarProductoPorCodigo("P001")).thenReturn(p);
            when(mockModelo.obtenerTodosProductos()).thenReturn(new Producto[0]);
            when(mockModelo.obtenerTodasVentas()).thenReturn(new Venta[0]);

            ActionEvent event = new ActionEvent(vista.panelVentas.btnProcesarVenta, ActionEvent.ACTION_PERFORMED, "");
            controlador.actionPerformed(event);

            verify(mockModelo).agregarVenta(any(Venta.class));
            assertTrue(carrito.estaVacio());
        }
    }

    // ====== Tests for eliminarProductoDelCarrito ======

    @Test
    public void testEliminarDelCarritoNoSeleccionado() {
        try (MockedStatic<JOptionPane> mocked = mockStatic(JOptionPane.class)) {
            vista.panelVentas.tablaCarrito.clearSelection();

            ActionEvent event = new ActionEvent(vista.panelVentas.btnEliminarDelCarrito, ActionEvent.ACTION_PERFORMED, "");
            controlador.actionPerformed(event);
        }
    }

    // ====== Tests for generarReporte ======

    @Test
    public void testGenerarReporteVentas() {
        try (MockedStatic<JOptionPane> mocked = mockStatic(JOptionPane.class)) {
            vista.panelReportes.cmbTipoReporte.setSelectedItem("Reporte de Ventas");

            Venta v = new Venta("P001", "Martillo", 2, 12.50, 25.0);
            when(mockModelo.obtenerTodasVentas()).thenReturn(new Venta[]{v});

            ActionEvent event = new ActionEvent(vista.panelReportes.btnGenerarReporte, ActionEvent.ACTION_PERFORMED, "");
            controlador.actionPerformed(event);

            String reporte = vista.panelReportes.txtReporte.getText();
            assertTrue(reporte.contains("REPORTE DE VENTAS"));
        }
    }

    @Test
    public void testGenerarReporteAlertaStockBajo() {
        try (MockedStatic<JOptionPane> mocked = mockStatic(JOptionPane.class)) {
            vista.panelReportes.cmbTipoReporte.setSelectedItem("Alerta de Stock Bajo");

            Producto p = new Producto("P001", "Martillo", "D", 2, 12.50, 5);
            when(mockModelo.obtenerTodosProductos()).thenReturn(new Producto[]{p});

            ActionEvent event = new ActionEvent(vista.panelReportes.btnGenerarReporte, ActionEvent.ACTION_PERFORMED, "");
            controlador.actionPerformed(event);

            String reporte = vista.panelReportes.txtReporte.getText();
            assertTrue(reporte.contains("ALERTA DE STOCK BAJO"));
            assertTrue(reporte.contains("Martillo"));
        }
    }

    @Test
    public void testGenerarReporteAlertaStockBajoSinProductos() {
        try (MockedStatic<JOptionPane> mocked = mockStatic(JOptionPane.class)) {
            vista.panelReportes.cmbTipoReporte.setSelectedItem("Alerta de Stock Bajo");

            Producto p = new Producto("P001", "Martillo", "D", 100, 12.50, 5);
            when(mockModelo.obtenerTodosProductos()).thenReturn(new Producto[]{p});

            ActionEvent event = new ActionEvent(vista.panelReportes.btnGenerarReporte, ActionEvent.ACTION_PERFORMED, "");
            controlador.actionPerformed(event);

            String reporte = vista.panelReportes.txtReporte.getText();
            assertTrue(reporte.contains("No hay productos con stock bajo"));
        }
    }

    @Test
    public void testGenerarReporteCatalogo() {
        try (MockedStatic<JOptionPane> mocked = mockStatic(JOptionPane.class)) {
            vista.panelReportes.cmbTipoReporte.setSelectedItem("Catálogo de Productos");

            Producto p = new Producto("P001", "Martillo", "D", 10, 12.50, 5);
            when(mockModelo.obtenerTodosProductos()).thenReturn(new Producto[]{p});

            ActionEvent event = new ActionEvent(vista.panelReportes.btnGenerarReporte, ActionEvent.ACTION_PERFORMED, "");
            controlador.actionPerformed(event);

            String reporte = vista.panelReportes.txtReporte.getText();
            assertTrue(reporte.contains("CATÁLOGO DE PRODUCTOS"));
            assertTrue(reporte.contains("Martillo"));
        }
    }

    // ====== Tests for getters ======

    @Test
    public void testGetCarrito() {
        assertSame(carrito, controlador.getCarrito());
    }

    @Test
    public void testGetModelo() {
        assertSame(mockModelo, controlador.getModelo());
    }

    // ====== Tests for DocumentListener ======

    @Test
    public void testInsertUpdate() {
        assertDoesNotThrow(() -> controlador.insertUpdate(null));
    }

    @Test
    public void testRemoveUpdate() {
        assertDoesNotThrow(() -> controlador.removeUpdate(null));
    }

    @Test
    public void testChangedUpdate() {
        assertDoesNotThrow(() -> controlador.changedUpdate(null));
    }

    // ====== Tests for eliminarProducto happy path ======

    @Test
    public void testEliminarProductoExitoso() {
        try (MockedStatic<JOptionPane> mocked = mockStatic(JOptionPane.class)) {
            // Add a row to the product table and select it
            vista.panelProductos.modeloTabla.addRow(new Object[]{"P001", "Martillo", "Herramienta", 10, "$12.50", 5, "A-1-2", "OK"});
            vista.panelProductos.tablaProductos.setRowSelectionInterval(0, 0);

            mocked.when(() -> JOptionPane.showConfirmDialog(any(), any(), anyString(), anyInt()))
                  .thenReturn(JOptionPane.YES_OPTION);

            when(mockModelo.eliminarProducto("P001")).thenReturn(true);
            when(mockModelo.obtenerTodosProductos()).thenReturn(new Producto[0]);

            ActionEvent event = new ActionEvent(vista.panelProductos.btnEliminarProducto, ActionEvent.ACTION_PERFORMED, "");
            controlador.actionPerformed(event);

            verify(mockModelo).eliminarProducto("P001");
        }
    }

    @Test
    public void testEliminarProductoFalla() {
        try (MockedStatic<JOptionPane> mocked = mockStatic(JOptionPane.class)) {
            vista.panelProductos.modeloTabla.addRow(new Object[]{"P001", "Martillo", "Herramienta", 10, "$12.50", 5, "A-1-2", "OK"});
            vista.panelProductos.tablaProductos.setRowSelectionInterval(0, 0);

            mocked.when(() -> JOptionPane.showConfirmDialog(any(), any(), anyString(), anyInt()))
                  .thenReturn(JOptionPane.YES_OPTION);

            when(mockModelo.eliminarProducto("P001")).thenReturn(false);

            ActionEvent event = new ActionEvent(vista.panelProductos.btnEliminarProducto, ActionEvent.ACTION_PERFORMED, "");
            controlador.actionPerformed(event);

            verify(mockModelo).eliminarProducto("P001");
        }
    }

    @Test
    public void testEliminarProductoCancela() {
        try (MockedStatic<JOptionPane> mocked = mockStatic(JOptionPane.class)) {
            vista.panelProductos.modeloTabla.addRow(new Object[]{"P001", "Martillo", "Herramienta", 10, "$12.50", 5, "A-1-2", "OK"});
            vista.panelProductos.tablaProductos.setRowSelectionInterval(0, 0);

            mocked.when(() -> JOptionPane.showConfirmDialog(any(), any(), anyString(), anyInt()))
                  .thenReturn(JOptionPane.NO_OPTION);

            ActionEvent event = new ActionEvent(vista.panelProductos.btnEliminarProducto, ActionEvent.ACTION_PERFORMED, "");
            controlador.actionPerformed(event);

            verify(mockModelo, never()).eliminarProducto(anyString());
        }
    }

    // ====== Tests for eliminarProductoDelCarrito happy path ======

    @Test
    public void testEliminarDelCarritoExitoso() {
        try (MockedStatic<JOptionPane> mocked = mockStatic(JOptionPane.class)) {
            // Add an item to the carrito model table
            vista.panelVentas.modeloCarrito.addRow(new Object[]{"P001", "Martillo", "$12.50", 2, "$25.00"});
            vista.panelVentas.tablaCarrito.setRowSelectionInterval(0, 0);

            // Also add to the real carrito so eliminarProducto works
            Producto p = new Producto("P001", "Martillo", "D", 10, 12.50, 5);
            carrito.agregarProducto(p, 2);

            mocked.when(() -> JOptionPane.showConfirmDialog(any(), any(), anyString(), anyInt()))
                  .thenReturn(JOptionPane.YES_OPTION);

            ActionEvent event = new ActionEvent(vista.panelVentas.btnEliminarDelCarrito, ActionEvent.ACTION_PERFORMED, "");
            controlador.actionPerformed(event);

            assertTrue(carrito.estaVacio());
        }
    }

    @Test
    public void testEliminarDelCarritoCancela() {
        try (MockedStatic<JOptionPane> mocked = mockStatic(JOptionPane.class)) {
            vista.panelVentas.modeloCarrito.addRow(new Object[]{"P001", "Martillo", "$12.50", 2, "$25.00"});
            vista.panelVentas.tablaCarrito.setRowSelectionInterval(0, 0);

            Producto p = new Producto("P001", "Martillo", "D", 10, 12.50, 5);
            carrito.agregarProducto(p, 2);

            mocked.when(() -> JOptionPane.showConfirmDialog(any(), any(), anyString(), anyInt()))
                  .thenReturn(JOptionPane.NO_OPTION);

            ActionEvent event = new ActionEvent(vista.panelVentas.btnEliminarDelCarrito, ActionEvent.ACTION_PERFORMED, "");
            controlador.actionPerformed(event);

            assertFalse(carrito.estaVacio());
        }
    }

    // ====== Tests for buscarPorUbicacion ======

    @Test
    public void testBuscarPorUbicacionConResultados() {
        try (MockedStatic<JOptionPane> mocked = mockStatic(JOptionPane.class)) {
            // Return pasillo, estante, posicion via showInputDialog
            mocked.when(() -> JOptionPane.showInputDialog(any(), anyString(), anyString(), anyInt()))
                  .thenReturn("A", "1", "2");

            Producto p1 = new Producto("P001", "Martillo", "Herramienta", 10, 12.50, 5, "A", "1", "2");
            Producto p2 = new Producto("P002", "Sierra", "Herramienta", 5, 8.00, 2, "B", "2", "1");
            when(mockModelo.obtenerTodosProductos()).thenReturn(new Producto[]{p1, p2});

            ActionEvent event = new ActionEvent(vista.panelProductos.btnBuscarPorUbicacion, ActionEvent.ACTION_PERFORMED, "");
            controlador.actionPerformed(event);

            // Should filter table to show only p1
            assertEquals(1, vista.panelProductos.modeloTabla.getRowCount());
        }
    }

    @Test
    public void testBuscarPorUbicacionSinResultados() {
        try (MockedStatic<JOptionPane> mocked = mockStatic(JOptionPane.class)) {
            mocked.when(() -> JOptionPane.showInputDialog(any(), anyString(), anyString(), anyInt()))
                  .thenReturn("Z", "99", "99");

            Producto p1 = new Producto("P001", "Martillo", "Herramienta", 10, 12.50, 5, "A", "1", "2");
            when(mockModelo.obtenerTodosProductos()).thenReturn(new Producto[]{p1});

            ActionEvent event = new ActionEvent(vista.panelProductos.btnBuscarPorUbicacion, ActionEvent.ACTION_PERFORMED, "");
            controlador.actionPerformed(event);

            // No results, table not populated
            assertEquals(0, vista.panelProductos.modeloTabla.getRowCount());
        }
    }

    @Test
    public void testBuscarPorUbicacionVacioCoincideTodos() {
        try (MockedStatic<JOptionPane> mocked = mockStatic(JOptionPane.class)) {
            // Empty strings → match everything
            mocked.when(() -> JOptionPane.showInputDialog(any(), anyString(), anyString(), anyInt()))
                  .thenReturn("", "", "");

            Producto p1 = new Producto("P001", "Martillo", "Herramienta", 10, 12.50, 5, "A", "1", "2");
            Producto p2 = new Producto("P002", "Sierra", "Herramienta", 5, 8.00, 2, "B", "2", "1");
            when(mockModelo.obtenerTodosProductos()).thenReturn(new Producto[]{p1, p2});

            ActionEvent event = new ActionEvent(vista.panelProductos.btnBuscarPorUbicacion, ActionEvent.ACTION_PERFORMED, "");
            controlador.actionPerformed(event);

            assertEquals(2, vista.panelProductos.modeloTabla.getRowCount());
        }
    }

    @Test
    public void testBuscarPorUbicacionCancelaPasillo() {
        try (MockedStatic<JOptionPane> mocked = mockStatic(JOptionPane.class)) {
            mocked.when(() -> JOptionPane.showInputDialog(any(), anyString(), anyString(), anyInt()))
                  .thenReturn(null);

            ActionEvent event = new ActionEvent(vista.panelProductos.btnBuscarPorUbicacion, ActionEvent.ACTION_PERFORMED, "");
            controlador.actionPerformed(event);

            verify(mockModelo, never()).obtenerTodosProductos();
        }
    }

    @Test
    public void testBuscarPorUbicacionCancelaEstante() {
        try (MockedStatic<JOptionPane> mocked = mockStatic(JOptionPane.class)) {
            mocked.when(() -> JOptionPane.showInputDialog(any(), anyString(), anyString(), anyInt()))
                  .thenReturn("A", null);

            ActionEvent event = new ActionEvent(vista.panelProductos.btnBuscarPorUbicacion, ActionEvent.ACTION_PERFORMED, "");
            controlador.actionPerformed(event);

            verify(mockModelo, never()).obtenerTodosProductos();
        }
    }

    @Test
    public void testBuscarPorUbicacionCancelaPosicion() {
        try (MockedStatic<JOptionPane> mocked = mockStatic(JOptionPane.class)) {
            mocked.when(() -> JOptionPane.showInputDialog(any(), anyString(), anyString(), anyInt()))
                  .thenReturn("A", "1", null);

            ActionEvent event = new ActionEvent(vista.panelProductos.btnBuscarPorUbicacion, ActionEvent.ACTION_PERFORMED, "");
            controlador.actionPerformed(event);

            verify(mockModelo, never()).obtenerTodosProductos();
        }
    }

    @Test
    public void testBuscarPorUbicacionProductoStockBajo() {
        try (MockedStatic<JOptionPane> mocked = mockStatic(JOptionPane.class)) {
            mocked.when(() -> JOptionPane.showInputDialog(any(), anyString(), anyString(), anyInt()))
                  .thenReturn("A", "", "");

            // Product with stock <= stockMinimo
            Producto p1 = new Producto("P001", "Martillo", "Herramienta", 2, 12.50, 5, "A", "1", "2");
            when(mockModelo.obtenerTodosProductos()).thenReturn(new Producto[]{p1});

            ActionEvent event = new ActionEvent(vista.panelProductos.btnBuscarPorUbicacion, ActionEvent.ACTION_PERFORMED, "");
            controlador.actionPerformed(event);

            assertEquals(1, vista.panelProductos.modeloTabla.getRowCount());
            assertEquals("STOCK BAJO", vista.panelProductos.modeloTabla.getValueAt(0, 7));
        }
    }

    // ====== Tests for generarReporte Historial de Accesos ======

    @Test
    public void testGenerarReporteHistorialAccesos() {
        try (MockedStatic<JOptionPane> mockedPane = mockStatic(JOptionPane.class);
             MockedStatic<App.Main> mockedMain = mockStatic(App.Main.class)) {

            DAO.ServicioAutenticacion mockAuth = mock(DAO.ServicioAutenticacion.class);
            when(mockAuth.getUsuarioActual()).thenReturn("admin");
            mockedMain.when(App.Main::getServicioAutenticacion).thenReturn(mockAuth);

            vista.panelReportes.cmbTipoReporte.setSelectedItem("Historial de Accesos");

            AccesoSistema acceso = new AccesoSistema("admin", "Administrador", "2025-01-01 10:00:00", "EXITOSO", "localhost", "Inicio de sesión");
            List<AccesoSistema> accesos = new ArrayList<>();
            accesos.add(acceso);

            when(mockAccesoDAO.obtenerTodosAccesos()).thenReturn(accesos);
            when(mockAccesoDAO.contarAccesosTotales()).thenReturn(10L);
            when(mockAccesoDAO.contarAccesosExitosos()).thenReturn(8L);
            when(mockAccesoDAO.contarAccesosFallidos()).thenReturn(2L);

            ActionEvent event = new ActionEvent(vista.panelReportes.btnGenerarReporte, ActionEvent.ACTION_PERFORMED, "");
            controlador.actionPerformed(event);

            String reporte = vista.panelReportes.txtReporte.getText();
            assertTrue(reporte.contains("HISTORIAL DE ACCESOS AL SISTEMA"));
            assertTrue(reporte.contains("Total de intentos de acceso: 10"));
            assertTrue(reporte.contains("Accesos exitosos: 8"));
        }
    }

    @Test
    public void testGenerarReporteHistorialAccesosVacio() {
        try (MockedStatic<JOptionPane> mockedPane = mockStatic(JOptionPane.class);
             MockedStatic<App.Main> mockedMain = mockStatic(App.Main.class)) {

            DAO.ServicioAutenticacion mockAuth = mock(DAO.ServicioAutenticacion.class);
            when(mockAuth.getUsuarioActual()).thenReturn("admin");
            mockedMain.when(App.Main::getServicioAutenticacion).thenReturn(mockAuth);

            vista.panelReportes.cmbTipoReporte.setSelectedItem("Historial de Accesos");

            when(mockAccesoDAO.obtenerTodosAccesos()).thenReturn(new ArrayList<>());
            when(mockAccesoDAO.contarAccesosTotales()).thenReturn(0L);
            when(mockAccesoDAO.contarAccesosExitosos()).thenReturn(0L);
            when(mockAccesoDAO.contarAccesosFallidos()).thenReturn(0L);

            ActionEvent event = new ActionEvent(vista.panelReportes.btnGenerarReporte, ActionEvent.ACTION_PERFORMED, "");
            controlador.actionPerformed(event);

            String reporte = vista.panelReportes.txtReporte.getText();
            assertTrue(reporte.contains("No hay registros de acceso"));
        }
    }

    // ====== Tests for valueChanged ======

    @Test
    public void testValueChangedConProducto() {
        try (MockedStatic<JOptionPane> mocked = mockStatic(JOptionPane.class)) {
            // Add a row to the product table
            when(mockModelo.obtenerTodosProductos()).thenReturn(new Producto[0]);
            vista.panelProductos.modeloTabla.addRow(new Object[]{"P001", "Martillo", "Herramienta", 10, "$12.50", 5, "A-1-2", "OK"});

            Producto p = new Producto("P001", "Martillo", "Herramienta", 10, 12.50, 5, "A", "1", "2");
            when(mockModelo.buscarProductoPorCodigo("P001")).thenReturn(p);

            // Simulate row selection
            vista.panelProductos.tablaProductos.setRowSelectionInterval(0, 0);

            // Fire valueChanged manually
            javax.swing.event.ListSelectionEvent lse = new javax.swing.event.ListSelectionEvent(
                vista.panelProductos.tablaProductos.getSelectionModel(), 0, 0, false);
            controlador.valueChanged(lse);

            assertEquals("P001", vista.panelProductos.txtCodigo.getText());
            assertEquals("Martillo", vista.panelProductos.txtNombre.getText());
            assertEquals("A", vista.panelProductos.txtPasillo.getText());
            assertFalse(vista.panelProductos.txtCodigo.isEditable());
        }
    }

    @Test
    public void testValueChangedProductoNoEncontrado() {
        try (MockedStatic<JOptionPane> mocked = mockStatic(JOptionPane.class)) {
            when(mockModelo.obtenerTodosProductos()).thenReturn(new Producto[0]);
            vista.panelProductos.modeloTabla.addRow(new Object[]{"P999", "Ghost", "Desc", 0, "$0.00", 0, "", "OK"});

            when(mockModelo.buscarProductoPorCodigo("P999")).thenReturn(null);

            vista.panelProductos.tablaProductos.setRowSelectionInterval(0, 0);

            javax.swing.event.ListSelectionEvent lse = new javax.swing.event.ListSelectionEvent(
                vista.panelProductos.tablaProductos.getSelectionModel(), 0, 0, false);
            controlador.valueChanged(lse);

            // Form should not be filled
        }
    }

    @Test
    public void testValueChangedAdjusting() {
        // When isAdjusting is true, nothing should happen
        javax.swing.event.ListSelectionEvent lse = new javax.swing.event.ListSelectionEvent(
            vista.panelProductos.tablaProductos.getSelectionModel(), 0, 0, true);
        controlador.valueChanged(lse);
        // No exception, no interaction with model
        verify(mockModelo, never()).buscarProductoPorCodigo(anyString());
    }

    @Test
    public void testValueChangedNoSelection() {
        vista.panelProductos.tablaProductos.clearSelection();
        javax.swing.event.ListSelectionEvent lse = new javax.swing.event.ListSelectionEvent(
            vista.panelProductos.tablaProductos.getSelectionModel(), 0, 0, false);
        controlador.valueChanged(lse);
        verify(mockModelo, never()).buscarProductoPorCodigo(anyString());
    }

    // ====== Tests for actualizarTablaProductos with data ======

    @Test
    public void testActualizarTablaConProductos() {
        try (MockedStatic<JOptionPane> mocked = mockStatic(JOptionPane.class)) {
            Producto p1 = new Producto("P001", "Martillo", "Herramienta", 2, 12.50, 5, "A", "1", "2");
            Producto p2 = new Producto("P002", "Sierra", "Herramienta", 100, 25.00, 10, "B", "2", "1");
            when(mockModelo.obtenerTodosProductos()).thenReturn(new Producto[]{p1, p2});
            when(mockModelo.eliminarProducto("P001")).thenReturn(true);

            // Add row and select to trigger eliminar which calls actualizarTablaProductos
            vista.panelProductos.modeloTabla.addRow(new Object[]{"P001", "Martillo", "Herramienta", 2, "$12.50", 5, "A-1-2", "STOCK BAJO"});
            vista.panelProductos.tablaProductos.setRowSelectionInterval(0, 0);

            mocked.when(() -> JOptionPane.showConfirmDialog(any(), any(), anyString(), anyInt()))
                  .thenReturn(JOptionPane.YES_OPTION);

            ActionEvent event = new ActionEvent(vista.panelProductos.btnEliminarProducto, ActionEvent.ACTION_PERFORMED, "");
            controlador.actionPerformed(event);

            // Table should now have 2 rows from obtenerTodosProductos
            assertEquals(2, vista.panelProductos.modeloTabla.getRowCount());
            assertEquals("STOCK BAJO", vista.panelProductos.modeloTabla.getValueAt(0, 7));
            assertEquals("OK", vista.panelProductos.modeloTabla.getValueAt(1, 7));
        }
    }

    // ====== Tests for cargarProductosAlCombo sin stock ======

    @Test
    public void testCargarProductosAlComboSinStock() {
        try (MockedStatic<JOptionPane> mocked = mockStatic(JOptionPane.class)) {
            Producto p1 = new Producto("P001", "Martillo", "D", 0, 12.50, 5);
            Producto p2 = new Producto("P002", "Sierra", "D", 0, 8.00, 2);
            when(mockModelo.obtenerTodosProductos()).thenReturn(new Producto[]{p1, p2});
            when(mockModelo.obtenerCantidadProductos()).thenReturn(2);

            // Trigger cargarProductosAlCombo via limpiarFormularioProducto after eliminar exitoso
            when(mockModelo.eliminarProducto("P001")).thenReturn(true);
            when(mockModelo.obtenerTodasVentas()).thenReturn(new Venta[0]);

            // We can trigger it by creating a new controlador which calls cargarProductosAlCombo in constructor
            ControladorInventario ctrl2 = new ControladorInventario(vista, mockModelo, new CarritoCompra(), mockAccesoDAO);

            // The combo should have 0 items since all products have stock 0
            assertEquals(0, vista.panelVentas.cmbProductos.getItemCount());
        }
    }

    // ====== Tests for procesarVenta with actualizarTablaVentas ======

    @Test
    public void testProcesarVentaConVentasEnTabla() {
        try (MockedStatic<JOptionPane> mocked = mockStatic(JOptionPane.class)) {
            mocked.when(() -> JOptionPane.showConfirmDialog(any(), any(), anyString(), anyInt(), anyInt()))
                  .thenReturn(JOptionPane.YES_OPTION);

            Producto p = new Producto("P001", "Martillo", "D", 10, 12.50, 5);
            carrito.agregarProducto(p, 2);

            when(mockModelo.buscarProductoPorCodigo("P001")).thenReturn(p);
            when(mockModelo.obtenerTodosProductos()).thenReturn(new Producto[]{p});
            Venta v = new Venta("P001", "Martillo", 2, 12.50, 25.0);
            when(mockModelo.obtenerTodasVentas()).thenReturn(new Venta[]{v});

            ActionEvent event = new ActionEvent(vista.panelVentas.btnProcesarVenta, ActionEvent.ACTION_PERFORMED, "");
            controlador.actionPerformed(event);

            // After processing, ventas table should have the sale
            assertEquals(1, vista.panelVentas.modeloTabla.getRowCount());
            assertTrue(carrito.estaVacio());
        }
    }
}
