package planmyrun;

import planmyrun.graph.Graph;
import planmyrun.graph.MutableGraph;
import planmyrun.graph.QuadTreeGraph;
import planmyrun.graph.QueryableGraph;
import planmyrun.graph.node.EarthNode;
import planmyrun.router.Router;
import planmyrun.router.SometimesVisitTwiceRouter;
import planmyrun.ui.RouteVisualizer;
import planmyrun.util.GraphUtil;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;

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

        final Point2D.Double[] graphBoundingBox = GraphUtil.getBoundingBox(graph);
        final MutableGraph<EarthNode> queryableGraph = new QuadTreeGraph<>(graphBoundingBox[0].getX(), graphBoundingBox[0].getY(), graphBoundingBox[1].getX(), graphBoundingBox[1].getY());
        graph.getNodes().forEach(queryableGraph::addNode);

        final Router<EarthNode> router = new SometimesVisitTwiceRouter<>();
        new Thread(() -> router.findRoute(startingPoint, startingPoint, 40000, 45000)).start();
        new Thread(() -> {
            while (true) {
                router.getWorkingRoutes().stream()
                        .findFirst()
                        .ifPresent(routeVisualizer::setRouteToVisualize);
            }
        }).start();
        routeVisualizer.setVisible(true);
    }

    private static Graph<EarthNode> buildGraphFromOSMExport() {
        try (InputStream inputStream = Main.class.getClassLoader().getResourceAsStream("export.json")) {
            return GraphUtil.buildGraphFromOSM(inputStream);
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
