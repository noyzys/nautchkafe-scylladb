package dev.nautchkafe.scylladb.bridge;

import io.vavr.collection.List;
import io.vavr.control.Option;
import io.vavr.control.Try;
import java.util.function.Function;
import java.util.function.Predicate
import java.util.concurrent.CompletableFuture;

interface ScyllaRepository<T> {

    Try<List<T>> findAll();

    Try<List<T>> findWithPredicate(final T input);

    Try<Option<T>> find(final Function1<T, Boolean> predicate);

    Try<Void> save(final T entity);

    Try<Void> deleteWithPredicate(final T input);

    CompletableFuture<List<T>> findAllAsync();

    CompletableFuture<List<T>> findWithPredicateAsync(final T input);

    Try<List<T>> findAllPaginated(final int limit, final int offset);

    CompletableFuture<List<T>> findAllPaginatedAsync(final int limit, final int offset);

    CompletableFuture<Void> deleteWithPredicateAsync(final T input);
}
