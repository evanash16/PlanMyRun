package planmyrun.router;

import me.tongfei.progressbar.ProgressBar;
import planmyrun.graph.node.Node;
import planmyrun.route.MutableRoute;
import planmyrun.route.Route;
import planmyrun.route.SimpleRoute;

import java.util.*;

public class SimpleRouter<T extends Node> implements Router<T> {

    private static final int MAX_DEPTH = 10000;
    private final Queue<Route<T>> workingRoutes;

    public SimpleRouter() {
        // sort working routes from best to worst
        workingRoutes = new PriorityQueue<>(Comparator.comparing(this::rateRoute, Comparator.reverseOrder()));
    }

    public Route<T> findRoute(T start, T end, double minimumDistance, double maximumDistance) {
        try (ProgressBar pb = new ProgressBar("SimpleRouter", (long) minimumDistance)) {
            MutableRoute<T> longestRoute = new SimpleRoute<>(start);
            workingRoutes.add(longestRoute);

            while (!workingRoutes.isEmpty()) {
                final MutableRoute<T> workingRoute = (MutableRoute<T>) workingRoutes.remove();
                final T currentNode = workingRoute.getNodes().get(workingRoute.getNodes().size() - 1);

                for (final Node nextNode : currentNode.getConnections()) {
                    final MutableRoute<T> routeWithConnectionAdded = workingRoute.shallowClone();
                    routeWithConnectionAdded.addNode((T) nextNode);

                    // a route is complete if it meets the distance constraints and ends
                    // with the requested node
                    if (routeWithConnectionAdded.getDistance() >= minimumDistance &&
                            routeWithConnectionAdded.getDistance() <= maximumDistance &&
                            nextNode.equals(end)) {
                        return routeWithConnectionAdded;
                        // a route is considered infeasible if it exceeds the distance constraints
                        // or has too many nodes
                    } else if (routeWithConnectionAdded.getDistance() > maximumDistance ||
                            routeWithConnectionAdded.getNodes().size() > MAX_DEPTH) {
                        continue;
                    } else if (routeWithConnectionAdded.getDistance() > longestRoute.getDistance()) {
                        pb.stepTo((long) routeWithConnectionAdded.getDistance());
                        longestRoute = routeWithConnectionAdded;
                    }

                    workingRoutes.add(routeWithConnectionAdded);
                }
            }

            return null;
        }
    }

    public Collection<Route<T>> getWorkingRoutes() {
        return this.workingRoutes;
    }

    /**
     * Rate a route based on how many repeated nodes there are in the route.
     *
     * @param route the route to rate
     * @return a double between 0 and 1 where a route with rating 1 is the best, and a route with rating 0 is the worst.
     */
    private double rateRoute(final Route<T> route) {
        final Map<Node, Integer> repeatVisitCountByNode = new HashMap<>();

        route.getNodes().forEach(node -> repeatVisitCountByNode.put(node, repeatVisitCountByNode.getOrDefault(node, -1) + 1));

        final double totalRepeatVisits = repeatVisitCountByNode.values().stream()
                .reduce(0, Integer::sum);
        final int routeLength = route.getNodes().size();

        return 1 - (totalRepeatVisits / routeLength);
    }
}
