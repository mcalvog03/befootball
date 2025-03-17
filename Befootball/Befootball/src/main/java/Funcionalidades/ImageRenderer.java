/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Funcionalidades;

import javax.swing.ImageIcon;
import static javax.swing.SwingConstants.CENTER;
import javax.swing.table.DefaultTableCellRenderer;

public class ImageRenderer extends DefaultTableCellRenderer {

    @Override
    public void setValue(Object value) {
        if (value instanceof ImageIcon) {
            // Establecer el icono en la celda
            setIcon((ImageIcon) value);
            // Evita que se muestre texto en la celda
            setText(""); 
        } else {
            setText((value != null) ? value.toString() : "");
            setIcon(null);
        }
        setHorizontalAlignment(CENTER);
    }
}
