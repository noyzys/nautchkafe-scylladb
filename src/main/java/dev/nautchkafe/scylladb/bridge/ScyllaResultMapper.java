package dev.nautchkafe.scylladb.bridge;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import io.vavr.collection.List;

@FunctionalInterface
interface ScyllaResultMapper<T> {

    T map(final Row row);

    static <T> List<T> mapAll(final ResultSet resultSet, final ScyllaResultMapper<T> mapper) {
        return List.ofAll(resultSet).map(mapper::map);
    }
}
