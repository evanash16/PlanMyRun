package planmyrun.geometry;

import com.google.common.collect.ImmutableList;
import lombok.Getter;
import planmyrun.graph.node.Node2D;

import java.awt.geom.Point2D;
import java.util.List;
import java.util.function.BiFunction;

public class Rectangle<T extends Node2D> implements Shape<T> {

    @Getter
    private final List<T> nodes;

    public Rectangle(final T nw, final T se, final BiFunction<Double, Double, T> constructor) {
        final Point2D.Double nwAsPoint = nw.asPoint();
        final Point2D.Double seAsPoint = se.asPoint();

        this.nodes = ImmutableList.of(
                constructor.apply(nwAsPoint.getX(), nwAsPoint.getY()), // NW
                constructor.apply(seAsPoint.getX(), nwAsPoint.getY()), // NE
                constructor.apply(seAsPoint.getX(), seAsPoint.getY()), // SE
                constructor.apply(nwAsPoint.getX(), seAsPoint.getY())); // SW
    }
}
