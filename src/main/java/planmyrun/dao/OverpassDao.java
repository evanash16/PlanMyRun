package planmyrun.dao;

import de.westnordost.osmapi.map.data.BoundingBox;
import planmyrun.model.osm.QueryResult;

public interface OverpassDao {

    /**
     * Perform an Overpass query to load all highways in a given bounding box.
     * @param boundingBox the bounding box within which to query.
     * @return a {@link QueryResult} containing the results from the Overpass query.
     */
    QueryResult getHighwaysWithinArea(BoundingBox boundingBox);

    /**
     * Perform an Overpass query to load the bounding box for a given country, state, and city.
     * @param country the country (e.g. US)
     * @param state the state (e.g. CA)
     * @param city the city (e.g. San Luis Obispo)
     * @return the {@link BoundingBox} for the city
     */
    BoundingBox getBoundingBox(String country, String state, String city);
}
