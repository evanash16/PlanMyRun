package planmyrun.graph.node;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

@EqualsAndHashCode(exclude = { "connections" })
public class SimpleNode implements MutableNode, Node2D {

    @Getter
    private final double x;
    @Getter
    private final double y;
    @Getter
    private final Collection<Node> connections;

    private final Map<Node, Double> distanceCache;

    public SimpleNode(final double x, final double y) {
        this.x = x;
        this.y = y;
        this.connections = new HashSet<>();
        this.distanceCache = new HashMap<>();
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

    public Point2D.Double asPoint() {
        return new Point2D.Double(this.x, this.y);
    }
}
