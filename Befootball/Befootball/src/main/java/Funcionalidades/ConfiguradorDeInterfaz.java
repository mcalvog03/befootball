package Funcionalidades;

import Interfaces.MainFrame;

public class ConfiguradorDeInterfaz {

    public static void configurarPorRol(String rol, MainFrame principal) {
        switch (rol) {
            case "INVITADO":
                configurarParaInvitado(principal);
                break;
            case "DESARROLLADOR":
                configurarParaDesarrollador(principal);
                break;
            case "USUARIO":
                configurarParaUsuario(principal);
                break;
            default:
                break;
        }
    }

    private static void configurarParaInvitado(MainFrame principal) {
        System.out.println("Configurando para INVITADO...");
        principal.ventanaInvitado();
    }
    
    private static void configurarParaDesarrollador(MainFrame principal) {
        System.out.println("Configurando para DESARROLLADOR...");
        // Mostrar todos los paneles (sin cambios adicionales)
    }

    private static void configurarParaUsuario(MainFrame principal) {
        System.out.println("Configurando para USUARIO...");
        // Mostrar configuración específica para jugador (si es necesario)
        principal.ventanaUsuario();
    }
}
