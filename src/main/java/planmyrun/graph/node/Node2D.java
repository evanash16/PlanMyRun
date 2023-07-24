package planmyrun.graph.node;

import java.awt.geom.Point2D;

public interface Node2D extends Node {

    /**
     * Convert to a {@link Point2D.Double} form.
     *
     * @return a {@link Point2D.Double}
     */
    Point2D.Double asPoint();
}
