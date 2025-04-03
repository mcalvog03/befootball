/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import java.util.List;
import model.Ligas;
import model.Partidos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import service.LigasService;

@RestController
@RequestMapping("/api")
public class LigasController {

    private final LigasService ligasService;

    @Autowired
    public LigasController(LigasService ligaService) {
        this.ligasService = ligaService;
    }

    // Obtener todas las ligas
    @GetMapping("/ligas")
    public ResponseEntity<List<Ligas>> getAllLigas() {
        List<Ligas> ligas = ligasService.obtenerTodasLigas();
        if (ligas.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(ligas, HttpStatus.OK);
    }

}
