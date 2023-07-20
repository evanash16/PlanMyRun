package planmyrun.model.osm;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

import javax.annotation.Nullable;
import java.util.List;

@Value.Immutable
@JsonDeserialize(as = ImmutableElement.class)
public interface Element {

    @JsonProperty("type")
    String getType();

    @JsonProperty("id")
    Long getId();

    // Type.NODE
    @JsonProperty("lat")
    @Nullable
    Double getLat();

    @JsonProperty("lon")
    @Nullable
    Double getLon();

    // Type.WAY
    @JsonProperty("nodes")
    @Nullable
    List<Long> getNodes();

    default Node toNode() {
        if (getLat() == null || getLon() == null) {
            throw new UnsupportedOperationException(String.format("Cannot convert Element of type %s to type Node", getType()));
        }
        return ImmutableNode.builder()
                .id(getId())
                .lat(getLat())
                .lon(getLon())
                .build();
    }

    default Way toWay() {
        if (getNodes() == null) {
            throw new UnsupportedOperationException(String.format("Cannot convert Element of type %s to type Way", getType()));
        }
        return ImmutableWay.builder()
                .id(getId())
                .nodes(getNodes())
                .build();
    }
}
