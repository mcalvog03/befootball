package Sockets;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClienteEscudos extends JFrame {

    private static final String SERVER_IP = "192.168.25.45";
    private static final int SERVER_PORT = 5000;
    private static final String LOCAL_FOLDER = "EscudosDescargados";

    private JTable table;
    private DefaultTableModel model;

    public ClienteEscudos() {
        File localDir = new File(LOCAL_FOLDER);
        if (!localDir.exists()) {
            localDir.mkdir();
        }

        initComponents();
        refreshPhotoList(); // Cargar imágenes al iniciar
    }

    private void initComponents() {
        setTitle("Cliente de Escudos");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Crear modelo de tabla con columna para imágenes
        model = new DefaultTableModel(new Object[]{"Equipo", "Escudo"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Evitar edición de celdas
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 1) {
                    return ImageIcon.class;
                }
                return String.class;
            }
        };

        table = new JTable(model);
        table.setRowHeight(80);
        table.getColumnModel().getColumn(1).setCellRenderer(new ImageRenderer()); // Renderizar imágenes

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        JButton refreshButton = new JButton("Actualizar");
        refreshButton.addActionListener(e -> refreshPhotoList());
        add(refreshButton, BorderLayout.SOUTH);

        pack();
        setSize(500, 400);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void refreshPhotoList() {
        model.setRowCount(0); // Limpiar tabla antes de actualizar
        List<String> equipos = obtenerListaEscudos();

        for (String equipo : equipos) {
            ImageIcon escudo = descargarEscudo(equipo);
            model.addRow(new Object[]{equipo, escudo});
        }
    }

    private List<String> obtenerListaEscudos() {
        List<String> equipos = new ArrayList<>();
        try (Socket socket = new Socket(SERVER_IP, SERVER_PORT);
             PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            writer.println("LIST");
            String response;
            while (!(response = reader.readLine()).equals("END")) {
                equipos.add(response);
            }

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error al conectar con el servidor", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        return equipos;
    }

    private ImageIcon descargarEscudo(String fileName) {
        File tempFile = new File(LOCAL_FOLDER, fileName);
        if (tempFile.exists()) {
            return new ImageIcon(new ImageIcon(tempFile.getAbsolutePath()).getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH));
        }

        try (Socket socket = new Socket(SERVER_IP, SERVER_PORT);
             PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
             InputStream in = socket.getInputStream();
             FileOutputStream fileOut = new FileOutputStream(tempFile)) {

            writer.println("DOWNLOAD " + fileName);

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                fileOut.write(buffer, 0, bytesRead);
            }

            return new ImageIcon(new ImageIcon(tempFile.getAbsolutePath()).getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH));

        } catch (IOException e) {
            System.out.println("Error al descargar el escudo: " + fileName);
            e.printStackTrace();
            return null;
        }
    }

    private static class ImageRenderer extends DefaultTableCellRenderer {
        @Override
        public void setValue(Object value) {
            if (value instanceof ImageIcon) {
                setIcon((ImageIcon) value);
                setText("");
            } else {
                setText((value != null) ? value.toString() : "");
                setIcon(null);
            }
            setHorizontalAlignment(CENTER);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ClienteEscudos::new);
    }
}