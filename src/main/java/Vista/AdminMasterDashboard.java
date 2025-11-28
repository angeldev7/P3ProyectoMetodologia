package Vista;

import Controlador.ControladorUsuario;
import Controlador.ControladorBitacora;
import Modelo.Usuario;
import Modelo.TipoRol;

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
        cmbRol = new JComboBox<>(new String[]{TipoRol.JEFATURA_FINANCIERA, TipoRol.ASISTENTE_CONTABLE});
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
        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.CENTER));
        acciones.add(btnAgregar); acciones.add(btnEditar); acciones.add(btnEliminar);

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
            JOptionPane.showMessageDialog(this, "Seleccione un usuario.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String id = (String) modeloUsuarios.getValueAt(row, 0);
        String usuario = (String) modeloUsuarios.getValueAt(row, 1);
        String nombre = (String) modeloUsuarios.getValueAt(row, 2);
        String rol = (String) modeloUsuarios.getValueAt(row, 3);
        JTextField txtNombre = new JTextField(nombre, 20);
        JPasswordField txtPass = new JPasswordField(20);
        JComboBox<String> cmbNuevoRol = new JComboBox<>(new String[]{TipoRol.JEFATURA_FINANCIERA, TipoRol.ASISTENTE_CONTABLE});
        cmbNuevoRol.setSelectedItem(rol);
        JPanel panel = new JPanel(new GridLayout(0,1,5,5));
        panel.add(new JLabel("ID: "+id));
        panel.add(new JLabel("Usuario: "+usuario));
        panel.add(labeled("Nombre Completo", txtNombre));
        panel.add(labeled("Nueva Contraseña (opcional)", txtPass));
        panel.add(labeled("Nuevo Rol", cmbNuevoRol));
        int opt = JOptionPane.showConfirmDialog(this, panel, "Editar Usuario", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (opt != JOptionPane.OK_OPTION) return;
        String nuevoNombre = txtNombre.getText().trim();
        String nuevaPass = new String(txtPass.getPassword()).trim();
        String nuevoRol = (String) cmbNuevoRol.getSelectedItem();
        boolean ok = ControladorUsuario.getInstancia().actualizarUsuario(id, nuevoNombre, nuevaPass, nuevoRol);
        if (ok) {
            cargarUsuarios();
            JOptionPane.showMessageDialog(this, "Usuario actualizado.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "No se pudo actualizar.", "Error", JOptionPane.ERROR_MESSAGE);
        }
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
}
