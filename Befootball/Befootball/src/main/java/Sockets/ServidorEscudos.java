/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package Sockets;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class ServidorEscudos {

    /**
     * @param args the command line arguments
     */
    private static final int PORT = 5000;
    private static final String PHOTOS_FOLDER = "Escudos";

    public static void main(String[] args) {
        File photosDir = new File(PHOTOS_FOLDER);
        if (!photosDir.exists()) {
            photosDir.mkdir();
        }

        System.out.println("Servidor de Escudos escuchando en el puerto " + PORT);
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler implements Runnable {

        private final Socket socket;

        ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (InputStream in = socket.getInputStream(); OutputStream out = socket.getOutputStream(); BufferedReader reader = new BufferedReader(new InputStreamReader(in)); PrintWriter writer = new PrintWriter(out, true)) {

                String command = reader.readLine();
                if ("LIST".equalsIgnoreCase(command)) {
                    handleList(writer);
                } else if (command.startsWith("DOWNLOAD")) {
                    String fileName = command.substring(9).trim();
                    handleDownload(fileName, out);
                } else {
                    writer.println("Comando desconocido");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void handleList(PrintWriter writer) {
            File photosDir = new File(PHOTOS_FOLDER);
            String[] photoFiles = photosDir.list((dir, name) -> name.endsWith(".png"));
            if (photoFiles != null) {
                for (String photo : photoFiles) {
                    System.out.println("Escudo disponible: " + photo); // Imprime el nombre de cada foto
                    writer.println(photo);
                }
            }
            writer.println("END");
        }

        private void handleDownload(String fileName, OutputStream out) throws IOException {
            File photoFile = new File(PHOTOS_FOLDER, fileName);
            if (photoFile.exists()) {
                System.out.println("Enviando archivo: " + photoFile.getAbsolutePath() + " (Tamaño: " + photoFile.length() + " bytes)");
                try (FileInputStream fileIn = new FileInputStream(photoFile)) {
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = fileIn.read(buffer)) != -1) {
                        out.write(buffer, 0, bytesRead);
                    }
                }
            } else {
                PrintWriter writer = new PrintWriter(out, true);
                writer.println("ERROR: Archivo no encontrado");
            }
        }

    }

}
