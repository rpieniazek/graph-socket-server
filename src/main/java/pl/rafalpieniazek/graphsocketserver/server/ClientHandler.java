package pl.rafalpieniazek.graphsocketserver.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.UUID;

import static java.lang.String.*;

public class ClientHandler implements Runnable {

    private static final int TIMEOUT = 30_000;
    private final long connectionStartedTime;
    private Socket socket;
    private UUID uuid;
    private String name;

    private BufferedReader in;
    private PrintWriter out;


    public ClientHandler(Socket socket, UUID uuid) {
        this.socket = socket;
        this.uuid = uuid;
        this.connectionStartedTime = System.currentTimeMillis();
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

    private void sendConnectedSuccessMessage() {
        System.out.println("Sending connected success message: " + uuid.toString());
        out.println(format(Events.HI_UUID.getMessage(), uuid.toString()));
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
        if (requestMessage.startsWith("HI")) {
            handleHiEvent(requestMessage);
        } else if (requestMessage.startsWith("BYE")) {
            disconnectUser();
        } else {
            out.println(Events.UNKNOW_COMMAND.getMessage());
        }
    }

    private void handleHiEvent(String requestMessage) {
        name = requestMessage.substring(requestMessage.lastIndexOf(" ") + 1);
        String message = format(Events.HI_NAME.getMessage(), name);
        out.println(message);
    }

    private void disconnectUser() {
        System.out.println("Disconnecting user");
        String message = format("BYE %s, WE SPOKE FOR %d MS", name, calculateConnectionTime());
        out.println(message);
        closeSocket();
    }

    private long calculateConnectionTime() {
        return System.currentTimeMillis() - this.connectionStartedTime;
    }

    private void closeSocket() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
