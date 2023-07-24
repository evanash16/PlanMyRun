package planmyrun.router.util;


import com.google.common.collect.ImmutableList;
import org.junit.Test;
import planmyrun.TestUtil;
import planmyrun.graph.Graph;
import planmyrun.graph.node.EarthNode;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class AStarTest {

    @Test
    public void testFindPath() throws IOException {
        final Graph<EarthNode> testGraph = TestUtil.loadTestGraph("simpleGraph.json");

        final List<EarthNode> nodes = testGraph.getNodes();
        final EarthNode start = nodes.get(0); // (0, 0)
        final EarthNode goal = nodes.get(6); // (0, 2)

        final List<EarthNode> expected = ImmutableList.of(
                start,
                new EarthNode(0, 1),
                goal);
        final List<EarthNode> actual = AStar.findPath(start, goal, EarthNode::distanceTo);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testFindPathWithNoPath() throws IOException {
        final Graph<EarthNode> testGraph = TestUtil.loadTestGraph("simpleGraph.json");

        final List<EarthNode> nodes = testGraph.getNodes();
        final EarthNode start = nodes.get(0); // (0, 0)
        final EarthNode goal = new EarthNode(-1, -1); // MUST NOT exist in simpleGraph.json

        final List<EarthNode> expected = ImmutableList.of();
        final List<EarthNode> actual = AStar.findPath(start, goal, EarthNode::distanceTo);
        assertThat(actual).isEqualTo(expected);
    }

}
