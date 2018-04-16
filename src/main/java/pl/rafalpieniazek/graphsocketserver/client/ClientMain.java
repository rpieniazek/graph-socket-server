package pl.rafalpieniazek.graphsocketserver.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientMain {


    public static void main(String[] args) {
        BufferedReader in;
        PrintWriter out;
        try {
            Socket socket = new Socket("127.0.0.1", 50_000);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            System.out.println("waiting for events");
            String event;
            while ((event = in.readLine()) != null) {
                System.out.printf("received event%s\n", event);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
