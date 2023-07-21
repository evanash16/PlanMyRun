package planmyrun.route;

import planmyrun.graph.node.Node;

import java.util.ArrayList;
import java.util.List;

public class SimpleRoute<T extends Node> implements MutableRoute<T> {

    private final List<T> nodes;
    private double distance;

    public SimpleRoute(final T node) {
        this.nodes = new ArrayList<>();
        this.nodes.add(node);

        this.distance = 0.0;
    }

    protected SimpleRoute(final List<T> nodes, final double distance) {
        this.nodes = nodes;
        this.distance = distance;
    }

    public List<T> getNodes() {
        return this.nodes;
    }

    public void addNode(final T node) {
        this.distance += nodes.get(nodes.size() - 1).distanceTo(node);
        this.nodes.add(node);
    }

    public double getDistance() {
        return this.distance;
    }

    public SimpleRoute<T> shallowClone() {
        return new SimpleRoute<>(new ArrayList<>(this.nodes), this.distance);
    }
}
