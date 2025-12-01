package Controlador;

import Modelo.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import Repositorio.TransaccionRepository;
import Repositorio.impl.TransaccionRepositoryMongo;

/**
 * Controlador para gestión de transacciones contables
 * Persistencia MongoDB
 */
public class ControladorTransaccion {
    private static ControladorTransaccion instancia;
    private List<Transaccion> transacciones;
    private ControladorBitacora controladorBitacora;
    private TransaccionRepository transaccionRepository;
    
    private ControladorTransaccion() {
        controladorBitacora = ControladorBitacora.getInstancia();
        try {
            transaccionRepository = new TransaccionRepositoryMongo();
            transacciones = transaccionRepository.findAll();
        } catch (Exception e) {
            transaccionRepository = null;
            transacciones = new ArrayList<>();
        }
    }
    
    public static ControladorTransaccion getInstancia() {
        if (instancia == null) {
            instancia = new ControladorTransaccion();
        }
        return instancia;
    }
    
    /**
     * Registra una nueva factura
     */
    public boolean registrarFactura(LocalDate fecha, String cliente, double subtotal,
                                   String cuentaContable, String numeroFactura, 
                                   Usuario usuario) {
        Factura factura = new Factura(null, fecha, cliente, subtotal, 
                                     cuentaContable, numeroFactura, usuario);
        if (!factura.validar()) return false;
        if (transaccionRepository != null) {
            transaccionRepository.saveFactura(factura);
            transacciones = transaccionRepository.findAll();
        } else {
            transacciones.add(factura);
        }
        controladorBitacora.registrar(usuario, "REGISTRAR_FACTURA", 
            String.format("Factura %s por $%.2f", numeroFactura, factura.getMonto()));
        return true;
    }
    
    /**
     * Registra un nuevo gasto
     */
    public boolean registrarGasto(LocalDate fecha, String proveedor, double monto,
                                 String cuentaContable, String numeroComprobante,
                                 boolean deducible, Usuario usuario) {
        Gasto gasto = new Gasto(null, fecha, proveedor, monto, 
                               cuentaContable, numeroComprobante, deducible, usuario);
        if (!gasto.validar()) return false;
        if (transaccionRepository != null) {
            transaccionRepository.saveGasto(gasto);
            transacciones = transaccionRepository.findAll();
        } else {
            transacciones.add(gasto);
        }
        controladorBitacora.registrar(usuario, "REGISTRAR_GASTO", 
            String.format("Gasto %s por $%.2f", numeroComprobante, monto));
        return true;
    }
    
    /**
     * Aprueba una transacción (solo Jefatura)
     */
    public boolean aprobarTransaccion(String idTransaccion, Usuario usuario) {
        if (!usuario.tienePermiso("APROBAR_TRANSACCION")) {
            return false;
        }
        
        for (Transaccion t : transacciones) {
            if (t.getIdTransaccion().equals(idTransaccion)) {
                t.setEstado(Transaccion.ESTADO_APROBADO);
                if (transaccionRepository != null) transaccionRepository.updateEstado(idTransaccion, Transaccion.ESTADO_APROBADO);
                controladorBitacora.registrar(usuario, "APROBAR_TRANSACCION", 
                    "Transacción #" + idTransaccion + " aprobada");
                return true;
            }
        }
        return false;
    }
    
    /**
     * Rechaza una transacción (solo Jefatura)
     */
    public boolean rechazarTransaccion(String idTransaccion, Usuario usuario) {
        if (!usuario.tienePermiso("APROBAR_TRANSACCION")) { // Mismo permiso que aprobar
            return false;
        }
        
        for (Transaccion t : transacciones) {
            if (t.getIdTransaccion().equals(idTransaccion)) {
                t.setEstado(Transaccion.ESTADO_RECHAZADO);
                if (transaccionRepository != null) transaccionRepository.updateEstado(idTransaccion, Transaccion.ESTADO_RECHAZADO);
                controladorBitacora.registrar(usuario, "RECHAZAR_TRANSACCION", 
                    "Transacción #" + idTransaccion + " rechazada");
                return true;
            }
        }
        return false;
    }
    
    /**
     * Elimina una factura con errores
     */
    public boolean eliminarFactura(String idTransaccion, Usuario usuario) {
        if (!usuario.tienePermiso("ELIMINAR_FACTURA")) {
            return false;
        }
        
        for (Transaccion t : transacciones) {
            if (t.getIdTransaccion().equals(idTransaccion) && 
                t.getEstado().equals(Transaccion.ESTADO_REGISTRADO)) {
                t.setEstado(Transaccion.ESTADO_ELIMINADO);
                if (transaccionRepository != null) transaccionRepository.eliminarLogico(idTransaccion);
                controladorBitacora.registrar(usuario, "ELIMINAR_FACTURA", 
                    "Factura #" + idTransaccion + " eliminada");
                return true;
            }
        }
        return false;
    }
    
    /**
     * Obtiene todas las transacciones activas
     */
    public List<Transaccion> getTransaccionesActivas() {
        return transacciones.stream()
            .filter(t -> !t.getEstado().equals(Transaccion.ESTADO_ELIMINADO))
            .collect(Collectors.toList());
    }
    
    /**
     * Calcula la retención de IVA anual
     * Retiene el 30% del IVA en compras (gastos)
     */
    public double calcularRetencionIVA(int anio) {
        double ivaCompras = 0;
        
        for (Transaccion t : transacciones) {
            if (t.getFecha().getYear() == anio && 
                t.getEstado().equals(Transaccion.ESTADO_APROBADO)) {
                if (t instanceof Gasto) {
                    ivaCompras += ((Gasto) t).getIvaCompra();
                }
            }
        }
        
        // Retención del 30% del IVA en compras
        return ivaCompras * 0.30;
    }
    
    public List<Transaccion> getTransacciones() {
        return new ArrayList<>(transacciones);
    }
}
