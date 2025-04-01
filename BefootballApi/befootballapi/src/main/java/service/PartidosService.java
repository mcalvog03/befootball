/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import model.Partidos;
import repository.PartidosRepository;

@Service
public class PartidosService {
    
    @Autowired
    private final PartidosRepository partidosRepository;
    
    public PartidosService(PartidosRepository partidoRepository) {
        this.partidosRepository = partidoRepository;
    }
    
    // Método para obtener todos los partidos
    public List<Partidos> obtenerTodosLosPartidos() {
        return partidosRepository.findAll(); // Devuelve todos los partidos desde la base de datos
    }

    public List<Partidos> obtenerPartidosPorLiga(int ligaId) {
        return partidosRepository.findByLigaPkLiga(ligaId);
    }

    public List<Partidos> obtenerPartidosPorJornada(int jornada) {
        return partidosRepository.findByJornada(jornada);
    }

    public List<Partidos> obtenerPartidosPorEquipoLocal(int equipoLocalId) {
        return partidosRepository.findByEquipoLocalPkEquipo(equipoLocalId);
    }

    public List<Partidos> obtenerPartidosPorEquipoVisitante(int equipoVisitanteId) {
        return partidosRepository.findByEquipoVisitantePkEquipo(equipoVisitanteId);
    }

    public Optional<Partidos> obtenerPartidoPorId(int pkPartido) {
        return partidosRepository.findById(pkPartido);  // Devuelve un Optional
    }

    public Partidos guardarPartido(Partidos partido) {
        return partidosRepository.save(partido);
    }

    public void eliminarPartido(int id) {
        partidosRepository.deleteById(id);
    }

}
