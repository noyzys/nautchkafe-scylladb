package dev.nautchkafe.scylladb.bridge;

import io.vavr.collection.List;
import io.vavr.control.Option;
import io.vavr.control.Try;

import java.util.function.Consumer
import java.util.function.Function
import java.util.function.Predicate

final class ScyllaPipelineFunctor<T> {
    
    private final List<T> data;

    private ScyllaPipelineFunctor(final List<T> data) {
        this.data = data;
    }

    public static <T> ScyllaPipelineFunctor<T> of(final List<T> data) {
        return new ScyllaPipelineFunctor<>(data);
    }

    public ScyllaPipelineFunctor<T> filter(final Predicate<T> predicate) {
        return new ScyllaPipelineFunctor<>(data.filter(predicate::test));
    }

    public <R> ScyllaPipelineFunctor<R> map(final Function<T, R> mapper) {
        return new ScyllaPipelineFunctor<>(data.map(mapper::apply));
    }

    public Option<T> findFirst(final Predicate<T> predicate) {
        return data.find(predicate::test);
    }

    public void forEach(final Consumer<T> action) {
        data.forEach(action::accept);
    }

    public List<T> collect() {
        return data;
    }
}