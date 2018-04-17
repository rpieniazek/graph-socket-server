package pl.rafalpieniazek.graphsocketserver.server;

import pl.rafalpieniazek.graphsocketserver.graph.Graph;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private static final int PORT = 50_000;

    public Server() {
        Graph graph = new Graph();

        try {
            ServerSocket listener = new ServerSocket(PORT);
            System.out.println("Server started, listening");

            while (true) {
                Socket socket = listener.accept();
                ClientHandler clientHandler = new ClientHandler(socket, graph);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
