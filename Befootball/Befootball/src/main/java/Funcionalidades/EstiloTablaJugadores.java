/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Funcionalidades;

import java.awt.Color;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

public class EstiloTablaJugadores {
    public static void estilizarTabla(JTable table) {
        if (table == null) {
            return;
        }

        // Estilo del encabezado
        JTableHeader header = table.getTableHeader();
        //header.setFont(new Font("Arial", Font.BOLD, 14)); // Fuente del encabezado
        header.setBackground(new Color(0, 0, 0));
        header.setForeground(Color.WHITE); // Texto blanco
        header.setOpaque(true);

        // Alinear texto en el encabezado
        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer();
        headerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        headerRenderer.setBackground(new Color(0, 0, 0));
        headerRenderer.setForeground(Color.WHITE);
        //headerRenderer.setFont(new Font("Arial", Font.BOLD, 14));

        // Aplicar el renderer a todas las columnas del encabezado
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
        }
        
        // Cambiar color de fondo de la tabla
        table.getParent().setBackground(new Color(0, 0, 0));

        // Centrar texto en columnas
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
    }
}
