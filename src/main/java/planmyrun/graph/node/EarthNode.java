package planmyrun.graph.node;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

@EqualsAndHashCode(exclude = { "connections", "distanceCache" })
public class EarthNode implements MutableNode, Node2D {

    private static final double EARTH_RADIUS_IN_METERS = 6_371_000.0;

    @Getter
    private final double lat;
    @Getter
    private final double lon;
    @Getter
    private final Collection<Node> connections;
    private final Map<Node, Double> distanceCache;

    public EarthNode(final double lat, final double lon) {
        this.lat = lat;
        this.lon = lon;
        this.connections = new HashSet<>();
        this.distanceCache = new HashMap<>();
    }

    public void addConnection(final Node node) {
        if (!(node instanceof EarthNode)) {
            throw new IllegalArgumentException(String.format("Cannot add connection to node of type %s", node.getClass()));
        }

        this.connections.add(node);
    }

    public double distanceTo(final Node node) {
        if (!(node instanceof EarthNode)) {
            throw new IllegalArgumentException(
                    String.format(
                            "Cannot calculate distance between a node of type %s and a node of type %s",
                            getClass().getName(),
                            node.getClass().getName()));
        }

        return this.distanceCache.computeIfAbsent(node, other -> this.distanceTo((EarthNode) other));
    }

    // Haversine formula for lon/lat -> lon/lat distance
    private double distanceTo(final EarthNode node) {
        // a = {[1 - cos(lat2 - lat1)] + cos(lat1) * cos(lat2) * [1 - cos(lon2 - lon1)]} / 2
        final double a =
                ((1 - Math.cos(Math.toRadians(node.getLat() - this.getLat()))) +
                        Math.cos(Math.toRadians(this.getLat())) * Math.cos(Math.toRadians(node.getLat())) * (1 - Math.cos(Math.toRadians(node.getLon() - this.getLon())))) / 2;
        // distance = 2 * R * tan-1(sqrt(a) - sqrt(1 - a))
        return 2 * EARTH_RADIUS_IN_METERS * Math.atan(Math.sqrt(a) / Math.sqrt(1 - a));
    }

    public Point2D.Double asPoint() {
        return new Point2D.Double(this.lat, this.lon);
    }
}
