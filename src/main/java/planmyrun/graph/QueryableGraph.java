package planmyrun.graph;

import planmyrun.graph.node.Node2D;

import java.awt.geom.Point2D;

public interface QueryableGraph<T extends Node2D> extends Graph<T> {

    /**
     * Return the closest {@link T} to {@param point}.
     *
     * @param point the point to compare against
     * @return the closest {@link T}, if one exists. Otherwise, return {@code null}.
     */
    T closestTo(final Point2D.Double point);
}
