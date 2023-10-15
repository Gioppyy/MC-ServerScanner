package it.giopanno;

import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.Console;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.util.Scanner;

public class ServerScanner {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";

    public static void main(String[] args) {
        Scanner scn = new Scanner(System.in);
        System.out.print("Inserisci l'ip del server: ");
        String ip = scn.next();
        String result = getJSONfromUrl(ip);
        try {
            JSONObject jsonObj = new JSONObject(result);
            String serverip = jsonObj.getString("ip");
            int port = jsonObj.getInt("port");
            int maxPlayers = jsonObj.getJSONObject("players").getInt("max");
            String version = jsonObj.getString("version");

            System.out.println("IP: " + serverip);
            System.out.println("Porta: " + port);
            if(jsonObj.getJSONObject("players") != null) {
                int onlinePlayers = jsonObj.getJSONObject("players").getInt("online");
                System.out.println("Player online: " + (Math.min(onlinePlayers, 0)) + "/" + maxPlayers);
            }
            System.out.println("Versione: " + version.replace("Requires MC", ""));

            AnsiConsole.systemInstall();

            int[] portRangesStart = {400, 500, 600, 700, 800, 900, 777, 12345, 24500, 25500, 10001, 20000, 30000, 40001, 50001, 60001};
            int[] portRangesEnd = {400, 500, 600, 700, 800, 900, 777, 12345, 24600, 25600, 10020, 20020, 30005, 40010, 50010, 60010};

            for (int i = 0; i < portRangesStart.length; i++) {
                int start = portRangesStart[i];
                int end = portRangesEnd[i];

                for (int porta = start; porta <= end; porta++) {
                    try {
                        Socket socket = new Socket();
                        socket.connect(new InetSocketAddress(serverip, porta), 500);
                        System.out.println(Ansi.ansi().fgGreen().a("Porta Aperta: " + porta).reset());
                        socket.close();
                    } catch (Exception ex) {
                        System.out.println(Ansi.ansi().fgRed().a("Porta Chiusa: " + porta).reset());
                    }
                }
            }

            AnsiConsole.systemUninstall();

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        scn.close();
    }

    private static String getJSONfromUrl(String serverip) {
        StringBuilder json = new StringBuilder();

        try {
            URL url = new URL("https://api.mcsrvstat.us/2/" + serverip);
            InputStream is = url.openStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));

            String line;
            while ((line = reader.readLine()) != null) {
                json.append(line);
            }

            is.close();
            reader.close();

        } catch (Exception ex) {
            System.out.println("Errore durante la connessione all'API del server.");
        }

        return json.toString();
    }

    public static void printColoredText(String text, Color color) {
        Console console = System.console();
        if (console == null) {
            System.err.println("La console non è disponibile");
            System.exit(1);
        }

        // Seleziona il codice di colore in base al colore specificato
        switch (color) {
            case RED:
                console.printf("\u001B[31m%s\u001B[0m%n", text); // Codice di colore rosso ANSI
                break;
            case GREEN:
                console.printf("\u001B[32m%s\u001B[0m%n", text); // Codice di colore verde ANSI
                break;
        }
    }

    public enum Color {
        RED,
        GREEN
    }
}
