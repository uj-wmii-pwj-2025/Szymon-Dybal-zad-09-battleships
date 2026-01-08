package uj.wmii.pwj.collections.gameLogic;

import uj.wmii.pwj.collections.gameHelpers.ShotResult;
import uj.wmii.pwj.collections.gameHelpers.myBoard;
import uj.wmii.pwj.collections.gameHelpers.point;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;
import java.util.Scanner;

public class Controller {
    private myBoard board;
    private boolean isServer;
    private BufferedReader in;
    private PrintWriter out;
    private boolean game = true;
    private Scanner scanner;
    private final char[][] enemyMap = new char[10][10];
    private point lastTarget = null;
    private String lastSentMessage = "";

    public Controller(myBoard board, boolean isServer, BufferedReader in, PrintWriter out) {
        this.board = board;
        this.isServer = isServer;
        this.in = in;
        this.out = out;
        this.scanner = new Scanner(System.in);

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                enemyMap[i][j] = '.'; //~ pudlo, @ trafienie
            }
        }
    }

    public void play() throws IOException {
        if (isServer) {
            System.out.println("Oczekiwanie na klienta...");
        } else {
            System.out.println("Zaczynam jako klient");
            point firstShot = getShot();
            send("start", firstShot);
        }

        int errorCount = 0;

        while (game) {
            try {

                String message = in.readLine();

                if (message == null) break;


                errorCount = 0;

                System.out.println(">> Otrzymano: " + message);
                processMessage(message);

            } catch (java.net.SocketTimeoutException e) {
                errorCount++;
                System.out.println("timeout. Próba ponowienia " + errorCount + "/3");

                if (errorCount >= 3) {
                    System.out.println("Błąd komunikacji - koniec gry.");
                    System.exit(1);
                }


                if (!lastSentMessage.isEmpty()) {
                    System.out.println("<< Ponawiam: " + lastSentMessage);
                    out.println(lastSentMessage);
                    out.flush();
                }
            }
        }
    }

    private void processMessage(String message) {
        String[] parts = message.split(";");
        String result = parts[0];
        if (lastTarget != null) {
            if (result.equalsIgnoreCase("pudło") || result.equalsIgnoreCase("pudlo")) {
                enemyMap[lastTarget.y()][lastTarget.x()] = '~';
            } else if ((result.toLowerCase().contains("trafiony"))) {
                enemyMap[lastTarget.y()][lastTarget.x()] = '@';

            }
        }
        if (result.equals("ostatni zatopiony")) {

            if (lastTarget != null) enemyMap[lastTarget.y()][lastTarget.x()] = '@';
            printEnemyMap();
            System.out.println("WYGRANA! ");
            game = false;
            return;
        }
        point enemyShotCoords = point.fromString(parts[1]);
        ShotResult resultOnMyBoard = board.shoot(enemyShotCoords);

        if (resultOnMyBoard == ShotResult.WSZYSTKIE_ZATOPIONE) {
            out.println(resultOnMyBoard.getMessage());
            out.flush();
            System.out.println("PRZEGRANA.");
            game = false;
            return;
        }
        printEnemyMap();
        point myNextShot = getShot();
        send(resultOnMyBoard.getMessage(), myNextShot);
    }


    private void send(String command, point coords) {
        String coordsStr = Character.toString('A' + coords.x()) + (coords.y() + 1);
        String msg = command + ";" + coordsStr;


        this.lastTarget = coords;

        System.out.println("<< Wysyłam: " + msg);
        out.println(msg);
        out.flush();
    }

    private point getShot() {
        Random rand = new Random();
        return new point(rand.nextInt(10), rand.nextInt(10));

//        for (int i = 0; i < 3; i++) {
//            System.out.print("Twój ruch wpisz współrzędne");
//            String line = scanner.nextLine().trim().toUpperCase();
//
//            try {
//                if (line.length() < 2 || line.length() > 3) {
//                    System.out.println("Błędny format");
//                    continue;
//                }
//                char colChar = line.charAt(0);
//                int rowNum = Integer.parseInt(line.substring(1));
//                if (colChar < 'A' || colChar > 'J' || rowNum < 1 || rowNum > 10) {
//                    System.out.println("Współrzędne poza planszą");
//                    continue;
//                }
//                int x = colChar - 'A';
//                int y = rowNum - 1;
//
//                return new point(x, y);
//
//            } catch (NumberFormatException e) {
//                System.out.println("Niepoprawna liczba wiersza");
//            } catch (Exception e) {
//                System.out.println("Błąd danych wejściowych.");
//            }
//        }
//        System.out.println("Błąd komunikacji");
//        System.exit(1);
//        return null;
    }

    private void printEnemyMap() {
        System.out.println("\n----- MAPA PRZECIWNIKA  -----");
        System.out.println("   A B C D E F G H I J");

        for (int y = 0; y < 10; y++) {

            System.out.print((y + 1) < 10 ? " " + (y + 1) + " " : (y + 1) + " ");

            for (int x = 0; x < 10; x++) {
                char symbol = enemyMap[y][x];

                System.out.print(symbol + " ");
            }
            System.out.println();
        }
        System.out.println("-------------------------------------------\n");
    }
}
