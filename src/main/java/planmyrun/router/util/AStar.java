package planmyrun.router.util;

import lombok.experimental.UtilityClass;
import planmyrun.graph.node.Node;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.function.BiFunction;

@UtilityClass
public class AStar {

    private static <T extends Node> List<T> reconstructPath(final Map<T, T> cameFrom, final T end) {
        final List<T> totalPath = new LinkedList<>(Collections.singletonList(end));

        T currentNode = end;
        while (cameFrom.containsKey(currentNode)) {
            currentNode  = cameFrom.get(currentNode);
            totalPath.add(0, currentNode);
        }
        return totalPath;
    }

    // https://www.wikiwand.com/en/A*_search_algorithm#Pseudocode
    public static <T extends Node> List<T> findPath(T start, T goal, BiFunction<T, T, Double> h) {
        // For node n, cameFrom[n] is the node immediately preceding it on the cheapest path from the start
        // to n currently known.
        final Map<T, T> cameFrom = new HashMap<>();

        // For node n, gScore[n] is the cost of the cheapest path from start to n currently known.
        final Map<T, Double> gScore = new HashMap<>();
        gScore.put(start, 0.0);

        // For node n, fScore[n] := gScore[n] + h(n). fScore[n] represents our current best guess as to
        // how cheap a path could be from start to finish if it goes through n.
        final Map<T, Double> fScore = new HashMap<>(); // should default to Infinity
        fScore.put(start, h.apply(start, goal));

        // The set of discovered nodes that may need to be (re-)expanded.
        // Initially, only the start node is known.
        final Queue<T> openSet =
                new PriorityQueue<>(Comparator.comparingDouble(node -> fScore.getOrDefault(node, Double.MAX_VALUE)));
        openSet.add(start);

        while (!openSet.isEmpty()) {
            // the node in openSet having the lowest fScore[] value
            T current = openSet.peek();
            if (current.equals(goal)) {
                return reconstructPath(cameFrom, current);
            }

            openSet.remove(current);
            for (final T neighbor: (Collection<T>) current.getConnections()) {
                // d(current,neighbor) is the weight of the edge from current to neighbor
                // tentativeGScore is the distance from start to the neighbor through current
                double tentativeGScore = gScore.get(current) + current.distanceTo(neighbor);
                if (tentativeGScore < gScore.getOrDefault(neighbor, Double.MAX_VALUE)) {
                    // This path to neighbor is better than any previous one. Record it!
                    cameFrom.put(neighbor, current);
                    gScore.put(neighbor, tentativeGScore);
                    fScore.put(neighbor, tentativeGScore + h.apply(neighbor, goal));

                    if (!openSet.contains(neighbor)) {
                        openSet.add(neighbor);
                    }
                }
            }
        }

        // Open set is empty but goal was never reached
        return Collections.emptyList();
    }
}
