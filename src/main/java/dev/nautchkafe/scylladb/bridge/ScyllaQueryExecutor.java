package dev.nautchkafe.scylladb.bridge;

import com.datastax.driver.core.ResultSet;
import io.vavr.collection.List;
import io.vavr.control.Try;

import java.util.concurrent.CompletableFuture;

final class ScyllaQueryExecutor implements SqlQueryExecutor {

    private final Session session;

    ScyllaQueryExecutor(final Session session) {
        this.session = session;
    }

    @Override
    public Try<ResultSet> executeSync(final String query, final List<Object> params) {
        return Try.of(() -> {
            var prepared = session.prepare(query);
            var bound = prepared.bind(params.toJavaArray());
            return session.execute(bound);
        });
    }

    @Override
    public CompletableFuture<ResultSet> executeAsync(final String query, final List<Object> params) {
        return CompletableFuture.supplyAsync(() -> {
            var prepared = session.prepare(query);
            var bound = prepared.bind(params.toJavaArray());
            return session.execute(bound);
        });
    }
}