package model;

import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

@DisplayName("Test de la clase ItemCarrito")
class ItemCarritoTest {

    private ItemCarrito item;
    private Producto producto;
    private static final int CANTIDAD = 2;

    @BeforeEach
    void setUp() {
        producto = new Producto("P001", "Martillo", "Martillo de acero", 10, 25.50, 5);
        item = new ItemCarrito(producto, CANTIDAD);
    }

    @Test
    @DisplayName("Constructor debe inicializar correctamente")
    void testConstructor() {
        assertThat(item.getCodigoProducto()).isEqualTo("P001");
        assertThat(item.getNombreProducto()).isEqualTo("Martillo");
        assertThat(item.getCantidad()).isEqualTo(CANTIDAD);
        assertThat(item.getPrecioUnitario()).isEqualTo(25.50);
    }

    @Test
    @DisplayName("getSubtotal debe calcular correctamente")
    void testGetSubtotal() {
        double subtotalEsperado = CANTIDAD * 25.50;
        assertThat(item.getSubtotal()).isEqualTo(subtotalEsperado);
    }

    @Test
    @DisplayName("setCantidad debe actualizar cantidad")
    void testSetCantidad() {
        item.setCantidad(5);
        
        assertThat(item.getCantidad()).isEqualTo(5);
        assertThat(item.getSubtotal()).isEqualTo(127.50); // 5 * 25.50
    }

    @Test
    @DisplayName("getProducto debe retornar el producto")
    void testGetProducto() {
        assertThat(item.getProducto()).isEqualTo(producto);
        assertThat(item.getProducto().getCodigo()).isEqualTo("P001");
    }

    @Test
    @DisplayName("toString debe incluir información del item")
    void testToString() {
        String resultado = item.toString();
        
        assertThat(resultado)
                .contains("P001")
                .contains("Martillo")
                .contains(String.valueOf(CANTIDAD));
    }
}