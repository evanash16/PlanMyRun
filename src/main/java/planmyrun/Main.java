package planmyrun;

import de.westnordost.osmapi.map.data.BoundingBox;
import planmyrun.dao.OverpassDao;
import planmyrun.dao.OverpassDaoImpl;
import planmyrun.geometry.Circle;
import planmyrun.geometry.Rectangle;
import planmyrun.graph.Graph;
import planmyrun.graph.MutableGraph;
import planmyrun.graph.QuadTreeGraph;
import planmyrun.graph.QueryableGraph;
import planmyrun.graph.node.EarthNode;
import planmyrun.model.osm.QueryResult;
import planmyrun.route.Route;
import planmyrun.router.Router;
import planmyrun.router.ShapeBasedRouter;
import planmyrun.ui.RouteVisualizer;
import planmyrun.util.GraphUtil;

import java.awt.geom.Point2D;

public class Main {

    public static void main(final String[] args) {
        final RouteVisualizer routeVisualizer = new RouteVisualizer();
        routeVisualizer.setVisible(true);

        final OverpassDao overpassDao = new OverpassDaoImpl();
        final BoundingBox boundingBox = overpassDao.getBoundingBox("US", "CA", "San Luis Obispo");
        final QueryResult queryResult = overpassDao.getHighwaysWithinArea(boundingBox);
        final Graph<EarthNode> graph = GraphUtil.buildGraphFromQueryResult(queryResult);

        final Point2D.Double[] graphBoundingBox = GraphUtil.getBoundingBox(graph);
        final QueryableGraph<EarthNode> queryableGraph = new QuadTreeGraph<>(graphBoundingBox[0].getX(), graphBoundingBox[0].getY(), graphBoundingBox[1].getX(), graphBoundingBox[1].getY());
        graph.getNodes().forEach(((MutableGraph<EarthNode>) queryableGraph)::addNode);

        // Broad & Islay
        final EarthNode startingPoint = new EarthNode(35.264082, -120.658134);
        final double minDistance = 5_000;
        final double maxDistance = 6_000;

        final Router<EarthNode> circularRouter = new ShapeBasedRouter<>((start, minimumDistance) -> {
            final double degreesPerMeter = 1.0 / 111_139.0;
            final double radius = minimumDistance / (2 * Math.PI);
            final double radiusInDegrees = degreesPerMeter * radius;

            final Point2D.Double center = new Point2D.Double(start.getX() - radiusInDegrees, start.getY());

            return new Circle(center, radiusInDegrees, 100);
        }, queryableGraph);
        final Route<EarthNode> circularRoute = circularRouter.findRoute(startingPoint, startingPoint, minDistance, maxDistance);

        final Router<EarthNode> rectangularRouter = new ShapeBasedRouter<>((start, minimumDistance) -> {
            final double horizontalDistance = 0.5 * minimumDistance;
            final double verticalDistance = minimumDistance - horizontalDistance;
            final double degreesPerMeter = 1.0 / 111_139.0;

            return new Rectangle(startingPoint.asPoint(), new Point2D.Double(start.getX() + degreesPerMeter * (horizontalDistance / 2), startingPoint.asPoint().getY() + degreesPerMeter * (verticalDistance / 2)));
        }, queryableGraph);
        final Route<EarthNode> rectangularRoute = rectangularRouter.findRoute(startingPoint, startingPoint, minDistance, maxDistance);

        System.out.printf("Circle distance: %fm%n", circularRoute.getDistance());
        routeVisualizer.addRoute(circularRoute);
        System.out.printf("Rectangle distance: %fm%n", rectangularRoute.getDistance());
        routeVisualizer.addRoute(rectangularRoute);
    }
}
