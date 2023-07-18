package planmyrun.graph;

public interface MutableGraph extends Graph {

    /**
     * @param node the {@link Node} to add to the graph
     */
    void addNode(Node node);
}
