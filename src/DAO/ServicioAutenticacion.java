package DAO;

import model.Usuario;
import DAO.AccesoSistemaDAO;
import model.AccesoSistema;
import model.Rol;
import java.util.List;
import util.PasswordHasher;

public class ServicioAutenticacion {
    private DAOUsuario daoUsuario;
    private DAORol daoRol;
    private Usuario usuarioActual;
    private Rol rolActual;
    private AccesoSistemaDAO accesoDAO;

    public ServicioAutenticacion() {
        this.daoUsuario = new DAOUsuario();
        this.daoRol = new DAORol();
        this.accesoDAO = new AccesoSistemaDAO();
        inicializarAdminPorDefecto();
    }

    private void inicializarAdminPorDefecto() {
        Usuario admin = daoUsuario.buscarUsuarioPorNombre("admin");
        if (admin == null) {
            System.out.println("Creando usuario administrador por defecto...");
            
            Rol rolAdmin = daoRol.buscarRolPorNombre("Administrador");
            if (rolAdmin == null) {
                System.out.println("Creando rol Administrador...");
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
                System.out.println("Usuario administrador creado: admin / admin");
            } else {
                System.err.println("Error al crear usuario administrador");
            }
        } else {
            System.out.println("Usuario admin ya existe en el sistema");
            if (!PasswordHasher.isHashed(admin.getContrasena())) {
                System.out.println("Migrando contraseña de admin...");
                daoUsuario.cambiarContrasena("admin", admin.getContrasena());
            }
        }
    }

    public boolean autenticar(String usuario, String contrasena) {
        System.out.println("Intentando autenticar usuario: " + usuario);
        
        Usuario user = daoUsuario.buscarUsuarioPorNombre(usuario);
        if (user == null) {
            accesoDAO.registrarAcceso(new AccesoSistema(
                usuario, 
                "NO_ENCONTRADO", 
                "FALLIDO", 
                "Usuario no existe en el sistema"
            ));
            System.err.println("Usuario no encontrado: " + usuario);
            return false;
        }
        
        if (user.isBloqueado()) {
            accesoDAO.registrarAcceso(new AccesoSistema(
                usuario, 
                user.getRol(), 
                "FALLIDO", 
                "Usuario bloqueado"
            ));
            System.err.println("Usuario bloqueado: " + usuario);
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
                
                System.out.println("Usuario autenticado correctamente: " + usuario);
                System.out.println("Rol asignado: " + usuarioActual.getRol());
                
                if (rolActual != null) {
                    System.out.println("Permisos cargados: " + rolActual.getPermisos());
                } else {
                    System.err.println("Rol no encontrado, creando rol básico: " + usuarioActual.getRol());
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
        
        System.err.println("Autenticación fallida para: " + usuario);
        return false;
    }

    private void crearRolBasico(String nombreRol) {
        System.out.println("Creando rol básico: " + nombreRol);
        Rol rolBasico = new Rol(nombreRol);
        rolBasico.agregarPermiso("puedeGestionarProductos");
        rolBasico.agregarPermiso("puedeVender");
        daoRol.crearRol(rolBasico);
    }

    public boolean tienePermiso(String permiso) {
        if ("admin".equals(getUsuarioActual())) {
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
        System.out.println("Cerrando sesión de: " + (usuarioActual != null ? usuarioActual.getUsuario() : "N/A"));
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
            
            System.out.println("\n--- Estadísticas de Accesos ---");
            System.out.println("Total de accesos: " + total);
            System.out.println("Accesos exitosos: " + exitosos);
            System.out.println("Accesos fallidos: " + fallidos);
            if (total > 0) {
                System.out.println("Tasa de éxito: " + String.format("%.2f%%", (exitosos * 100.0 / total)));
            }
            System.out.println("-------------------------------\n");
        } catch (Exception e) {
            System.err.println("Error al obtener estadísticas: " + e.getMessage());
        }
    }
}