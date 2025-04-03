/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import java.util.List;
import model.Ligas;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import repository.LigasRepository;

@Service
public class LigasService {
    
    @Autowired
    private final LigasRepository ligasRepository;
    
    public LigasService (LigasRepository ligaRepository) {
        this.ligasRepository = ligaRepository;
    }
    
    // Método para obtener todas las ligas
    public List<Ligas> obtenerTodasLigas() {
        return ligasRepository.findAll();
    }
}
