    /*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package Interfaces;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JWindow;
import javax.swing.UnsupportedLookAndFeelException;

public class Splash extends JWindow {

    public Splash() {
        super();
        setSize(512, 512);
        setLocationRelativeTo(null);
        setLayout(null);
        crearGUI();
        setVisible(true);
        try {
            Thread.sleep(3000);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al ejecutar la ventana Splash");
        }
        setVisible(false);//ocultar el Splash
        dispose(); // destruir el Splash
        MainFrame login = new MainFrame(null, true);
        login.setVisible(true);

        //System.exit(0);
    }

    private void crearGUI() {
        ImageIcon img = new ImageIcon(getClass().getResource("/images/logo.png"));
        JLabel jl = new JLabel(img);
        jl.setBounds(0, 0, 512, 512);
        add(jl);
    }

    public static void main(String[] args) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (InstantiationException ex) {
            Logger.getLogger(Splash.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(Splash.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedLookAndFeelException ex) {
            Logger.getLogger(Splash.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Splash.class.getName()).log(Level.SEVERE, null, ex);
        }
        Splash obj = new Splash();
    }
}
