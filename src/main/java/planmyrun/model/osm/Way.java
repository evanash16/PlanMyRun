package planmyrun.model.osm;

import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
public interface Way {

    Long getId();

    List<Long> getNodes();
}
