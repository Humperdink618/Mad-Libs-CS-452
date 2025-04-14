package Stories;

import com.google.gson.Gson;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class SQLStoryDAO implements StoryDAO {

    public SQLStoryDAO() throws DataAccessException {
        ConfigureDatabase.configureDatabase();
    }
    // admin only
    public int createStory(String storyName, StoryObjData myStory) throws DataAccessException{
        String statement = "INSERT INTO storydata (storyName, story) VALUES (?, ?)";
        String story = new Gson().toJson(myStory);
        int storyID = executeUpdate(statement, storyName, story);
        StoryData storyData = new StoryData(storyID, storyName, myStory);
        return storyData.storyID();
    }

    public Collection<StoryData> listStories() throws DataAccessException {
        Collection<StoryData> myStories = new HashSet<>();
        try (Connection conn = DatabaseManager.getConnection()) {
            String statement = "SELECT storyID, storyName FROM storydata";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        myStories.add(readStory(rs));
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
        return myStories;
        // not storing anything in a collection in this DAO, but can still return a collection (data retrieved from
        //  database)
    }

    private StoryData readStory(ResultSet rs) throws SQLException {
        int storyID = rs.getInt("storyID");
        String storyName = rs.getString("storyName");
        return new StoryData(storyID, storyName, null);
    }

    public StoryData getStory(int storyID) throws DataAccessException {
        String statement = "SELECT storyID, storyName, story FROM storydata WHERE storyID=?";
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setInt(1, storyID);
                try (ResultSet rs = preparedStatement.executeQuery()) {
                    while (rs.next()) {
                        int myStoryID = rs.getInt("storyID");
                        String storyName = rs.getString("storyName");
                        String story = rs.getString("story");
                        StoryObjData myStory = new Gson().fromJson(story, StoryObjData.class);
                        return new StoryData(myStoryID, storyName, myStory);
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
        return null;
    }

    public void clear() throws DataAccessException {
        //  var statement = "TRUNCATE TABLE user";
        // String statement = "DELETE FROM user";
        String statement = "TRUNCATE storydata";
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(
                    String.format("Unable to update database: %s, %s", statement, e.getMessage()));
        }
    }

    // for testing purposes only
    public boolean empty() {
        String statement = "SELECT COUNT(*) FROM storydata";
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement preparedStatement = conn.prepareStatement(statement)) {
                try (ResultSet rs = preparedStatement.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt(1) == 0;
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error connecting to the Server");
        } catch (DataAccessException ex) {
            System.out.println("Unable to read data");
        }
        return false;
        // SQL COUNT method might be helpful here
    }


    private int executeUpdate(String statement, Object... params) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (int i = 0; i < params.length; i++) {
                    Object param = params[i];
                    if (param instanceof String p) {
                        ps.setString(i + 1, p);
                    }
                    else if (param instanceof Integer p) {
                        ps.setInt(i + 1, p);
                    }
                    else if (param == null) {
                        ps.setNull(i + 1, NULL);
                    }
                }
                ps.executeUpdate();

                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
                return 0;
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("unable to update database: %s, %s",
                    statement,
                    e.getMessage()));
        }
    }
}
