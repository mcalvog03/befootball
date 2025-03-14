/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Funcionalidades;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class PasswordUtils {

    // Genera un salt aleatorio
    public static String generateSalt() {
        SecureRandom sr = new SecureRandom();
        // Generar salt de 16 bytes
        byte[] salt = new byte[16];
        sr.nextBytes(salt);
        // Codifica el salt en Base64
        return Base64.getEncoder().encodeToString(salt);
    }

    // Hashea la contraseña con el salt
    public static String hashPassword(String password, String salt) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        // Agregar el salt al hash
        md.update(Base64.getDecoder().decode(salt));
        byte[] hashedPassword = md.digest(password.getBytes());
        return Base64.getEncoder().encodeToString(hashedPassword);
    }
}

