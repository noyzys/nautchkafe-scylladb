package dev.nautchkafe.scylladb.bridge;

import io.vavr.control.Validation;
import io.vavr.control.Try;

final class ScyllaValidator {

    private ScyllaValidator() {
    }

    public static Validation<String, T> validateEntity(final T entity) {
        if (entity == null) {
            return Validation.invalid("Entity cannot be null.");
        }

        return Validation.valid(entity);
    }

    public static <T> Try<T> validateAndExecute(final T entity, final Function<T, Try<T>> operation) {
        return validateEntity(entity)
                .map(validEntity -> operation.apply(validEntity))
                .getOrElse(() -> Try.failure(new IllegalArgumentException("Validation failed")));
    }
}
