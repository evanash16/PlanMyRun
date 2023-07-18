package planmyrun.graph;

public interface MutableNode extends Node {

    /**
     * @param node the node to connect to.
     */
    void addConnection(Node node);
}
