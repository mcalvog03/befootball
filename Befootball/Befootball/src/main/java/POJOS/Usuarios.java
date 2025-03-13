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
@Table(name = "usuarios")
public class Usuarios {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pk_usuario")
    private int pkUsuario;
    
    @Column(name = "nombre", columnDefinition = "LONGTEXT")
    private String nombre;
    
    @Column(name = "correo", columnDefinition = "LONGTEXT")
    private String correo;
    
    @Column(name = "contraseña", columnDefinition = "LONGTEXT")
    private String contraseña;
    
    @Column(name = "rol", columnDefinition = "LONGTEXT")
    private String rol;
    
    @ManyToOne
    @JoinColumn(name = "equipo_favorito", referencedColumnName = "pk_equipo")
    private Equipos equipoFavorito;

    public int getPkUsuario() {
        return pkUsuario;
    }

    public void setPkUsuario(int pkUsuario) {
        this.pkUsuario = pkUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getContraseña() {
        return contraseña;
    }

    public void setContraseña(String contraseña) {
        this.contraseña = contraseña;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public Equipos getEquipoFavorito() {
        return equipoFavorito;
    }

    public void setEquipoFavorito(Equipos equipoFavorito) {
        this.equipoFavorito = equipoFavorito;
    }
    
    
}
