package planmyrun.router.util;


import com.google.common.collect.ImmutableList;
import org.junit.Test;
import planmyrun.TestUtil;
import planmyrun.graph.Graph;
import planmyrun.graph.node.EarthNode;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class AStarTest {

    @Test
    public void testFindPath() {
        final Graph<EarthNode> testGraph = TestUtil.buildTestGraph();

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
    public void testFindPathWithNoPath() {
        final Graph<EarthNode> testGraph = TestUtil.buildTestGraph();

        final List<EarthNode> nodes = testGraph.getNodes();
        final EarthNode start = nodes.get(0); // (0, 0)
        final EarthNode goal = new EarthNode(-1, -1); // MUST NOT exist in simpleGraph.json

        final List<EarthNode> expected = ImmutableList.of();
        final List<EarthNode> actual = AStar.findPath(start, goal, EarthNode::distanceTo);
        assertThat(actual).isEqualTo(expected);
    }

}
