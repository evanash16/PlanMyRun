package planmyrun.graph.node;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class EarthNode implements MutableNode {

    private static final double EARTH_RADIUS = 40_075_017.0;

    private final double lat;
    private final double lon;
    private final Set<Node> connections;
    private final Map<Node, Double> distanceCache;

    public EarthNode(final double lat, final double lon) {
        this.lat = lat;
        this.lon = lon;
        this.connections = new HashSet<>();
        this.distanceCache = new HashMap<>();
    }

    public Collection<Node> getConnections() {
        return this.connections;
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
        } else if (!this.getConnections().contains(node)) {
            throw new IllegalArgumentException("Cannot calculate distance to a node which is not connected to this one.");
        }

        return this.distanceCache.computeIfAbsent(node, other -> this.distanceTo((EarthNode) other));
    }

    // Haversine formula for lon/lat -> lon/lat distance
    private double distanceTo(final EarthNode node) {
        // a = {[1 - cos(lat2 - lat1)] + cos(lat1) * cos(lat2) * [1 - cos(lon2 - lon1)]} / 2
        final double a =
                ((1 - Math.cos(node.getLat() - this.getLat())) +
                        Math.cos(this.getLat()) * Math.cos(node.getLat()) * (1 - Math.cos(node.getLon() - this.getLon()))) / 2;
        // distance = 2 * R * tan-1(sqrt(a) - sqrt(1 - a))
        return 2 * EARTH_RADIUS * Math.atan(Math.sqrt(a) / Math.sqrt(1 - a));
    }

    public double getLat() {
        return this.lat;
    }

    public double getLon() {
        return this.lon;
    }

}
