package planmyrun.graph;

import planmyrun.graph.node.Node;

import java.util.List;

public interface Graph<T extends Node> {

    /**
     * @return the {@link Node} in the graph.
     */
    List<T> getNodes();
}
