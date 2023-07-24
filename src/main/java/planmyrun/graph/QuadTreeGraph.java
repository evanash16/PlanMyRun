package planmyrun.graph;

import com.google.common.collect.ImmutableList;
import planmyrun.graph.node.Node2D;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class QuadTreeGraph<T extends Node2D> extends SimpleGraph<T> implements QueryableGraph<T> {

    final QuadTree<T> quadTree;

    public QuadTreeGraph(double minX, double minY, double maxX, double maxY) {
        this.quadTree = new QuadTree<>(minX, minY, maxX, maxY);
    }

    @Override
    public void addNode(final T node) {
        super.addNode(node);
        quadTree.insert(node);
    }

    public T closestTo(final Point2D.Double point) {
        return quadTree.closestTo(point);
    }
}

class QuadTree<T extends Node2D> {

    private static final int MAX_DEPTH = 2000;
    private static final int MAX_NODES = 5;

    private List<T> nodes;

    private QuadTree<T> ne;
    private QuadTree<T> se;
    private QuadTree<T> sw;
    private QuadTree<T> nw;

    private final double x0;
    private final double y0;
    private final double x1;
    private final double y1;

    private final int depth;
    private final Direction direction;

    private boolean isSubdivided = false;

    public QuadTree(double x0, double y0, double x1, double y1) {
        this(x0, y0, x1, y1, 0, null);
    }

    private QuadTree(double x0, double y0, double x1, double y1, int depth, final Direction direction) {
        this.nodes = new ArrayList<>();

        this.x0 = x0;
        this.y0 = y0;
        this.x1 = x1;
        this.y1 = y1;

        this.depth = depth;
        this.direction = direction;
    }

    private boolean encapsulates(final Point2D.Double point) {
        switch (this.direction) {
            case NORTH_EAST:
                return point.getX() > this.x0 && point.getY() < this.y1;
            case SOUTH_EAST:
                return point.getX() > this.x0 && point.getY() > this.y0;
            case SOUTH_WEST:
                return point.getX() < this.x1 && point.getY() > this.y0;
            case NORTH_WEST:
                return point.getX() < this.x1 && point.getY() < this.y1;
            default:
                return true; // we should not get here, but if we do
        }
    }

    public void insert(final T node) {
        if (nodes != null && (nodes.size() < MAX_NODES || this.depth >= MAX_DEPTH)) {
            this.nodes.add(node);
            return;
        }

        if (!this.isSubdivided) {
            subdivide();
        }

        ImmutableList.of(this.ne, this.se, this.sw, this.nw).stream()
                .filter(qt -> qt.encapsulates(node.asPoint()))
                .findFirst()
                .ifPresent(qt -> qt.insert(node));
    }

    private void subdivide() {
        if (isSubdivided) {
            return;
        }

        double xMid = x0 + x1 / 2;
        double yMid = y0 + y1 / 2;

        /*
         x0, y0 ----- xMid, y0 ----- x1, y0
           |              |             |
           |     NW       |     NE      |
           |              |             |
         x0, yMid --- xMid, yMid --- x1, yMid
           |              |             |
           |     SW       |     SE      |
           |              |             |
         x0, y1 ----- xMid, y1 ----- x1, y1
         */

        this.ne = new QuadTree<>(xMid, this.y0, this.x1, yMid, this.depth + 1, Direction.NORTH_EAST);
        this.se = new QuadTree<>(xMid, yMid, this.x1, this.y1, this.depth + 1, Direction.SOUTH_EAST);
        this.sw = new QuadTree<>(this.x0, yMid, xMid, this.y1, this.depth + 1, Direction.SOUTH_WEST);
        this.nw = new QuadTree<>(this.x0, this.y0, xMid, yMid, this.depth + 1, Direction.NORTH_WEST);

        this.nodes.forEach(node -> ImmutableList.of(this.ne, this.se, this.sw, this.nw).stream()
                .filter(qt -> qt.encapsulates(node.asPoint()))
                .findFirst()
                .ifPresent(qt -> qt.insert(node)));

        this.nodes = null;
        this.isSubdivided = true;
    }

    public T closestTo(final Point2D.Double point) {
        if (!this.isSubdivided) {
            return nodes.stream()
                    .min(Comparator.comparingDouble(n -> n.asPoint().distance(point)))
                    .orElse(null);
        }

        return ImmutableList.of(this.ne, this.se, this.sw, this.nw).stream()
                .filter(qt -> qt.encapsulates(point))
                .findFirst()
                .map(qt -> qt.closestTo(point))
                .orElse(null);
    }

    public enum Direction {
        NORTH_EAST,
        SOUTH_EAST,
        SOUTH_WEST,
        NORTH_WEST
    }
}
