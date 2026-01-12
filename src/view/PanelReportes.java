// View/PanelReportes.java
package view;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

public class PanelReportes extends JPanel {
    private static final long serialVersionUID = 1L;
    public JComboBox<String> cmbTipoReporte;
    public JButton btnGenerarReporte;
    public JTextArea txtReporte;

    public PanelReportes() {
        setBackground(new Color(45, 45, 45));
        setBorder(new TitledBorder(new LineBorder(new Color(90, 90, 90)), " Reportes ", 
                  TitledBorder.LEADING, TitledBorder.TOP, 
                  new Font(Font.SANS_SERIF, Font.BOLD, 14), new Color(220, 220, 220)));
        
        inicializarComponentes();
        configurarLayout();
    }

    private void inicializarComponentes() {
        // ComboBox
        cmbTipoReporte = new JComboBox<>(new String[]{"Reporte de Ventas", "Alerta de Stock Bajo", "Catálogo de Productos"});
        cmbTipoReporte.addItem("Historial de Accesos");
        estiloComboBox(cmbTipoReporte);

        // Botón
        btnGenerarReporte = crearBoton("📊 Generar Reporte", new Color(108, 117, 125));

        // Área de texto
        txtReporte = new JTextArea(20, 50);
        estiloAreaTexto(txtReporte);
        txtReporte.setEditable(false);
    }

    private void configurarLayout() {
        setLayout(new BorderLayout(10, 10));
        
        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBackground(new Color(45, 45, 45));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Panel de control
        JPanel panelControl = crearPanelControl();
        panelPrincipal.add(panelControl, BorderLayout.NORTH);
        
        // Panel de reporte
        JPanel panelReporte = crearPanelReporte();
        panelPrincipal.add(panelReporte, BorderLayout.CENTER);
        
        add(panelPrincipal, BorderLayout.CENTER);
    }

    private JPanel crearPanelControl() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panel.setBackground(new Color(55, 55, 55));
        panel.setBorder(new TitledBorder(new LineBorder(new Color(90, 90, 90)), " Configuración de Reporte ", 
                    TitledBorder.LEADING, TitledBorder.TOP, 
                    new Font(Font.SANS_SERIF, Font.BOLD, 12), new Color(200, 200, 200)));
        
        panel.add(crearEtiqueta("Tipo de Reporte:"));
        cmbTipoReporte.setPreferredSize(new Dimension(180, 30));
        panel.add(cmbTipoReporte);
        panel.add(btnGenerarReporte);
        
        return panel;
    }

    private JPanel crearPanelReporte() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(55, 55, 55));
        panel.setBorder(new TitledBorder(new LineBorder(new Color(90, 90, 90)), " Reporte Generado ", 
                    TitledBorder.LEADING, TitledBorder.TOP, 
                    new Font(Font.SANS_SERIF, Font.BOLD, 12), new Color(200, 200, 200)));
        
        JScrollPane scrollReporte = new JScrollPane(txtReporte);
        scrollReporte.setBorder(new LineBorder(new Color(90, 90, 90)));
        scrollReporte.setPreferredSize(new Dimension(0, 400));
        
        panel.add(scrollReporte, BorderLayout.CENTER);
        return panel;
    }

    private JLabel crearEtiqueta(String texto) {
        JLabel etiqueta = new JLabel(texto);
        etiqueta.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        etiqueta.setForeground(new Color(200, 200, 200));
        return etiqueta;
    }

    private void estiloComboBox(JComboBox<String> comboBox) {
        comboBox.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        comboBox.setBackground(new Color(60, 60, 60));
        comboBox.setForeground(new Color(220, 220, 220));
        comboBox.setBorder(new LineBorder(new Color(90, 90, 90), 2));
    }

    private JButton crearBoton(String texto, Color colorFondo) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        boton.setBackground(colorFondo);
        boton.setForeground(Color.WHITE);
        boton.setFocusPainted(false);
        boton.setBorder(new LineBorder(colorFondo.darker(), 2));
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boton.setPreferredSize(new Dimension(160, 35));
        return boton;
    }

    private void estiloAreaTexto(JTextArea areaTexto) {
        areaTexto.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        areaTexto.setBackground(new Color(60, 60, 60));
        areaTexto.setForeground(new Color(220, 220, 220));
        areaTexto.setCaretColor(Color.WHITE);
        areaTexto.setBorder(new LineBorder(new Color(90, 90, 90), 2));
    }
}