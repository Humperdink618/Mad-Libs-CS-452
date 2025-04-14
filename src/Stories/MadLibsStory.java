package Stories;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MadLibsStory {

    private String storyName;
    private List<String> words; // List to store words (verbs, adjectives, etc.)
    private List<String> storyTemplate; // List to store the story template
    private List<String> wordTypes; // List to store the expected word types to be used (verb, adjective, etc.)

    public MadLibsStory(String storyName, List<String> storyTemplate, List<String> wordTypes) {

        this.words = new ArrayList<>();
        this.storyName = storyName;
        this.storyTemplate = storyTemplate;
        this.wordTypes = wordTypes;
    }

    // Add a word to the words list
    public void addWord(String word) {
        this.words.add(word);
    }

    // Get the list of words
    public List<String> getWords() {
        return this.words;
    }

    // Get the story template
    public List<String> getStoryTemplate() {
        return this.storyTemplate;
    }

    // Get the list of wordTypes
    public List<String> getWordTypes() {
        return this.wordTypes;
    }

    protected String heading(){
        return "\n" + this + "\n---------------\n";
    }

    public String toString() {
        return this.storyName;
    }

    public String createStoryString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(heading());

        stringBuilder.append(storyTemplate.getFirst());
        for (int i = 0; i < words.size(); i++) {
            stringBuilder.append(words.get(i));
            stringBuilder.append(storyTemplate.get(i + 1));
        }

        return stringBuilder.toString();
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);
        // iterate over list of words
        for (int i = 0; i < wordTypes.size(); i++) {

            System.out.printf("pick a(n) %s: ", wordTypes.get(i));

            String input = scanner.nextLine();

            addWord(input);

        }
        // once all words are inputted, print()
        System.out.println(createStoryString());
    }
}
