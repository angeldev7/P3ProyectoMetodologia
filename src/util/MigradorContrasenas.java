
package util;

import DAO.DAOUsuario;
import model.Usuario;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MigradorContrasenas {
	private static final Logger logger = LoggerFactory.getLogger(MigradorContrasenas.class);
    public static void migrarUsuariosExistentes() {
        logger.info("Iniciando migracion de contrasenas...");
        DAOUsuario daoUsuario = new DAOUsuario();
        List<Usuario> usuarios = daoUsuario.obtenerTodosUsuarios();
        
        
        int migrados = 0;
        for (Usuario usuario : usuarios) {
            // Si la contraseña NO está hasheada, migrarla
            if (!PasswordHasher.isHashed(usuario.getContrasena())) {
                logger.info("Migrando contrasena para: " + usuario.getUsuario());
                if (daoUsuario.cambiarContrasena(usuario.getUsuario(), usuario.getContrasena())) {
                    migrados++;
                }
            }
        }
        
        logger.info("Migracion completada: " + migrados + " usuarios migrados");
    }
}