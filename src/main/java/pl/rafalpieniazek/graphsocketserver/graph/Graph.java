package pl.rafalpieniazek.graphsocketserver.graph;

import java.util.*;
import java.util.stream.Stream;

public class Graph {
    private final Set<Node> nodes;
    private final List<Edge> edges;

    public Graph() {
        this.nodes = new LinkedHashSet<>();
        this.edges = new ArrayList<>();
    }


    public Optional<Node> findNodeByName(String nodeName) {
        return nodes.stream()
                .filter(node -> node.getName().equals(nodeName))
                .findFirst();
    }

    public boolean addNode(Node node) {
        return this.nodes.add(node);
    }

    public void addEdge(Node source, Node destination, int weight) {
        Edge edge = new Edge(source, destination, weight);
        edges.add(edge);
    }

    public boolean removeEdge(Node source, Node destination) {
        return edges.removeIf(edge -> edge.getSource().equals(source) && edge.getDestination().equals(destination));
    }

    public void removeNode(Node node) {
        edges.removeIf(edge -> edge.getSource().equals(node) || edge.getDestination().equals(node));
        nodes.remove(node);
    }

    public int shortestPath(Node source, Node dest) {
        DijkstraAlgorithm dijkstraAlgorithm = new DijkstraAlgorithm(this);
        dijkstraAlgorithm.execute(source);
        Integer distance = dijkstraAlgorithm.getDistance(dest);
        System.out.printf("calculated path from node: %s to node: %s equals %d\n", source.getName(), dest.getName(), distance);
        return Optional.ofNullable(distance).orElse(Integer.MAX_VALUE);
    }

    public Stream<String> closerFromThan(Node source, Integer weight) {
        DijkstraAlgorithm dijkstraAlgorithm = new DijkstraAlgorithm(this);
        dijkstraAlgorithm.execute(source);
        return dijkstraAlgorithm.getDistance().entrySet().stream()
                .filter(entry -> entry.getValue() < weight)
                .map(nodeIntegerEntry -> nodeIntegerEntry.getKey().getName());
    }

    public Set<Node> getNodes() {
        return nodes;
    }

    public List<Edge> getEdges() {
        return edges;
    }
}
