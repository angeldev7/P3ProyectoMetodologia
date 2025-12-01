package Controlador;

import Modelo.Bitacora;
import Modelo.Usuario;
import java.util.ArrayList;
import java.util.List;
import Repositorio.BitacoraRepository;
import Repositorio.impl.BitacoraRepositoryMongo;

/**
 * Controlador para gestión de la bitácora de auditoría
 * Persistencia MongoDB
 */
public class ControladorBitacora {
    private static ControladorBitacora instancia;
    private List<Bitacora> registros;
    private BitacoraRepository bitacoraRepository;
    
    private ControladorBitacora() {
        registros = new ArrayList<>();
        try {
            bitacoraRepository = new BitacoraRepositoryMongo();
            registros = bitacoraRepository.findAll();
        } catch (Exception e) {
            bitacoraRepository = null;
        }
    }
    
    public static ControladorBitacora getInstancia() {
        if (instancia == null) {
            instancia = new ControladorBitacora();
        }
        return instancia;
    }
    
    /**
     * Registra una acción en la bitácora
     */
    public void registrar(Usuario usuario, String accion, String descripcion) {
        Bitacora registro = new Bitacora(null, usuario, accion, descripcion);
        if (bitacoraRepository != null) {
            bitacoraRepository.registrar(registro);
            registros = bitacoraRepository.findAll();
        } else {
            registros.add(registro);
        }
    }
    
    /**
     * Obtiene todos los registros de la bitácora
     */
    public List<Bitacora> getRegistros() {
        if (bitacoraRepository != null) {
            registros = bitacoraRepository.findAll();
        }
        return new ArrayList<>(registros);
    }

    public List<Bitacora> getUltimosRegistros(int cantidad) {
        return bitacoraRepository.findLast(cantidad);
    }

    public List<Bitacora> getRegistrosPorUsuario(String idUsuario) {
        return bitacoraRepository.findByUsuario(idUsuario);
    }
}
