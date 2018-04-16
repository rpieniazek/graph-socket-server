package pl.rafalpieniazek.graphsocketserver.graph;

import java.util.Objects;

public class Node {

    final private String name;

    public Node(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return Objects.equals(name, node.name);
    }

    @Override
    public int hashCode() {

        return Objects.hash(name);
    }
}
