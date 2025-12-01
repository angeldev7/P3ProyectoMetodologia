package Repositorio;

import Modelo.RolPersonalizado;
import java.util.List;

/**
 * Interfaz para gestión de roles personalizados
 */
public interface RolRepository {
    /**
     * Guarda un rol personalizado
     */
    void save(RolPersonalizado rol);
    
    /**
     * Busca un rol por su nombre
     */
    RolPersonalizado findByNombre(String nombreRol);
    
    /**
     * Obtiene todos los roles personalizados
     */
    List<RolPersonalizado> findAll();
    
    /**
     * Elimina un rol por su ID
     */
    boolean deleteById(String idRol);
    
    /**
     * Verifica si existe un rol con el nombre dado
     */
    boolean existsByNombre(String nombreRol);
}
