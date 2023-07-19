package planmyrun.graph.node;

import sun.java2d.pipe.SpanShapeRenderer;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SimpleNode implements MutableNode {

    private final double x;
    private final double y;
    private final Set<Node> connections;
    private final Map<Node, Double> distanceCache;

    public SimpleNode(final double x, final double y) {
        this.x = x;
        this.y = y;
        this.connections = new HashSet<>();
        this.distanceCache = new HashMap<>();
    }

    public Collection<Node> getConnections() {
        return this.connections;
    }

    public void addConnection(final Node node) {
        if (!(node instanceof SimpleNode)) {
            throw new IllegalArgumentException(String.format("Cannot add connection to node of type %s", node.getClass()));
        }

        this.connections.add(node);
    }

    public double distanceTo(final Node node) {
        if (!(node instanceof SimpleNode)) {
            throw new IllegalArgumentException(
                    String.format(
                            "Cannot calculate distance between a node of type %s and a node of type %s",
                            getClass().getName(),
                            node.getClass().getName()));
        } else if (!this.getConnections().contains(node)) {
            throw new IllegalArgumentException("Cannot calculate distance to a node which is not connected to this one.");
        }

        return this.distanceCache.computeIfAbsent(node, other -> this.distanceTo((SimpleNode) other));
    }

    private double distanceTo(final SimpleNode node) {
        return Math.sqrt(Math.pow(node.getX() - this.getX(), 2) + Math.pow(node.getY() - this.getY(), 2));
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }
}
