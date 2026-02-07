package DAO;

import model.Usuario;
import DAO.AccesoSistemaDAO;
import model.AccesoSistema;
import model.Rol;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.PasswordHasher;

public class ServicioAutenticacion {
    private DAOUsuario daoUsuario;
    private DAORol daoRol;
    private Usuario usuarioActual;
    private Rol rolActual;
    private AccesoSistemaDAO accesoDAO;
    private static final Logger logger = LoggerFactory.getLogger(ServicioAutenticacion.class);

    public ServicioAutenticacion() {
        this.daoUsuario = new DAOUsuario();
        this.daoRol = new DAORol();
        this.accesoDAO = new AccesoSistemaDAO();
        inicializarAdminPorDefecto();
    }

    private void inicializarAdminPorDefecto() {
        Usuario admin = daoUsuario.buscarUsuarioPorNombre("admin");
        if (admin == null) {
            logger.info("Creando usuario administrador por defecto...");
            
            Rol rolAdmin = daoRol.buscarRolPorNombre("Administrador");
            if (rolAdmin == null) {
                logger.info("Creando rol Administrador...");
                rolAdmin = new Rol("Administrador");
                rolAdmin.agregarPermiso("puedeGestionarProductos");
                rolAdmin.agregarPermiso("puedeVender");
                rolAdmin.agregarPermiso("puedeVerReportes");
                rolAdmin.agregarPermiso("puedeExportar");
                rolAdmin.agregarPermiso("puedeGestionarUsuarios");
                daoRol.crearRol(rolAdmin);
            }

            Usuario nuevoAdmin = new Usuario("admin", "admin", "Administrador del Sistema", "Administrador");
            
            if (daoUsuario.crearUsuario(nuevoAdmin)) {
                daoRol.actualizarContadorUsuarios("Administrador", 1);
                logger.info("Usuario administrador creado: admin / admin");
            } else {
                logger.error("Error al crear usuario administrador");
            }
        } else {
            logger.info("Usuario admin ya existe en el sistema");
            if (!PasswordHasher.isHashed(admin.getContrasena())) {
                logger.info("Migrando contraseña de admin...");
                daoUsuario.cambiarContrasena("admin", admin.getContrasena());
            }
        }
    }

    public boolean autenticar(String usuario, String contrasena) {
    	logger.info("Intentando autenticar usuario: " + usuario);
        
        // Validar entrada
        if (usuario == null || contrasena == null) {
            logger.error("⚠️ Usuario o contraseña nulos");
            return false;
        }
        
        Usuario user = daoUsuario.buscarUsuarioPorNombre(usuario);
        if (user == null) {
            accesoDAO.registrarAcceso(new AccesoSistema(
                usuario != null ? usuario : "NULO", 
                "NO_ENCONTRADO", 
                "FALLIDO", 
                "Usuario no existe en el sistema"
            ));
            logger.error("Usuario no encontrado: " + usuario);
            return false;
        }
        
        // Verificar que el rol no sea null
        String rolUsuario = user.getRol();
        if (rolUsuario == null) {
            rolUsuario = "SIN_ROL";
            logger.error("⚠️ Usuario " + usuario + " no tiene rol asignado");
        }
        
        if (user.isBloqueado()) {
            accesoDAO.registrarAcceso(new AccesoSistema(
                usuario, 
                user.getRol(), 
                "FALLIDO", 
                "Usuario bloqueado"
            ));
            logger.error("Usuario bloqueado: " + usuario);
            return false;
        }
        
        boolean autenticado = daoUsuario.autenticar(usuario, contrasena);
        
        if (autenticado) {
            this.usuarioActual = user;
            if (this.usuarioActual != null) {
                this.rolActual = daoRol.buscarRolPorNombre(usuarioActual.getRol());
                
                accesoDAO.registrarAcceso(new AccesoSistema(
                    usuario, 
                    usuarioActual.getRol(), 
                    "EXITOSO", 
                    "Acceso concedido al sistema"
                ));
                
                logger.info("Usuario autenticado correctamente: " + usuario);
                logger.info("Rol asignado: " + usuarioActual.getRol());
                
                if (rolActual != null) {
                    logger.info("Permisos cargados: " + rolActual.getPermisos());
                } else {
                    logger.error("Rol no encontrado, creando rol básico: " + usuarioActual.getRol());
                    crearRolBasico(usuarioActual.getRol());
                    this.rolActual = daoRol.buscarRolPorNombre(usuarioActual.getRol());
                }
                return true;
            }
        } else {
            accesoDAO.registrarAcceso(new AccesoSistema(
                usuario, 
                user.getRol(), 
                "FALLIDO", 
                "Contraseña incorrecta"
            ));
        }
        
        logger.error("Autenticación fallida para: " + usuario);
        return false;
    }

    private void crearRolBasico(String nombreRol) {
        logger.info("Creando rol básico: " + nombreRol);
        Rol rolBasico = new Rol(nombreRol);
        rolBasico.agregarPermiso("puedeGestionarProductos");
        rolBasico.agregarPermiso("puedeVender");
        daoRol.crearRol(rolBasico);
    }

    public boolean tienePermiso(String permiso) {
    	 if (permiso == null) {
    	        return false;
    	    }
    	    
    	    String usuarioActual = getUsuarioActual();
    	    if (usuarioActual != null && "admin".equals(usuarioActual)) {
    	        return true;
    	    }
    	    
    	    if (rolActual == null) {
    	        return false;
    	    }
    	    
    	    return rolActual.tienePermiso(permiso);
    }

    public String getUsuarioActual() {
        return usuarioActual != null ? usuarioActual.getUsuario() : null;
    }

    public String getRolActual() {
        return usuarioActual != null ? usuarioActual.getRol() : null;
    }

    public Usuario getUsuarioActualObj() {
        return usuarioActual;
    }

    public void cerrarSesion() {
        logger.info("Cerrando sesión de: " + (usuarioActual != null ? usuarioActual.getUsuario() : "N/A"));
        this.usuarioActual = null;
        this.rolActual = null;
    }

    public List<String> getPermisosActuales() {
        return (rolActual != null) ? rolActual.getPermisos() : null;
    }

    public void mostrarEstadisticasAccesos() {
        try {
            long total = accesoDAO.contarAccesosTotales();
            long exitosos = accesoDAO.contarAccesosExitosos();
            long fallidos = accesoDAO.contarAccesosFallidos();
            
            logger.info("\n--- Estadísticas de Accesos ---");
            logger.info("Total de accesos: " + total);
            logger.info("Accesos exitosos: " + exitosos);
            logger.info("Accesos fallidos: " + fallidos);
            if (total > 0) {
                logger.info("Tasa de éxito: " + String.format("%.2f%%", (exitosos * 100.0 / total)));
            }
            logger.info("-------------------------------\n");
        } catch (Exception e) {
            logger.error("Error al obtener estadísticas: " + e.getMessage());
        }
    }
}