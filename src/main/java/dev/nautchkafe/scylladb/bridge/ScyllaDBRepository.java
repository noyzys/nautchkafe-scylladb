package dev.nautchkafe.scylladb.bridge;

import io.vavr.collection.List;
import io.vavr.control.Option;
import io.vavr.control.Try;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

final class ScyllaDBRepository<T> implements ScyllaRepository<T> {
    
    private final QueryExecutor queryExecutor;
    private final ScyllaResultMapper<T> mapper;
    private final Function<T, List<Object>> binder;
    private final String tableName;

    public ScyllaDBRepository(final QueryExecutor queryExecutor,
                             final ScyllaResultMapper<T> mapper,
                             final Function<T, List<Object>> binder,
                             final String tableName) {
        this.queryExecutor = queryExecutor;
        this.mapper = mapper;
        this.binder = binder;
        this.tableName = tableName;
    }

    @Override
    public Try<List<T>> findAll() {
        final String query = ScyllaSqlConstants.SELECT_ALL.formatted(tableName);
        return queryExecutor.executeSync(query, List.empty())
                .map(rs -> ScyllaResultMapper.mapAll(rs, configuration.getMapper()));
    }

    @Override
    public Try<Option<T>> find(final Function<T, Boolean> predicate) {
        return findAll().map(list -> list.find(predicate));
    }

    @Override
    public Try<Void> save(final T entity) {
        final String placeholders = configuration.toBindValues().apply(entity)
                .map(value -> "?")
                .mkString(", ");
        
        final String query = ScyllaSqlConstants.INSERT.formatted(tableName, placeholders);
        return queryExecutor.executeSync(query, configuration.toBindValues().apply(entity)).map(rs -> null);
    }

    @Override
    public Try<Void> delete(final Function<T, Boolean> predicate) {
        return find(predicate)
                .flatMap(opt -> opt.map(entity -> {

            final String query = ScyllaSqlConstants.DELETE_BY_ID.formatted(tableName);
            return queryExecutor.executeSync(query, List.of(configuration.getPrimaryKey().apply(entity)));
        }).getOrElse(Try.success(null)));
    }

    @Override
    public CompletableFuture<List<T>> findAllAsync() {
        final String query = ScyllaSqlConstants.SELECT_ALL.formatted(tableName);
        return queryExecutor.executeAsync(query, List.empty())
                .thenApply(rs -> ScyllaResultMapper.mapAll(rs, configuration.getMapper()));
    }

    @Override
    public CompletableFuture<Option<T>> findAsync(final Function<T, Boolean> predicate) {
        return findAllAsync().thenApply(list -> list.find(predicate));
    }

    @Override
    public CompletableFuture<Void> saveAsync(final T entity) {
        final String placeholders = configuration.toBindValues().apply(entity)
                .map(value -> "?")
                .mkString(", ");
        
        final String query = ScyllaSqlConstants.INSERT.formatted(tableName, placeholders);
        return queryExecutor.executeAsync(query, configuration.toBindValues().apply(entity)).thenApply(rs -> null);
    }

    @Override
    public CompletableFuture<Void> deleteAsync(final Function<T, Boolean> predicate) {
        return findAsync(predicate).thenCompose(opt -> opt.map(entity -> {
            
            final String query = ScyllaSqlConstants.DELETE_BY_ID.formatted(tableName);
            return queryExecutor.executeAsync(query, List.of(configuration.getPrimaryKey().apply(entity))).thenApply(rs -> null);
        }).orElse(CompletableFuture.completedFuture(null)));
    }
}