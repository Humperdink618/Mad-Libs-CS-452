package Stories;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

import static Stories.MadLibsClient.*;

public class AdminOnly {
    private final MadLibsClient client;
    private static final String PASSWORD;

    public AdminOnly(MadLibsClient client){
        //Main Main;
        MadLibsClient MadLibsClient;
        this.client = client;}

    /*
     * Load the database information for the db.properties file.
     */
    static {
        try {
            try (InputStream propStream =
                         Thread.currentThread().getContextClassLoader().getResourceAsStream("admin.properties")) {
                if (propStream == null) {
                    throw new Exception("Unable to load admin.properties");
                }
                Properties props = new Properties();
                props.load(propStream);
                PASSWORD = props.getProperty("admin.password");
            }
        } catch (Exception ex) {
            throw new RuntimeException("unable to process admin.properties. " + ex.getMessage());
        }
    }

    protected static boolean validateAdmin(String input) {
        // is it a number?
        if (input.equals(PASSWORD)){
            return true;
        } else {
            return false;
        }
    }
    protected static Boolean run_Admin(StoryDAO storyDAO, Scanner scanner) {
        // may also add a delete at some point too
        while (true) {
            System.out.println("\nChoose an option (Options, Exit, Help, or Create):\n");
            String input = scanner.nextLine();
            if (input.equalsIgnoreCase("options")) {
                System.out.println("Exit, Create, Options, Help");
                // again, may or may not include a delete option
            } else if (input.equalsIgnoreCase("help")) {
                System.out.println("Exit: return to main gameplay menu.");
                System.out.println("Create: create a new Mad Libs story template in the database (admin only).");
                System.out.println("Options: display the possible options you can do here.");
                System.out.println("Help: if you picked this option, then I think the above speaks for itself.");
                // again, may or may not include a delete option

            } else if (input.equalsIgnoreCase("exit")) {
                break;
            } else if (input.equalsIgnoreCase("create")) {
                List<String> wordTypes = new ArrayList<>();
                boolean isNotDoneWritingPrompts = true;
                while (isNotDoneWritingPrompts) {
                    System.out.printf("add a word type for the story (i.e., noun, verb, adj, etc.): ");
                    String input4 = scanner.nextLine();
                    if (input4.equalsIgnoreCase("\\r\\n\\r\\n")) {
                        isNotDoneWritingPrompts = false;
                    } else {
                        wordTypes.add(input4);
                    }
                }
                List<String> storyTemplate = new ArrayList<>();
                boolean isNotDoneWritingLines = true;
                while (isNotDoneWritingLines) {
                    System.out.printf("add a line for the story: ");
                    String input3 = scanner.nextLine();
                    String newLine = "<newLine>";
                    String newLine_replace = "\n";
                    if (input3.equalsIgnoreCase("\\r\\n\\r\\n")) {
                        isNotDoneWritingLines = false;
                    } else if (input3.contains(newLine)) {
                        int index = input3.indexOf(newLine);
                        String before = input3.substring(0, index);
                        String after = input3.substring(index + newLine.length());
                        String newString = before + newLine_replace + after;
                        storyTemplate.add(newString);
//                        storyTemplate.add("\n");
                    } else {
                        storyTemplate.add(input3);
                    }
                }

                String createPrompt = "Give the story a name: ";
                System.out.printf(createPrompt);
                String storyName = scanner.nextLine();
    //                String storyName = input5;
                while (storyName.isBlank()) {
                    storyName = getInputAgainBecauseInvalid(scanner, createPrompt);
                }
                StoryObjData myStory = new StoryObjData(wordTypes, storyTemplate);
                try {
                    int storyID = storyDAO.createStory(storyName, myStory);
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(storyID);

                    checkIfStorySuccessfullyCreated(stringBuilder.toString());
//                    Integer.toString(storyID)
                } catch (DataAccessException ex) {
                    System.out.println("Error: Unable to create Story");
                    break;
                }
            }
        }
        return false;
    }

    protected static void checkIfStorySuccessfullyCreated(String storyID) {
        if(!isNumeric(storyID)){
            System.out.println("Error: Failed to create game due to poor user input.");

        } else {
            System.out.println("Story successfully created!");
        }

    }
}
