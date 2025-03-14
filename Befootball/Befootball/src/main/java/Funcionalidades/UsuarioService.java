package Funcionalidades;

import POJOS.Usuarios;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import javax.swing.JOptionPane;
import org.hibernate.Session;

public class UsuarioService {

    // Método para registrar un usuario
    public void registerUser(String nombre, String correo, String contraseña, String rol) {
        // Validar que los campos no estén vacíos
        if (nombre == null || nombre.trim().isEmpty() || correo == null || correo.trim().isEmpty() || contraseña == null || contraseña.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Por favor, complete todos los campos.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Generar un salt aleatorio
        String salt = generateSalt();

        // Hashear la contraseña con el salt
        String passwordHash = hashPassword(contraseña, salt);

        // Crear el usuario
        Usuarios usuario = new Usuarios();
        usuario.setNombre(nombre);
        usuario.setCorreo(correo);
        usuario.setSalt(salt);
        usuario.setPasswordHash(passwordHash);
        usuario.setRol(rol);  // Asignar el rol al usuario
        usuario.setFechaRegistro(LocalDateTime.now());  // Asignar la fecha actual

        // Abrir sesión con Hibernate
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();

        try {
            // Guardar el usuario en la base de datos utilizando persist
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

    // Método para generar un salt único
    private String generateSalt() {
        // Usamos SecureRandom para generar un salt aleatorio de 16 bytes
        SecureRandom secureRandom = new SecureRandom();
        byte[] saltBytes = new byte[16];
        secureRandom.nextBytes(saltBytes);
        
        // Convertir el salt a una cadena Base64
        return Base64.getEncoder().encodeToString(saltBytes);
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
