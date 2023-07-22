package planmyrun.ui;

import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.openstreetmap.gui.jmapviewer.MapMarkerDot;
import org.openstreetmap.gui.jmapviewer.MapPolygonImpl;
import org.openstreetmap.gui.jmapviewer.OsmTileLoader;
import planmyrun.graph.node.EarthNode;
import planmyrun.route.Route;

import javax.swing.JFrame;
import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RouteVisualizer extends JFrame {

    private final JMapViewer jMapViewer;
    private transient Route<EarthNode> routeToVisualize;

    public RouteVisualizer() {
        jMapViewer = new JMapViewer();
        jMapViewer.setTileLoader(new OsmTileLoader(jMapViewer));
        jMapViewer.setDisplayToFitMapPolygons();

        setSize(500, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        add(jMapViewer);
    }

    @Override
    public void paint(final Graphics g) {
        super.paint(g);

        if (routeToVisualize != null) {
            visualizeRoute(routeToVisualize);
        }
    }

    public void setRouteToVisualize(final Route<EarthNode> route) {
        if (route != null && !route.equals(routeToVisualize)) {
            this.routeToVisualize = route.shallowClone();
            this.repaint();
        }
    }

    private void visualizeRoute(final Route<EarthNode> route) {
        jMapViewer.removeAllMapMarkers();
        jMapViewer.removeAllMapPolygons();
        final EarthNode first = route.getNodes().get(0);
        final EarthNode last = route.getNodes().get(route.getNodes().size() - 1);

        jMapViewer.addMapMarker(new MapMarkerDot(Color.GREEN, first.getLat(), first.getLon()));
        jMapViewer.addMapMarker(new MapMarkerDot(Color.RED, last.getLat(), last.getLon()));

        final List<Coordinate> pathForward = route.getNodes().stream()
                .map(node -> new Coordinate(node.getLat(), node.getLon()))
                .collect(Collectors.toList());

        final List<Coordinate> pathReverse = new ArrayList<>(pathForward);
        Collections.reverse(pathReverse);

        final List<Coordinate> coordinates = Stream.of(pathForward, pathReverse)
                .flatMap(List::stream)
                .collect(Collectors.toList());

        jMapViewer.addMapPolygon(new MapPolygonImpl(coordinates));
        jMapViewer.setDisplayToFitMapPolygons();
    }
}
