package planmyrun.graph;

import org.junit.Test;
import org.junit.jupiter.params.ParameterizedTest;
import planmyrun.TestUtil;
import planmyrun.graph.node.EarthNode;
import planmyrun.graph.node.SimpleNode;
import planmyrun.util.GraphUtil;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class QuadTreeGraphTest {

    @Test
    public void testClosestToWithSimpleNodesAndNoSubdivisions() {
        final QuadTreeGraph<SimpleNode> quadTreeGraph = new QuadTreeGraph<>(-5, -5, 5, 5);
        quadTreeGraph.addNode(new SimpleNode(0, 0));

        final SimpleNode expected = new SimpleNode(0, 0);
        final SimpleNode actual = quadTreeGraph.closestTo(new SimpleNode(0.25, 0.25));

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testClosestToWithTestOSMGraphAndInBoundsPoint() throws IOException {
        final QueryableGraph<EarthNode> quadTreeGraph = buildQuadTreeGraph();

        final EarthNode expected = new EarthNode(0, 0);
        final EarthNode actual = quadTreeGraph.closestTo(new EarthNode(0.25, 0.25));

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testClosestToWithTestOSMGraphAndOutOfBoundsPoint() throws IOException {
        final QueryableGraph<EarthNode> quadTreeGraph = buildQuadTreeGraph();

        final EarthNode expected = quadTreeGraph.getNodes().get(0); // (0, 0)
        final EarthNode actual = quadTreeGraph.closestTo(new EarthNode(-1000, -1000)); // very out of bounds

        assertThat(actual).isEqualTo(expected);
    }

    private QueryableGraph<EarthNode> buildQuadTreeGraph() throws IOException {
        final Graph<EarthNode> testGraph = TestUtil.loadTestGraph("simpleGraph.json");
        final Point2D.Double[] boundingBox = GraphUtil.getBoundingBox(testGraph);

        final QuadTreeGraph<EarthNode> testQuadTreeGraph = new QuadTreeGraph<>(
                boundingBox[0].getX(),
                boundingBox[0].getY(),
                boundingBox[1].getX(),
                boundingBox[1].getY());
        final List<EarthNode> nodes = testGraph.getNodes();
        nodes.forEach(testQuadTreeGraph::addNode);

        return testQuadTreeGraph;
    }
}
