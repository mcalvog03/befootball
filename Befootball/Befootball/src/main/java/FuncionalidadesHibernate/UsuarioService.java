package FuncionalidadesHibernate;

import Funcionalidades.PasswordUtils;
import POJOS.Usuarios;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import javax.swing.JOptionPane;
import org.hibernate.Session;

public class UsuarioService {

    // Método para registrar un usuario
    public void registerUser(String nombre, String correo, String contraseña) {
        // Validar que los campos no estén vacíos
        if (nombre == null || nombre.trim().isEmpty() || correo == null || correo.trim().isEmpty() || contraseña == null || contraseña.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Por favor, complete todos los campos.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Generar un salt aleatorio con el metodo de PasswordUtils
        String salt = PasswordUtils.generateSalt();

        // Hashear la contraseña con el salt
        String passwordHash = hashPassword(contraseña, salt);

        // Crear el usuario
        Usuarios usuario = new Usuarios();
        usuario.setNombre(nombre);
        usuario.setCorreo(correo);
        usuario.setSalt(salt);
        usuario.setPasswordHash(passwordHash);
        usuario.setRol("USUARIO");
        usuario.setFechaRegistro(LocalDateTime.now());

        // Abrir sesión con Hibernate
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();

        try {
            // Guardar el usuario en la base de datos
            session.persist(usuario);
            session.getTransaction().commit();
            JOptionPane.showMessageDialog(null, "Usuario registrado con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            session.getTransaction().rollback();
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al registrar el usuario.", "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            session.close();
        }
    }

    
    // Método para hashear la contraseña con el salt
    private String hashPassword(String password, String salt) {
        try {
            // Utilizamos el método hashPassword de PasswordUtils
            return PasswordUtils.hashPassword(password, salt);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al generar el hash de la contraseña.", "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
}
