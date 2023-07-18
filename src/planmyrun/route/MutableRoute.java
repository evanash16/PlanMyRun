package planmyrun.route;

import planmyrun.graph.Node;

public interface MutableRoute<T extends Node> extends Route<T> {

    /**
     * Add a node to the route. The total distance must be updated to reflect the new node.
     */
    void addNode(T node);

    /**
     * @return a shallow clone of the route.
     */
    MutableRoute<T> shallowClone();
}
