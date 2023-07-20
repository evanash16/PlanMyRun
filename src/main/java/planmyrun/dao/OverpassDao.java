package planmyrun.dao;

import planmyrun.model.osm.QueryResult;

import java.io.InputStream;

public interface OverpassDao {

    /**
     * Read a JSON Overpass query result.
     * @param inputStream the {@link InputStream} from which to load an Overpass query result.
     * @return
     */
    QueryResult loadQueryResult(InputStream inputStream);
}
