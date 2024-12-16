package dev.nautchkafe.scylladb.bridge;

final class ScyllaSqlConstants {

    private ScyllaSqlConstants() {
    }

    static final String SELECT_ALL = "SELECT * FROM %s";
    static final String SELECT_PAGINATED = "SELECT * FROM %s LIMIT ? OFFSET ?";
    static final String SELECT_WHERE = "SELECT * FROM %s WHERE %s";
    static final String DELETE_WHERE = "DELETE FROM %s WHERE %s";
    static final String INSERT = "INSERT INTO %s VALUES (%s)";

}