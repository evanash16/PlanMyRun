package planmyrun.router.util;


import com.google.common.collect.ImmutableList;
import org.junit.Test;
import planmyrun.graph.Graph;
import planmyrun.graph.node.EarthNode;
import planmyrun.util.GraphUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class AStarTest {

    @Test
    public void testFindPath() throws IOException {
        final Graph<EarthNode> testGraph = loadTestGraph();

        final List<EarthNode> nodes = testGraph.getNodes();
        final EarthNode start = nodes.get(0); // (0, 0)
        final EarthNode goal = nodes.get(6); // (0, 2)

        final List<EarthNode> expected = ImmutableList.of(
                start,
                nodes.get(3), // (0, 1)
                goal);
        final List<EarthNode> actual = AStar.findPath(start, goal, EarthNode::distanceTo);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testFindPathWithNoPath() throws IOException {
        final Graph<EarthNode> testGraph = loadTestGraph();

        final List<EarthNode> nodes = testGraph.getNodes();
        final EarthNode start = nodes.get(0); // (0, 0)
        final EarthNode goal = new EarthNode(10, 10);

        final List<EarthNode> expected = ImmutableList.of();
        final List<EarthNode> actual = AStar.findPath(start, goal, EarthNode::distanceTo);
        assertThat(actual).isEqualTo(expected);
    }

    private Graph<EarthNode> loadTestGraph() throws IOException {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("simpleGraph.json")){
            return GraphUtil.buildGraphFromOSM(inputStream);
        }
    }
}
