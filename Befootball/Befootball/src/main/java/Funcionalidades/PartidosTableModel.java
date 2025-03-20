package Funcionalidades;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.util.List;
import POJOS.Partidos;

public class PartidosTableModel extends AbstractTableModel {

    private List<Partidos> partidos;
    private final String[] columnNames = {"Escudo Local", "Equipo Local", "Goles Local", "Goles Visitante", "Equipo Visitante", "Escudo Visitante"};
    private static final String SERVER_IP = "192.168.1.45";
    private static final int SERVER_PORT = 5000;
    private static final String LOCAL_FOLDER = "EscudosDescargados";

    public PartidosTableModel(List<Partidos> partidos) {
        this.partidos = partidos;
    }

    @Override
    public int getRowCount() {
        return partidos.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Partidos partido = partidos.get(rowIndex);
        switch (columnIndex) {
            case 0: // Escudo Local
                return descargarEscudo(partido.getEquipoLocal().getEscudo());
            case 1: // Equipo Local
                return partido.getEquipoLocal().getNombreEquipo();
            case 2: // Goles Local
                return partido.getGolesLocal();
            case 3: // Goles Visitante
                return partido.getGolesVisitante();
            case 4: // Equipo Visitante
                return partido.getEquipoVisitante().getNombreEquipo();
            case 5: // Escudo Visitante
                return descargarEscudo(partido.getEquipoVisitante().getEscudo());
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

    public void setPartidos(List<Partidos> partidos) {
        this.partidos = partidos;
        fireTableDataChanged();
    }

    private ImageIcon descargarEscudo(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return null;
        }

        File tempFile = new File(LOCAL_FOLDER, fileName);
        if (tempFile.exists()) {
            return new ImageIcon(new ImageIcon(tempFile.getAbsolutePath()).getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH));
        }

        try (Socket socket = new Socket(SERVER_IP, SERVER_PORT); PrintWriter writer = new PrintWriter(socket.getOutputStream(), true); InputStream in = socket.getInputStream(); FileOutputStream fileOut = new FileOutputStream(tempFile)) {

            writer.println("DOWNLOAD " + fileName);
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                fileOut.write(buffer, 0, bytesRead);
            }

            return new ImageIcon(new ImageIcon(tempFile.getAbsolutePath()).getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH));
        } catch (IOException e) {
            System.out.println("Error al descargar el escudo: " + fileName);
            JOptionPane.showMessageDialog(null, "Error al descargar escudos.", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return null;
        }
    }
    
    // Método añadido para obtener el partido en la fila seleccionada
    public Partidos getPartidoEnFila(int rowIndex) {
        return partidos.get(rowIndex);
    }
    
}
