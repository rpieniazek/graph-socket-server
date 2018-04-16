package pl.rafalpieniazek.graphsocketserver.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Server {

    public static final int PORT = 50_000;

    Map<UUID, ClientHandler> clients = new HashMap<>();

    public Server() {
        try {
            ServerSocket listener = new ServerSocket(PORT);

            System.out.println("Server started, listening");
            while (true) {
                Socket socket = listener.accept();
                UUID generatedUUID = UUID.randomUUID();
                ClientHandler clientHandler = new ClientHandler(socket, generatedUUID);
                clients.put(generatedUUID, clientHandler);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
