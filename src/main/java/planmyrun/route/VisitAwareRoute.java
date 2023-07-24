package planmyrun.route;

import lombok.EqualsAndHashCode;
import planmyrun.graph.node.Node;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
public class VisitAwareRoute<T extends Node> extends SimpleRoute<T> {

    private final Set<T> visited;

    public VisitAwareRoute(final T node) {
        super(node);

        visited = new HashSet<>(Collections.singletonList(node));
    }

    private VisitAwareRoute(final List<T> nodes, final double distance, final Set<T> visited) {
        super(nodes, distance);

        this.visited = visited;
    }

    @Override
    public void addNode(final T node) {
        super.addNode(node);
        visited.add(node);
    }

    public boolean hasVisited(final T node) {
        return visited.contains(node);
    }

    @Override
    public VisitAwareRoute<T> shallowClone() {
        return new VisitAwareRoute<>(new ArrayList<>(getNodes()), getDistance(), new HashSet<>(this.visited));
    }
}
