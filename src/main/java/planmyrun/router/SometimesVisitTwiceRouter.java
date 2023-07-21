package planmyrun.router;

import me.tongfei.progressbar.ProgressBar;
import me.tongfei.progressbar.ProgressBarBuilder;
import me.tongfei.progressbar.ProgressBarStyle;
import planmyrun.graph.node.Node;
import planmyrun.route.Route;
import planmyrun.route.SometimesVisitTwiceRoute;

import java.util.*;
import java.util.stream.Collectors;

public class SometimesVisitTwiceRouter<T extends Node> implements Router<T> {

    private static final int MAX_DEPTH = 10000;
    private final Queue<Route<T>> workingRoutes;

    public SometimesVisitTwiceRouter() {
        workingRoutes =
                new PriorityQueue<>(Comparator.comparing(Route::getDistance, Comparator.reverseOrder()));
    }

    public Route<T> findRoute(T start, T end, double minimumDistance, double maximumDistance) {
        try (ProgressBar pb = new ProgressBarBuilder()
                .setTaskName("SometimesVisitTwiceRouter")
                .setInitialMax((long) maximumDistance)
                .setMaxRenderedLength(160)
                .setStyle(ProgressBarStyle.ASCII)
                .build()) {
            // sort working routes from longest to shortest

            Route<T> longestRoute = new SometimesVisitTwiceRoute<>(start);
            workingRoutes.add(longestRoute);

            while (!workingRoutes.isEmpty()) {
                final SometimesVisitTwiceRoute<T> workingRoute = (SometimesVisitTwiceRoute<T>) workingRoutes.remove();
                List<Node> unvisitedNodes = new ArrayList<>();
                T currentNode;

                int backtraceIndex = 0;
                while (unvisitedNodes.isEmpty()) {
                    currentNode = workingRoute.getNodes().get(workingRoute.getNodes().size() - (++backtraceIndex));
                    unvisitedNodes = currentNode.getConnections().stream()
                            .filter(node -> !workingRoute.hasVisited((T) node))
                            .collect(Collectors.toList());
                }

                final List<T> backtracedNodes = new ArrayList<>();
                for (int i = 2; i <= backtraceIndex; i++) {
                    backtracedNodes.add(workingRoute.getNodes().get(workingRoute.getNodes().size() - i));
                }
                backtracedNodes.forEach(workingRoute::addNode);

                for (final Node nextNode : unvisitedNodes) {
                    final SometimesVisitTwiceRoute<T> routeWithConnectionAdded = workingRoute.shallowClone();
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
                        pb.stepTo((long) routeWithConnectionAdded.getDistance());
                        longestRoute = workingRoutes.peek();
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
}
