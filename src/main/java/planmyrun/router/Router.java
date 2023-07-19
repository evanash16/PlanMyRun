package planmyrun.router;

import planmyrun.graph.node.Node;
import planmyrun.route.Route;

public interface Router {

    /**
     * Find a route between a start {@link Node} and an end {@link Node} (of the same type) which has a length between
     * {@param minimumDistance} and {@param maximumDistance}. The farther apart {@param minimumDistance} and
     * {@param maximumDistance} are, the more feasible finding a route will be.
     *
     * @param start the node the route is starting from
     * @param end the node the route is ending on
     * @param minimumDistance the minimum total distance for the route
     * @param maximumDistance the maximum total distance for the route
     * @return a route, if possible, between {@param start} and {@param end} meeting the distance constraints set by
     * {@param minimumDistance} and {@param maximumDistance}.
     */
    <T extends Node> Route<T> findRoute(final T start, final T end, final double minimumDistance, final double maximumDistance);
}
