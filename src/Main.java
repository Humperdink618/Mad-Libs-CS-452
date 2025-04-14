// Import statements to include other classes into main
import Stories.DataAccessException;
import Stories.*;

public class Main {

    public static void main(String[] args) throws ResponseException, DataAccessException {
        // Note: this is just for testing. Delete Later.
        String myURL = "http://localhost:8080"; // placeholder. Fix later.
        MadLibsClient client = new MadLibsClient(myURL);
        client.run();

    }
}