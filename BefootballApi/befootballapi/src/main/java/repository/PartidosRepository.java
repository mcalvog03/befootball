/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package repository;

import org.springframework.data.jpa.repository.JpaRepository;
import model.Partidos;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public interface PartidosRepository extends JpaRepository<Partidos, Integer> {

    // Método para buscar partidos por liga
    List<Partidos> findByLigaPkLiga(int ligaId);

    // Método para buscar partidos por jornada
    List<Partidos> findByJornada(int jornada);

    // Método para buscar partidos por equipo local
    List<Partidos> findByEquipoLocalPkEquipo(int equipoLocalId);

    // Método para buscar partidos por equipo visitante
    List<Partidos> findByEquipoVisitantePkEquipo(int equipoVisitanteId);

    // Método para obtener un partido por su id (en caso de que quieras buscar por pkPartido)
    Optional<Partidos> findByPkPartido(int pkPartido);
}
