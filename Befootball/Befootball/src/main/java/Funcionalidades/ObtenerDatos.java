/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Funcionalidades;

import POJOS.Equipos;
import POJOS.Ligas;
import POJOS.Partidos;
import POJOS.Usuarios;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

public class ObtenerDatos {

    private SessionFactory factory;

    public ObtenerDatos(SessionFactory factory) {
        this.factory = factory;
    }
    
    public Usuarios obtenerDatosUsuario(int pkUsuario) {
    
        Usuarios usuario = null;
    try (Session session = factory.openSession()) {
        // Iniciar transacción
        Transaction transaction = session.beginTransaction();
        
        // Buscar el usuario en la base de datos
        usuario = session.find(Usuarios.class, pkUsuario); 
        
        // Confirmar la transacción
        transaction.commit();
    } catch (Exception e) {
        e.printStackTrace();
    }
    
    return usuario;
}
    
    // Obtener ligas
    public List<String> obtenerLigas() {
        List<String> nombresLigas = new ArrayList<>();
        Transaction tx = null;

        try (Session session = factory.openSession()) {
            tx = session.beginTransaction();

            // Obtener nombres de ligas de la base de datos
            List<Ligas> ligasList = session.createQuery("FROM Ligas", Ligas.class).getResultList();

            // Agregar los nombres de las ligas en una lista
            for (Ligas liga : ligasList) {
                nombresLigas.add(liga.getNombreLiga());
            }

            tx.commit();
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            JOptionPane.showMessageDialog(null, "Error al obtener las ligas: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            System.err.println("Error al obtener las ligas: " + e.getMessage());
        }

        return nombresLigas;
    }

    // Obtener los equipos de una liga
    public List<String> obtenerEquiposDeLiga(Ligas liga) {
        List<String> nombresEquipos = new ArrayList<>();
        Transaction tx = null;

        try (Session session = factory.openSession()) {
            tx = session.beginTransaction();

            // Obtener nombres de los equipos dependiendo de la liga seleccionada en el comboBox
            List<Equipos> equiposList = session.createQuery("FROM Equipos e WHERE e.liga = :liga", Equipos.class)
                    .setParameter("liga", liga)
                    .getResultList();

            // Agregar los nombres a una lista
            for (Equipos equipo : equiposList) {
                nombresEquipos.add(equipo.getNombreEquipo());
            }

            tx.commit();
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            JOptionPane.showMessageDialog(null, "Error al obtener equipos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            System.err.println("Error al obtener los equipos: " + e.getMessage());
        }

        return nombresEquipos;
    }

    // Obtener las jornadas existentes por liga
    public List<Integer> obtenerJornadas(String ligaSeleccionada) {
        List<Integer> jornadas = new ArrayList<>();
        Transaction tx = null;

        try (Session session = factory.openSession()) {
            tx = session.beginTransaction();

            // Obtener el objeto Ligas correspondiente al nombre de la liga
            Ligas liga = session.createQuery("FROM Ligas l WHERE l.nombreLiga = :ligaNombre", Ligas.class)
                    .setParameter("ligaNombre", ligaSeleccionada)
                    .uniqueResult();

            // Si no se encuentra la liga mostrar mensaje de error
            if (liga == null) {
                JOptionPane.showMessageDialog(null, "Liga no encontrada: " + ligaSeleccionada, "Error", JOptionPane.ERROR_MESSAGE);
                // Retorna lista vacía si no se encuentra la liga
                return jornadas;
            }

            // Obtener los partidos correspondientes a la liga utilizando el ID de la liga
            List<Partidos> partidosList = session.createQuery("FROM Partidos p WHERE p.liga.id = :ligaId", Partidos.class)
                    .setParameter("ligaId", liga.getPkLiga())
                    .getResultList();

            // Agregar las jornadas a la lista
            for (Partidos partido : partidosList) {
                jornadas.add(partido.getJornada());
            }

            tx.commit();
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            JOptionPane.showMessageDialog(null, "Error al obtener las jornadas: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            System.err.println("Error al obtener las jornadas: " + e.getMessage());
        }

        return jornadas;
    }

}
