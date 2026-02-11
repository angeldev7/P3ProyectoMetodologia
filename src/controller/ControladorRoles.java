package controller;

import view.PanelGestionUsuarios;
import model.Rol;
import DAO.DAORol;
import javax.swing.JOptionPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class ControladorRoles implements ActionListener {
    private static final Logger logger = LoggerFactory.getLogger(ControladorRoles.class);
    private PanelGestionUsuarios vista;
    private DAORol daoRol;
    private static final String ROL_NO_SELECCIONADO = "Rol No Seleccionado";
    
    public ControladorRoles(PanelGestionUsuarios vista, DAORol daoRol) {
        this.vista = vista;
        this.daoRol = daoRol;
        
        // Registrar listeners específicos de roles
        this.vista.btnGuardarRol.addActionListener(this);
        this.vista.btnNuevoRol.addActionListener(this);
        this.vista.btnEditarRol.addActionListener(this);
        this.vista.btnEliminarRol.addActionListener(this);
        
        // Agregar listener de doble clic
        if (vista.tablaRoles != null) {
            vista.tablaRoles.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        editarRol();
                    }
                }
            });
        }
        
        // Cargar datos iniciales
        cargarRolesDesdeBaseDatos();
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        Object fuente = e.getSource();
        
        if (fuente == vista.btnGuardarRol) {
            guardarRol();
        } else if (fuente == vista.btnNuevoRol) {
            limpiarFormularioRol();
        } else if (fuente == vista.btnEditarRol) {
            editarRol();
        } else if (fuente == vista.btnEliminarRol) {
            eliminarRol();
        }
    }
    
    private void guardarRol() {
        if (vista.txtNombreRol == null) {
            logger.error("Campo txtNombreRol no inicializado");
            return;
        }
        
        String nombreRol = vista.txtNombreRol.getText().trim();
        String permisos = obtenerPermisosComoString();
        
        if (nombreRol.isEmpty()) {
            JOptionPane.showMessageDialog(vista, "Por favor ingrese un nombre para el rol.", "Nombre Requerido", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Rol rol = new Rol(nombreRol);
        if (permisos != null && !permisos.isEmpty()) {
            String[] arrayPermisos = permisos.split(",");
            for (String perm : arrayPermisos) {
                if (!perm.trim().isEmpty()) {
                    rol.agregarPermiso(perm.trim());
                }
            }
        }
        
        // Verificar si el rol ya existe
        Rol rolExistente = daoRol.buscarRolPorNombre(nombreRol);
        if (rolExistente != null) {
            // Actualizar rol existente
            if (daoRol.actualizarRol(nombreRol, rol)) {
                JOptionPane.showMessageDialog(vista, "Rol actualizado exitosamente.", "Actualización Exitosa", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(vista, "Error al actualizar el rol.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            // Crear nuevo rol
            if (daoRol.crearRol(rol)) {
                JOptionPane.showMessageDialog(vista, "Rol creado exitosamente.", "Creación Exitosa", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(vista, "Error al crear el rol.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        
        cargarRolesDesdeBaseDatos();
        limpiarFormularioRol();
    }
    
    private String obtenerPermisosComoString() {
        StringBuilder permisos = new StringBuilder();
        
        if (vista.chkGestionarProductos != null && vista.chkGestionarProductos.isSelected()) {
            permisos.append("Gestionar Productos,");
        }
        if (vista.chkAccederVentas != null && vista.chkAccederVentas.isSelected()) {
            permisos.append("Acceder Ventas,");
        }
        if (vista.chkVerReportes != null && vista.chkVerReportes.isSelected()) {
            permisos.append("Ver Reportes,");
        }
        if (vista.chkExportarDatos != null && vista.chkExportarDatos.isSelected()) {
            permisos.append("Exportar Datos,");
        }
        if (vista.chkGestionarUsuarios != null && vista.chkGestionarUsuarios.isSelected()) {
            permisos.append("Gestionar Usuarios,");
        }
        
        // Eliminar la última coma si existe
        if (permisos.length() > 0) {
            permisos.setLength(permisos.length() - 1);
        }
        
        return permisos.toString();
    }
    
    private void establecerPermisosDesdeString(String permisosStr) {
        if (permisosStr == null || permisosStr.isEmpty()) {
            return;
        }
        
        String[] permisos = permisosStr.split(",");
        
        for (String perm : permisos) {
            String permiso = perm.trim();
            switch (permiso) {
                case "Gestionar Productos":
                    if (vista.chkGestionarProductos != null) vista.chkGestionarProductos.setSelected(true);
                    break;
                case "Acceder Ventas":
                    if (vista.chkAccederVentas != null) vista.chkAccederVentas.setSelected(true);
                    break;
                case "Ver Reportes":
                    if (vista.chkVerReportes != null) vista.chkVerReportes.setSelected(true);
                    break;
                case "Exportar Datos":
                    if (vista.chkExportarDatos != null) vista.chkExportarDatos.setSelected(true);
                    break;
                case "Gestionar Usuarios":
                    if (vista.chkGestionarUsuarios != null) vista.chkGestionarUsuarios.setSelected(true);
                    break;
            }
        }
    }
    
    public void cargarRolesDesdeBaseDatos() {
        if (vista == null || vista.modeloTablaRoles == null) {
            logger.error("Vista o modelo de tabla de roles no inicializado");
            return;
        }
        
        vista.modeloTablaRoles.setRowCount(0);
        List<Rol> roles = daoRol.obtenerTodosRoles();
        
        if (roles != null) {
            for (Rol rol : roles) {
                Object[] datosFila = {
                    rol.getNombre(),
                    rol.getPermisosComoString(),
                    rol.getContadorUsuarios(),
                    rol.getFechaCreacion()
                };
                vista.modeloTablaRoles.addRow(datosFila);
            }
        }
        
        if (vista != null) {
            vista.actualizarComboRoles();
        }
    }
    
    private void limpiarFormularioRol() {
        if (vista.txtNombreRol != null) {
            vista.txtNombreRol.setText("");
        }
        if (vista.chkGestionarProductos != null) {
            vista.chkGestionarProductos.setSelected(false);
        }
        if (vista.chkAccederVentas != null) {
            vista.chkAccederVentas.setSelected(false);
        }
        if (vista.chkVerReportes != null) {
            vista.chkVerReportes.setSelected(false);
        }
        if (vista.chkExportarDatos != null) {
            vista.chkExportarDatos.setSelected(false);
        }
        if (vista.chkGestionarUsuarios != null) {
            vista.chkGestionarUsuarios.setSelected(false);
        }
        if (vista.tablaRoles != null) {
            vista.tablaRoles.clearSelection();
        }
    }
    
    private void editarRol() {
        if (vista.tablaRoles == null) {
            logger.error("Tabla de roles no inicializada");
            return;
        }
        
        int filaSeleccionada = vista.tablaRoles.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(vista, "Por favor seleccione un rol para editar.", ROL_NO_SELECCIONADO, JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int filaModelo = vista.tablaRoles.convertRowIndexToModel(filaSeleccionada);
        String nombreRol = (String) vista.modeloTablaRoles.getValueAt(filaModelo, 0);
        String permisos = (String) vista.modeloTablaRoles.getValueAt(filaModelo, 1);
        
        // Cargar datos en el formulario
        if (vista.txtNombreRol != null) {
            vista.txtNombreRol.setText(nombreRol);
        }
        establecerPermisosDesdeString(permisos);
        
        JOptionPane.showMessageDialog(vista, 
            "Rol '" + sanitizarTexto(nombreRol) + "' cargado para edición.\nModifique los permisos y haga clic en 'Guardar Rol' para actualizar.", 
            "Editar Rol", JOptionPane.INFORMATION_MESSAGE);
    }

    private void eliminarRol() {
        if (vista.tablaRoles == null) {
            logger.error("Tabla de roles no inicializada");
            return;
        }
        
        int filaSeleccionada = vista.tablaRoles.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(vista, "Por favor seleccione un rol para eliminar.", ROL_NO_SELECCIONADO, JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int filaModelo = vista.tablaRoles.convertRowIndexToModel(filaSeleccionada);
        String nombreRol = (String) vista.modeloTablaRoles.getValueAt(filaModelo, 0);
        int contadorUsuarios = (int) vista.modeloTablaRoles.getValueAt(filaModelo, 2);
        
        // Verificar si hay usuarios con este rol
        if (contadorUsuarios > 0) {
            JOptionPane.showMessageDialog(vista, 
                "❌ No se puede eliminar el rol '" + sanitizarTexto(nombreRol) + "' porque tiene " + contadorUsuarios + " usuario(s) asignado(s).\n\n" +
                "Reasigne los usuarios a otro rol antes de eliminar este.", 
                "Rol en Uso", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int respuesta = JOptionPane.showConfirmDialog(vista, 
            "¿Está seguro de que desea eliminar el rol '" + sanitizarTexto(nombreRol) + "'?\n\nEsta acción no se puede deshacer.", 
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
    
    private String sanitizarTexto(String texto) {
        if (texto == null) return "";
        
        // Remover caracteres peligrosos para HTML/JavaScript
        return texto.replaceAll("[<>\"'&;]", "");
    }
    
    // Método para que GestorUsuariosRoles pueda acceder al DAO
    public DAORol getDaoRol() {
        return daoRol;
    }
}