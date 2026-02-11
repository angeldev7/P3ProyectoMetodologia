package controller;

import view.PanelGestionUsuarios;
import model.Usuario;
import model.Rol;
import DAO.DAOUsuario;
import DAO.DAORol;
import javax.swing.JOptionPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class ControladorUsuarios implements ActionListener {
    private static final Logger logger = LoggerFactory.getLogger(ControladorUsuarios.class);
    private PanelGestionUsuarios vista;
    private DAOUsuario daoUsuario;
    private DAORol daoRol;  // Agregado para manejar roles
    private static final String USUARIO_NO_SELECCIONADO = "Usuario No Seleccionado";
    
<<<<<<< HEAD:src/controller/ControladorUsuarios.java
    public ControladorUsuarios(PanelGestionUsuarios vista, DAOUsuario daoUsuario) {
=======
    // Constructor para testing con inyección de dependencias
    public ControladorGestionUsuarios(PanelGestionUsuarios vista, DAOUsuario daoUsuario, DAORol daoRol) {
        this.vista = vista;
        this.daoUsuario = daoUsuario;
        this.daoRol = daoRol;
    }
    
    public ControladorGestionUsuarios(PanelGestionUsuarios vista) {
>>>>>>> origin/Test:src/controller/ControladorGestionUsuarios.java
        this.vista = vista;
        this.daoUsuario = daoUsuario;
        this.daoRol = new DAORol(); // Inicializamos DAORol
        
        // Registrar listeners específicos de usuarios
        this.vista.btnGuardarUsuario.addActionListener(this);
        this.vista.btnNuevoUsuario.addActionListener(this);
        this.vista.btnEliminarUsuario.addActionListener(this);
        this.vista.btnResetearContrasena.addActionListener(this);
        this.vista.btnBloquearUsuario.addActionListener(this);
        this.vista.btnDesbloquearUsuario.addActionListener(this);
        
        // Agregar listener de doble clic
        if (vista.tablaUsuarios != null) {
            vista.tablaUsuarios.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        cargarUsuarioSeleccionado();
                    }
                }
            });
        }
        
        // Cargar datos iniciales
        cargarUsuariosDesdeBaseDatos();
    }
    
    // Constructor alternativo si necesitas pasar ambos DAOs
    public ControladorUsuarios(PanelGestionUsuarios vista, DAOUsuario daoUsuario, DAORol daoRol) {
        this.vista = vista;
        this.daoUsuario = daoUsuario;
        this.daoRol = daoRol;
        
        // ... resto del constructor igual
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        Object fuente = e.getSource();
        
        if (fuente == vista.btnGuardarUsuario) {
            guardarUsuario();
        } else if (fuente == vista.btnNuevoUsuario) {
            limpiarFormularioUsuario();
        } else if (fuente == vista.btnBloquearUsuario) {
            bloquearUsuario();
        } else if (fuente == vista.btnDesbloquearUsuario) {
            desbloquearUsuario();
        } else if (fuente == vista.btnEliminarUsuario) {
            eliminarUsuario();
        } else if (fuente == vista.btnResetearContrasena) {
            resetearContrasena();
        }
    }
    
    private void guardarUsuario() {
        if (vista == null) {
            logger.error("⚠️ Vista es nula en guardarUsuario");
            return;
        }

        String usuario = vista.txtUsuario != null ? vista.txtUsuario.getText().trim() : "";
        String contrasena = vista.txtContrasena != null ? new String(vista.txtContrasena.getPassword()) : "";
        String nombreCompleto = vista.txtNombreCompleto != null ? vista.txtNombreCompleto.getText().trim() : "";
        String rol = vista.cmbRol != null ? (String) vista.cmbRol.getSelectedItem() : null;

        if (usuario.isEmpty() || contrasena.isEmpty() || nombreCompleto.isEmpty() || rol == null) {
            if (vista != null) {
                JOptionPane.showMessageDialog(vista, "Por favor complete todos los campos requeridos.",
                        "Campos Incompletos", JOptionPane.WARNING_MESSAGE);
            }
            return;
        }

        Usuario user = new Usuario(usuario, contrasena, nombreCompleto, rol);

        // Verificar si el usuario ya existe
        Usuario usuarioExistente = daoUsuario.buscarUsuarioPorNombre(usuario);
        if (usuarioExistente != null) {
            // Actualizar usuario existente
            if (daoUsuario.actualizarUsuario(usuario, user)) {
                JOptionPane.showMessageDialog(vista, "Usuario actualizado exitosamente.", "Actualización Exitosa", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(vista, "Error al actualizar el usuario.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            // Crear nuevo usuario
            if (daoUsuario.crearUsuario(user)) {
                // Actualizar contador de usuarios en el rol (si el método existe)
                actualizarContadorUsuariosEnRol(rol, 1);
                JOptionPane.showMessageDialog(vista, "Usuario creado exitosamente.", "Creación Exitosa", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(vista, "Error al crear el usuario.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        
        cargarUsuariosDesdeBaseDatos();
        if (vista != null) {
            vista.actualizarComboRoles(); // LLAMADA DIRECTA
        }
        limpiarFormularioUsuario();
    }
    
    // Método auxiliar para actualizar contador de usuarios en rol
    private void actualizarContadorUsuariosEnRol(String nombreRol, int cambio) {
        if (daoRol != null) {
            try {
                // Buscar el rol
                Rol rol = daoRol.buscarRolPorNombre(nombreRol);
                if (rol != null) {
                    // Actualizar el contador
                    int nuevoContador = rol.getContadorUsuarios() + cambio;
                    rol.setContadorUsuarios(Math.max(0, nuevoContador)); // No permitir negativo
                    
                    // Actualizar en la base de datos
                    daoRol.actualizarRol(nombreRol, rol);
                }
            } catch (Exception e) {
                logger.error("Error al actualizar contador de usuarios en rol: " + e.getMessage());
            }
        }
    }
    
    // Método para cargar roles desde base de datos
    private void cargarRolesDesdeBaseDatos() {
        if (vista != null) {
            vista.actualizarComboRoles();
        }
    }
    
    private void eliminarUsuario() {
        if (vista.tablaUsuarios == null || vista.modeloTablaUsuarios == null) {
            logger.error("Tabla de usuarios no inicializada");
            return;
        }
        
        int filaSeleccionada = vista.tablaUsuarios.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(vista, "Por favor seleccione un usuario para eliminar.", USUARIO_NO_SELECCIONADO, JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int filaModelo = vista.tablaUsuarios.convertRowIndexToModel(filaSeleccionada);
        String usuario = sanitizarTexto((String) vista.modeloTablaUsuarios.getValueAt(filaModelo, 0));
        String rol = sanitizarTexto((String) vista.modeloTablaUsuarios.getValueAt(filaModelo, 2));
        
        int respuesta = JOptionPane.showConfirmDialog(vista, 
            "¿Está seguro de que desea eliminar al usuario '" + sanitizarTexto(usuario) + "'?", 
            "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);
        
        if (respuesta == JOptionPane.YES_OPTION) {
            if (daoUsuario.eliminarUsuario(usuario)) {
                // Actualizar contador de usuarios en el rol
                actualizarContadorUsuariosEnRol(rol, -1);
                JOptionPane.showMessageDialog(vista, "Usuario eliminado exitosamente.", "Eliminación Exitosa", JOptionPane.INFORMATION_MESSAGE);
                cargarUsuariosDesdeBaseDatos();
                cargarRolesDesdeBaseDatos(); // Llama al método corregido
                limpiarFormularioUsuario();
            } else {
                JOptionPane.showMessageDialog(vista, "Error al eliminar el usuario.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    public void actualizarComboRoles() {
        if (vista != null && vista.cmbRol != null && daoRol != null) {
            vista.cmbRol.removeAllItems();
            List<Rol> roles = daoRol.obtenerTodosRoles();
            
            if (roles != null && !roles.isEmpty()) {
                for (Rol rol : roles) {
                    vista.cmbRol.addItem(rol.getNombre());
                }
            } else {
                vista.cmbRol.addItem("Sin roles disponibles");
            }
        }
    }
    
    private void resetearContrasena() {
        if (vista.tablaUsuarios == null || vista.modeloTablaUsuarios == null) {
            logger.error("Tabla de usuarios no inicializada");
            return;
        }
        
        int filaSeleccionada = vista.tablaUsuarios.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(vista, "Por favor seleccione un usuario para resetear la contraseña.", USUARIO_NO_SELECCIONADO, JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int filaModelo = vista.tablaUsuarios.convertRowIndexToModel(filaSeleccionada);
        String usuario = sanitizarTexto((String) vista.modeloTablaUsuarios.getValueAt(filaModelo, 0));
        String usuarioSanitizado = sanitizarTexto(usuario);
        
        String nuevaContrasena = JOptionPane.showInputDialog(vista, 
            "Ingrese la nueva contraseña para " + usuarioSanitizado + ":");
        
        if (nuevaContrasena != null && !nuevaContrasena.trim().isEmpty()) {
            if (daoUsuario.cambiarContrasena(usuario, nuevaContrasena.trim())) {
                JOptionPane.showMessageDialog(vista, "✅ Contraseña actualizada exitosamente (encriptada).", "Contraseña Actualizada", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(vista, "❌ Error al actualizar la contraseña.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void bloquearUsuario() {
        if (vista.tablaUsuarios == null || vista.modeloTablaUsuarios == null) {
            logger.error("Tabla de usuarios no inicializada");
            return;
        }
        
        int filaSeleccionada = vista.tablaUsuarios.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(vista, "Por favor seleccione un usuario para bloquear.", 
                USUARIO_NO_SELECCIONADO, JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int filaModelo = vista.tablaUsuarios.convertRowIndexToModel(filaSeleccionada);
        String usuario = sanitizarTexto((String) vista.modeloTablaUsuarios.getValueAt(filaModelo, 0));
        
        // Verificar si no es el usuario admin
        if ("admin".equals(usuario)) {
            JOptionPane.showMessageDialog(vista, 
                "No se puede bloquear al usuario administrador principal (admin).", 
                "Bloqueo No Permitido", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Verificar si ya está bloqueado
        String estado = sanitizarTexto((String) vista.modeloTablaUsuarios.getValueAt(filaModelo, 4));
        boolean yaBloqueado = estado != null && 
                             (estado.contains("BLOQUEADO") || 
                              estado.contains("Bloqueado") || 
                              estado.contains("🚫"));
        
        if (yaBloqueado) {
            JOptionPane.showMessageDialog(vista, 
                "El usuario '" + usuario + "' ya está bloqueado.", 
                "Usuario Ya Bloqueado", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int respuesta = JOptionPane.showConfirmDialog(vista, 
            "¿Está seguro de que desea bloquear al usuario '" + sanitizarTexto(usuario) + "'?\n\n" +
            "El usuario no podrá iniciar sesión hasta que sea desbloqueado.", 
            "Confirmar Bloqueo", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        
        if (respuesta == JOptionPane.YES_OPTION) {
            if (daoUsuario.bloquearUsuario(usuario)) {
                JOptionPane.showMessageDialog(vista, 
                    "✅ Usuario '" + usuario + "' bloqueado exitosamente.\n\n" +
                    "El usuario no podrá iniciar sesión hasta que sea desbloqueado por un administrador.", 
                    "Bloqueo Exitoso", JOptionPane.INFORMATION_MESSAGE);
                cargarUsuariosDesdeBaseDatos();
            } else {
                JOptionPane.showMessageDialog(vista, 
                    "❌ Error al bloquear el usuario.\n\n" +
                    "Verifique la conexión a la base de datos.", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void desbloquearUsuario() {
        if (vista.tablaUsuarios == null || vista.modeloTablaUsuarios == null) {
            logger.error("Tabla de usuarios no inicializada");
            return;
        }
        
        int filaSeleccionada = vista.tablaUsuarios.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(vista, "Por favor seleccione un usuario para desbloquear.", 
                USUARIO_NO_SELECCIONADO, JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int filaModelo = vista.tablaUsuarios.convertRowIndexToModel(filaSeleccionada);
        String usuario = sanitizarTexto((String) vista.modeloTablaUsuarios.getValueAt(filaModelo, 0));
        
        // Verificar si ya está activo
        String estado = sanitizarTexto((String) vista.modeloTablaUsuarios.getValueAt(filaModelo, 4));
        boolean yaDesbloqueado = !(estado != null && 
                                  (estado.contains("BLOQUEADO") || 
                                   estado.contains("Bloqueado") || 
                                   estado.contains("🚫")));
        
        if (yaDesbloqueado) {
            JOptionPane.showMessageDialog(vista, 
                "El usuario '" + usuario + "' no está bloqueado.", 
                "Usuario No Bloqueado", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int respuesta = JOptionPane.showConfirmDialog(vista, 
            "¿Está seguro de que desea desbloquear al usuario '" + sanitizarTexto(usuario) + "'?\n\n" +
            "El usuario podrá iniciar sesión nuevamente.", 
            "Confirmar Desbloqueo", JOptionPane.YES_NO_OPTION);
        
        if (respuesta == JOptionPane.YES_OPTION) {
            if (daoUsuario.desbloquearUsuario(usuario)) {
                JOptionPane.showMessageDialog(vista, 
                    "✅ Usuario '" + usuario + "' desbloqueado exitosamente.\n\n" +
                    "El usuario ya puede iniciar sesión nuevamente.", 
                    "Desbloqueo Exitoso", JOptionPane.INFORMATION_MESSAGE);
                cargarUsuariosDesdeBaseDatos();
            } else {
                JOptionPane.showMessageDialog(vista, 
                    "❌ Error al desbloquear el usuario.\n\n" +
                    "Verifique la conexión a la base de datos.", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void limpiarFormularioUsuario() {
        if (vista.txtUsuario != null) {
            vista.txtUsuario.setText("");
        }
        if (vista.txtContrasena != null) {
            vista.txtContrasena.setText("");
        }
        if (vista.txtNombreCompleto != null) {
            vista.txtNombreCompleto.setText("");
        }
        if (vista.txtBuscar != null) {
            vista.txtBuscar.setText("");
        }
        if (vista.tablaUsuarios != null) {
            vista.tablaUsuarios.clearSelection();
        }
    }
    
    public void cargarUsuariosDesdeBaseDatos() {
        if (vista == null || vista.modeloTablaUsuarios == null) {
            logger.error("⚠️ Vista o modelo de tabla es nulo");
            return;
        }

        vista.modeloTablaUsuarios.setRowCount(0);
        List<Usuario> usuarios = daoUsuario.obtenerTodosUsuarios();

        if (usuarios == null) {
            logger.error("⚠️ Lista de usuarios es nula");
            return;
        }
        
        // Crear array reutilizable fuera del loop
        Object[] datosFila = new Object[6];

        for (Usuario usuario : usuarios) {
            if (usuario == null) {
                continue;
            }
            
            // Calcular estadoMostrar para cada usuario
            String estadoMostrar;
            if (usuario.isBloqueado()) {
                estadoMostrar = "🚫 BLOQUEADO";
                String fechaBloqueo = usuario.getFechaBloqueo();
                if (fechaBloqueo != null && !fechaBloqueo.isEmpty()) {
                    estadoMostrar += " (" + fechaBloqueo + ")";
                }
            } else {
                String estado = usuario.getEstado();
                estadoMostrar = (estado != null) ? estado : "Activo";
            }
            
            // Rellenar array reutilizable
            datosFila[0] = usuario.getUsuario();
            datosFila[1] = usuario.getNombreCompleto();
            datosFila[2] = usuario.getRol();
            datosFila[3] = usuario.getFechaCreacion();
            datosFila[4] = estadoMostrar;
            datosFila[5] = usuario.isBloqueado() ? "🚫 Sí" : "✅ No";
            
            vista.modeloTablaUsuarios.addRow(datosFila);
        }
    }
    
    private void cargarUsuarioSeleccionado() {
        if (vista.tablaUsuarios == null || vista.modeloTablaUsuarios == null) {
            return;
        }
        
        int filaSeleccionada = vista.tablaUsuarios.getSelectedRow();
        if (filaSeleccionada == -1) {
            return;
        }
        
        int filaModelo = vista.tablaUsuarios.convertRowIndexToModel(filaSeleccionada);
        String usuario = sanitizarTexto((String) vista.modeloTablaUsuarios.getValueAt(filaModelo, 0));
        
        Usuario user = daoUsuario.buscarUsuarioPorNombre(usuario);
        if (user != null) {
            if (vista.txtUsuario != null) {
                vista.txtUsuario.setText(user.getUsuario());
            }
            if (vista.txtNombreCompleto != null) {
                vista.txtNombreCompleto.setText(user.getNombreCompleto());
            }
            if (vista.cmbRol != null) {
                vista.cmbRol.setSelectedItem(user.getRol());
            }
            // No cargamos la contraseña por seguridad
        }
    }
    
    private String sanitizarTexto(String texto) {
        if (texto == null) return "";
        
        // Remover caracteres peligrosos para HTML/JavaScript
        return texto.replaceAll("[<>\"'&;]", "");
    }
    
    // Getter para DAOUsuario si es necesario
    public DAOUsuario getDaoUsuario() {
        return daoUsuario;
    }
    
    // Getter para DAORol si es necesario
    public DAORol getDaoRol() {
        return daoRol;
    }
}