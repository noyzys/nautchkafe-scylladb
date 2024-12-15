package dev.nautchkafe.scylladb.bridge;

import io.vavr.control.Try;
import java.util.function.Consumer;
import java.util.function.Function;

final class ScyllaResource<T> {

    private final Try<T> resource;
    private final Consumer<T> closer;

    public ScyllaResource(final Function<Void, Try<T>> initializer, final Consumer<T> closer) {
        this.resource = initializer.apply(null);
        this.closer = closer;
    }

    public <R> Try<R> use(final Function<T, Try<R>> action) {
        return resource.flatMap(res -> {
            final Try<R> result = action.apply(res);
            
            closer.accept(res);
            return result;
        });
    }
}