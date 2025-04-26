/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ComponentesModelos;

import POJOS.Clasificacion;
import POJOS.Equipos;
import java.awt.Image;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URL;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;

public class ClasificacionTableModel extends AbstractTableModel {

    private List<Clasificacion> clasificaciones;
    private final String[] columnNames = {"Posición", "Escudo", "Equipo", "PTS", "PJ", "PG", "PE", "PP", "DG"};
    private static final String SERVER_IP = "192.168.1.45";
    private static final int SERVER_PORT = 5000;
    private static final String LOCAL_FOLDER = "EscudosDescargados";
    private static boolean errorDescarga = false;

    public ClasificacionTableModel(List<Clasificacion> clasificaciones) {
        this.clasificaciones = clasificaciones;
        ordenarClasificaciones();
    }

    @Override
    public int getRowCount() {
        return clasificaciones.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Clasificacion clasificacion = clasificaciones.get(rowIndex);
        switch (columnIndex) {
            case 0: // Posicion
                return rowIndex + 1;
            case 1: // Escudo
                return descargarEscudo(clasificacion.getEquipo().getEscudo());
            case 2: // Nombre
                return clasificacion.getEquipo().getNombreEquipo();
            case 3: // Puntos
                return clasificacion.getPuntos();
            case 4: // Partidos jugados
                return clasificacion.getPartidosJugados();
            case 5: // Partidos ganados
                return clasificacion.getPartidosGanados();
            case 6: // Partidos empatados
                return clasificacion.getPartidosEmpatados();
            case 7: // Partidos perdidos
                return clasificacion.getPartidosPerdidos();
            case 8: // Diferencia de goles
                return clasificacion.getDiferenciaGoles();
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

    public void setClasificaciones(List<Clasificacion> clasificaciones) {
        this.clasificaciones = clasificaciones;
        ordenarClasificaciones();
        fireTableDataChanged();
    }

    // Método para obtener el equipo en la fila seleccionada
    public Equipos getEquipoEnFila(int rowIndex) {
        if (clasificaciones != null && rowIndex >= 0 && rowIndex < clasificaciones.size()) {
            return clasificaciones.get(rowIndex).getEquipo();
        }
        return null;
    }

    // Método para ordenar las clasificaciones por puntos y diferencia de goles
    private void ordenarClasificaciones() {
        if (clasificaciones != null) {
            Collections.sort(clasificaciones, new Comparator<Clasificacion>() {
                @Override
                public int compare(Clasificacion c1, Clasificacion c2) {
                    // Primero ordenar por puntos (de mayor a menor)
                    int puntosComparacion = Integer.compare(c2.getPuntos(), c1.getPuntos());
                    if (puntosComparacion != 0) {
                        return puntosComparacion;
                    }
                    // Si los puntos son iguales, ordenar por diferencia de goles (de mayor a menor)
                    return Integer.compare(c2.getDiferenciaGoles(), c1.getDiferenciaGoles());
                }
            });
        }
    }
    
    // Descargar escudo del servidor socket
    private ImageIcon descargarEscudo(String fileName) {
        // Si ya hubo un error, no intentar más descargas
        if (fileName == null || fileName.isEmpty() || errorDescarga) {
            return obtenerEscudoPorDefecto();
        }

        // Se crea un objeto File que apunta a la ubicación local donde se guardará el escudo y si este existe se devuelve redimensionado a 60x60
        File tempFile = new File(LOCAL_FOLDER, fileName);
        if (tempFile.exists()) {
            return redimensionarImagen(tempFile);
        }

        // Descargar escudo del servidor 
        try (Socket socket = new Socket(SERVER_IP, SERVER_PORT); PrintWriter writer = new PrintWriter(socket.getOutputStream(), true); InputStream in = socket.getInputStream(); FileOutputStream fileOut = new FileOutputStream(tempFile)) {

            // Enviar comando para solicitar el archivo
            writer.println("DOWNLOAD " + fileName);

            // Se lee el archivo recibido en bloques de 1024 bytes y se escribe en el archivo local
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                fileOut.write(buffer, 0, bytesRead);
            }

            return redimensionarImagen(tempFile);

        } catch (IOException e) {
            System.err.println("Error al descargar el escudo: " + fileName);
            e.printStackTrace();
            if (!errorDescarga) {
                JOptionPane.showMessageDialog(null, "No se pudieron descargar los escudos, reinicia la aplicación para volver a intentar descargarlos.", "Error", JOptionPane.ERROR_MESSAGE);
                // No se permitirán más descargas
                errorDescarga = true;
            }
            return obtenerEscudoPorDefecto();
        }
    }

    // Obtener el escudo por defecto para cuando no se pueda descargar
    private ImageIcon obtenerEscudoPorDefecto() {
        URL url = getClass().getClassLoader().getResource("images/escudoPlaceHolder.png");
        if (url != null) {
            return new ImageIcon(new ImageIcon(url).getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH));
        } else {
            System.out.println("Escudo por defecto no encontrado.");
            // Devuelve un icono vacío si no se encuentra el recurso
            return new ImageIcon(); 
        }
    }

    // Redimensionar la imagen a 60x60
    private ImageIcon redimensionarImagen(File file) {
        return new ImageIcon(new ImageIcon(file.getAbsolutePath()).getImage()
                .getScaledInstance(60, 60, Image.SCALE_SMOOTH));
    }

}
