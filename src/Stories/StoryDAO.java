package Stories;

import java.util.Collection;

public interface StoryDAO {
    int createStory(String storyName, StoryObjData myStory) throws DataAccessException;

    Collection<StoryData> listStories() throws DataAccessException;

//    void updateStory(StoryData storyData) throws DataAccessException;

    StoryData getStory(int storyID) throws DataAccessException;

    void clear() throws DataAccessException;

    // For testing purposes
    boolean empty();
}

// old data (use if trying to make this more available to the general public)

//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.SQLException;
//
//public class StoryDAO {
//
//    private String jdbcUrl;
//
//    public StoryDAO(String jdbcUrl) {
//        this.jdbcUrl = jdbcUrl;
//    }
//    protected Connection getConnection() throws SQLException {
//        return DriverManager.getConnection(jdbcUrl);
//
//        StoryDAO dao = new StoryDAO(jdbcUrl);
//
//        try (Connection connection = dao.getConnection()) {
//            if (connection != null) {
//                System.out.println("Connection success!");
//            }
//        } catch (SQLException e) {
//            System.err.println("Connection failed: " + e.getMessage());
//        }
//    }
//}
