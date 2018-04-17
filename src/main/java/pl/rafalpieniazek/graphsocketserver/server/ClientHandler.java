package pl.rafalpieniazek.graphsocketserver.server;

import pl.rafalpieniazek.graphsocketserver.graph.Graph;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.UUID;

import static java.lang.String.format;
import static pl.rafalpieniazek.graphsocketserver.server.ServerResponse.*;
import static pl.rafalpieniazek.graphsocketserver.server.ServerResponse.HI_NAME;

public class ClientHandler implements Runnable {

    private static final int TIMEOUT = 30_000;
    private final long connectionStartedTime;

    private Socket socket;
    private UUID uuid;
    private String name;

    private BufferedReader in;
    private PrintWriter out;
    private CommandResolver commandResolver;

    public ClientHandler(Socket socket, Graph graph) {
        this.uuid = UUID.randomUUID();
        this.connectionStartedTime = System.currentTimeMillis();
        this.socket = socket;
        this.commandResolver = new CommandResolver(graph, this);
    }

    public void run() {
        System.out.println("Client handler created");
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            sendConnectedSuccessMessage();
            waitForEvent();
        } catch (SocketTimeoutException exception) {
            disconnectUser();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeSocket();
        }
    }

    public void sendResponseToClient(ServerResponse response) {
        out.println(response.getMessage());
    }

    public void sendResponseToClient(String response) {
        out.println(response);
    }

    private void sendConnectedSuccessMessage() {
        System.out.println("Client connected with UUID: " + uuid.toString());
        out.println(format(HI_UUID.getMessage(), uuid.toString()));
    }

    protected void handleHiEvent(String requestMessage) {
        this.name = lastWordInSentence(requestMessage);
        String message = String.format(HI_NAME.getMessage(), this.name);
        out.println(message);
    }

    private void waitForEvent() throws IOException {
        String requestMessage;
        while ((requestMessage = in.readLine()) != null) {
            handleEvent(requestMessage);
            socket.setSoTimeout(TIMEOUT);
        }
    }

    private void handleEvent(String requestMessage) {
        System.out.printf("Handling message from client: %s, message body: %s \n", uuid, requestMessage);
        commandResolver.resolve(requestMessage);
    }

    protected void disconnectUser() {
        System.out.println("Disconnecting user");
        String message = format(BYE.getMessage(), name, calculateConnectionTime());
        out.println(message);
    }

    private long calculateConnectionTime() {
        return System.currentTimeMillis() - this.connectionStartedTime;
    }

    private String lastWordInSentence(String requestMessage) {
        return requestMessage.substring(requestMessage.lastIndexOf(" ") + 1);
    }

    private void closeSocket() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
