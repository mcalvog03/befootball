package ComponentesModelos;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.util.List;
import POJOS.Partidos;
import java.net.URL;

public class PartidosTableModel extends AbstractTableModel {

    private List<Partidos> partidos;
    private final String[] columnNames = {"Escudo Local", "Equipo Local", "Goles Local", "Goles Visitante", "Equipo Visitante", "Escudo Visitante"};
    private static final String SERVER_IP = "192.168.1.45";
    private static final int SERVER_PORT = 5000;
    private static final String LOCAL_FOLDER = "EscudosDescargados";
    private static boolean errorDescarga = false;

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

    // Método para obtener el partido en la fila seleccionada
    public Partidos getPartidoEnFila(int rowIndex) {
        return partidos.get(rowIndex);
    }

}
