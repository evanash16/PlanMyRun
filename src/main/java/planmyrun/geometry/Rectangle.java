package planmyrun.geometry;

import com.google.common.collect.ImmutableList;
import lombok.Getter;
import planmyrun.graph.node.Node2D;

import java.awt.geom.Point2D;
import java.util.List;
import java.util.function.BiFunction;

public class Rectangle implements Shape2D {

    @Getter
    private final List<Point2D.Double> points;

    public Rectangle(final Point2D.Double nw, final Point2D.Double se) {
        this.points = ImmutableList.of(
                new Point2D.Double(nw.getX(), nw.getY()), // NW
                new Point2D.Double(se.getX(), nw.getY()), // NE
                new Point2D.Double(se.getX(), se.getY()), // SE
                new Point2D.Double(nw.getX(), se.getY())); // SW
    }

    private Rectangle(final List<Point2D.Double> points) {
        this.points = points;
    }

    public Shape2D fromPoints(List<Point2D.Double> points) {
        return new Rectangle(points);
    }
}
