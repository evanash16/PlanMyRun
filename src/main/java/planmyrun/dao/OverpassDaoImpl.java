package planmyrun.dao;

import com.google.common.collect.MoreCollectors;
import de.westnordost.osmapi.OsmConnection;
import de.westnordost.osmapi.map.MapDataFactory;
import de.westnordost.osmapi.map.data.BoundingBox;
import de.westnordost.osmapi.map.data.LatLon;
import de.westnordost.osmapi.map.data.Node;
import de.westnordost.osmapi.map.data.OsmNode;
import de.westnordost.osmapi.map.data.Relation;
import de.westnordost.osmapi.map.data.Way;
import de.westnordost.osmapi.map.handler.MapDataHandler;
import de.westnordost.osmapi.overpass.MapDataWithGeometryHandler;
import de.westnordost.osmapi.overpass.OverpassMapDataApi;
import hu.supercluster.overpasser.library.output.OutputModificator;
import hu.supercluster.overpasser.library.output.OutputOrder;
import hu.supercluster.overpasser.library.output.OutputVerbosity;
import hu.supercluster.overpasser.library.query.OverpassQuery;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import planmyrun.model.osm.ImmutableQueryResult;
import planmyrun.model.osm.QueryResult;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

@Log4j2
public class OverpassDaoImpl implements OverpassDao {

    private final GroupingOverpassMapDataApi overpassApi;

    public OverpassDaoImpl() {
        overpassApi = new GroupingOverpassMapDataApi(new OsmConnection("https://overpass-api.de/api/", null));
    }

    @Override
    public QueryResult getHighwaysWithinArea(final BoundingBox boundingBox) {
        final String query = new OverpassQuery()
                .boundingBox(
                        boundingBox.getMinLatitude(),
                        boundingBox.getMinLongitude(),
                        boundingBox.getMaxLatitude(),
                        boundingBox.getMaxLongitude())
                .filterQuery()
                .way()
                .tag("highway")
                .end()
                .output(OutputVerbosity.SKEL, OutputModificator.GEOM, OutputOrder.QT, Integer.MAX_VALUE)
                .build();
        return overpassApi.queryElementsWithGeometry(query);
    }

    @Override
    public BoundingBox getBoundingBox(String country, String state, String city) {
        final String query = new OverpassQuery()
                .filterQuery()
                .rel()
                .tag("is_in:country_code", country)
                .tag("is_in:state_code", state)
                .tag("name", city)
                .end()
                .output(OutputVerbosity.IDS, OutputModificator.BB, OutputOrder.QT, Integer.MAX_VALUE)
                .build()
                .substring(2); // skip "; "
        final QueryResult queryResult = overpassApi.queryElements(query);

        return queryResult.getBoundingBoxes()
                .stream()
                .collect(MoreCollectors.onlyElement());
    }
}

class GroupingOverpassMapDataApi extends OverpassMapDataApi {

    public GroupingOverpassMapDataApi(@NotNull final OsmConnection osm, @NotNull final MapDataFactory mapDataFactory) {
        super(osm, mapDataFactory);
    }

    public GroupingOverpassMapDataApi(@NotNull final OsmConnection osm) {
        super(osm);
    }

    public QueryResult queryElements(final String query) {
        final GroupedMapDataHandler handler = new GroupedMapDataHandler();
        super.queryElements(query, handler);
        return handler.collectResults();
    }

    public QueryResult queryElementsWithGeometry(final String query) {
        final GroupedMapDataHandler handler = new GroupedMapDataHandler();
        super.queryElementsWithGeometry(query, handler);
        return handler.collectResults();
    }

    public void queryTable(final String query) {
        throw new UnsupportedOperationException("Grouped requests for tabular data is not supported");
    }
}

class GroupedMapDataHandler implements MapDataHandler, MapDataWithGeometryHandler {

    @Getter
    private final List<BoundingBox> boundingBoxes;
    @Getter
    private final Map<Long, Node> nodesByNodeId;
    @Getter
    private final List<Way> ways;
    @Getter
    private final List<Relation> relations;

    public GroupedMapDataHandler() {
        this.boundingBoxes = new ArrayList<>();
        this.nodesByNodeId = new LinkedHashMap<>();
        this.ways = new ArrayList<>();
        this.relations = new ArrayList<>();
    }

    public void handle(@NotNull final BoundingBox boundingBox) {
        this.boundingBoxes.add(boundingBox);
    }

    public void handle(@NotNull final Node node) {
        this.nodesByNodeId.put(node.getId(), node);
    }

    public void handle(final Way way) {
        this.ways.add(way);
    }

    public void handle(final Relation relation) {
        this.relations.add(relation);
    }

    @Override
    public void handle(
            @NotNull final Way way,
            @NotNull final BoundingBox boundingBox,
            @NotNull final List<LatLon> nodeCoordinates) {
        this.handle(way);
        this.handle(boundingBox);
        IntStream.range(0, nodeCoordinates.size())
                .mapToObj(i -> new OsmNode(way.getNodeIds().get(i), way.getVersion(), nodeCoordinates.get(i), way.getTags()))
                .forEach(this::handle);
    }

    @Override
    public void handle(
            @NotNull final Relation relation,
            @NotNull final BoundingBox boundingBox,
            @NotNull final Map<Long, LatLon> coordinatesByNodeId,
            @NotNull final Map<Long, List<LatLon>> coordinatesByWayId) {
        // no-op
    }

    public QueryResult collectResults() {
        return ImmutableQueryResult.builder()
                .boundingBoxes(this.boundingBoxes)
                .nodes(this.nodesByNodeId.values())
                .ways(this.ways)
                .relations(this.relations)
                .build();
    }
}