
package util;

import DAO.DAOUsuario;
import model.Usuario;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MigradorContrasenas {
	private static final Logger logger = LoggerFactory.getLogger(MigradorContrasenas.class);
    public static void migrarUsuariosExistentes() {
        System.out.println("🔄 Iniciando migración de contraseñas...");
        DAOUsuario daoUsuario = new DAOUsuario();
        List<Usuario> usuarios = daoUsuario.obtenerTodosUsuarios();
        
        
        int migrados = 0;
        for (Usuario usuario : usuarios) {
            // Si la contraseña NO está hasheada, migrarla
            if (!PasswordHasher.isHashed(usuario.getContrasena())) {
                logger.info("🔄 Migrando contraseña para: " + usuario.getUsuario());
                if (daoUsuario.cambiarContrasena(usuario.getUsuario(), usuario.getContrasena())) {
                    migrados++;
                }
            }
        }
        
        logger.info("✅ Migración completada: " + migrados + " usuarios migrados");
    }
}