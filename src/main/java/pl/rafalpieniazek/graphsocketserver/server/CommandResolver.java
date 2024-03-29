package pl.rafalpieniazek.graphsocketserver.server;

import pl.rafalpieniazek.graphsocketserver.graph.Graph;
import pl.rafalpieniazek.graphsocketserver.graph.Node;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static pl.rafalpieniazek.graphsocketserver.server.ServerResponse.*;

public class CommandResolver {

    private Graph graph;
    private ClientHandler clientHandler;

    public CommandResolver(Graph graph, ClientHandler clientHandler) {
        this.graph = graph;
        this.clientHandler = clientHandler;
    }

    public void resolve(String requestMessage) {
        if (requestMessage.startsWith("HI")) {
            clientHandler.handleHiEvent(requestMessage);
        } else if (requestMessage.startsWith("BYE")) {
            clientHandler.disconnectUser();
        } else if (requestMessage.startsWith("ADD NODE")) {
            addNodeIfNotExists(requestMessage);
        } else if (requestMessage.startsWith("ADD EDGE")) {
            addEdge(requestMessage);
        } else if (requestMessage.startsWith("REMOVE NODE")) {
            removeNode(requestMessage);
        } else if (requestMessage.startsWith("REMOVE EDGE")) {
            removeEdge(requestMessage);
        } else if (requestMessage.startsWith("SHORTEST PATH")) {
            calculateShortestPath(requestMessage);
        } else if (requestMessage.startsWith("CLOSER THAN")) {
            calculateCloserThan(requestMessage);
        } else {
            clientHandler.sendResponseToClient(UNKNOWN_COMMAND);
        }
    }


    private void addEdge(String requestMessage) {
        String[] split = requestMessage.split(" ");
        Optional<Node> maybeSource = graph.findNodeByName(split[2]);
        Optional<Node> maybeDestination = graph.findNodeByName(split[3]);
        int vertexWeight = Integer.parseInt(split[4]);

        if (maybeSource.isPresent() && maybeDestination.isPresent()) {
            graph.addEdge(maybeSource.get(), maybeDestination.get(), vertexWeight);
            clientHandler.sendResponseToClient(EDGE_ADDED);
        } else {
            clientHandler.sendResponseToClient(NODE_NOT_FOUND);
        }
    }

    private void addNodeIfNotExists(String requestMessage) {
        String[] split = requestMessage.split(" ");
        String nodeName = split[2];
        if (graph.addNode(new Node(nodeName))) {
            clientHandler.sendResponseToClient(NODE_ADDED);
        } else {
            clientHandler.sendResponseToClient(NODE_ALREADY_EXISTS);
        }
    }

    private void removeNode(String requestMessage) {
        String[] split = requestMessage.split(" ");
        Optional<Node> maybeSource = graph.findNodeByName(split[2]);
        if (maybeSource.isPresent()) {
            graph.removeNode(maybeSource.get());
            clientHandler.sendResponseToClient(NODE_REMOVED);
        } else {
            clientHandler.sendResponseToClient(NODE_NOT_FOUND);
        }
    }

    private void removeEdge(String requestMessage) {
        String[] split = requestMessage.split(" ");
        Optional<Node> maybeSource = graph.findNodeByName(split[2]);
        Optional<Node> maybeDestination = graph.findNodeByName(split[3]);

        if (maybeSource.isPresent() && maybeDestination.isPresent()) {
            graph.removeEdge(maybeSource.get(), maybeDestination.get());
            clientHandler.sendResponseToClient(EDGE_REMOVED);
        } else
            clientHandler.sendResponseToClient(NODE_NOT_FOUND);
    }

    private void calculateShortestPath(String requestMessage) {
        String[] split = requestMessage.split(" ");
        Optional<Node> maybeSource = graph.findNodeByName(split[2]);
        Optional<Node> maybeDestination = graph.findNodeByName(split[3]);

        if (maybeSource.isPresent() && maybeDestination.isPresent()) {
            int weightsSum = graph.shortestPath(maybeSource.get(), maybeDestination.get());
            clientHandler.sendResponseToClient(String.valueOf(weightsSum));
        } else {
            clientHandler.sendResponseToClient(NODE_NOT_FOUND);
        }
    }

    private void calculateCloserThan(String requestMessage) {
        String[] split = requestMessage.split(" ");
        Integer width = Integer.parseInt(split[2]);
        Optional<Node> nodeByName = graph.findNodeByName(split[3]);

        if (nodeByName.isPresent()) {
            Stream<Node> nodesStream = graph.closerThan(nodeByName.get(), width);
            String commaSeparatesNodesNames = sortNodesAndJoinNames(nodesStream);
            clientHandler.sendResponseToClient(commaSeparatesNodesNames);
        } else {
            clientHandler.sendResponseToClient(NODE_NOT_FOUND);
        }
    }

    private String sortNodesAndJoinNames(Stream<Node> nodesStream) {
        return nodesStream
                .map(Node::getName)
                .sorted(String::compareTo)
                .collect(Collectors.joining(","));
    }
}
