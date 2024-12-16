package dev.nautchkafe.scylladb.bridge;

import io.vavr.Function1;
import io.vavr.Function2;
import io.vavr.collection.List;
import io.vavr.control.Option;

final class ScyllaPredicateSQL<T> {

    private final List<Function1<T, Option<String>>> predicateMappings;

    ScyllaPredicateSQL(final List<Function1<T, Option<String>>> predicateMappings) {
        this.predicateMappings = predicateMappings;
    }

    public ScyllaPredicateSQL<T> addMapping(final Function1<T, Option<String>> mapping) {
        return new ScyllaPredicateSQL<>(predicateMappings.append(mapping));
    }

    public String buildWhereClause(final T input) {
        return predicateMappings
                .map(mapping -> mapping.apply(input))
                .flatMap(Option::toList)
                .mkString(" AND ");
    }

    public static <T> ScyllaPredicateSQL<T> defaultMappings() {
        return new ScyllaPredicateSQL<>(List.empty());
    }


    public static <T, R> Function1<T, Option<String>> mapField(final Function1<T, R> fieldExtractor, final String sqlTemplate) {
        return entity -> Option.of(fieldExtractor.apply(entity))
                               .map(value -> sqlTemplate);
    }
}