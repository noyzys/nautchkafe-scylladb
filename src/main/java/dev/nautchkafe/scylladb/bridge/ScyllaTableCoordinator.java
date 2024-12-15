package dev.nautchkafe.scylladb.bridge;

final class ScyllaTableCoordinator implements ScyllaTable {

    private final SqlQueryExecutor queryExecutor;

    public ScyllaTableCoordinator(final SqlQueryExecutor queryExecutor) {
        this.queryExecutor = queryExecutor;
    }

    @Override
    public Try<Void> createTable(final String tableName, final String schema) {
        final String query = "CREATE TABLE IF NOT EXISTS " + tableName + " (" + schema + ")";
        return queryExecutor.executeSync(query, List.empty()).map(rs -> null);
    }

    @Override
    public Try<Void> dropTable(final String tableName) {
        final String query = "DROP TABLE IF EXISTS " + tableName;
        return queryExecutor.executeSync(query, List.empty()).map(rs -> null);
    }

    @Override
    public Try<Boolean> tableExists(final String tableName) {
        final String query = "SELECT table_name FROM system_schema.tables WHERE table_name = ?";
        return queryExecutor.executeSync(query, List.of(tableName))
            .map(rs -> rs.iterator().hasNext());
    }

    @Override
    public Try<Void> truncateTable(final String tableName) {
        final String query = "TRUNCATE TABLE " + tableName;
        return queryExecutor.executeSync(query, List.empty()).map(rs -> null);
    }
}