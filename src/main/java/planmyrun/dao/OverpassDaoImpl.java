package planmyrun.dao;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import lombok.extern.log4j.Log4j2;
import planmyrun.model.osm.QueryResult;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;

@Log4j2
public class OverpassDaoImpl implements OverpassDao {

    private final ObjectMapper objectMapper;

    public OverpassDaoImpl() {
        objectMapper = new ObjectMapper()
                .registerModule(new GuavaModule());
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    public QueryResult loadQueryResult(final InputStream inputStream) {
        try {
            return objectMapper.readValue(inputStream, QueryResult.class);
        } catch (final IOException e) {
            log.error("Failed to read stream as QueryResult.", e);
            throw new UncheckedIOException(e);
        }
    }
}
