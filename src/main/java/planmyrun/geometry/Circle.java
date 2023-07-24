package planmyrun.geometry;

import lombok.Getter;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class Circle implements Shape2D {

    private static final double TWO_PI = 2.0 * Math.PI;

    @Getter
    private final List<Point2D.Double> points;

    public Circle(final Point2D.Double center, final double radius, final int numPoints) {
        points = new ArrayList<>();

        for (double theta = 0; theta < TWO_PI; theta += TWO_PI / numPoints) {
            points.add(new Point2D.Double(
                    center.getX() + Math.cos(theta) * radius,
                    center.getY() + Math.sin(theta) * radius));
        }
    }

    private Circle(final List<Point2D.Double> points) {
        this.points = points;
    }

    public Shape2D fromPoints(List<Point2D.Double> points) {
        return new Circle(points);
    }
}
