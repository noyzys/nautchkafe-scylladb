package dev.nautchkafe.scylladb.bridge;

import com.datastax.driver.core.Session;
import io.vavr.control.Try;

interface ScyllaSessionConnection {

    Try<ScyllaResource<Session>> openSession(final String contactPoint, final String keyspace);

    Try<Void> closeSession(final Session session);
}