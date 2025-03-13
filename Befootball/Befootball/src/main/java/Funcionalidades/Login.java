/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Funcionalidades;

import POJOS.Usuarios;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

public class Login {

    private final SessionFactory factory;

    public Login() {
        // Configuración manual de Hibernate sin HibernateUtil
        this.factory = new Configuration().configure("hibernate.cfg.xml").buildSessionFactory();
    }

    public Usuarios autenticar(String correo, String contraseña) {
        try (Session session = factory.openSession()) {
            Query<Usuarios> query = session.createQuery(
                    "FROM Usuarios WHERE correo = :correo AND contraseña = :contraseña", Usuarios.class);
            query.setParameter("correo", correo);
            query.setParameter("contraseña", contraseña);

            return query.uniqueResult(); // Retorna el usuario si existe, sino null
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void cerrar() {
        if (factory != null) {
            factory.close();
        }
    }
}
