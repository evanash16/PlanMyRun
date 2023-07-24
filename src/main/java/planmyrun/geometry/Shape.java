package planmyrun.geometry;

import planmyrun.graph.node.Node;

import java.util.List;

public interface Shape<T extends Node> {

    /**
     * Return the {@link Shape} as a ordered list of {@link T}s.
     *
     * @return the list of {@link T}s comprising the {@link Shape}.
     */
    List<T> getNodes();
}
