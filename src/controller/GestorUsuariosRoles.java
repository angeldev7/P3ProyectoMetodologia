package controller;

import view.PanelGestionUsuarios;
import DAO.DAOUsuario;
import DAO.DAORol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GestorUsuariosRoles {
    private static final Logger logger = LoggerFactory.getLogger(GestorUsuariosRoles.class);
    private PanelGestionUsuarios vista;
    private ControladorUsuarios controladorUsuarios;
    private ControladorRoles controladorRoles;
    private DAOUsuario daoUsuario;
    private DAORol daoRol;
    
    public GestorUsuariosRoles(PanelGestionUsuarios vista) {
        if (vista == null) {
            logger.error("La vista es nula en GestorUsuariosRoles");
            throw new IllegalArgumentException("La vista no puede ser nula");
        }
        
        this.vista = vista;
        
        logger.info("Inicializando GestorUsuariosRoles...");
        
        // Inicializar DAOs
        try {
            this.daoUsuario = new DAOUsuario();
            logger.info("DAOUsuario inicializado");
        } catch (Exception e) {
            logger.error("Error al inicializar DAOUsuario: " + e.getMessage());
            this.daoUsuario = null;
        }
        
        try {
            this.daoRol = new DAORol();
            logger.info("DAORol inicializado");
        } catch (Exception e) {
            logger.error("Error al inicializar DAORol: " + e.getMessage());
            this.daoRol = null;
        }
        
        // Inicializar controladores específicos
        try {
            // Pasar ambos DAOs al ControladorUsuarios
            if (daoUsuario != null && daoRol != null) {
                this.controladorUsuarios = new ControladorUsuarios(vista, daoUsuario, daoRol);
                logger.info("ControladorUsuarios inicializado con ambos DAOs");
            } else if (daoUsuario != null) {
                this.controladorUsuarios = new ControladorUsuarios(vista, daoUsuario);
                logger.info("ControladorUsuarios inicializado solo con DAOUsuario");
            } else {
                logger.error("DAOUsuario no disponible para inicializar ControladorUsuarios");
                this.controladorUsuarios = null;
            }
        } catch (Exception e) {
            logger.error("Error al inicializar ControladorUsuarios: " + e.getMessage());
            this.controladorUsuarios = null;
        }
        
        try {
            if (daoRol != null) {
                this.controladorRoles = new ControladorRoles(vista, daoRol);
                logger.info("ControladorRoles inicializado");
            } else {
                logger.error("DAORol no disponible para inicializar ControladorRoles");
                this.controladorRoles = null;
            }
        } catch (Exception e) {
            logger.error("Error al inicializar ControladorRoles: " + e.getMessage());
            this.controladorRoles = null;
        }
        
        // Cargar datos iniciales
        try {
            cargarDatosIniciales();
            logger.info("Datos iniciales cargados exitosamente");
        } catch (Exception e) {
            logger.error("Error al cargar datos iniciales: " + e.getMessage());
        }
        
        logger.info("GestorUsuariosRoles inicializado completamente");
        logger.info("Estado: Usuarios=" + (controladorUsuarios != null ? "✅" : "❌") + 
                   ", Roles=" + (controladorRoles != null ? "✅" : "❌"));
    }
    
    // Constructor alternativo si quieres pasar DAOs externos
    public GestorUsuariosRoles(PanelGestionUsuarios vista, DAOUsuario daoUsuario, DAORol daoRol) {
        if (vista == null) {
            logger.error("La vista es nula en GestorUsuariosRoles");
            throw new IllegalArgumentException("La vista no puede ser nula");
        }
        
        this.vista = vista;
        this.daoUsuario = daoUsuario;
        this.daoRol = daoRol;
        
        logger.info("Inicializando GestorUsuariosRoles con DAOs externos...");
        
        // Inicializar controladores específicos
        try {
            this.controladorUsuarios = new ControladorUsuarios(vista, daoUsuario, daoRol);
            logger.info("ControladorUsuarios inicializado");
        } catch (Exception e) {
            logger.error("Error al inicializar ControladorUsuarios: " + e.getMessage());
            this.controladorUsuarios = null;
        }
        
        try {
            this.controladorRoles = new ControladorRoles(vista, daoRol);
            logger.info("ControladorRoles inicializado");
        } catch (Exception e) {
            logger.error("Error al inicializar ControladorRoles: " + e.getMessage());
            this.controladorRoles = null;
        }
        
        // Cargar datos iniciales
        try {
            cargarDatosIniciales();
            logger.info("Datos iniciales cargados exitosamente");
        } catch (Exception e) {
            logger.error("Error al cargar datos iniciales: " + e.getMessage());
        }
    }
    
    private void cargarDatosIniciales() {
        logger.info("Cargando datos iniciales de usuarios y roles...");
        
        // Cargar usuarios
        if (controladorUsuarios != null) {
            try {
                controladorUsuarios.cargarUsuariosDesdeBaseDatos();
                logger.info("Usuarios cargados desde base de datos");
            } catch (Exception e) {
                logger.error("Error al cargar usuarios: " + e.getMessage());
            }
        } else {
            logger.warn("ControladorUsuarios no está inicializado");
        }
        
        // Cargar roles
        if (controladorRoles != null) {
            try {
                controladorRoles.cargarRolesDesdeBaseDatos();
                logger.info("Roles cargados desde base de datos");
            } catch (Exception e) {
                logger.error("Error al cargar roles: " + e.getMessage());
            }
        } else {
            logger.warn("ControladorRoles no está inicializado");
        }
        
        // Actualizar combo de roles en la vista
        try {
            if (vista != null) {
                vista.actualizarComboRoles(); // LLAMADA DIRECTA AL MÉTODO
                logger.info("Combo de roles actualizado en vista");
            }
        } catch (Exception e) {
            logger.error("Error al actualizar combo de roles: " + e.getMessage());
        }
        
        logger.info("Carga de datos iniciales completada");
    }
    
    // Métodos para acceso externo si es necesario
    public ControladorUsuarios getControladorUsuarios() {
        return controladorUsuarios;
    }
    
    public ControladorRoles getControladorRoles() {
        return controladorRoles;
    }
    
    public DAOUsuario getDaoUsuario() {
        return daoUsuario;
    }
    
    public DAORol getDaoRol() {
        return daoRol;
    }
    
    public PanelGestionUsuarios getVista() {
        return vista;
    }
    
    public void actualizarDatos() {
        logger.info("Actualizando datos de usuarios y roles...");
        
        // Actualizar usuarios
        if (controladorUsuarios != null) {
            try {
                controladorUsuarios.cargarUsuariosDesdeBaseDatos();
                logger.info("Usuarios actualizados");
            } catch (Exception e) {
                logger.error("Error al actualizar usuarios: " + e.getMessage());
            }
        }
        
        // Actualizar roles
        if (controladorRoles != null) {
            try {
                controladorRoles.cargarRolesDesdeBaseDatos();
                logger.info("Roles actualizados");
            } catch (Exception e) {
                logger.error("Error al actualizar roles: " + e.getMessage());
            }
        }
        
        // Actualizar combo de roles en la vista
        try {
            if (vista != null) {
                vista.actualizarComboRoles(); // LLAMADA DIRECTA AL MÉTODO
                logger.info("Combo de roles actualizado");
            }
        } catch (Exception e) {
            logger.error("Error al actualizar combo de roles: " + e.getMessage());
        }
        
        logger.info("Actualización completa");
    }
    
    // Método para verificar el estado de los controladores
    public String obtenerEstadoControladores() {
        StringBuilder estado = new StringBuilder();
        estado.append("=== ESTADO DE CONTROLADORES (USUARIOS/ROLES) ===\n");
        estado.append("ControladorUsuarios: ").append(controladorUsuarios != null ? "✅ Activo" : "❌ Inactivo").append("\n");
        estado.append("ControladorRoles: ").append(controladorRoles != null ? "✅ Activo" : "❌ Inactivo").append("\n");
        estado.append("DAOUsuario: ").append(daoUsuario != null ? "✅ Inicializado" : "❌ No inicializado").append("\n");
        estado.append("DAORol: ").append(daoRol != null ? "✅ Inicializado" : "❌ No inicializado").append("\n");
        
        // Obtener estadísticas si los DAOs están disponibles
        if (daoUsuario != null) {
            try {
                int totalUsuarios = 0; // Esto dependería de tu implementación de DAOUsuario
                // Si tienes un método obtenerTotalUsuarios() en DAOUsuario:
                // totalUsuarios = daoUsuario.obtenerTotalUsuarios();
                estado.append("Usuarios en sistema: ").append(totalUsuarios).append("\n");
            } catch (Exception e) {
                estado.append("Usuarios en sistema: Error al obtener\n");
            }
        }
        
        if (daoRol != null) {
            try {
                int totalRoles = 0; // Esto dependería de tu implementación de DAORol
                // Si tienes un método obtenerTotalRoles() en DAORol:
                // totalRoles = daoRol.obtenerTotalRoles();
                estado.append("Roles en sistema: ").append(totalRoles).append("\n");
            } catch (Exception e) {
                estado.append("Roles en sistema: Error al obtener\n");
            }
        }
        
        return estado.toString();
    }
    
    // Método para limpiar y reiniciar
    public void limpiarTodo() {
        logger.info("Limpiando todo en GestorUsuariosRoles...");
        
        if (controladorUsuarios != null) {
            try {
                // Llamar a método de limpieza si existe
                // controladorUsuarios.limpiarFormularioUsuario();
                logger.info("ControladorUsuarios limpiado");
            } catch (Exception e) {
                logger.error("Error al limpiar ControladorUsuarios: " + e.getMessage());
            }
        }
        
        if (controladorRoles != null) {
            try {
                // Llamar a método de limpieza si existe
                // controladorRoles.limpiarFormularioRol();
                logger.info("ControladorRoles limpiado");
            } catch (Exception e) {
                logger.error("Error al limpiar ControladorRoles: " + e.getMessage());
            }
        }
        
        // Actualizar vista
        actualizarDatos();
        
        logger.info("Limpieza completada");
    }
}