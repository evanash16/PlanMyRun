package planmyrun.graph.node;

import java.util.Collection;

public interface Node {

    /**
     * @return nodes to which this node is connected.
     */
    Collection<Node> getConnections();

    /**
     * @return the distance to a connected node. If the node is not connected to this one (i.e. it's not contained
     * in the collection of next {@link Node}s), this method **must** throw an IllegalArgumentException.
     */
    double distanceTo(final Node node);
}
