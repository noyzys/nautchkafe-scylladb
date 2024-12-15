package dev.nautchkafe.scylladb.bridge;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import io.vavr.control.Try;

final class ScyllaSessionCoordinator implements ScyllaSessionConnection {

    @Override
    public Try<ScyllaResource<Session>> openSession(final String contactPoint, final String keyspace) {
        return Try.of(() -> {
            final Cluster cluster = Cluster.builder().addContactPoint(contactPoint).build();
            final Session session = cluster.connect(keyspace);
            return new ScyllaResource<>(() -> Try.success(session), s -> s.close());
        });
    }

    @Override
    public Try<Void> closeSession(final Session session) {
        return Try.run(() -> session.close());
    }
}
