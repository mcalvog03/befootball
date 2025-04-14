/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package FuncionalidadesHibernate;

import Funcionalidades.PasswordUtils;
import POJOS.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.JOptionPane;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

public class ObtenerSubirDatos {

    private SessionFactory factory;

    public ObtenerSubirDatos(SessionFactory factory) {
        this.factory = factory;
    }

    // Obtener datos del usuario que ha iniciado sesión
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

    // Obtener datos del equipo seleccionado
    public Equipos obtenerDatosEquipos(int pkEquipo) {
        Equipos equipo = null;
        try (Session session = factory.openSession()) {
            // Iniciar transacción
            Transaction transaction = session.beginTransaction();

            // Buscar el equipo en la base de datos
            equipo = session.find(Equipos.class, pkEquipo);

            // Confirmar la transacción
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return equipo;
    }

    public List<Jugadores> obtenerJugadoresEquipo(int pkEquipo) {
        List<Jugadores> jugadores = new ArrayList<>();
        Transaction tx = null;

        try (Session session = factory.openSession()) {
            tx = session.beginTransaction();

            // Obtener los partidos correspondientes a la liga y jornada seleccionada
            jugadores = session.createQuery("FROM Jugadores j WHERE j.equipo.id = :equipoid", Jugadores.class)
                    .setParameter("equipoid", pkEquipo)
                    .getResultList();

            tx.commit();
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            JOptionPane.showMessageDialog(null, "Error al obtener los jugadores: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            System.err.println("Error al obtener los jugadores: " + e.getMessage());
        }

        return jugadores;
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
        // Usar un Set para evitar jornadas duplicadas
        Set<Integer> jornadasSet = new HashSet<>();
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
                return new ArrayList<>();
            }

            // Obtener los partidos correspondientes a la liga utilizando el ID de la liga
            List<Partidos> partidosList = session.createQuery("FROM Partidos p WHERE p.liga.id = :ligaId", Partidos.class)
                    .setParameter("ligaId", liga.getPkLiga())
                    .getResultList();

            // Agregar las jornadas al Set para evitar duplicados
            for (Partidos partido : partidosList) {
                jornadasSet.add(partido.getJornada());
            }

            tx.commit();
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            JOptionPane.showMessageDialog(null, "Error al obtener las jornadas: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            System.err.println("Error al obtener las jornadas: " + e.getMessage());
        }

        // Convertir el Set de jornadas a una lista y devolverla
        return new ArrayList<>(jornadasSet);
    }

    // Obtener los partidos de la jornada y liga seleccionados mediante los comboBox de la interfaz
    public List<Partidos> obtenerPartidosFiltrados(String ligaSeleccionada, int jornadaSeleccionada) {
        List<Partidos> partidosFiltrados = new ArrayList<>();
        Transaction tx = null;

        try (Session session = factory.openSession()) {
            tx = session.beginTransaction();

            // Obtener el objeto Ligas correspondiente al nombre de la liga
            Ligas liga = session.createQuery("FROM Ligas l WHERE l.nombreLiga = :ligaNombre", Ligas.class)
                    .setParameter("ligaNombre", ligaSeleccionada)
                    .uniqueResult();

            // Si no se encuentra la liga, mostrar mensaje de error
            if (liga == null) {
                JOptionPane.showMessageDialog(null, "Liga no encontrada: " + ligaSeleccionada, "Error", JOptionPane.ERROR_MESSAGE);
                return partidosFiltrados;
            }

            // Obtener los partidos correspondientes a la liga y jornada seleccionada
            partidosFiltrados = session.createQuery("FROM Partidos p WHERE p.liga.id = :ligaId AND p.jornada = :jornada", Partidos.class)
                    .setParameter("ligaId", liga.getPkLiga())
                    .setParameter("jornada", jornadaSeleccionada)
                    .getResultList();

            tx.commit();
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            JOptionPane.showMessageDialog(null, "Error al obtener los partidos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            System.err.println("Error al obtener los partidos: " + e.getMessage());
        }

        return partidosFiltrados;
    }

    // Obtener clasificación de la liga seleccionada
    public List<Clasificacion> obtenerClasifiacionFiltrada(String ligaSeleccionada) {
        List<Clasificacion> clasificacionFiltrada = new ArrayList<>();
        Transaction tx = null;

        try (Session session = factory.openSession()) {
            tx = session.beginTransaction();

            // Obtener el objeto Ligas correspondiente al nombre de la liga
            Ligas liga = session.createQuery("FROM Ligas l WHERE l.nombreLiga = :ligaNombre", Ligas.class)
                    .setParameter("ligaNombre", ligaSeleccionada)
                    .uniqueResult();

            // Obtener los equipos correspondientes a la liga seleccionada
            clasificacionFiltrada = session.createQuery("FROM Clasificacion c WHERE c.liga.id = :ligaId", Clasificacion.class)
                    .setParameter("ligaId", liga.getPkLiga())
                    .getResultList();

            tx.commit();
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            JOptionPane.showMessageDialog(null, "Error al obtener la clasificacion: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            System.err.println("Error al obtener la clasificacion: " + e.getMessage());
        }

        return clasificacionFiltrada;
    }

    // Obtener un partido por id
    public Partidos obtenerDatosPartido(int pkPartido) {
        Partidos partido = null;
        try (Session session = factory.openSession()) {
            // Iniciar transacción
            Transaction transaction = session.beginTransaction();

            // Buscar el partido en la base de datos
            partido = session.find(Partidos.class, pkPartido);

            // Confirmar la transacción
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return partido;
    }

    // Metodo para seleccionar el equipo favorito del usuario utilizando
    public void seleccionarEquipoFav(int pkUsuario, int pkEquipo) {
        Transaction tx = null;
        try (Session session = factory.openSession()) {
            tx = session.beginTransaction();

            // Obtener el usuario por id
            Usuarios usuario = session.find(Usuarios.class, pkUsuario);

            if (usuario != null) {
                // Establecer el equipo seleccionado en el Jlist
                usuario.setEquipoFavorito(obtenerDatosEquipos(pkEquipo));

                // Guardar los cambios en la base de datos
                session.persist(usuario);

                tx.commit();
            } else {
                System.out.println("Usuario no encontrado con ID: " + pkUsuario);
            }
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
        }
    }

    // Actualizar el nombre del usuario
    public void actualizarNombreUsuario(String nombre, int pkUsuario) {
        Transaction tx = null;
        try (Session session = factory.openSession()) {
            tx = session.beginTransaction();

            // Obtener el usuario por id
            Usuarios usuario = session.find(Usuarios.class, pkUsuario);

            if (nombre == null ? usuario.getNombre() != null : !nombre.equals(usuario.getNombre())) {
                usuario.setNombre(nombre);
                session.persist(usuario);
                tx.commit();
                JOptionPane.showMessageDialog(null, "Nombre actualizado.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "El nombre introducido es igual al actual", "Error", JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        }
    }

    // Actualizar el correo del usuario
    public void actualizarCorreoUsuario(String correo, int pkUsuario) {
        if (!correo.contains("@") || !correo.contains(".")) {
            JOptionPane.showMessageDialog(null, "El correo debe contener '@' y '.'", "Correo no válido", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Transaction tx = null;
        try (Session session = factory.openSession()) {
            tx = session.beginTransaction();

            // Obtener el usuario por id
            Usuarios usuario = session.find(Usuarios.class, pkUsuario);

            if (correo == null ? usuario.getCorreo() != null : !correo.equals(usuario.getCorreo())) {
                usuario.setCorreo(correo);
                session.persist(usuario);
                tx.commit();
                JOptionPane.showMessageDialog(null, "Correo actualizado.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "El correo introducido es igual al actual", "Error", JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        }
    }

    // Actualizar la contraseña del usuario
    public void actualizarContraseñaUsuario(String contraseña, int pkUsuario) {
        Transaction tx = null;
        try (Session session = factory.openSession()) {
            tx = session.beginTransaction();

            // Obtener el usuario por id
            Usuarios usuario = session.find(Usuarios.class, pkUsuario);

            // Generar un salt aleatorio con el metodo de PasswordUtils
            String salt = PasswordUtils.generateSalt();

            // Hashear la contraseña con el salt
            String passwordHash = PasswordUtils.hashPassword(contraseña, salt);

            usuario.setSalt(salt);
            usuario.setPasswordHash(passwordHash);
            session.persist(usuario);
            tx.commit();
            JOptionPane.showMessageDialog(null, "Contraseña actualizada.", "Éxito", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        }
    }

    public Roles obtenerRol(String nombreRol) {
        Roles rol = null;
        Transaction tx = null;

        try (Session session = factory.openSession()) {
            tx = session.beginTransaction();

            rol = session.createQuery("FROM Roles r WHERE r.rol = :nombre", Roles.class)
                    .setParameter("nombre", nombreRol)
                    .uniqueResult();

            tx.commit();
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            JOptionPane.showMessageDialog(null, "Error al obtener el rol: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            System.err.println("Error al obtener el rol: " + e.getMessage());
        }

        return rol;
    }

}
