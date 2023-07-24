package planmyrun.geometry;

import lombok.Getter;
import planmyrun.graph.node.Node2D;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

public class Circle<T extends Node2D> implements Shape<T> {

    private static final double TWO_PI = 2.0 * Math.PI;

    @Getter
    private final List<T> nodes;

    public Circle(final T center, final double radius, final int points, final BiFunction<Double, Double, T> constructor) {
        nodes = new ArrayList<>();

        final Point2D.Double centerAsPoint = center.asPoint();

        for (double theta = 0; theta < TWO_PI; theta += TWO_PI / points) {
            nodes.add(constructor.apply(
                    centerAsPoint.getX() + Math.cos(theta) * radius,
                    centerAsPoint.getY() + Math.sin(theta) * radius));
        }
    }
}
