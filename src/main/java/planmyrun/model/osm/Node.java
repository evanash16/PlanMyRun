package planmyrun.model.osm;

import org.immutables.value.Value;

@Value.Immutable
public interface Node {

    Long getId();

    Double getLat();

    Double getLon();
}
