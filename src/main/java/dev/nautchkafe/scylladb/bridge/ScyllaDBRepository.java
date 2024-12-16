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
    private final ScyllaPredicateSQL<T> predicateToSQL;

    public ScyllaDBRepository(final QueryExecutor queryExecutor,
                               final ScyllaResultMapper<T> mapper,
                               final Function<T, List<Object>> binder,
                               final String tableName,
                               final ScyllaPredicateSQL<T> predicateToSQL) {
        this.queryExecutor = queryExecutor;
        this.mapper = mapper;
        this.binder = binder;
        this.tableName = tableName;
        this.predicateToSQL = predicateToSQL;
    }

    @Override
    public Try<List<T>> findAll() {
        final String query = ScyllaSqlConstants.SELECT_ALL.formatted(tableName);
        return queryExecutor.executeSync(query, List.empty())
                .map(rs -> ScyllaResultMapper.mapAll(rs, mapper));
    }

    @Override
    public Try<List<T>> findWithPredicate(final T input) {
        return executeWithPredicate(input, ScyllaSqlConstants.SELECT_WHERE);
    }

    @Override
    public Try<Option<T>> find(final Function<T, Boolean> predicate) {
        return findAll().map(list -> list.find(predicate));
    }

    @Override
    public Try<Void> save(final T entity) {
        final String placeholders = List.ofAll(binder.apply(entity))
            .map(value -> "?")
            .mkString(", ");
            
        final String query = ScyllaSqlConstants.INSERT.formatted(tableName, placeholders);
        return queryExecutor.executeSync(query, binder.apply(entity)).map(rs -> null);
    }

    @Override
    public Try<Void> deleteWithPredicate(final T input) {
        return executeWithPredicate(input, ScyllaSqlConstants.DELETE_WHERE).map(rs -> null);
    }

    @Override
    public CompletableFuture<List<T>> findAllAsync() {
        final String query = ScyllaSqlConstants.SELECT_ALL.formatted(tableName);
        return queryExecutor.executeAsync(query, List.empty())
                .thenApply(rs -> ScyllaResultMapper.mapAll(rs, mapper));
    }

    @Override
    public CompletableFuture<List<T>> findWithPredicateAsync(final T input) {
        return executeWithPredicateAsync(input, ScyllaSqlConstants.SELECT_WHERE);
    }

    @Override
    public CompletableFuture<Void> deleteWithPredicateAsync(final T input) {
        return executeWithPredicateAsync(input, ScyllaSqlConstants.DELETE_WHERE).thenApply(rs -> null);
    }

    @Override
    public Try<List<T>> findAllPaginated(final int limit, final int offset) {
        final String query = ScyllaSqlConstants.SELECT_PAGINATED.formatted(tableName);
        return queryExecutor.executeSync(query, List.of(limit, offset))
                .map(rs -> ScyllaResultMapper.mapAll(rs, mapper));
    }

    @Override
    public CompletableFuture<List<T>> findAllPaginatedAsync(final int limit, final int offset) {
        final String query = ScyllaSqlConstants.SELECT_PAGINATED.formatted(tableName);
        return queryExecutor.executeAsync(query, List.of(limit, offset))
                .thenApply(rs -> ScyllaResultMapper.mapAll(rs, mapper));
    }


    private Try<List<T>> executeWithPredicate(final T input, final String queryTemplate) {
        final String whereClause = predicateToSQL.buildWhereClause(input);
        final String query = queryTemplate.formatted(tableName, whereClause);
        return queryExecutor.executeSync(query, List.empty())
                .map(rs -> ScyllaResultMapper.mapAll(rs, mapper));
    }

    private CompletableFuture<List<T>> executeWithPredicateAsync(final T input, final String queryTemplate) {
        final String whereClause = predicateToSQL.buildWhereClause(input);
        final String query = queryTemplate.formatted(tableName, whereClause);
        return queryExecutor.executeAsync(query, List.empty())
                .thenApply(rs -> ScyllaResultMapper.mapAll(rs, mapper));
    }
}