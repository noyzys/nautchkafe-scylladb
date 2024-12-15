package dev.nautchkafe.scylladb.bridge;

import java.util.function.Predicate;

final class ScyllaEntityService<T> implements SqlEntityService<T> {

    private final ScyllaRepository<T> repository;

    ScyllaEntityService(final ScyllaRepository<T> repository) {
        this.repository = repository;
    }

    @Override
    public Try<Void> add(final T entity) {
        return repository.save(entity);
    }

    @Override
    public Try<Option<T>> findById(final Object id) {
        return repository.findById(id);
    }

    @Override
    public Try<List<T>> getAll() {
        return repository.findAll();
    }

    @Override
    public Try<Void> update(final T entity) {
        return repository.update(entity);
    }

    @Override
    public Try<Void> deleteByPredicate(final Predicate<T> predicate) {
        return repository.delete(predicate);
    }
}