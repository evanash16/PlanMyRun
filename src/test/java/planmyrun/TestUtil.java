package planmyrun;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import de.westnordost.osmapi.map.data.Node;
import de.westnordost.osmapi.map.data.OsmLatLon;
import de.westnordost.osmapi.map.data.OsmNode;
import de.westnordost.osmapi.map.data.OsmWay;
import de.westnordost.osmapi.map.data.Way;
import planmyrun.graph.Graph;
import planmyrun.graph.node.EarthNode;
import planmyrun.model.osm.ImmutableQueryResult;
import planmyrun.model.osm.QueryResult;
import planmyrun.util.GraphUtil;

import java.util.List;

public class TestUtil {

    public static Graph<EarthNode> buildTestGraph() {
        final List<Node> nodes = ImmutableList.of(
                new OsmNode(0L, 0, new OsmLatLon(0, 0), ImmutableMap.of()),
                new OsmNode(1L, 0, new OsmLatLon(1, 0), ImmutableMap.of()),
                new OsmNode(2L, 0, new OsmLatLon(2, 0), ImmutableMap.of()),
                new OsmNode(3L, 0, new OsmLatLon(0, 1), ImmutableMap.of()),
                new OsmNode(4L, 0, new OsmLatLon(1, 1), ImmutableMap.of()),
                new OsmNode(5L, 0, new OsmLatLon(2, 1), ImmutableMap.of()),
                new OsmNode(6L, 0, new OsmLatLon(0, 2), ImmutableMap.of()),
                new OsmNode(7L, 0, new OsmLatLon(1, 2), ImmutableMap.of()),
                new OsmNode(8L, 0, new OsmLatLon(2, 2), ImmutableMap.of()));
        final List<Way> ways = ImmutableList.of(
                new OsmWay(0L, 0, ImmutableList.of(0L, 1L, 2L), ImmutableMap.of()),
                new OsmWay(1L, 0, ImmutableList.of(3L, 4L, 5L), ImmutableMap.of()),
                new OsmWay(2L, 0, ImmutableList.of(6L, 7L, 8L), ImmutableMap.of()),
                new OsmWay(3L, 0, ImmutableList.of(0L, 3L, 6L), ImmutableMap.of()),
                new OsmWay(4L, 0, ImmutableList.of(1L, 4L, 7L), ImmutableMap.of()),
                new OsmWay(5L, 0, ImmutableList.of(3L, 5L, 8L), ImmutableMap.of()));
        final QueryResult queryResult = ImmutableQueryResult.builder()
                .nodes(nodes)
                .ways(ways)
                .build();

        return GraphUtil.buildGraphFromQueryResult(queryResult);
    }
}
