package Repositorio;

import Modelo.Transaccion;
import Modelo.Factura;
import Modelo.Gasto;
import java.util.List;

public interface TransaccionRepository {
    Factura saveFactura(Factura factura);
    Gasto saveGasto(Gasto gasto);
    boolean updateEstado(String idTransaccion, String nuevoEstado);
    boolean eliminarLogico(String idTransaccion); // marca como eliminado
    List<Transaccion> findAll();
    List<Transaccion> findActive();
    long count();
}
