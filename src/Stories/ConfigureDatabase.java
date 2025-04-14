package Stories;

import java.sql.SQLException;

public class ConfigureDatabase {

    private static final String[] CREATE_STATEMENTS = {
//            """
//            CREATE TABLE IF NOT EXISTS user (
//              username VARCHAR(100) NOT NULL,
//              password VARCHAR(100) NOT NULL,
//              email VARCHAR(100) NOT NULL,
//              PRIMARY KEY (username)
//            )
//            """,
//            """
//            CREATE TABLE IF NOT EXISTS authdata (
//              authToken VARCHAR(100) NOT NULL,
//              username VARCHAR(100) NOT NULL,
//              PRIMARY KEY (authToken)
//            )
//            """,

            """
            CREATE TABLE IF NOT EXISTS storydata (
              storyID INT(100) NOT NULL AUTO_INCREMENT,
              storyName VARCHAR(100) NOT NULL,
              story LONGTEXT NOT NULL,
              PRIMARY KEY (storyID)
            )
            """
    };

    static void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : CREATE_STATEMENTS) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }
}
