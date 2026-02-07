
package util;

import DAO.DAOUsuario;
import model.Usuario;
import java.util.List;

public class MigradorContrasenas {
    
    public static void migrarUsuariosExistentes() {
        System.out.println("🔄 Iniciando migración de contraseñas...");
        DAOUsuario daoUsuario = new DAOUsuario();
        List<Usuario> usuarios = daoUsuario.obtenerTodosUsuarios();
        
        int migrados = 0;
        for (Usuario usuario : usuarios) {
            // Si la contraseña NO está hasheada, migrarla
            if (!PasswordHasher.isHashed(usuario.getContrasena())) {
                System.out.println("🔄 Migrando contraseña para: " + usuario.getUsuario());
                if (daoUsuario.cambiarContrasena(usuario.getUsuario(), usuario.getContrasena())) {
                    migrados++;
                }
            }
        }
        
        System.out.println("Migración completada: " + migrados + " usuarios migrados");
    }
}