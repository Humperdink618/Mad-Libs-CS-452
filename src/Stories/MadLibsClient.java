package Stories;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.*;

public class MadLibsClient {

    private StoryDAO storyDAO;
    private Collection<Integer> storyIDs = new HashSet<>();

    private Collection<StoryData> storyList = new ArrayList<>();
    private Scanner scanner = new Scanner(System.in);
    private final String serverURL;

    // Old data: may use some of these if I decide to make this more public:

    // private String auth = null;
    // private Collection<StoryData> storyDataList = new HashSet<>();
    // private final ServerFacade serverFacade;
//    private Boolean isLoggedIn = false;
//    private Boolean isPlayingGame = false;
//    private Collection<Integer> gameIDs = new HashSet<>();
//    private Collection<GameData> gameDataList = new HashSet<>();


    public MadLibsClient(String serverURL) {
        //serverFacade = new ServerFacade(serverURL);
        this.serverURL = serverURL;
        try {
            // userDAO, authDAO, and gameDAO are not currently implemented, but I have commented out
            //  code here just in case I want to make this Mad Libs game more accessible to the
            //  general public.

//            userDAO = new SQLUserDAO();
//            // userDAO = new MemoryUserDAO();
//            authDAO = new SQLAuthDAO();
//            // authDAO = new MemoryAuthDAO();
//            gameDAO = new SQLGameDAO();
            // gameDAO = new MemoryGameDAO();
            storyDAO = new SQLStoryDAO();

        } catch (DataAccessException ex) {
            System.out.printf("Error creating database: %s", ex.getMessage());
        }
    }

    public void run() throws ResponseException, DataAccessException {

        while (true) {

            //  TODO : things to fix:
            //   optional: create an option that chooses a random story based on Random
            //   or perhaps make this more accessible to the general public by creating a server, service,
            //   handlers, websocket, additional DAOs, and account for logging in/registering users and
            //   implementing user authentication. May also perhaps add multiplayer functionality later on
            //   in the future. Use CS 240 code as a reference for this, if I decide to do it.

            System.out.println("\nWelcome to Mad Libs! Choose an option (Options, Exit, or Story):\n");
            String input = scanner.nextLine();
            // STEP 3:
            if (input.equalsIgnoreCase("exit")) {
                break;
            } else if (input.equalsIgnoreCase("story")) {
                try {
                    listStories();
                } catch (ResponseException ex) {
                    System.out.println(ex.getMessage());
                }
                String storyID = getInputStoryID(scanner, storyIDs);
                Integer newID = 0;
                newID = getNewStoryID(storyID, newID, storyIDs, storyList);
                StoryData storyData = storyDAO.getStory(newID);
                if (storyData == null) {
                    throw new DataAccessException("Error: bad request");
                }
                playStory(storyData);

            } else if (input.equalsIgnoreCase("options")) {
                System.out.println("Exit, Story, Options");
            } else if (input.equalsIgnoreCase("admin")) {
                System.out.println("\nplease validate credentials: ");
                String input1 = scanner.nextLine();
                if (AdminOnly.validateAdmin(input1)) {
                    System.out.println("\nWelcome Admin!");
                    // may also add a "delete" at some point too
                    while (true) {
                        if (!AdminOnly.run_Admin(storyDAO, scanner)) {
                            break;
                        }
                    }
                }

            } else {
                System.out.println("Invalid option. Try again");
            }
        }
    }


    protected static Integer getNewStoryID(String storyID, Integer newID,
                                           Collection<Integer> storyIDs, Collection<StoryData> storyList) {
        for (int id : storyIDs) {
            if (Integer.parseInt(storyID) == id) {
                newID = getStoryIDFromStoryList(id, storyID, storyList);
                if (newID != 0) {
                    break;
                }
            }
        }
        return newID;
    }

