/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import java.util.List;
import java.util.Optional;
import model.Partidos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.PartidosService;

@RestController
@RequestMapping("/api/partidos")
public class PartidosController {
    
    private final PartidosService partidosService;
    
    @Autowired
    public PartidosController(PartidosService partidoService) {
        this.partidosService = partidoService;
    }

    // Obtener todos los partidos
    @GetMapping
    public ResponseEntity<List<Partidos>> getAllPartidos() {
        List<Partidos> partidos = partidosService.obtenerTodosLosPartidos();
        if (partidos.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // Si no hay partidos, devuelve 204 No Content
        }
        return new ResponseEntity<>(partidos, HttpStatus.OK); // Devuelve los partidos con estado 200 OK
    }

    // Obtener partidos por liga
    @GetMapping("/liga/{ligaId}")
    public ResponseEntity<List<Partidos>> getPartidosByLiga(@PathVariable int ligaId) {
        List<Partidos> partidos = partidosService.obtenerPartidosPorLiga(ligaId);
        if (partidos.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Si no hay partidos, retorna 404
        }
        return new ResponseEntity<>(partidos, HttpStatus.OK);
    }

    // Obtener partidos por jornada
    @GetMapping("/jornada/{jornada}")
    public ResponseEntity<List<Partidos>> getPartidosByJornada(@PathVariable int jornada) {
        List<Partidos> partidos = partidosService.obtenerPartidosPorJornada(jornada);
        if (partidos.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Si no hay partidos, retorna 404
        }
        return new ResponseEntity<>(partidos, HttpStatus.OK);
    }

    // Obtener partido por ID
    @GetMapping("/{id}")
    public ResponseEntity<Partidos> getPartidoById(@PathVariable int id) {
        Optional<Partidos> partido = partidosService.obtenerPartidoPorId(id);
        if (partido.isPresent()) {
            return new ResponseEntity<>(partido.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Si no se encuentra el partido, retorna 404
    }

    // Crear un nuevo partido
    @PostMapping
    public ResponseEntity<Partidos> createPartido(@RequestBody Partidos partido) {
        // Aquí puedes agregar validaciones, por ejemplo si los datos son correctos
        Partidos nuevoPartido = partidosService.guardarPartido(partido);
        return new ResponseEntity<>(nuevoPartido, HttpStatus.CREATED);
    }

    // Eliminar partido
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePartido(@PathVariable int id) {
        // Verifica si el partido existe antes de eliminar
        if (!partidosService.obtenerPartidoPorId(id).isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        partidosService.eliminarPartido(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
