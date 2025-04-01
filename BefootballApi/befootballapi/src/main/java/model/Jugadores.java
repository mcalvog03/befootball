/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "jugadores")
public class Jugadores {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pk_jugador")
    private int pkJugador;
    
    @Column(name = "nombre", columnDefinition = "LONGTEXT")
    private String nombreJugador;
    
    @ManyToOne
    @JoinColumn(name = "equipo_id", referencedColumnName = "pk_equipo")
    private Equipos equipo;
    
    @ManyToOne
    @JoinColumn(name = "nacionalidad", referencedColumnName = "pk_pais")
    private Paises nacionalidad;
    
    @Column(name = "posicion", columnDefinition = "LONGTEXT")
    private String posicion;
    
    @Column(name = "dorsal")
    private int dorsal;

    public int getPkJugador() {
        return pkJugador;
    }

    public void setPkJugador(int pkJugador) {
        this.pkJugador = pkJugador;
    }

    public String getNombreJugador() {
        return nombreJugador;
    }

    public void setNombreJugador(String nombreJugador) {
        this.nombreJugador = nombreJugador;
    }

    public Equipos getEquipo() {
        return equipo;
    }

    public void setEquipo(Equipos equipo) {
        this.equipo = equipo;
    }

    public Paises getNacionalidad() {
        return nacionalidad;
    }

    public void setNacionalidad(Paises nacionalidad) {
        this.nacionalidad = nacionalidad;
    }

    public String getPosicion() {
        return posicion;
    }

    public void setPosicion(String posicion) {
        this.posicion = posicion;
    }

    public int getDorsal() {
        return dorsal;
    }

    public void setDorsal(int dorsal) {
        this.dorsal = dorsal;
    }
    
    
}
