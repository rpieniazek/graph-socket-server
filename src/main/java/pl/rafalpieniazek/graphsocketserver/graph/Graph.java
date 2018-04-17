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

    public void removeEdge(Node source, Node destination) {
        edges.removeIf(edge -> edge.getSource().equals(source) && edge.getDestination().equals(destination));
    }

    public void removeNode(Node node) {
        edges.removeIf(edge -> edge.getSource().equals(node) || edge.getDestination().equals(node));
        nodes.remove(node);
    }

    public int shortestPath(Node source, Node dest) {
        DijkstraAlgorithm dijkstraAlgorithm = new DijkstraAlgorithm(this);
        int distance = dijkstraAlgorithm.shortestPath(source, dest);
        System.out.printf("Calculated path from node: %s to node: %s equals %d\n", source.getName(), dest.getName(), distance);
        return distance;
    }

    public Stream<Node> closerThan(Node source, Integer weight) {
        DijkstraAlgorithm dijkstraAlgorithm = new DijkstraAlgorithm(this);
        Map<Node, Integer> nodeWeightMap = dijkstraAlgorithm.shortestDistanceForEachNode(source);

        return nodeWeightMap.entrySet().stream()
                .filter(entry -> entry.getValue() < weight)
                .filter(entry -> !entry.getKey().equals(source))
                .map(Map.Entry::getKey);
    }

    public List<Edge> getEdges() {
        return edges;
    }
}
