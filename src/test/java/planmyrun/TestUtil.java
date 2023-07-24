package planmyrun;

import planmyrun.graph.Graph;
import planmyrun.graph.node.EarthNode;
import planmyrun.util.GraphUtil;

import java.io.IOException;
import java.io.InputStream;

public class TestUtil {

    public static Graph<EarthNode> loadTestGraph(final String graph) throws IOException {
        try (InputStream inputStream = TestUtil.class.getClassLoader().getResourceAsStream(graph)) {
            return GraphUtil.buildGraphFromOSM(inputStream);
        }
    }
}
