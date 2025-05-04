/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ComponentesModelos;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;

public class EstiloTablaPartidos {

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

        for (int i = 1; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }
}
