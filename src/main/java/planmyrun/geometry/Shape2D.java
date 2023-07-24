package planmyrun.geometry;

import com.google.common.collect.ImmutableList;

import java.awt.geom.Point2D;
import java.util.List;
import java.util.stream.Collectors;

public interface Shape2D {

    /**
     * Return the {@link Shape} as a ordered list of {@link T}s.
     *
     * @return the list of {@link T}s comprising the {@link Shape}.
     */
    List<Point2D.Double> getPoints();

    /**
     * Return a {@link Shape2D} composed of {@param nodes}.
     *
     * @param nodes the nodes to construct the {@link Shape2D} from.
     * @return a {@link Shape2D} composed of {@param nodes}
     */
    Shape2D fromPoints(List<Point2D.Double> nodes);

    /**
     * Return a new shape shrunk by the provided percentage.
     *
     * @param percentage the percentage of a given {@link Point2D.Double}'s distance from the center to the nudge said {@link Point2D.Double}.
     * @return a new {@link Shape2D} shrunk by the provided {@param percentage}
     */
    default Shape2D shrink(double percentage) {
        final Point2D.Double total = getPoints().stream()
                .reduce(new Point2D.Double(0, 0), (acc, next) -> new Point2D.Double(acc.getX() + next.getX(), acc.getY() + next.getY()));
        final Point2D.Double average = new Point2D.Double(
                total.getX() / getPoints().size(),
                total.getY() / getPoints().size());

        final List<Point2D.Double> points = getPoints().stream()
                .map(point -> new Point2D.Double(
                        point.getX() + percentage * (average.getX() - point.getX()),
                        point.getY() + percentage * (average.getY() - point.getY())))
                .collect(ImmutableList.toImmutableList());
        return fromPoints(points);
    }
}
