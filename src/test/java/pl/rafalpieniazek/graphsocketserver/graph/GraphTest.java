package pl.rafalpieniazek.graphsocketserver.graph;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

public class GraphTest {

    @Test
    public void shouldReturnNodesCloserThan() throws Exception {
        //given
        Graph graph = new Graph();
        Node mark = new Node("Mark");
        Node michael = new Node("Michael");
        Node madeleine = new Node("Madeleine");
        Node mufasa = new Node("Mufasa");

        graph.addNode(mark);
        graph.addNode(michael);
        graph.addNode(madeleine);
        graph.addNode(mufasa);

        graph.addEdge(mark, michael, 5);
        graph.addEdge(michael, madeleine, 2);
        graph.addEdge(madeleine, mufasa, 8);

        //when
        List<Node> nodeCloserThan = graph.closerThan(mark, 8).collect(Collectors.toList());

        //then

        Assert.assertTrue(nodeCloserThan.contains(madeleine));
        Assert.assertTrue(nodeCloserThan.contains(michael));
        Assert.assertEquals(2, nodeCloserThan.size());
    }

    @Test
    public void shouldReturnNodeCloserThanWithoutSourceNode() throws Exception {
        //given
        Graph graph = new Graph();
        Node mark = new Node("Mark");
        Node michael = new Node("Michael");
        Node madeleine = new Node("Madeleine");
        Node mufasa = new Node("Mufasa");

        graph.addNode(mark);
        graph.addNode(michael);
        graph.addNode(madeleine);
        graph.addNode(mufasa);

        graph.addEdge(mark, michael, 5);
        graph.addEdge(michael, mark, 1);
        graph.addEdge(michael, madeleine, 2);
        graph.addEdge(madeleine, mufasa, 8);

        //when
        List<Node> nodeCloserThan = graph.closerThan(mark, 8).collect(Collectors.toList());

        //then
        Assert.assertTrue(nodeCloserThan.contains(madeleine));
        Assert.assertTrue(nodeCloserThan.contains(michael));
        Assert.assertEquals(2, nodeCloserThan.size());
    }
}