package planmyrun.graph;

import java.util.ArrayList;
import java.util.List;

public class SimpleGraph implements MutableGraph {

    private final List<Node> nodes;

    public SimpleGraph() {
        this.nodes = new ArrayList<>();
    }

    public List<Node> getNodes() {
        return this.nodes;
    }

    public void addNode(final Node node) {
        this.nodes.add(node);
    }
}
