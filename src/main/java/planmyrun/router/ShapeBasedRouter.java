package planmyrun.router;

import planmyrun.geometry.Shape2D;
import planmyrun.graph.QueryableGraph;
import planmyrun.graph.node.Node2D;
import planmyrun.route.MutableRoute;
import planmyrun.route.Route;
import planmyrun.route.SimpleRoute;
import planmyrun.router.util.AStar;

import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class ShapeBasedRouter<T extends Node2D> implements Router<T> {

    private final BiFunction<Point2D.Double, Double, Shape2D> shapeSupplier;
    private final QueryableGraph<T> queryableGraph;

    public ShapeBasedRouter(final BiFunction<Point2D.Double, Double, Shape2D> shapeSupplier, final QueryableGraph<T> queryableGraph) {
        this.shapeSupplier = shapeSupplier;
        this.queryableGraph = queryableGraph;
    }

    public Route<T> findRoute(T start, T end, double minimumDistance, double maximumDistance) {
        if (!start.equals(end)) {
            throw new UnsupportedOperationException("Shape-based routes must start and stop at the same point.");
        }

        final Shape2D shape = shapeSupplier.apply(start.asPoint(), minimumDistance);

        MutableRoute<T> route = null;
        while (route == null || route.getDistance() > maximumDistance) {
            Shape2D workingShape = shape;
            if (route != null) {
                final double percentDifference = (route.getDistance() - minimumDistance) / minimumDistance;
                workingShape = shape.shrink(percentDifference);
            }

            final List<T> straightenedNodes = workingShape.getPoints().stream()
                    .map(queryableGraph::closestTo)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            final MutableRoute<T> workingRoute = new SimpleRoute<>(straightenedNodes.get(0));
            for (int i = 1; i < straightenedNodes.size() + 1; i++) {
                final T previous = straightenedNodes.get((i - 1) % straightenedNodes.size());
                final T current = straightenedNodes.get(i % straightenedNodes.size());

                final List<T> pathToCurrent = AStar.findPath(previous, current, T::distanceTo);
                if (!pathToCurrent.isEmpty()) {
                    pathToCurrent.forEach(workingRoute::addNode);
                }
            }

            // If the shrunken route is worse, or is less than the minimum distance, return what we have
            if (route != null && (workingRoute.getDistance() > route.getDistance() || workingRoute.getDistance() < minimumDistance)) {
                return route;
            }

            route = workingRoute;
        }

        if (route.getDistance() < minimumDistance || route.getDistance() > maximumDistance) {
            return null;
        }

        return route;
    }

    public Collection<Route<T>> getWorkingRoutes() {
        return null;
    }
}
