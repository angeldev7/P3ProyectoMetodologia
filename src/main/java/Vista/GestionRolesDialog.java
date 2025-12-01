package Vista;

import Modelo.Usuario;
import javax.swing.*;
import java.awt.*;
import java.util.*;

/**
 * Diálogo para crear roles personalizados con permisos específicos
 * Solo accesible para Admin Master
 */
public class GestionRolesDialog extends JDialog {
    private JTextField txtNombreRol;
    private Map<String, JCheckBox> checkboxPermisos;
    private Set<String> permisosSeleccionados;
    private boolean confirmado = false;
    
    public GestionRolesDialog(Frame parent) {
        super(parent, "Crear Rol Personalizado", true);
        permisosSeleccionados = new HashSet<>();
        checkboxPermisos = new HashMap<>();
        
        setSize(500, 600);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));
        
        construirUI();
    }
    
    private void construirUI() {
        // Panel superior - Nombre del rol
        JPanel panelNombre = new JPanel(new BorderLayout(5, 5));
        panelNombre.setBorder(BorderFactory.createTitledBorder("Información del Rol"));
        panelNombre.setBackground(Color.WHITE);
        
        JLabel lblNombre = new JLabel("Nombre del Rol:");
        lblNombre.setFont(new Font("Arial", Font.BOLD, 12));
        txtNombreRol = new JTextField(20);
        txtNombreRol.setFont(new Font("Arial", Font.PLAIN, 12));
        
        JPanel panelInput = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelInput.setBackground(Color.WHITE);
        panelInput.add(lblNombre);
        panelInput.add(txtNombreRol);
        panelNombre.add(panelInput, BorderLayout.CENTER);
        
        // Panel central - Permisos
        JPanel panelPermisos = new JPanel();
        panelPermisos.setLayout(new BoxLayout(panelPermisos, BoxLayout.Y_AXIS));
        panelPermisos.setBorder(BorderFactory.createTitledBorder("Permisos del Rol"));
        panelPermisos.setBackground(Color.WHITE);
        
        // Crear checkboxes para cada permiso
        agregarCheckboxPermiso(panelPermisos, Usuario.PERMISO_REGISTRAR_TRANSACCION, "Registrar Transacciones");
        agregarCheckboxPermiso(panelPermisos, Usuario.PERMISO_APROBAR_TRANSACCION, "Aprobar Transacciones");
        agregarCheckboxPermiso(panelPermisos, Usuario.PERMISO_RECHAZAR_TRANSACCION, "Rechazar Transacciones");
        agregarCheckboxPermiso(panelPermisos, Usuario.PERMISO_ELIMINAR_TRANSACCION, "Eliminar Transacciones");
        agregarCheckboxPermiso(panelPermisos, Usuario.PERMISO_VER_BITACORA, "Ver Bitácora de Auditoría");
        agregarCheckboxPermiso(panelPermisos, Usuario.PERMISO_CALCULAR_IVA, "Calcular Retención de IVA");
        agregarCheckboxPermiso(panelPermisos, Usuario.PERMISO_GENERAR_RESPALDO, "Generar Respaldos");
        agregarCheckboxPermiso(panelPermisos, Usuario.PERMISO_GESTIONAR_USUARIOS, "Gestionar Usuarios");
        agregarCheckboxPermiso(panelPermisos, Usuario.PERMISO_CREAR_ROLES, "Crear Roles Personalizados");
        
        JScrollPane scrollPermisos = new JScrollPane(panelPermisos);
        scrollPermisos.setBorder(BorderFactory.createEmptyBorder());
        
        // Panel inferior - Botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panelBotones.setBackground(new Color(236, 240, 241));
        
        JButton btnCrear = new JButton("Crear Rol");
        btnCrear.setFont(new Font("Arial", Font.BOLD, 12));
        btnCrear.setBackground(new Color(39, 174, 96));
        btnCrear.setForeground(Color.WHITE);
        btnCrear.setFocusPainted(false);
        btnCrear.setBorderPainted(false);
        btnCrear.setPreferredSize(new Dimension(120, 35));
        btnCrear.addActionListener(e -> confirmarCreacion());
        
        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setFont(new Font("Arial", Font.BOLD, 12));
        btnCancelar.setBackground(new Color(231, 76, 60));
        btnCancelar.setForeground(Color.WHITE);
        btnCancelar.setFocusPainted(false);
        btnCancelar.setBorderPainted(false);
        btnCancelar.setPreferredSize(new Dimension(120, 35));
        btnCancelar.addActionListener(e -> dispose());
        
        panelBotones.add(btnCrear);
        panelBotones.add(btnCancelar);
        
        // Ensamblar todo
        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.setBackground(Color.WHITE);
        panelSuperior.add(panelNombre, BorderLayout.NORTH);
        panelSuperior.add(new JLabel(" "), BorderLayout.CENTER); // Espaciador
        
        add(panelSuperior, BorderLayout.NORTH);
        add(scrollPermisos, BorderLayout.CENTER);
        add(panelBotones, BorderLayout.SOUTH);
    }
    
    private void agregarCheckboxPermiso(JPanel panel, String codigoPermiso, String nombrePermiso) {
        JCheckBox checkbox = new JCheckBox(nombrePermiso);
        checkbox.setFont(new Font("Arial", Font.PLAIN, 12));
        checkbox.setBackground(Color.WHITE);
        checkbox.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        checkboxPermisos.put(codigoPermiso, checkbox);
        panel.add(checkbox);
    }
    
    private void confirmarCreacion() {
        String nombreRol = txtNombreRol.getText().trim();
        
        if (nombreRol.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Debe ingresar un nombre para el rol.",
                "Validación",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Recopilar permisos seleccionados
        permisosSeleccionados.clear();
        for (Map.Entry<String, JCheckBox> entry : checkboxPermisos.entrySet()) {
            if (entry.getValue().isSelected()) {
                permisosSeleccionados.add(entry.getKey());
            }
        }
        
        if (permisosSeleccionados.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Debe seleccionar al menos un permiso para el rol.",
                "Validación",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        confirmado = true;
        dispose();
    }
    
    public boolean isConfirmado() {
        return confirmado;
    }
    
    public String getNombreRol() {
        return txtNombreRol.getText().trim();
    }
    
    public Set<String> getPermisosSeleccionados() {
        return new HashSet<>(permisosSeleccionados);
    }
}
