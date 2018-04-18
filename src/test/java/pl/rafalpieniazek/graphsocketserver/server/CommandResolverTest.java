package pl.rafalpieniazek.graphsocketserver.server;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pl.rafalpieniazek.graphsocketserver.graph.Graph;
import pl.rafalpieniazek.graphsocketserver.graph.Node;

import java.util.Optional;
import java.util.Random;
import java.util.stream.Stream;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static pl.rafalpieniazek.graphsocketserver.server.ServerResponse.*;

public class CommandResolverTest {

    @Mock
    private ClientHandler clientHandler;

    @Mock
    private Graph graph;

    @InjectMocks
    private CommandResolver commandResolver;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldResolveHiMessage() throws Exception {
        //given
        String requestMessage = "HI I'AM";

        //when
        commandResolver.resolve(requestMessage);

        //then
        verify(clientHandler).handleHiEvent(eq(requestMessage));
    }

    @Test
    public void shouldResolveByeMessage() throws Exception {
        //given
        String requestMessage = "BYE MATE";

        //when
        commandResolver.resolve(requestMessage);

        //then
        verify(clientHandler).disconnectUser();
    }

    @Test
    public void shouldAddNode() throws Exception {
        //given
        String requestMessage = "ADD NODE node-name-1";
        when(graph.addNode(any(Node.class))).thenReturn(true);

        //when
        commandResolver.resolve(requestMessage);

        //then
        verify(graph).addNode(eq(new Node("node-name-1")));
        verify(clientHandler).sendResponseToClient(eq(NODE_ADDED));
    }

    @Test
    public void shouldNotAddNodeWhenNodeExists() throws Exception {
        //given
        String requestMessage = "ADD NODE node-name-1";
        when(graph.addNode(any(Node.class))).thenReturn(false);

        //when
        commandResolver.resolve(requestMessage);

        //then
        verify(clientHandler).sendResponseToClient(eq(NODE_ALREADY_EXISTS));
    }

    @Test
    public void shouldAddEdge() throws Exception {
        //given
        String requestMessage = "ADD EDGE node-name-from node-name-to 42";
        Node nodeFrom = new Node("node-name-from");
        Node nodeTo = new Node("node-name-to");
        when(graph.findNodeByName(eq("node-name-from"))).thenReturn(Optional.of(nodeFrom));
        when(graph.findNodeByName(eq("node-name-to"))).thenReturn(Optional.of(nodeTo));

        //when
        commandResolver.resolve(requestMessage);

        //then
        verify(graph).addEdge(eq(nodeFrom), eq(nodeTo), eq(42));
        verify(clientHandler).sendResponseToClient(eq(EDGE_ADDED));
    }

    @Test
    public void shouldNotAddEdgeIfNodeIsNotPresent() throws Exception {
        //given
        String requestMessage = "ADD EDGE node-name-from node-name-to 42";
        Node nodeFrom = new Node("node-name-from");
        when(graph.findNodeByName(eq("node-name-from"))).thenReturn(Optional.of(nodeFrom));
        when(graph.findNodeByName(eq("node-name-to"))).thenReturn(Optional.empty());

        //when
        commandResolver.resolve(requestMessage);

        //then
        verify(clientHandler).sendResponseToClient(eq(NODE_NOT_FOUND));
    }

    @Test
    public void shouldRemoveNode() throws Exception {
        //given
        String requestMessage = "REMOVE NODE node-name-1";
        Node nodeToRemove = new Node("node-name-1");
        when(graph.findNodeByName(eq("node-name-1"))).thenReturn(Optional.of(nodeToRemove));

        //when
        commandResolver.resolve(requestMessage);

        //then
        verify(graph).removeNode(eq(nodeToRemove));
        verify(clientHandler).sendResponseToClient(eq(NODE_REMOVED));
    }

    @Test
    public void shouldNotRemoveNodeIfNoExists() throws Exception {
        //given
        String requestMessage = "REMOVE NODE node-name-1";
        Node nodeToRemove = new Node("node-name-1");
        when(graph.findNodeByName(eq("node-name-1"))).thenReturn(Optional.empty());

        //when
        commandResolver.resolve(requestMessage);

        //then
        verify(graph, times(0)).removeNode(any());
        verify(clientHandler).sendResponseToClient(eq(NODE_NOT_FOUND));
    }

    @Test
    public void shouldRemoveEdge() throws Exception {
        //given
        String requestMessage = "REMOVE EDGE node-name-from node-name-to";
        Node nodeFrom = new Node("node-name-from");
        Node nodeTo = new Node("node-name-to");
        when(graph.findNodeByName(eq("node-name-from"))).thenReturn(Optional.of(nodeFrom));
        when(graph.findNodeByName(eq("node-name-to"))).thenReturn(Optional.of(nodeTo));

        //when
        commandResolver.resolve(requestMessage);

        //then
        verify(graph).removeEdge(eq(nodeFrom), eq(nodeTo));
        verify(clientHandler).sendResponseToClient(eq(EDGE_REMOVED));
    }

    @Test
    public void shouldNotRemoveEdgeIfAnyNodeIsEmpty() throws Exception {
        //given
        String requestMessage = "REMOVE EDGE node-name-from node-name-to";
        Node nodeTo = new Node("node-name-to");
        when(graph.findNodeByName(eq("node-name-from"))).thenReturn(Optional.empty());
        when(graph.findNodeByName(eq("node-name-to"))).thenReturn(Optional.of(nodeTo));

        //when
        commandResolver.resolve(requestMessage);

        //then
        verify(clientHandler).sendResponseToClient(eq(NODE_NOT_FOUND));
    }

    @Test
    public void shouldResolveCalculateShortestPath() throws Exception {
        //given
        String requestMessage = "SHORTEST PATH node-name-from node-name-to";
        Node nodeFrom = new Node("node-name-from");
        Node nodeTo = new Node("node-name-to");
        when(graph.findNodeByName(eq("node-name-from"))).thenReturn(Optional.of(nodeFrom));
        when(graph.findNodeByName(eq("node-name-to"))).thenReturn(Optional.of(nodeTo));

        int mockWeightSum = new Random().nextInt();
        when(graph.shortestPath(eq(nodeFrom), eq(nodeTo))).thenReturn(mockWeightSum);

        //when
        commandResolver.resolve(requestMessage);

        //then
        verify(graph).shortestPath(eq(nodeFrom), eq(nodeTo));
        verify(clientHandler).sendResponseToClient(String.valueOf(mockWeightSum));
    }

    @Test
    public void shouldResolveCalculateCloserThan() throws Exception {
        //given
        int mockCloserThan = new Random().nextInt();
        String requestMessage = String.format("CLOSER THAN %d node-name-from", mockCloserThan);

        Node nodeFrom = new Node("node-name-from");
        when(graph.findNodeByName(eq("node-name-from"))).thenReturn(Optional.of(nodeFrom));

        when(graph.closerThan(eq(nodeFrom), eq(mockCloserThan)))
                .thenReturn(Stream.of(new Node("node-name-2"), new Node("node-name-1")));

        //when
        commandResolver.resolve(requestMessage);

        //then
        verify(graph).closerThan(eq(nodeFrom), eq(mockCloserThan));
        verify(clientHandler).sendResponseToClient(eq("node-name-1,node-name-2"));
    }
}