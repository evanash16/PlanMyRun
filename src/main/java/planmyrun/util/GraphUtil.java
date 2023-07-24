package planmyrun.util;

import lombok.experimental.UtilityClass;
import planmyrun.dao.OverpassDao;
import planmyrun.dao.OverpassDaoImpl;
import planmyrun.graph.Graph;
import planmyrun.graph.MutableGraph;
import planmyrun.graph.SimpleGraph;
import planmyrun.graph.node.EarthNode;
import planmyrun.graph.node.Node2D;
import planmyrun.model.osm.Element;
import planmyrun.model.osm.QueryResult;
import planmyrun.model.osm.Way;

import java.awt.geom.Point2D;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@UtilityClass
public class GraphUtil {

    private static final OverpassDao overpassDao = new OverpassDaoImpl();

    public static Graph<EarthNode> buildGraphFromOSM(final InputStream osmInputStream) {
        final QueryResult queryResult = overpassDao.loadQueryResult(osmInputStream);

        final Map<Long, EarthNode> earthNodesByNodeId = queryResult.getElements().stream()
                .filter(element -> "node".equals(element.getType()))
                .map(Element::toNode)
                .collect(Collectors.toMap(
                        planmyrun.model.osm.Node::getId,
                        osmNode -> new EarthNode(osmNode.getLat(), osmNode.getLon())));
        final List<Way> ways = queryResult.getElements().stream()
                .filter(element -> "way".equals(element.getType()))
                .map(Element::toWay)
                .collect(Collectors.toList());

        for (final Way way : ways) {
            for (int i = 1; i < way.getNodes().size(); i++) {
                final Long previousNode = way.getNodes().get(i - 1);
                final Long currentNode = way.getNodes().get(i);

                final EarthNode previous = earthNodesByNodeId.get(previousNode);
                final EarthNode current = earthNodesByNodeId.get(currentNode);

                current.addConnection(previous);
                previous.addConnection(current);
            }
        }

        final MutableGraph<EarthNode> graph = new SimpleGraph<>();
        earthNodesByNodeId.values().forEach(graph::addNode);

        return graph;
    }

    public <T extends Node2D> Point2D.Double[] getBoundingBox(final Graph<T> graph) {
        double x0 = Double.MAX_VALUE;
        double y0 = Double.MAX_VALUE;
        double x1 = -Double.MAX_VALUE;
        double y1 = -Double.MAX_VALUE;
        for (T node : graph.getNodes()) {
            final Point2D.Double nodeAsPoint = node.asPoint();
            x0 = Math.min(x0, nodeAsPoint.getX());
            y0 = Math.min(y0, nodeAsPoint.getY());
            x1 = Math.max(x1, nodeAsPoint.getX());
            y1 = Math.max(y1, nodeAsPoint.getY());
        }

        return new Point2D.Double[] {new Point2D.Double(x0, y0), new Point2D.Double(x1, y1)};
    }
}
