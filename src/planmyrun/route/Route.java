package planmyrun.route;

import planmyrun.graph.Node;

import java.util.List;

public interface Route<T extends Node> {

    /**
     * @return the nodes in the route in the order that they appear in the route.
     */
    List<T> getNodes();

    /**
     * @return the total distance of the route.
     */
    double getDistance();
}
