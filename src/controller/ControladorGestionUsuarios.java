// Controller/ControladorGestionUsuarios.java
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

public class ControladorGestionUsuarios implements ActionListener {
	private static final Logger logger = LoggerFactory.getLogger(ControladorGestionUsuarios.class);
    private PanelGestionUsuarios vista;
    private DAOUsuario daoUsuario;
    private DAORol daoRol;
    
    public ControladorGestionUsuarios(PanelGestionUsuarios vista) {
        this.vista = vista;
        this.daoUsuario = new DAOUsuario();
        this.daoRol = new DAORol();
        
        // Registrar listeners
        this.vista.btnGuardarUsuario.addActionListener(this);
        this.vista.btnNuevoUsuario.addActionListener(this);
        this.vista.btnEliminarUsuario.addActionListener(this);
        this.vista.btnResetearContrasena.addActionListener(this);
        this.vista.btnGuardarRol.addActionListener(this);
        this.vista.btnNuevoRol.addActionListener(this);
        this.vista.btnBloquearUsuario.addActionListener(this);
        this.vista.btnDesbloquearUsuario.addActionListener(this);
        this.vista.btnEditarRol.addActionListener(this);
        this.vista.btnEliminarRol.addActionListener(this);
        
        vista.tablaRoles.addMouseListener(new MouseAdapter() {
        	
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) { // Doble clic
                    editarRol();
                }
            }
        });

        // Agregar listener de doble clic en la tabla de usuarios
        vista.tablaUsuarios.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) { // Doble clic
                    cargarUsuarioSeleccionado();
                }
            }
        });
        // Cargar datos iniciales desde MongoDB
        cargarUsuariosDesdeBaseDatos();
        cargarRolesDesdeBaseDatos();
        
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
    	
        Object fuente = e.getSource();
        if (fuente == vista.btnGuardarUsuario) {
            guardarUsuario();
        } else if (fuente == vista.btnNuevoUsuario) {
            limpiarFormularioUsuario();
        } else if (fuente == vista.btnBloquearUsuario) { // NUEVO
            bloquearUsuario();
        } else if (fuente == vista.btnDesbloquearUsuario) { // NUEVO
            desbloquearUsuario();
        } else if (fuente == vista.btnEliminarUsuario) {
            eliminarUsuario();
        } else if (fuente == vista.btnResetearContrasena) {
            resetearContrasena();
        } else if (fuente == vista.btnGuardarRol) {
            guardarRol();
        } else if (fuente == vista.btnNuevoRol) {
            limpiarFormularioRol();
        } else if (fuente == vista.btnEditarRol) { // NUEVO
            editarRol();
        } else if (fuente == vista.btnEliminarRol) { // NUEVO
            eliminarRol();
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
            }
        } else {
            // Crear nuevo usuario
            if (daoUsuario.crearUsuario(user)) {
                // Actualizar contador de usuarios en el rol
                daoRol.actualizarContadorUsuarios(rol, 1);
                JOptionPane.showMessageDialog(vista, "Usuario creado exitosamente.", "Creación Exitosa", JOptionPane.INFORMATION_MESSAGE);
            }
        }
        
        cargarUsuariosDesdeBaseDatos();
        cargarRolesDesdeBaseDatos();
        limpiarFormularioUsuario();
    }
    
    private void guardarRol() {
        String nombreRol = vista.txtNombreRol.getText().trim();
        String permisos = vista.obtenerPermisosComoString();
        
        if (nombreRol.isEmpty()) {
            JOptionPane.showMessageDialog(vista, "Por favor ingrese un nombre para el rol.", "Nombre Requerido", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Rol rol = new Rol(nombreRol);
        String[] arrayPermisos = permisos.split(",");
        for (String perm : arrayPermisos) {
            if (!perm.trim().isEmpty()) {
                rol.agregarPermiso(perm.trim());
            }
        }
        
        // Verificar si el rol ya existe
        Rol rolExistente = daoRol.buscarRolPorNombre(nombreRol);
        if (rolExistente != null) {
            // Actualizar rol existente
            if (daoRol.actualizarRol(nombreRol, rol)) {
                JOptionPane.showMessageDialog(vista, "Rol actualizado exitosamente.", "Actualización Exitosa", JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            // Crear nuevo rol
            if (daoRol.crearRol(rol)) {
                JOptionPane.showMessageDialog(vista, "Rol creado exitosamente.", "Creación Exitosa", JOptionPane.INFORMATION_MESSAGE);
            }
        }
        
        cargarRolesDesdeBaseDatos();
        limpiarFormularioRol();
    }
    
    private void eliminarUsuario() {
        int filaSeleccionada = vista.tablaUsuarios.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(vista, "Por favor seleccione un usuario para eliminar.", "Usuario No Seleccionado", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int filaModelo = vista.tablaUsuarios.convertRowIndexToModel(filaSeleccionada);
        String usuario = (String) vista.modeloTablaUsuarios.getValueAt(filaModelo, 0);
        String rol = (String) vista.modeloTablaUsuarios.getValueAt(filaModelo, 2);
        
        int respuesta = JOptionPane.showConfirmDialog(vista, 
            "¿Está seguro de que desea eliminar al usuario '" + usuario + "'?", 
            "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);
        
        if (respuesta == JOptionPane.YES_OPTION) {
            if (daoUsuario.eliminarUsuario(usuario)) {
                // Actualizar contador de usuarios en el rol
                daoRol.actualizarContadorUsuarios(rol, -1);
                JOptionPane.showMessageDialog(vista, "Usuario eliminado exitosamente.", "Eliminación Exitosa", JOptionPane.INFORMATION_MESSAGE);
                cargarUsuariosDesdeBaseDatos();
                cargarRolesDesdeBaseDatos();
                limpiarFormularioUsuario();
            } else {
                JOptionPane.showMessageDialog(vista, "Error al eliminar el usuario.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void resetearContrasena() {
        int filaSeleccionada = vista.tablaUsuarios.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(vista, "Por favor seleccione un usuario para resetear la contraseña.", "Usuario No Seleccionado", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int filaModelo = vista.tablaUsuarios.convertRowIndexToModel(filaSeleccionada);
        String usuario = (String) vista.modeloTablaUsuarios.getValueAt(filaModelo, 0);
        
        String nuevaContrasena = JOptionPane.showInputDialog(vista, "Ingrese la nueva contraseña para " + usuario + ":");
        if (nuevaContrasena != null && !nuevaContrasena.trim().isEmpty()) {
            if (daoUsuario.cambiarContrasena(usuario, nuevaContrasena.trim())) {
                JOptionPane.showMessageDialog(vista, "✅ Contraseña actualizada exitosamente (encriptada).", "Contraseña Actualizada", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(vista, "❌ Error al actualizar la contraseña.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    

    private void cargarRolesDesdeBaseDatos() {
        vista.modeloTablaRoles.setRowCount(0);
        List<Rol> roles = daoRol.obtenerTodosRoles();
        for (Rol rol : roles) {
            Object[] datosFila = {
                rol.getNombre(),
                rol.getPermisosComoString(),
                rol.getContadorUsuarios(),
                rol.getFechaCreacion()
            };
            vista.modeloTablaRoles.addRow(datosFila);
        }
        vista.actualizarComboRoles();
    }
    
    private void limpiarFormularioUsuario() {
        vista.txtUsuario.setText("");
        vista.txtContrasena.setText("");
        vista.txtNombreCompleto.setText("");
        vista.txtBuscar.setText("");
        vista.tablaUsuarios.clearSelection();
    }
    
    private void limpiarFormularioRol() {
        vista.txtNombreRol.setText("");
        vista.chkGestionarProductos.setSelected(false);
        vista.chkAccederVentas.setSelected(false);
        vista.chkVerReportes.setSelected(false);
        vista.chkExportarDatos.setSelected(false);
        vista.chkGestionarUsuarios.setSelected(false);
        vista.tablaRoles.clearSelection();
    }
    

    private void bloquearUsuario() {
        int filaSeleccionada = vista.tablaUsuarios.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(vista, "Por favor seleccione un usuario para bloquear.", 
                "Usuario No Seleccionado", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int filaModelo = vista.tablaUsuarios.convertRowIndexToModel(filaSeleccionada);
        String usuario = (String) vista.modeloTablaUsuarios.getValueAt(filaModelo, 0);
        
        // Verificar si no es el usuario admin
        if ("admin".equals(usuario)) {
            JOptionPane.showMessageDialog(vista, 
                "No se puede bloquear al usuario administrador principal (admin).", 
                "Bloqueo No Permitido", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Verificar si ya está bloqueado
        String estado = (String) vista.modeloTablaUsuarios.getValueAt(filaModelo, 4);
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
            "¿Está seguro de que desea bloquear al usuario '" + usuario + "'?\n\n" +
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
        int filaSeleccionada = vista.tablaUsuarios.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(vista, "Por favor seleccione un usuario para desbloquear.", 
                "Usuario No Seleccionado", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int filaModelo = vista.tablaUsuarios.convertRowIndexToModel(filaSeleccionada);
        String usuario = (String) vista.modeloTablaUsuarios.getValueAt(filaModelo, 0);
        
        // Verificar si ya está activo
        String estado = (String) vista.modeloTablaUsuarios.getValueAt(filaModelo, 4);
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
            "¿Está seguro de que desea desbloquear al usuario '" + usuario + "'?\n\n" +
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
    private void editarRol() {
        int filaSeleccionada = vista.tablaRoles.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(vista, "Por favor seleccione un rol para editar.", "Rol No Seleccionado", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int filaModelo = vista.tablaRoles.convertRowIndexToModel(filaSeleccionada);
        String nombreRol = (String) vista.modeloTablaRoles.getValueAt(filaModelo, 0);
        String permisos = (String) vista.modeloTablaRoles.getValueAt(filaModelo, 1);
        
        // Cargar datos en el formulario
        vista.txtNombreRol.setText(nombreRol);
        vista.establecerPermisosDesdeString(permisos);
        
        JOptionPane.showMessageDialog(vista, 
            "Rol '" + nombreRol + "' cargado para edición.\nModifique los permisos y haga clic en 'Guardar Rol' para actualizar.", 
            "Editar Rol", JOptionPane.INFORMATION_MESSAGE);
    }

    private void eliminarRol() {
        int filaSeleccionada = vista.tablaRoles.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(vista, "Por favor seleccione un rol para eliminar.", "Rol No Seleccionado", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int filaModelo = vista.tablaRoles.convertRowIndexToModel(filaSeleccionada);
        String nombreRol = (String) vista.modeloTablaRoles.getValueAt(filaModelo, 0);
        int contadorUsuarios = (int) vista.modeloTablaRoles.getValueAt(filaModelo, 2);
        
        // Verificar si hay usuarios con este rol
        if (contadorUsuarios > 0) {
            JOptionPane.showMessageDialog(vista, 
                "❌ No se puede eliminar el rol '" + nombreRol + "' porque tiene " + contadorUsuarios + " usuario(s) asignado(s).\n\n" +
                "Reasigne los usuarios a otro rol antes de eliminar este.", 
                "Rol en Uso", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int respuesta = JOptionPane.showConfirmDialog(vista, 
            "¿Está seguro de que desea eliminar el rol '" + nombreRol + "'?\n\nEsta acción no se puede deshacer.", 
            "Confirmar Eliminación de Rol", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        
        if (respuesta == JOptionPane.YES_OPTION) {
            if (daoRol.eliminarRol(nombreRol)) {
                JOptionPane.showMessageDialog(vista, "✅ Rol eliminado exitosamente.", "Eliminación Exitosa", JOptionPane.INFORMATION_MESSAGE);
                cargarRolesDesdeBaseDatos();
                limpiarFormularioRol();
            } else {
                JOptionPane.showMessageDialog(vista, "❌ Error al eliminar el rol.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    private void cargarUsuariosDesdeBaseDatos() {
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

		for (Usuario usuario : usuarios) {
			if (usuario == null) {
				continue; // Saltar usuarios nulos
			}

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
            
            Object[] datosFila = {
                usuario.getUsuario(),
                usuario.getNombreCompleto(),
                usuario.getRol(),
                usuario.getFechaCreacion(),
                estadoMostrar,
                usuario.isBloqueado() ? "🚫 Sí" : "✅ No"
            };
            vista.modeloTablaUsuarios.addRow(datosFila);
        }
    }
    private void cargarUsuarioSeleccionado() {
        int filaSeleccionada = vista.tablaUsuarios.getSelectedRow();
        if (filaSeleccionada == -1) {
        	return;
        }
        
        int filaModelo = vista.tablaUsuarios.convertRowIndexToModel(filaSeleccionada);
        String usuario = (String) vista.modeloTablaUsuarios.getValueAt(filaModelo, 0);
        
        Usuario user = daoUsuario.buscarUsuarioPorNombre(usuario);
        if (user != null) {
            vista.txtUsuario.setText(user.getUsuario());
            vista.txtNombreCompleto.setText(user.getNombreCompleto());
            vista.cmbRol.setSelectedItem(user.getRol());
            // No cargamos la contraseña por seguridad
        }
    }

}