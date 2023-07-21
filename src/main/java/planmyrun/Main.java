package planmyrun;

import planmyrun.dao.OverpassDao;
import planmyrun.dao.OverpassDaoImpl;
import planmyrun.graph.Graph;
import planmyrun.graph.MutableGraph;
import planmyrun.graph.SimpleGraph;
import planmyrun.graph.node.EarthNode;
import planmyrun.model.osm.Element;
import planmyrun.model.osm.QueryResult;
import planmyrun.model.osm.Way;
import planmyrun.route.Route;
import planmyrun.router.Router;
import planmyrun.router.SometimesVisitTwiceRouter;
import planmyrun.ui.RouteVisualizer;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Main {

    public static void main(final String[] args) {
        final RouteVisualizer routeVisualizer = new RouteVisualizer();

        final Graph<EarthNode> graph = buildGraphFromOSMExport();
        final double lat = 35.2586027;
        final double lon = -120.6360669;
        final EarthNode startingPoint = graph.getNodes().stream()
                .filter(node -> node.getLat() == lat && node.getLon() == lon)
                .findFirst()
                .orElseThrow(RuntimeException::new);

        final Router<EarthNode> router = new SometimesVisitTwiceRouter<>();
        final Route<EarthNode> route = router.findRoute(startingPoint, startingPoint, 40000, 45000);
        routeVisualizer.setRouteToVisualize(route);
        routeVisualizer.setVisible(true);
    }

    private static Graph<EarthNode> buildGraphFromOSMExport() {
        final QueryResult queryResult;
        try (InputStream inputStream = Main.class.getClassLoader().getResourceAsStream("export.json")) {
            final OverpassDao overpassDao = new OverpassDaoImpl();
            queryResult = overpassDao.loadQueryResult(inputStream);
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }

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
}
