package planmyrun.route;

import planmyrun.graph.Node;

import java.util.*;

public class SimpleRouter implements Router {

    private static final int MAX_DEPTH = 10000;

    public <T extends Node> Route<T> findRoute(T start, T end, double minimumDistance, double maximumDistance) {
        // sort working routes from shortest to longest distance
        final Queue<MutableRoute<T>> workingRoutes = new PriorityQueue<>(Comparator.comparing(Route::getDistance));
        workingRoutes.add(new SimpleRoute<>(start));

        while (!workingRoutes.isEmpty()) {
            final MutableRoute<T> workingRoute = workingRoutes.remove();
            final T currentNode = workingRoute.getNodes().get(workingRoute.getNodes().size() - 1);

            for (final Node nextNode : currentNode.getConnections()) {
                final MutableRoute<T> routeWithConnectionAdded = workingRoute.shallowClone();
                routeWithConnectionAdded.addNode((T) nextNode);

                // a route is complete if it meets the distance constraints and ends
                // with the requested node
                if (routeWithConnectionAdded.getDistance() > minimumDistance &&
                        routeWithConnectionAdded.getDistance() <= maximumDistance &&
                        nextNode.equals(end)) {
                    return routeWithConnectionAdded;
                // a route is considered infeasible if it exceeds the distance constraints
                // or has too many nodes
                } else if (routeWithConnectionAdded.getDistance() > maximumDistance ||
                        routeWithConnectionAdded.getNodes().size() > MAX_DEPTH) {
                    continue;
                }

                workingRoutes.add(routeWithConnectionAdded);
            }
        }

        return null;
    }
}
