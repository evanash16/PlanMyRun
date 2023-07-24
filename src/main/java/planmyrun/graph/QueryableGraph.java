package planmyrun.graph;

import planmyrun.graph.node.Node2D;

public interface QueryableGraph<T extends Node2D> extends Graph<T> {

    /**
     * Return the closest {@link T} to {@param node}.
     *
     * @param node the node to compare against
     * @return the closest {@link T}, if one exists. Otherwise, return {@code null}.
     */
    T closestTo(final T node);
}
