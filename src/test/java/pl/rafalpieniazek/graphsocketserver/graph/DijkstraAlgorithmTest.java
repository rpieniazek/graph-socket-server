package pl.rafalpieniazek.graphsocketserver.graph;

import org.junit.Assert;
import org.junit.Test;

public class DijkstraAlgorithmTest {

    @Test
    public void testSinglePath() {
        //given
        Graph graph = new Graph();
        Node sourceNode = new Node("Node1");
        Node node2 = new Node("Node2");
        Node destination = new Node("Node3");

        graph.addNode(sourceNode);
        graph.addNode(node2);
        graph.addNode(destination);

        graph.addEdge(sourceNode, node2, 10);
        graph.addEdge(node2, destination, 7);

        //when
        DijkstraAlgorithm algorithm = new DijkstraAlgorithm(graph);
        Integer distance = algorithm.shortestPath(sourceNode, destination);

        //then
        Assert.assertEquals(distance.intValue(), 17);
    }

    @Test
    public void shouldCalculateForMultipleEdgesBetweenNodes() throws Exception {
        //given
        Graph graph = new Graph();
        Node sourceNode = new Node("Node1");
        Node node2 = new Node("Node2");
        Node destination = new Node("Node3");

        graph.addNode(sourceNode);
        graph.addNode(node2);
        graph.addNode(destination);

        graph.addEdge(sourceNode, node2, 11);
        graph.addEdge(sourceNode, node2, 10);
        graph.addEdge(node2, destination, 4);
        graph.addEdge(node2, destination, 7);
        graph.addEdge(node2, destination, 3);

        //when
        DijkstraAlgorithm algorithm = new DijkstraAlgorithm(graph);
        Integer distance = algorithm.shortestPath(sourceNode, destination);

        //then
        Assert.assertEquals(13, distance.intValue());
    }
}