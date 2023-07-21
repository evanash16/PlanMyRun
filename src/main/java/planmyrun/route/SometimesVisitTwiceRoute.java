package planmyrun.route;

import planmyrun.graph.node.Node;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SometimesVisitTwiceRoute<T extends Node> extends SimpleRoute<T> {

    private final Set<T> visited;

    public SometimesVisitTwiceRoute(final T node) {
        super(node);

        visited = new HashSet<>(Collections.singletonList(node));
    }

    private SometimesVisitTwiceRoute(final List<T> nodes, final double distance, final Set<T> visited) {
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
    public SometimesVisitTwiceRoute<T> shallowClone() {
        return new SometimesVisitTwiceRoute<>(new ArrayList<>(getNodes()), getDistance(), new HashSet<>(this.visited));
    }
}
