package dev.nautchkafe.scylladb.bridge;

import com.datastax.driver.core.ResultSet;
import io.vavr.collection.List;
import io.vavr.control.Try;

import java.util.concurrent.CompletableFuture;

interface SqlQueryExecutor {
    
    Try<ResultSet> executeSync(final String query, final List<Object> params);

    CompletableFuture<ResultSet> executeAsync(final String query, final List<Object> params);
}