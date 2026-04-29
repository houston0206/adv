package edu.ttap.spellchecker;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.ArrayList;

/**
 * A spellchecker maintains an efficient representation of a dictionary for
 * the purposes of checking spelling and provided suggested corrections.
 */
public class SpellChecker {
    /** The number of letters in the alphabet. */
    private static final int NUM_LETTERS = 26;

    /** The path to the dictionary file. */
    private static final String DICT_PATH = "data/words_alpha.txt";

    /**
     * @param filename the path to the dictionary file
     * @return a SpellChecker over the words found in the given file.
     */
    public static SpellChecker fromFile(String filename) throws IOException {
        return new SpellChecker(Files.readAllLines(Paths.get(filename)));
    }

    /** A Node of the SpellChecker structure. */
    private class Node {
        char ch;
        List<Node> children;

        public Node(char ch) {
            this.ch = ch;
            this.children = new ArrayList<Node>();
        }
    }

    /** The root of the SpellChecker */
    private Node root;

    /**
     * Constructs a SpellChecker over the given dictionary.
     * @param dict the list of words to include in the dictionary
     */
    public SpellChecker(List<String> dict) {
        root = new Node(' ');
        for (String word : dict) {
            add(word);
        }
    }

    /**
     * Checks if a node's children contains a certain character
     * @param nodes the list of children
     * @param c the character to search for
     * @return the node if found, null otherwise
     */
    public Node findNode(List<Node> nodes, char c) {
        for (Node n : nodes) {
            if (n.ch == c) {
                return n;
            }
        }
        return null;
    }

    /**
     * Adds the given word to the trie
     * @param word the word to add
     */
    public void add(String word) {
        Node cur = root;
        for (int i = 0; i < word.length(); i++) {
            Node find = findNode(cur.children, word.charAt(i));
            if (find != null) {
                cur = find;
            } else {
                Node nxt = new Node(word.charAt(i));
                cur.children.add(nxt);
                cur = nxt;
            }
        }
    }

    /**
     * Checks if the given word is in the dictionary.
     * @param word the word to check
     * @return true if the word is in the dictionary, false otherwise
     */
    public boolean isWord(String word) {
        Node cur = root;
        for (int i = 0; i < word.length(); i++) {
            Node find = findNode(cur.children, word.charAt(i));
            if (find != null) {
                cur = find;
            } else {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns a list of all words in the dictionary that can be formed by
     * adding a single character to the end of the given word.
     * @param word the word to complete
     * @return a list of all possible completions
     */
    public List<String> getOneCharCompletions(String word) {
        List<String> lst = new ArrayList<>();
        Node cur = root;
        for (int i = 0; i < word.length(); i++) {
            Node find = findNode(cur.children, word.charAt(i));
            if (find != null) {
                cur = find;
            } else {
                return lst;
            }
        }
        for (Node n : cur.children) {
            String w = word + n.ch;
            lst.add(w);
        }
        return lst;
    }


    /**
     * Returns a list of all words in the dictionary that can be formed by changing
     * a single character at the end of the given word.
     * @param word the word to correct
     * @return a list of all possible corrections
     */
    public List<String> getOneCharEndCorrections(String word) {
        List<String> lst = new ArrayList<>();
        Node cur = root;
        for (int i = 0; i < word.length() - 1; i++) {
            Node find = findNode(cur.children, word.charAt(i));
            if (find != null) {
                cur = find;
            } else {
                return lst;
            }
        }
        for (Node n : cur.children) {
            String w = word + n.ch;
            lst.add(w);
            lst.remove(word.charAt(word.length()-1));
        }
        return lst;
    }

    /**
    * Returns a list of all words in the dictionary that can be formed by adding,
    * removing, or changing a single character in the given word.
    * @param word the word to correct
    * @return a list of all possible corrections
    */
    public List<String> getOneCharCorrections(String word) {
        // TODO: implement me!
        return null;
    }

    /**
     * The main entry point for the program.
     * @param args the command-line arguments
     */
    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            System.err.println("Usage: java SpellChecker <command> <word>");
            System.exit(1);
        } else {
            String command = args[0];
            String word = args[1];
            SpellChecker checker = SpellChecker.fromFile(DICT_PATH);
            switch (command) {
                case "check": {
                    System.out.println(checker.isWord(word) ? "correct" : "incorrect");
                    System.exit(0);
                }

                case "complete": {
                    List<String> completions = checker.getOneCharCompletions(word);
                    for (String completion : completions) {
                        System.out.println(completion);
                    }
                    System.exit(0);
                }

                case "correct": {
                    List<String> corrections = checker.getOneCharEndCorrections(word);
                    for (String correction : corrections) {
                        System.out.println(correction);
                    }
                    System.exit(0);
                }

                default: {
                    System.err.println("Unknown command: " + command);
                    System.exit(1);
                }
            }
        }
    }
}