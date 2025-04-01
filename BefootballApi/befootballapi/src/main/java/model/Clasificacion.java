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
@Table(name = "clasificacion")
public class Clasificacion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pk_clasificacion")
    private int pkClasificacion;
    
    @ManyToOne
    @JoinColumn(name = "liga_id", referencedColumnName = "pk_liga")
    private Ligas liga;
    
    @ManyToOne
    @JoinColumn(name = "equipo_id", referencedColumnName = "pk_equipo")
    private Equipos equipo;
    
    @Column(name = "puntos")
    private int puntos;
    
    @Column(name = "partidos_jugados")
    private int partidosJugados;
    
    @Column(name = "partidos_ganados")
    private int partidosGanados;
    
    @Column(name = "partidos_empatados")
    private int partidosEmpatados;
    
    @Column(name = "partidos_perdidos")
    private int partidosPerdidos;
    
    @Column(name = "diferencia_goles")
    private int diferenciaGoles;

    public int getPkClasificacion() {
        return pkClasificacion;
    }

    public void setPkClasificacion(int pkClasificacion) {
        this.pkClasificacion = pkClasificacion;
    }

    public Ligas getLiga() {
        return liga;
    }

    public void setLiga(Ligas liga) {
        this.liga = liga;
    }

    public Equipos getEquipo() {
        return equipo;
    }

    public void setEquipo(Equipos equipo) {
        this.equipo = equipo;
    }

    public int getPuntos() {
        return puntos;
    }

    public void setPuntos(int puntos) {
        this.puntos = puntos;
    }

    public int getPartidosJugados() {
        return partidosJugados;
    }

    public void setPartidosJugados(int partidosJugados) {
        this.partidosJugados = partidosJugados;
    }

    public int getPartidosGanados() {
        return partidosGanados;
    }

    public void setPartidosGanados(int partidosGanados) {
        this.partidosGanados = partidosGanados;
    }

    public int getPartidosEmpatados() {
        return partidosEmpatados;
    }

    public void setPartidosEmpatados(int partidosEmpatados) {
        this.partidosEmpatados = partidosEmpatados;
    }

    public int getPartidosPerdidos() {
        return partidosPerdidos;
    }

    public void setPartidosPerdidos(int partidosPerdidos) {
        this.partidosPerdidos = partidosPerdidos;
    }

    public int getDiferenciaGoles() {
        return diferenciaGoles;
    }

    public void setDiferenciaGoles(int diferenciaGoles) {
        this.diferenciaGoles = diferenciaGoles;
    }
    
    
}
