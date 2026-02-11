package controller;

import view.VentanaPrincipal;
import model.InventarioDAO;
import DAO.AccesoSistemaDAO;
import model.AccesoSistema;
import model.Producto;
import model.Venta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDate;
import java.util.List;

public class ControladorReportes {
    private static final Logger logger = LoggerFactory.getLogger(ControladorReportes.class);
    private VentanaPrincipal vista;
    private InventarioDAO modelo;
    private AccesoSistemaDAO accesoDAO;
    
    public ControladorReportes(VentanaPrincipal vista, InventarioDAO modelo, AccesoSistemaDAO accesoDAO) {
        this.vista = vista;
        this.modelo = modelo;
        this.accesoDAO = accesoDAO;
        
        // Registrar listener específico de reportes
        if (vista.panelReportes != null && vista.panelReportes.btnGenerarReporte != null) {
            this.vista.panelReportes.btnGenerarReporte.addActionListener(e -> generarReporte());
        }
    }

    private void generarReporte() {
        if (vista == null || vista.panelReportes == null) {
            logger.error("⚠️ Componentes de vista no inicializados para generar reporte");
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
    
    private StringBuilder generarReporteCatalogo() {
        StringBuilder reporte = new StringBuilder();
        reporte.append("=== CATÁLOGO DE PRODUCTOS ===\n");
        reporte.append("Fecha: ").append(LocalDate.now()).append("\n\n");
        
        agregarProductosAlCatalogo(reporte);
        
        return reporte;
    }
    
    private StringBuilder generarReporteHistorialAccesos() {
        StringBuilder reporte = new StringBuilder();
        
        agregarEncabezadoHistorialAccesos(reporte);
        agregarEstadisticasAccesos(reporte);
        agregarDetalleAccesos(reporte);
        
        return reporte;
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
    
    // Métodos auxiliares para Reporte de Ventas
    private double agregarDetalleVentas(StringBuilder reporte) {
        if (reporte == null || modelo == null) {
            return 0;
        }
        
        Venta[] ventas = modelo.obtenerTodasVentas();
        if (ventas == null || ventas.length == 0) {
            reporte.append("No hay datos de ventas disponibles.\n");
            return 0;
        }
        
        double totalVentas = 0;
        StringBuilder linea = new StringBuilder();
        
        for (Venta venta : ventas) {
            if (venta == null) {
                continue;
            }
            
            String nombre = venta.getNombreProducto() != null ? 
                    sanitizarTexto(venta.getNombreProducto()) : "Producto desconocido";
            int cantidad = venta.getCantidad();
            double total = venta.getTotal();
            String fecha = venta.getFecha() != null ? venta.getFecha() : "Fecha desconocida";
            
            linea.setLength(0); // Limpiar contenido anterior
            linea.append("Producto: ")
                  .append(nombre)
                  .append(" | Cant: ")
                  .append(cantidad)
                  .append(" | Total: $")
                  .append(String.format("%.2f", total))
                  .append(" | Fecha: ")
                  .append(fecha)
                  .append("\n");
            
            reporte.append(linea);
            totalVentas += total;
        }
        return totalVentas;
    }
    
    private void agregarTotalVentas(StringBuilder reporte, double totalVentas) {
        reporte.append(String.format("\nTOTAL VENTAS: $%.2f", totalVentas));
    }
    
    // Métodos auxiliares para Reporte de Stock Bajo
    private boolean agregarProductosStockBajo(StringBuilder reporte) {
        boolean tieneStockBajo = false;
        Producto[] productos = modelo.obtenerTodosProductos();
        
        if (productos != null) {
            for (Producto producto : productos) {
                if (producto != null && producto.getStock() <= producto.getStockMinimo()) {
                    String detalleProducto = String.format(
                        "Producto: %s | Stock Actual: %d | Mínimo: %d\n",
                        producto.getNombre(), producto.getStock(), producto.getStockMinimo()
                    );
                    reporte.append(detalleProducto);
                    tieneStockBajo = true;
                }
            }
        }
        return tieneStockBajo;
    }
    
    // Métodos auxiliares para Catálogo de Productos
    private void agregarProductosAlCatalogo(StringBuilder reporte) {
        Producto[] productos = modelo.obtenerTodosProductos();
        
        if (productos != null) {
            for (Producto producto : productos) {
                if (producto != null) {
                    String detalleProducto = String.format(
                        "Código: %s | Nombre: %s | Stock: %d | Precio: $%.2f\n",
                        producto.getCodigo(), producto.getNombre(), 
                        producto.getStock(), producto.getPrecio()
                    );
                    reporte.append(detalleProducto);
                }
            }
        } else {
            reporte.append("No hay productos en el catálogo.\n");
        }
    }
    
    // Métodos auxiliares para Historial de Accesos
    private void agregarEncabezadoHistorialAccesos(StringBuilder reporte) {
        if (reporte == null) return;
        
        reporte.append("=== HISTORIAL DE ACCESOS AL SISTEMA ===\n");
        reporte.append("Fecha del reporte: ").append(LocalDate.now()).append("\n");
        reporte.append("Generado por: Sistema de Reportes\n\n");
    }
    
    private void agregarEstadisticasAccesos(StringBuilder reporte) {
        if (reporte == null || accesoDAO == null) {
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
        if (reporte == null || accesoDAO == null) {
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
                if (acceso == null) continue;
                
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
    
    private String sanitizarTexto(String texto) {
        if (texto == null) return "";
        
        // Remover caracteres peligrosos para HTML/JavaScript
        return texto.replaceAll("[<>\"'&;]", "");
    }
}