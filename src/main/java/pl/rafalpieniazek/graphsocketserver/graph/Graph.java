package pl.rafalpieniazek.graphsocketserver.graph;

import java.util.*;

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

    public Set<Node> getNodes() {
        return nodes;
    }

    public List<Edge> getEdges() {
        return edges;
    }
}
