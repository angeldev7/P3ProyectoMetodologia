package Repositorio;

import Modelo.Usuario;
import java.util.List;

public interface UsuarioRepository {
    Usuario findByNombreUsuario(String nombreUsuario);
    boolean existsByNombreUsuario(String nombreUsuario);
    List<Usuario> findAll();
    Usuario save(Usuario usuario);
    boolean deleteById(String idUsuario);
    long count();
}
