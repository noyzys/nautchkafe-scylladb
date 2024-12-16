package dev.nautchkafe.scylladb.bridge;

public final class ScyllaTableCoordinator implements ScyllaTable {

    private final SqlQueryExecutor queryExecutor;

    public ScyllaTableCoordinator(final SqlQueryExecutor queryExecutor) {
        this.queryExecutor = queryExecutor;
    }

    @Override
    public Try<Void> createTable(final String tableName, final String schema) {
        final String query = ScyllaSqlConstants.CREATE_TABLE.formatted(tableName, schema);
        return queryExecutor.executeSync(query, List.empty()).map(rs -> null);
    }

    @Override
    public Try<Void> dropTable(final String tableName) {
        final String query = ScyllaSqlConstants.DROP_TABLE.formatted(tableName);
        return queryExecutor.executeSync(query, List.empty()).map(rs -> null);
    }

    @Override
    public Try<Boolean> tableExists(final String tableName) {
        final String query = ScyllaSqlConstants.TABLE_EXISTS;
        return queryExecutor.executeSync(query, List.of(tableName))
                .map(rs -> rs.iterator().hasNext());
    }

    @Override
    public Try<Void> truncateTable(final String tableName) {
        final String query = ScyllaSqlConstants.TRUNCATE_TABLE.formatted(tableName);
        return queryExecutor.executeSync(query, List.empty()).map(rs -> null);
    }
}