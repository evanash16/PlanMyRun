package planmyrun.ui;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import de.westnordost.osmapi.map.data.BoundingBox;
import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.openstreetmap.gui.jmapviewer.MapMarkerDot;
import org.openstreetmap.gui.jmapviewer.MapPolygonImpl;
import org.openstreetmap.gui.jmapviewer.MapRectangleImpl;
import org.openstreetmap.gui.jmapviewer.OsmTileLoader;
import org.openstreetmap.gui.jmapviewer.Style;
import org.openstreetmap.gui.jmapviewer.interfaces.MapRectangle;
import planmyrun.graph.node.EarthNode;
import planmyrun.route.Route;

import javax.swing.JFrame;
import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RouteVisualizer extends JFrame {

    private final JMapViewer jMapViewer;
    private final transient Set<Route<EarthNode>> routesToVisualize;
    private static final List<Color> COLORS = ImmutableList.of(
            Color.RED,
            Color.ORANGE,
            Color.GREEN,
            Color.BLUE,
            Color.CYAN,
            Color.MAGENTA);

    public RouteVisualizer() {
        routesToVisualize = new HashSet<>();

        jMapViewer = new JMapViewer();
        jMapViewer.setTileLoader(new OsmTileLoader(jMapViewer));

        setSize(500, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        add(jMapViewer);
    }

    @Override
    public void paint(final Graphics g) {
        super.paint(g);

        this.visualizeRoutes();
    }

    public void addRoute(final Route<EarthNode> route) {
        if (route != null) {
            this.routesToVisualize.add(route.shallowClone());
            this.repaint();
        }
    }

    public void clearRoutes() {
        this.routesToVisualize.clear();
    }

    private void visualizeRoutes() {
        this.jMapViewer.removeAllMapMarkers();
        this.jMapViewer.removeAllMapPolygons();
        this.jMapViewer.removeAllMapRectangles();

        final ImmutableMap<Route<EarthNode>, Color> colorsByEarthNode = this.routesToVisualize.stream()
                .collect(ImmutableMap.toImmutableMap(Function.identity(), n -> COLORS.get(Math.abs(n.hashCode() % COLORS.size()))));
        colorsByEarthNode.forEach(this::visualizeRoute);
    }

    private void visualizeRoute(final Route<EarthNode> route, final Color color) {
        final EarthNode first = route.getNodes().get(0);
        final EarthNode last = route.getNodes().get(route.getNodes().size() - 1);

        this.jMapViewer.addMapMarker(new MapMarkerDot(Color.GREEN, first.getLat(), first.getLon()));
        this.jMapViewer.addMapMarker(new MapMarkerDot(Color.RED, last.getLat(), last.getLon()));

        final List<Coordinate> pathForward = route.getNodes().stream()
                .map(node -> new Coordinate(node.getLat(), node.getLon()))
                .collect(Collectors.toList());

        final List<Coordinate> pathReverse = new ArrayList<>(pathForward);
        Collections.reverse(pathReverse);

        final List<Coordinate> coordinates = Stream.of(pathForward, pathReverse)
                .flatMap(List::stream)
                .collect(Collectors.toList());

        final Style defaultStyle = MapPolygonImpl.getDefaultStyle();
        this.jMapViewer.addMapPolygon(new MapPolygonImpl(
                null, // layer
                null, // name
                coordinates,
                new Style(color, defaultStyle.getBackColor(), defaultStyle.getStroke(), defaultStyle.getFont())));
    }

    public void displayToFitBoundingBox(final BoundingBox boundingBox) {
        final MapRectangle boundingBoxAsRectangle = new MapRectangleImpl(
                new Coordinate(boundingBox.getMinLatitude(), boundingBox.getMinLongitude()),
                new Coordinate(boundingBox.getMaxLatitude(), boundingBox.getMaxLongitude()));

        final List<MapRectangle> mapRectangles = this.jMapViewer.getMapRectangleList();
        this.jMapViewer.removeAllMapRectangles();

        this.jMapViewer.addMapRectangle(boundingBoxAsRectangle);
        this.jMapViewer.setDisplayToFitMapRectangles();
        this.jMapViewer.removeMapRectangle(boundingBoxAsRectangle);

        mapRectangles.forEach(this.jMapViewer::addMapRectangle);
    }
}
