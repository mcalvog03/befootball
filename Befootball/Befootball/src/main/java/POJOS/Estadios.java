/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package POJOS;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "estadios")
public class Estadios {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pkEstadio")
    private int pkEstadio;
    
    @Column(name = "nombre", columnDefinition = "LONGTEXT")
    private String nombreEstadio;

    public int getPkEstadio() {
        return pkEstadio;
    }

    public void setPkEstadio(int pkEstadio) {
        this.pkEstadio = pkEstadio;
    }

    public String getNombreEstadio() {
        return nombreEstadio;
    }

    public void setNombreEstadio(String nombreEstadio) {
        this.nombreEstadio = nombreEstadio;
    }
    
    
}
