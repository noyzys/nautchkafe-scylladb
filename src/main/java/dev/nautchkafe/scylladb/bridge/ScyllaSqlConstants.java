package dev.nautchkafe.scylladb.bridge;

final class ScyllaSqlConstants {

    private ScyllaSqlConstants() {
    }

    static final String SELECT_ALL = "SELECT * FROM %s";
    static final String SELECT_PAGINATED = "SELECT * FROM %s LIMIT ? OFFSET ?";
    static final String SELECT_WHERE = "SELECT * FROM %s WHERE %s";
    static final String DELETE_WHERE = "DELETE FROM %s WHERE %s";
    static final String INSERT = "INSERT INTO %s VALUES (%s)";

    static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS %s (%s)";
    static final String DROP_TABLE = "DROP TABLE IF EXISTS %s";
    static final String TABLE_EXISTS = "SELECT table_name FROM system_schema.tables WHERE table_name = ?";
    static final String TRUNCATE_TABLE = "TRUNCATE TABLE %s";
}