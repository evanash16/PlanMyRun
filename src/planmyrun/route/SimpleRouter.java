package planmyrun.route;

import planmyrun.graph.Node;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

public class SimpleRouter implements Router {

    private static final int MAX_DEPTH = 10000;

    public <T extends Node> Route<T> findRoute(T start, T end, double minimumDistance, double maximumDistance) {
        // sort working routes from shortest to longest distance
        final Queue<MutableRoute<T>> workingRoutes = new PriorityQueue<>(Comparator.comparing(Route::getDistance));
        workingRoutes.add(new SimpleRoute<>(start));

        final List<Route<T>> routes = new ArrayList<>();

        while (!workingRoutes.isEmpty()) {
            final MutableRoute<T> workingRoute = workingRoutes.remove();
            final T currentNode = workingRoute.getNodes().get(workingRoute.getNodes().size() - 1);

            for (final Node nextNode : currentNode.getConnections()) {
                final MutableRoute<T> routeWithConnectionAdded = workingRoute.shallowClone();
                routeWithConnectionAdded.addNode((T) nextNode);

                // a route is complete if it meets the distance constraints and ends
                // with the requested node
                if (routeWithConnectionAdded.getDistance() >= minimumDistance &&
                        routeWithConnectionAdded.getDistance() <= maximumDistance &&
                        nextNode.equals(end)) {
                    routes.add(routeWithConnectionAdded);
                // a route is considered infeasible if it exceeds the distance constraints
                // or has too many nodes
                } else if (routeWithConnectionAdded.getDistance() > maximumDistance ||
                        routeWithConnectionAdded.getNodes().size() > MAX_DEPTH) {
                    continue;
                }

                workingRoutes.add(routeWithConnectionAdded);
            }
        }

        return routes.stream()
                .max(Comparator.comparing(this::rateRoute))
                .orElse(null);
    }

    /**
     * Rate a route based on how many repeated nodes there are in the route.
     * @param route the route to rate
     * @return a double between 0 and 1 where a route with rating 1 is the best, and a route with rating 0 is the worst.
     */
    private <T extends Node> double rateRoute(final Route<T> route) {
        final Map<Node, Integer> repeatVisitCountByNode = new HashMap<>();

        route.getNodes().forEach(node -> repeatVisitCountByNode.put(node, repeatVisitCountByNode.getOrDefault(node, -1) + 1));

        final double totalRepeatVisits = repeatVisitCountByNode.values().stream()
                .reduce(0, Integer::sum);
        final int routeLength = route.getNodes().size();

        return 1 - (totalRepeatVisits / routeLength);
    }
}
