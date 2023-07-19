package planmyrun;

import planmyrun.graph.SimpleGraph;
import planmyrun.graph.node.SimpleNode;
import planmyrun.route.Route;
import planmyrun.router.Router;
import planmyrun.router.SimpleRouter;

import java.util.stream.Collectors;

public class Main {

    public static void main(final String[] args) {

        /*
         * node1 ---- node2
         *   |          |
         *   |          |
         * node4 ---- node3
         */
        final SimpleNode node1 = new SimpleNode(0, 0);
        final SimpleNode node2 = new SimpleNode(1, 0);
        final SimpleNode node3 = new SimpleNode(1, 1);
        final SimpleNode node4 = new SimpleNode(0, 1);

        node1.addConnection(node2);
        node1.addConnection(node4);

        node2.addConnection(node1);
        node2.addConnection(node3);

        node3.addConnection(node2);
        node3.addConnection(node4);

        node4.addConnection(node1);
        node4.addConnection(node3);

        final SimpleGraph<SimpleNode> graph = new SimpleGraph<>();
        graph.addNode(node1);
        graph.addNode(node2);
        graph.addNode(node3);
        graph.addNode(node4);

        final Router router = new SimpleRouter();
        final Route<SimpleNode> route = router.findRoute(node1, node1, 4, 4);

        if (route == null) {
            return;
        }

        System.out.println(route.getNodes().stream()
                .map((node) -> String.format("(%f, %f)", node.getX(), node.getY()))
                .collect(Collectors.joining(" -> ")));
    }
}
