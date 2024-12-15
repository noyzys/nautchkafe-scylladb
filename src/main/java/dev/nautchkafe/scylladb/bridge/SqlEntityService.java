package dev.nautchkafe.scylladb.bridge;

import java.util.function.Predicate;

interface ScyllaEntityService<T> {

    Try<Void> add(final T entity);

    Try<Option<T>> findById(final Object id);

    Try<List<T>> getAll();

    Try<Void> update(final T entity);

    Try<Void> deleteByPredicate(final Predicate<T> predicate);
}
