package dev.nautchkafe.scylladb.bridge;

import io.vavr.collection.List;
import io.vavr.control.Option;
import io.vavr.control.Try;
import java.util.function.Function;
import java.util.function.Predicate
import java.util.concurrent.CompletableFuture;

interface ScyllaRepository<T> {

    Try<List<T>> findAll();

    Try<Option<T>> find(final Predicate<T> predicate);

    Try<Option<T>> findById(final Object id);

    Try<Void> save(final T entity);

    Try<Void> update(final T entity);

    Try<Void> delete(final Predicate<T> predicate);

    
    CompletableFuture<List<T>> findAllAsync();
    
    CompletableFuture<Option<T>> findAsync(final Function<T, Boolean> predicate);
    
    CompletableFuture<Void> saveAsync(final T entity);
    
    CompletableFuture<Void> deleteAsync(final Function<T, Boolean> predicate);
}
