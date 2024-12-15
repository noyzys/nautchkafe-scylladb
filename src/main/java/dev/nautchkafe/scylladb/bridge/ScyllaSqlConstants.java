package dev.nautchkafe.scylladb.bridge;

final class ScyllaSqlConstants {

    private ScyllaSqlConstants() {
    }

    static final String SELECT_ALL = 
    """
    SELECT * FROM %s
    """;

    static final String INSERT = 
    """
    INSERT INTO %s VALUES (%s)
    """;

    static final String DELETE_BY_ID = 
    """
    DELETE FROM %s WHERE id = ?
    """;
}