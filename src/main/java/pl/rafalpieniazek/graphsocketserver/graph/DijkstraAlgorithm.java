package pl.rafalpieniazek.graphsocketserver.graph;


import java.util.*;
import java.util.stream.Collectors;

public class DijkstraAlgorithm {
    private final List<Edge> edges;
    private Set<Node> visitedNodes;
    private Set<Node> undVisitedNodes;

    private Map<Node, Integer> distances;


    DijkstraAlgorithm(Graph graph) {
        this.edges = new ArrayList<>(graph.getEdges());
    }

    public Integer shortestPath(Node source, Node dest) {
        execute(source);
        return getDistanceForNode(dest);
    }

    public Map<Node, Integer> shortestDistanceForEachNode(Node source) {
        execute(source);
        return distances;
    }

    public void execute(Node source) {
        visitedNodes = new HashSet<>();
        undVisitedNodes = new HashSet<>();
        undVisitedNodes.add(source);

        distances = new HashMap<>();
        distances.put(source, 0);

        while (undVisitedNodes.size() > 0) {
            Node node = getMinimum(undVisitedNodes);
            visitedNodes.add(node);
            undVisitedNodes.remove(node);
            findMinimalDistances(node);
        }
    }

    private void findMinimalDistances(Node node) {
        Map<Node, Integer> adjacentNodes = getNeighbours(node);
        for (Map.Entry<Node, Integer> entry : adjacentNodes.entrySet()) {
            Node destination = entry.getKey();
            int newDistance = getDistanceForNode(node) + entry.getValue();
            if (newDistance < getDistanceForNode(destination)) {
                distances.put(destination, newDistance);
                undVisitedNodes.add(destination);
            }
        }
    }

    private Map<Node, Integer> getNeighbours(Node node) {
        return edges.stream()
                .filter(edge -> edge.getSource().equals(node))
                .filter(edge -> !isVisited(edge.getDestination()))
                .collect(Collectors.toMap(Edge::getDestination, Edge::getWeight, Integer::min));
    }

    private Node getMinimum(Set<Node> nodes) {
        return nodes.stream()
                .min(Comparator.comparingInt(this::getDistanceForNode))
                .orElse(null);
    }

    private boolean isVisited(Node node) {
        return visitedNodes.contains(node);
    }

    private int getDistanceForNode(Node destination) {
        Integer d = distances.get(destination);
        return d == null ? Integer.MAX_VALUE : d;
    }
}
