package Vista;

import Controlador.ControladorUsuario;
import Controlador.ControladorBitacora;
import Modelo.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

/**
 * Dashboard exclusivo para el Admin Master.
 * Permite gestionar usuarios y ver bitácora completa.
 */
public class AdminMasterDashboard extends JFrame {
    private final ControladorUsuario controladorUsuario;
    private final ControladorBitacora controladorBitacora;

    private JTable tablaUsuarios;
    private DefaultTableModel modeloUsuarios;
    private JTable tablaBitacora;
    private DefaultTableModel modeloBitacora;
    private JTextField txtFiltroUsuarioBitacora;
    private JTextField txtFiltroUltimos;

    private JTextField txtNombreCompleto;
    private JTextField txtUsuario;
    private JPasswordField txtContrasena;
    private JComboBox<String> cmbRol;

    public AdminMasterDashboard() {
        super("Dashboard Admin Master - Sistema Contable");
        controladorUsuario = ControladorUsuario.getInstancia();
        controladorBitacora = ControladorBitacora.getInstancia();
        configurar();
        construirUI();
        cargarRolesPersonalizados(); // Cargar roles de MongoDB primero
        cargarUsuarios();
        cargarBitacora();
    }

    private void configurar() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10,10));
    }

    private void construirUI() {
        JPanel panelTop = new JPanel(new BorderLayout());
        JLabel lblTitulo = new JLabel("ADMIN MASTER - GESTIÓN CENTRAL", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitulo.setForeground(new Color(41,128,185));
        panelTop.add(lblTitulo, BorderLayout.CENTER);
        add(panelTop, BorderLayout.NORTH);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, crearPanelUsuarios(), crearPanelBitacora());
        split.setResizeWeight(0.55);
        add(split, BorderLayout.CENTER);

        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener(e -> dispose());
        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        south.add(btnCerrar);
        add(south, BorderLayout.SOUTH);
    }

    private JPanel crearPanelUsuarios() {
        JPanel p = new JPanel(new BorderLayout(5,5));
        p.setBorder(BorderFactory.createTitledBorder("Usuarios"));

        modeloUsuarios = new DefaultTableModel(new Object[]{"ID","Usuario","Nombre","Rol"},0){
            @Override public boolean isCellEditable(int r,int c){return false;}
        };
        tablaUsuarios = new JTable(modeloUsuarios);
        p.add(new JScrollPane(tablaUsuarios), BorderLayout.CENTER);

        JPanel form = new JPanel(new GridLayout(0,1,4,4));
        txtNombreCompleto = new JTextField();
        txtUsuario = new JTextField();
        txtContrasena = new JPasswordField();
        cmbRol = new JComboBox<>(); // Se cargará dinámicamente con roles de MongoDB
        form.add(labeled("Nombre Completo", txtNombreCompleto));
        form.add(labeled("Usuario", txtUsuario));
        form.add(labeled("Contraseña", txtContrasena));
        form.add(labeled("Rol", cmbRol));

        JButton btnAgregar = new JButton("Agregar Usuario");
        btnAgregar.addActionListener(this::agregarUsuario);
        JButton btnEliminar = new JButton("Eliminar Seleccionado");
        btnEliminar.addActionListener(this::eliminarSeleccionado);
        JButton btnEditar = new JButton("Editar Seleccionado");
        btnEditar.addActionListener(this::editarSeleccionado);
        JButton btnCrearRol = new JButton("Crear Rol Personalizado");
        btnCrearRol.setBackground(new Color(41, 128, 185));
        btnCrearRol.setForeground(Color.WHITE);
        btnCrearRol.addActionListener(this::crearRolPersonalizado);
        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.CENTER));
        acciones.add(btnAgregar); acciones.add(btnEditar); acciones.add(btnEliminar);
        acciones.add(btnCrearRol);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.add(form, BorderLayout.CENTER);
        bottom.add(acciones, BorderLayout.SOUTH);
        p.add(bottom, BorderLayout.SOUTH);
        return p;
    }

    private JPanel crearPanelBitacora() {
        JPanel p = new JPanel(new BorderLayout(5,5));
        p.setBorder(BorderFactory.createTitledBorder("Bitácora"));
        modeloBitacora = new DefaultTableModel(new Object[]{"ID","Usuario","Acción","Descripción"},0){
            @Override public boolean isCellEditable(int r,int c){return false;}
        };
        tablaBitacora = new JTable(modeloBitacora);
        p.add(new JScrollPane(tablaBitacora), BorderLayout.CENTER);

        JPanel filtroPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        txtFiltroUsuarioBitacora = new JTextField(10);
        txtFiltroUsuarioBitacora.setToolTipText("ID Usuario (ej: JEF-001) o vacío");
        txtFiltroUltimos = new JTextField(5);
        txtFiltroUltimos.setToolTipText("Últimos N");
        JButton btnAplicar = new JButton("Aplicar Filtro");
        btnAplicar.addActionListener(e -> aplicarFiltroBitacora());
        JButton btnRefrescar = new JButton("Refrescar");
        btnRefrescar.addActionListener(e -> cargarBitacora());
        filtroPanel.add(new JLabel("Usuario:"));
        filtroPanel.add(txtFiltroUsuarioBitacora);
        filtroPanel.add(new JLabel("Últimos:"));
        filtroPanel.add(txtFiltroUltimos);
        filtroPanel.add(btnAplicar);
        filtroPanel.add(btnRefrescar);
        p.add(filtroPanel, BorderLayout.SOUTH);
        return p;
    }

    private JPanel labeled(String label, JComponent comp) {
        JPanel p = new JPanel(new BorderLayout());
        JLabel l = new JLabel(label);
        l.setFont(new Font("Arial", Font.PLAIN, 12));
        p.add(l, BorderLayout.NORTH);
        p.add(comp, BorderLayout.CENTER);
        return p;
    }

    private void cargarUsuarios() {
        modeloUsuarios.setRowCount(0);
        List<Usuario> usuarios = controladorUsuario.getUsuarios();
        for (Usuario u : usuarios) {
            modeloUsuarios.addRow(new Object[]{u.getIdUsuario(), u.getNombreUsuario(), u.getNombreCompleto(), u.getRol()});
        }
    }

    private void cargarBitacora() {
        modeloBitacora.setRowCount(0);
        controladorBitacora.getRegistros().forEach(b -> {
            modeloBitacora.addRow(new Object[]{b.getIdRegistro(), b.getUsuario().getNombreUsuario(), b.getAccion(), b.getDescripcion()});
        });
    }

    private void agregarUsuario(ActionEvent e) {
        String nombre = txtNombreCompleto.getText().trim();
        String usuario = txtUsuario.getText().trim();
        String pass = new String(txtContrasena.getPassword());
        String rol = (String) cmbRol.getSelectedItem();
        if (nombre.isEmpty() || usuario.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Complete todos los campos.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }
        boolean ok = controladorUsuario.agregarUsuario(usuario, pass, nombre, rol);
        if (ok) {
            cargarUsuarios();
            JOptionPane.showMessageDialog(this, "Usuario agregado.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Usuario ya existe.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarSeleccionado(ActionEvent e) {
        int row = tablaUsuarios.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un usuario.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String id = (String) modeloUsuarios.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "¿Eliminar usuario ID="+id+"?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        boolean ok = ControladorUsuario.getInstancia().eliminarUsuario(id);
        if (ok) {
            cargarUsuarios();
            JOptionPane.showMessageDialog(this, "Usuario eliminado.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "No se pudo eliminar (ver permisos o inexistente).", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editarSeleccionado(ActionEvent e) {
        int row = tablaUsuarios.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, 
                "⚠️ Por favor seleccione un usuario de la tabla para editar.", 
                "Selección Requerida", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String id = (String) modeloUsuarios.getValueAt(row, 0);
        String usuario = (String) modeloUsuarios.getValueAt(row, 1);
        String nombre = (String) modeloUsuarios.getValueAt(row, 2);
        String rol = (String) modeloUsuarios.getValueAt(row, 3);
        
        // Crear diálogo personalizado
        JDialog dialogo = new JDialog(this, "✏️ Editar Usuario", true);
        dialogo.setLayout(new BorderLayout(10, 10));
        dialogo.setSize(450, 350);
        dialogo.setLocationRelativeTo(this);
        
        // Panel principal
        JPanel panelContenido = new JPanel(new BorderLayout(10, 10));
        panelContenido.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panelContenido.setBackground(Color.WHITE);
        
        // Panel de información no editable
        JPanel panelInfo = new JPanel(new GridLayout(2, 1, 5, 5));
        panelInfo.setBackground(new Color(236, 240, 241));
        panelInfo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Información del Usuario"),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        
        JLabel lblId = new JLabel("🆔 ID: " + id);
        lblId.setFont(new Font("Arial", Font.BOLD, 12));
        JLabel lblUsuario = new JLabel("👤 Usuario: " + usuario);
        lblUsuario.setFont(new Font("Arial", Font.BOLD, 12));
        panelInfo.add(lblId);
        panelInfo.add(lblUsuario);
        
        // Panel de campos editables
        JPanel panelCampos = new JPanel(new GridLayout(3, 2, 10, 10));
        panelCampos.setBackground(Color.WHITE);
        panelCampos.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Datos a Modificar"),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        
        JLabel lblNombre = new JLabel("Nombre Completo:");
        lblNombre.setFont(new Font("Arial", Font.PLAIN, 12));
        JTextField txtNombre = new JTextField(nombre, 20);
        txtNombre.setFont(new Font("Arial", Font.PLAIN, 12));
        
        JLabel lblPass = new JLabel("Nueva Contraseña:");
        lblPass.setFont(new Font("Arial", Font.PLAIN, 12));
        JPasswordField txtPass = new JPasswordField(20);
        txtPass.setFont(new Font("Arial", Font.PLAIN, 12));
        
        JLabel lblRol = new JLabel("Rol:");
        lblRol.setFont(new Font("Arial", Font.PLAIN, 12));
        JComboBox<String> cmbNuevoRol = new JComboBox<>();
        // Cargar todos los roles disponibles (predeterminados + personalizados)
        cmbNuevoRol.addItem(Usuario.ROL_JEFATURA_FINANCIERA);
        cmbNuevoRol.addItem(Usuario.ROL_ASISTENTE_CONTABLE);
        List<String> rolesDisponibles = controladorUsuario.getTodosLosRolesDisponibles();
        for (String r : rolesDisponibles) {
            if (!r.equals(Usuario.ROL_JEFATURA_FINANCIERA) && 
                !r.equals(Usuario.ROL_ASISTENTE_CONTABLE)) {
                cmbNuevoRol.addItem(r);
            }
        }
        cmbNuevoRol.setSelectedItem(rol);
        cmbNuevoRol.setFont(new Font("Arial", Font.PLAIN, 12));
        
        panelCampos.add(lblNombre);
        panelCampos.add(txtNombre);
        panelCampos.add(lblPass);
        panelCampos.add(txtPass);
        panelCampos.add(lblRol);
        panelCampos.add(cmbNuevoRol);
        
        // Nota informativa
        JLabel lblNota = new JLabel("<html><i>💡 Deje la contraseña vacía si no desea cambiarla</i></html>");
        lblNota.setFont(new Font("Arial", Font.PLAIN, 11));
        lblNota.setForeground(new Color(127, 140, 141));
        
        // Panel de contenido completo
        JPanel panelCentral = new JPanel(new BorderLayout(10, 10));
        panelCentral.setBackground(Color.WHITE);
        panelCentral.add(panelInfo, BorderLayout.NORTH);
        panelCentral.add(panelCampos, BorderLayout.CENTER);
        panelCentral.add(lblNota, BorderLayout.SOUTH);
        
        panelContenido.add(panelCentral, BorderLayout.CENTER);
        
        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        panelBotones.setBackground(Color.WHITE);
        
        JButton btnGuardar = new JButton("💾 Guardar Cambios");
        btnGuardar.setBackground(new Color(39, 174, 96));
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setFont(new Font("Arial", Font.BOLD, 12));
        btnGuardar.setFocusPainted(false);
        
        JButton btnCancelar = new JButton("❌ Cancelar");
        btnCancelar.setBackground(new Color(149, 165, 166));
        btnCancelar.setForeground(Color.WHITE);
        btnCancelar.setFont(new Font("Arial", Font.BOLD, 12));
        btnCancelar.setFocusPainted(false);
        
        btnGuardar.addActionListener(ev -> {
            String nuevoNombre = txtNombre.getText().trim();
            String nuevaPass = new String(txtPass.getPassword()).trim();
            String nuevoRol = (String) cmbNuevoRol.getSelectedItem();
            
            // Validar que al menos el nombre no esté vacío
            if (nuevoNombre.isEmpty()) {
                JOptionPane.showMessageDialog(dialogo, 
                    "⚠️ El nombre completo no puede estar vacío.", 
                    "Validación", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            boolean ok = controladorUsuario.actualizarUsuario(id, nuevoNombre, nuevaPass, nuevoRol);
            if (ok) {
                cargarUsuarios();
                dialogo.dispose();
                JOptionPane.showMessageDialog(this, 
                    "✅ Usuario actualizado exitosamente.\n\n" +
                    "📋 Cambios aplicados:\n" +
                    "• Nombre: " + nuevoNombre + "\n" +
                    "• Rol: " + nuevoRol + "\n" +
                    (nuevaPass.isEmpty() ? "• Contraseña: (sin cambios)" : "• Contraseña: (actualizada)"),
                    "Éxito", 
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(dialogo, 
                    "❌ No se pudo actualizar el usuario.\n\n" +
                    "Posibles razones:\n" +
                    "• Error de conexión con MongoDB\n" +
                    "• Usuario no encontrado", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        
        btnCancelar.addActionListener(ev -> dialogo.dispose());
        
        panelBotones.add(btnCancelar);
        panelBotones.add(btnGuardar);
        panelContenido.add(panelBotones, BorderLayout.SOUTH);
        
        dialogo.add(panelContenido);
        dialogo.setVisible(true);
    }

    private void aplicarFiltroBitacora() {
        String idUsuario = txtFiltroUsuarioBitacora.getText().trim();
        String ultimosStr = txtFiltroUltimos.getText().trim();
        boolean tieneUltimos = !ultimosStr.isBlank();
        modeloBitacora.setRowCount(0);
        if (!idUsuario.isBlank()) {
            controladorBitacora.getRegistrosPorUsuario(idUsuario)
                .forEach(b -> modeloBitacora.addRow(new Object[]{b.getIdRegistro(), b.getUsuario().getNombreUsuario(), b.getAccion(), b.getDescripcion()}));
        } else if (tieneUltimos) {
            try {
                int n = Integer.parseInt(ultimosStr);
                controladorBitacora.getUltimosRegistros(n)
                    .forEach(b -> modeloBitacora.addRow(new Object[]{b.getIdRegistro(), b.getUsuario().getNombreUsuario(), b.getAccion(), b.getDescripcion()}));
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Valor 'Últimos' inválido.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            cargarBitacora();
        }
    }

    /**
     * Carga todos los roles disponibles desde MongoDB al iniciar
     */
    private void cargarRolesPersonalizados() {
        cmbRol.removeAllItems();
        
        // Agregar roles predeterminados
        cmbRol.addItem(Usuario.ROL_JEFATURA_FINANCIERA);
        cmbRol.addItem(Usuario.ROL_ASISTENTE_CONTABLE);
        
        // Agregar roles personalizados desde MongoDB
        List<String> rolesPersonalizados = controladorUsuario.getTodosLosRolesDisponibles();
        for (String rol : rolesPersonalizados) {
            // Evitar duplicados de roles predeterminados
            if (!rol.equals(Usuario.ROL_JEFATURA_FINANCIERA) && 
                !rol.equals(Usuario.ROL_ASISTENTE_CONTABLE)) {
                cmbRol.addItem(rol);
            }
        }
    }
    
    private void crearRolPersonalizado(ActionEvent e) {
        GestionRolesDialog dialog = new GestionRolesDialog(this);
        dialog.setVisible(true);
        
        if (dialog.isConfirmado()) {
            String nombreRol = dialog.getNombreRol();
            java.util.Set<String> permisos = dialog.getPermisosSeleccionados();
            
            // Guardar el rol en MongoDB usando el controlador
            boolean creado = controladorUsuario.crearRolPersonalizado(nombreRol, permisos);
            
            if (creado) {
                // Recargar el combobox con todos los roles
                cargarRolesPersonalizados();
                cmbRol.setSelectedItem(nombreRol);
                
                JOptionPane.showMessageDialog(this,
                    "✅ Rol '" + nombreRol + "' creado y guardado exitosamente en MongoDB.\n\n" +
                    "Permisos asignados: " + permisos.size() + "\n" +
                    "Ahora puede asignar este rol al crear usuarios.",
                    "Rol Creado",
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                    "⚠️ No se pudo crear el rol.\n\n" +
                    "Posibles razones:\n" +
                    "• El rol ya existe\n" +
                    "• Error de conexión con MongoDB",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
