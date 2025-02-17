package dev.nautchkafe.scylladb.bridge;

import io.vavr.collection.List;
import io.vavr.control.Option;
import io.vavr.control.Try;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public final class ScyllaDBRepository<T> implements ScyllaRepository<T> {

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
        return ScyllaValidator.validateAndExecute(tableName, validTable -> {
            final String query = ScyllaSqlConstants.SELECT_ALL.formatted(validTable);
            return queryExecutor.executeSync(query, List.empty())
                .map(rs -> ScyllaResultMapper.mapAll(rs, mapper));
        });
    }

    @Override
    public CompletableFuture<List<T>> findAllAsync() {
        final String query = ScyllaSqlConstants.SELECT_ALL.formatted(tableName);
        return ScyllaValidator.validateAndExecute(tableName, validEntity -> queryExecutor.executeAsync(query, List.empty())
                .thenApply(rs -> ScyllaResultMapper.mapAll(rs, mapper)));
    }

    @Override
    public CompletableFuture<List<T>> findWithPredicateAsync(final T input) {
        return ScyllaValidator.validateAndExecute(input, validInput -> executeWithPredicateAsync(validInput, ScyllaSqlConstants.SELECT_WHERE));
    }

    @Override
    public Try<List<T>> findAllPaginated(final int limit, final int offset) {
        final String query = ScyllaSqlConstants.SELECT_PAGINATED.formatted(tableName);
        return ScyllaValidator.validateAndExecute(tableName, validTable -> queryExecutor.executeSync(query, List.of(limit, offset))
                .map(rs -> ScyllaResultMapper.mapAll(rs, mapper)));
    }

    @Override
    public CompletableFuture<List<T>> findAllPaginatedAsync(final int limit, final int offset) {
        final String query = ScyllaSqlConstants.SELECT_PAGINATED.formatted(tableName);
        return ScyllaValidator.validateAndExecute(tableName, validTable -> queryExecutor.executeAsync(query, List.of(limit, offset))
                .thenApply(rs -> ScyllaResultMapper.mapAll(rs, mapper)));
    }

    @Override
    public CompletableFuture<Void> deleteWithPredicateAsync(final T input) {
        return ScyllaValidator.validateAndExecute(input, validInput -> executeWithPredicateAsync(validInput, ScyllaSqlConstants.DELETE_WHERE)
                .thenApply(rs -> null));
    }

    @Override
    public Try<Void> deleteWithPredicate(final T input) {
        return ScyllaValidator.validateAndExecute(input, validInput -> executeWithPredicate(validInput, ScyllaSqlConstants.DELETE_WHERE).map(rs -> null));
    }

    private Try<List<T>> executeWithPredicate(final T input, final String queryTemplate) {
        final String whereClause = predicateToSQL.buildWhereClause(input);
        final String query = queryTemplate.formatted(tableName, whereClause);
        return ScyllaValidator.validateAndExecute(input, validInput -> queryExecutor.executeSync(query, List.empty())
                .map(rs -> ScyllaResultMapper.mapAll(rs, mapper)));
    }

    private CompletableFuture<List<T>> executeWithPredicateAsync(final T input, final String queryTemplate) {
        final String whereClause = predicateToSQL.buildWhereClause(input);
        final String query = queryTemplate.formatted(tableName, whereClause);
        return ScyllaValidator.validateAndExecute(input, validInput -> queryExecutor.executeAsync(query, List.empty())
                .thenApply(rs -> ScyllaResultMapper.mapAll(rs, mapper)));
    }
}

