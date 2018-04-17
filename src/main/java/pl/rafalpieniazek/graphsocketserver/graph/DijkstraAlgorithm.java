package pl.rafalpieniazek.graphsocketserver.graph;


import java.util.*;
import java.util.stream.Collectors;

public class DijkstraAlgorithm {
    private final List<Edge> edges;
    private Set<Node> settledNodes;
    private Set<Node> unSettledNodes;

    public Map<Node, Integer> getDistance() {
        return distance;
    }

    private Map<Node, Integer> distance;


    public DijkstraAlgorithm(Graph graph) {
        this.edges = new ArrayList<>(graph.getEdges());
    }

    public void execute(Node source) {
        settledNodes = new HashSet<>();
        unSettledNodes = new HashSet<>();
        distance = new HashMap<>();
        distance.put(source, 0);
        unSettledNodes.add(source);
        while (unSettledNodes.size() > 0) {
            Node node = getMinimum(unSettledNodes);
            settledNodes.add(node);
            unSettledNodes.remove(node);
            findMinimalDistances(node);
        }
    }

    public Integer getDistance(Node dest) {
        return distance.get(dest);
    }

    private void findMinimalDistances(Node node) {
        List<Node> adjacentNodes = getNeighbors(node);
        for (Node target : adjacentNodes) {
            if (getShortestDistance(target) > getShortestDistance(node) + getDistance(node, target)) {
                distance.put(target, getShortestDistance(node)
                        + getDistance(node, target));
                unSettledNodes.add(target);
            }
        }
    }

    private int getDistance(Node source, Node destination) {
        for (Edge edge : edges) {
            if (edge.getSource().equals(source)
                    && edge.getDestination().equals(destination)) {
                return edge.getWeight();
            }
        }
        throw new RuntimeException("Should not happen");
    }

    private List<Node> getNeighbors(Node node) {
        return edges.stream()
                .filter(edge -> edge.getSource().equals(node))
                .filter(edge -> !isSettled(edge.getDestination()))
                .map(Edge::getDestination)
                .collect(Collectors.toList());
    }

    private Node getMinimum(Set<Node> nodes) {
        return nodes.stream()
                .min(Comparator.comparingInt(this::getShortestDistance))
                .orElse(null);
    }

    private boolean isSettled(Node vertex) {
        return settledNodes.contains(vertex);
    }

    private int getShortestDistance(Node destination) {
        Integer d = distance.get(destination);
        return d == null ? Integer.MAX_VALUE : d;
    }

}
