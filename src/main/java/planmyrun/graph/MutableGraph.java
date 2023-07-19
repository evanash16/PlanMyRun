package planmyrun.graph;

import planmyrun.graph.node.Node;

public interface MutableGraph<T extends Node> extends Graph<T> {

    /**
     * @param node the {@link Node} to add to the graph
     */
    void addNode(T node);
}
