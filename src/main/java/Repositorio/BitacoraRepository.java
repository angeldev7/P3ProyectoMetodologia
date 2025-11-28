package Repositorio;

import Modelo.Bitacora;
import java.util.List;

public interface BitacoraRepository {
    Bitacora registrar(Bitacora bitacora);
    List<Bitacora> findAll();
    List<Bitacora> findLast(int cantidad);
    List<Bitacora> findByUsuario(String idUsuario);
    long count();
}
