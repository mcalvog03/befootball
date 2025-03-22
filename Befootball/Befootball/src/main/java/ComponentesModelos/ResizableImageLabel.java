/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ComponentesModelos;

import javax.swing.*;
import java.awt.*;
import java.beans.*;

public class ResizableImageLabel extends JLabel {
    private ImageIcon icon;

    public ResizableImageLabel() {
        super();
        setHorizontalAlignment(CENTER); // Opcional: centrar la imagen
        setVerticalAlignment(CENTER);
    }

    /**
     * Establece un nuevo ícono en el componente.
     * Esto permite que el ImageChooser de NetBeans funcione.
     * @param icon El ícono a mostrar.
     */
    @Override
    public void setIcon(Icon icon) {
        if (icon instanceof ImageIcon) {
            this.icon = (ImageIcon) icon;
            // Convierte el ícono a una imagen y ajusta la etiqueta
            this.setImage(this.icon.getImage());
        } else {
            super.setIcon(icon); // Si no es ImageIcon, usa el comportamiento predeterminado
        }
    }

    /**
     * Devuelve el ícono actual del componente.
     */
    @Override
    public ImageIcon getIcon() {
        return icon;
    }

    private Image image;

    /**
     * Establece una nueva imagen en el componente.
     * @param image La imagen a mostrar.
     */
    public void setImage(Image image) {
        this.image = image;
        repaint(); // Solicitar la redibujación del componente
    }

    /**
     * Sobrescribe el método paintComponent para ajustar la imagen al tamaño del componente.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (image != null) {
            Graphics2D g2d = (Graphics2D) g;
            // Dibujar la imagen escalada para ajustarse al tamaño del JLabel
            g2d.drawImage(image, 0, 0, getWidth(), getHeight(), this);
        }
    }
}
