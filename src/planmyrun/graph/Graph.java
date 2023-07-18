package planmyrun.graph;

import java.util.List;

public interface Graph<T extends Node> {

    /**
     * @return the {@link Node} in the graph.
     */
    List<T> getNodes();
}
