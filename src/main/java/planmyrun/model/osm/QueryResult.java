package planmyrun.model.osm;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
@JsonDeserialize(as = ImmutableQueryResult.class)
public interface QueryResult {

    @JsonProperty("elements")
    List<Element> getElements();
}
