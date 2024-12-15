package dev.nautchkafe.scylladb.bridge;

interface ScyllaTable {

    Try<Void> createTable(final String tableName, final String schema);

    Try<Void> dropTable(final String tableName);

    Try<Boolean> tableExists(final String tableName);

    Try<Void> truncateTable(final String tableName);

}