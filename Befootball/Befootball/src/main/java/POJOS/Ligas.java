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
@Table(name = "ligas")
public class Ligas {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pkLiga")
    private int pkLiga;
    
    @Column(name = "nombre", columnDefinition = "LONGTEXT")
    private String nombreLiga;
    
    @Column(name = "temporada", columnDefinition = "LONGTEXT")
    private String temporada;
    
    @ManyToOne
    @JoinColumn(name = "pais_id", referencedColumnName = "pkPais")
    private Paises pais;

    public int getPkLiga() {
        return pkLiga;
    }

    public void setPkLiga(int pkLiga) {
        this.pkLiga = pkLiga;
    }

    public String getNombreLiga() {
        return nombreLiga;
    }

    public void setNombreLiga(String nombreLiga) {
        this.nombreLiga = nombreLiga;
    }

    public String getTemporada() {
        return temporada;
    }

    public void setTemporada(String temporada) {
        this.temporada = temporada;
    }

    public Paises getPais() {
        return pais;
    }

    public void setPais(Paises pais) {
        this.pais = pais;
    }
    
}