    protected static Integer getStoryIDFromStoryList(int id, String storyID, Collection<StoryData> storyList) {
        for (StoryData story : storyList) {
            if (Integer.parseInt(storyID) == story.storyID()) {
                return id;
            }
        }
        return 0;
    }

    protected static String getStoryIDAgainIfInvalid(String storyID, Scanner scanner) {
        while (storyID.isBlank() || !isNumeric(storyID)) {
            storyID = getInputAgainBecauseInvalid(scanner, "Pick a story: ");
        }
        return storyID;
    }


    public static boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    protected static String getInputString(String prompt, Scanner scanner) {
        String inputString;
        System.out.println(prompt);
        inputString = scanner.nextLine();
        return inputString;
    }

    protected static String getInputStoryID(Scanner scanner, Collection<Integer> storyIDs) {
        String storyID = getInputString("Pick a story: ", scanner);
        storyID = getStoryIDAgainIfInvalid(storyID, scanner);
        storyID = checkIfValidStoryID(storyID, "Pick a story: ", storyIDs, scanner);
        return storyID;
    }

    private void listStories() throws ResponseException {
        System.out.println("Here are all the available stories: ");
        HashMap<Integer, String> storyMap = new HashMap<>();
        try {
            Collection<StoryData> storyDataList = storyDAO.listStories();
            for (StoryData storyData : storyDataList) {
                StringBuilder individualStoryData = new StringBuilder();
                individualStoryData.append(" " + storyData.storyName());
                storyIDs.add(storyData.storyID());
                this.storyList.add(storyData);
                storyMap.put(storyData.storyID(), individualStoryData.toString());
            }

            StringBuilder result = new StringBuilder();
            for (int i = 0; i < storyMap.size(); i++) {
                result.append(i + 1 + ". " + storyMap.get(i + 1)).append('\n');
            }
            System.out.println(result);
        } catch (DataAccessException ex) {
            System.out.println(ex.getMessage());
        }
    }

    protected static String getInputStringAgainBool(boolean isBlank, String x, String inputString, Scanner scanner) {
        while (isBlank) {
            inputString = getInputAgainBecauseInvalid(scanner, x);
        }
        return inputString;
    }

    protected static String getInputAgainBecauseInvalid(Scanner scanner, String prompt) {
        promptAgainBecauseInvalid("invalid input. Try again", prompt);
        String inputString;
        inputString = scanner.nextLine();
        return inputString;
    }

    protected static void promptAgainBecauseInvalid(String x, String prompt) {
        System.out.println(x);
        System.out.println(prompt);
    }

    protected static String checkIfValidStoryID(String storyID, String prompt, Collection<Integer> storyIDs,
                                                Scanner scanner) {
        storyID = getInputStringAgainBool(!storyIDs.contains(Integer.parseInt(storyID)) ||
                !isNumeric(storyID), prompt, storyID, scanner);
        return storyID;
    }


    private static void playStory(StoryData storyData) {

        if (storyData == null) {
            return;
        }
        StoryObjData storyObjData = storyData.story();
        List<String> wordTypes = storyObjData.wordTypes();
        List<String> storyTemplate = storyObjData.storyTemplate();

        String storyName = storyData.storyName();
        MadLibsStory story = new MadLibsStory(storyName, storyTemplate, wordTypes);
        story.run();

        Scanner scanner2 = new Scanner(System.in);

        while (true) {
            System.out.println("\nDo you wish to save your story? Note: this will overwrite the previous save. (Y/N)\n");
            String input2 = scanner2.nextLine();
            if (input2.equalsIgnoreCase("Y")) {
                // save to file

                String name = story.toString();
                try (PrintStream out = new PrintStream(new FileOutputStream(name + ".txt"))) {

                    out.print(story.createStoryString());

                } catch (FileNotFoundException e) {
                    // TODO handle
                    System.out.println("File not found exception");
                }
                return;
            } else if (input2.equalsIgnoreCase("N")) {
                return;
            } else {
                System.out.println("invalid option.");
            }
        }
    }
}


