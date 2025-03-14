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
import java.time.LocalDateTime;

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
    
    // Salt generado
    @Column(name = "salt", columnDefinition = "LONGTEXT")
    private String salt;  
    // Hash de la contraseña
    @Column(name = "password_hash", columnDefinition = "LONGTEXT")
    private String passwordHash;  
    
    @Column(name = "rol", columnDefinition = "LONGTEXT")
    private String rol;
    
    @ManyToOne
    @JoinColumn(name = "equipo_favorito", referencedColumnName = "pk_equipo")
    private Equipos equipoFavorito;
    
    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro;

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
    
    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
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

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
        
}
