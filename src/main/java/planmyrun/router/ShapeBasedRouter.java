package planmyrun.router;

import planmyrun.geometry.Shape;
import planmyrun.graph.QueryableGraph;
import planmyrun.graph.node.Node2D;
import planmyrun.route.MutableRoute;
import planmyrun.route.Route;
import planmyrun.route.SimpleRoute;
import planmyrun.router.util.AStar;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class ShapeBasedRouter<T extends Node2D> implements Router<T> {

    private final BiFunction<T, Double, Shape<T>> shapeSupplier;
    private final QueryableGraph<T> queryableGraph;

    public ShapeBasedRouter(final BiFunction<T, Double, Shape<T>> shapeSupplier, final QueryableGraph<T> queryableGraph) {
        this.shapeSupplier = shapeSupplier;
        this.queryableGraph = queryableGraph;
    }

    public Route<T> findRoute(T start, T end, double minimumDistance, double maximumDistance) {
        if (!start.equals(end)) {
            throw new UnsupportedOperationException("Shape-based routes must start and stop at the same point.");
        }

        final Shape<T> shape = shapeSupplier.apply(start, minimumDistance);
        final List<T> straightenedNodes = shape.getNodes().stream()
                .map(queryableGraph::closestTo)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        final MutableRoute<T> route = new SimpleRoute<>(straightenedNodes.get(0));
        for (int i = 1; i < straightenedNodes.size() + 1; i++) {
            final T previous = straightenedNodes.get((i - 1) % straightenedNodes.size());
            final T current = straightenedNodes.get(i % straightenedNodes.size());

            final List<T> pathToCurrent = AStar.findPath(previous, current, T::distanceTo);
            if (!pathToCurrent.isEmpty()) {
                pathToCurrent.forEach(route::addNode);
            }
        }

        return route;
    }

    public Collection<Route<T>> getWorkingRoutes() {
        return null;
    }
}
