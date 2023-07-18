package planmyrun.graph;

public interface MutableGraph<T extends Node> extends Graph<T> {

    /**
     * @param node the {@link Node} to add to the graph
     */
    void addNode(T node);
}
