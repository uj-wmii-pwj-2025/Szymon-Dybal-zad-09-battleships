package uj.wmii.pwj.collections;


// 1. IMPORTUJEMY TWÓJ GENERATOR
import uj.wmii.pwj.collections.engine.BattleshipGenerator;
import uj.wmii.pwj.collections.gameHelpers.myBoard;
import uj.wmii.pwj.collections.gameLogic.Controller;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) {
        String mode = null;
        int port = -1;
        String mapFile = null;
        String host = "localhost";

        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-mode" -> mode = args[++i];
                case "-port" -> port = Integer.parseInt(args[++i]);
                case "-map" -> mapFile = args[++i];
                case "-host" -> host = args[++i];
            }
        }

        if (mode == null || port == -1) {
            System.out.println("Uzycie: -mode [server|client] -port N [-map file] [-host host]");
            return;
        }

        try {

            String mapString;

            if (mapFile != null) {
                mapString = Files.readString(Path.of(mapFile));
            } else {
                mapString = BattleshipGenerator.defaultInstance().generateMap();
            }

            myBoard board = new myBoard(mapString);


            System.out.println("\n--- MOJA MAPA ---");
            System.out.println("   A B C D E F G H I J");


            String[] lines = mapString.trim().split("\n");

            for (int i = 0; i < 10; i++) {

                System.out.print((i + 1) < 10 ? " " + (i + 1) + " " : (i + 1) + " ");


                String line = (i < lines.length) ? lines[i].trim() : "";
                for (int j = 0; j < 10; j++) {
                    char c = (j < line.length()) ? line.charAt(j) : '.';
                    System.out.print(c + " ");
                }
                System.out.println();
            }
            System.out.println("--------------------\n");


            Socket socket;
            boolean isServer = mode.equalsIgnoreCase("server");

            if (isServer) {
                ServerSocket serverSocket = new ServerSocket(port);
                System.out.println("Serwer nasłuchuje na porcie " + port);
                socket = serverSocket.accept();
                System.out.println("Klient połaczony");
            } else {
                System.out.println("Łacze z serwerem " + host + ":" + port);
                socket = new Socket(host, port);
                System.out.println("Połaczono");
            }


            socket.setSoTimeout(1000);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8)
            );
            PrintWriter out = new PrintWriter(
                    new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8),
                    true // AutoFlush
            );


            Controller controller = new Controller(board, isServer, in, out);
            controller.play();


            socket.close();

        } catch (Exception e) {
            System.out.println("Bład krytyczny aplikacji: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
