package planmyrun.graph;

import planmyrun.graph.node.Node;

import java.util.ArrayList;
import java.util.List;

public class SimpleGraph<T extends Node> implements MutableGraph<T> {

    private final List<T> nodes;

    public SimpleGraph() {
        this.nodes = new ArrayList<>();
    }

    public List<T> getNodes() {
        return this.nodes;
    }

    public void addNode(final T node) {
        this.nodes.add(node);
    }
}
