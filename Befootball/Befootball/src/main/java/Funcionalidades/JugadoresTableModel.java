/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Funcionalidades;

import POJOS.Jugadores;
import java.util.List;
import javax.swing.table.AbstractTableModel;

public class JugadoresTableModel extends AbstractTableModel {

    private List<Jugadores> jugadores;
    private final String[] columnNames = {"Nombre", "Nacionalidad", "Posición", "Dorsal"};

    public JugadoresTableModel(List<Jugadores> jugadores) {
        this.jugadores = jugadores;
    }

    @Override
    public int getRowCount() {
        return jugadores.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Jugadores jugador = jugadores.get(rowIndex);

        switch (columnIndex) {
            case 0: // Nombre del jugador
                return (jugador.getNombreJugador() != null) ? jugador.getNombreJugador() : "Desconocido";
            case 1: // Nacionalidad del jugador
                return (jugador.getNacionalidad() != null && jugador.getNacionalidad().getNombrePais() != null)
                        ? jugador.getNacionalidad().getNombrePais() : "Desconocido";
            case 2: // Posición del jugador
                return (jugador.getPosicion() != null) ? jugador.getPosicion() : "Desconocido";
            case 3: // Dorsal del jugador
                return (jugador.getDorsal() != -1) ? jugador.getDorsal() : "Desconocido";
            default:
                return null;
        }
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    public void setJugadores(List<Jugadores> jugadores) {
        this.jugadores = jugadores;
        fireTableDataChanged();
    }
}
