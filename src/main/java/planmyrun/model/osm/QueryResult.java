package planmyrun.model.osm;

import de.westnordost.osmapi.map.data.BoundingBox;
import de.westnordost.osmapi.map.data.Node;
import de.westnordost.osmapi.map.data.Relation;
import de.westnordost.osmapi.map.data.Way;
import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
public interface QueryResult {
    List<BoundingBox> getBoundingBoxes();
    List<Node> getNodes();
    List<Way> getWays();
    List<Relation> getRelations();
}
