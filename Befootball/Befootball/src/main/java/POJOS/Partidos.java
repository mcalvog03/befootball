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
import java.util.Date;

@Entity
@Table(name = "partidos")
public class Partidos {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pk_partido")
    private int pkPartido;
    
    @ManyToOne
    @JoinColumn(name = "equipoLocal", referencedColumnName = "pk_equipo")
    private Equipos equipoLocal;
    
    @ManyToOne
    @JoinColumn(name = "equipoVisitante", referencedColumnName = "pk_equipo")
    private Equipos equipoVisitante;
    
    @Column(name = "estado", columnDefinition = "LONGTEXT")
    private String estado;
    
    @Column(name = "fecha", columnDefinition = "LONGTEXT")
    private Date fecha;
    
    @Column(name = "jornada")
    private int jornada;
    
    @ManyToOne
    @JoinColumn(name = "liga_id", referencedColumnName = "pk_liga")
    private Ligas liga;

    public int getPkPartido() {
        return pkPartido;
    }

    public void setPkPartido(int pkPartido) {
        this.pkPartido = pkPartido;
    }

    public Equipos getEquipoLocal() {
        return equipoLocal;
    }

    public void setEquipoLocal(Equipos equipoLocal) {
        this.equipoLocal = equipoLocal;
    }

    public Equipos getEquipoVisitante() {
        return equipoVisitante;
    }

    public void setEquipoVisitante(Equipos equipoVisitante) {
        this.equipoVisitante = equipoVisitante;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public int getJornada() {
        return jornada;
    }

    public void setJornada(int jornada) {
        this.jornada = jornada;
    }

    public Ligas getLiga() {
        return liga;
    }

    public void setLiga(Ligas liga) {
        this.liga = liga;
    }

    
}
