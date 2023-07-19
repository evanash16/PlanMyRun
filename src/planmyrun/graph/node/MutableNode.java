package planmyrun.graph.node;

public interface MutableNode extends Node {

    /**
     * @param node the node to connect to. If it is not of the same type as the "origin" node, an
     * IllegalArgumentException **must** be thrown.
     */
    void addConnection(Node node);
}
