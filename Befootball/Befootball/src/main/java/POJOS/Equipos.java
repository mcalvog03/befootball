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
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "equipos")
public class Equipos {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pkEquipo")
    private int pkEquipo;
    
    @Column(name = "nombre", columnDefinition = "LONGTEXT")
    private String nombreEquipo;
    
    @ManyToOne
    @JoinColumn(name = "liga_id", referencedColumnName = "pkLiga")
    private Ligas liga;
    
    @ManyToOne
    @JoinColumn(name = "estadio_id", referencedColumnName = "pkEstadio")
    private Estadios estadio;
    
    @Column(name = "escudo", columnDefinition = "LONGTEXT")
    private String escudo;

    public int getPkEquipo() {
        return pkEquipo;
    }

    public void setPkEquipo(int pkEquipo) {
        this.pkEquipo = pkEquipo;
    }

    public String getNombreEquipo() {
        return nombreEquipo;
    }

    public void setNombreEquipo(String nombreEquipo) {
        this.nombreEquipo = nombreEquipo;
    }

    public Ligas getLiga() {
        return liga;
    }

    public void setLiga(Ligas liga) {
        this.liga = liga;
    }

    public Estadios getEstadio() {
        return estadio;
    }

    public void setEstadio(Estadios estadio) {
        this.estadio = estadio;
    }

    public String getEscudo() {
        return escudo;
    }

    public void setEscudo(String escudo) {
        this.escudo = escudo;
    }
    
    
    
}
