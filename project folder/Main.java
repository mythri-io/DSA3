import java.io.*;
import java.util.*;

class Node {
    HashMap<Character, Integer> children = new HashMap<>();
    int fail = 0;
    ArrayList<String> output = new ArrayList<>();
}

public class Main {

    static ArrayList<Node> trie = new ArrayList<>();

    static void buildTrie(String[] keywords) {

        trie.add(new Node());

        for (String keyword : keywords) {
            int current = 0;

            for (char ch : keyword.toLowerCase().toCharArray()) {

                if (!trie.get(current).children.containsKey(ch)) {
                    trie.get(current).children.put(ch, trie.size());
                    trie.add(new Node());
                }

                current = trie.get(current).children.get(ch);
            }

            trie.get(current).output.add(keyword);
        }
    }

    static void buildFailureLinks() {

        Queue<Integer> queue = new LinkedList<>();

        for (int child : trie.get(0).children.values()) {
            trie.get(child).fail = 0;
            queue.add(child);
        }

        while (!queue.isEmpty()) {

            int current = queue.remove();

            for (char ch : trie.get(current).children.keySet()) {

                int child = trie.get(current).children.get(ch);
                queue.add(child);

                int failure = trie.get(current).fail;

                while (failure != 0 &&
                       !trie.get(failure).children.containsKey(ch)) {

                    failure = trie.get(failure).fail;
                }

                if (trie.get(failure).children.containsKey(ch)) {
                    trie.get(child).fail =
                            trie.get(failure).children.get(ch);
                } else {
                    trie.get(child).fail = 0;
                }

                trie.get(child).output.addAll(
                    trie.get(trie.get(child).fail).output
                );
            }
        }
    }

    static void searchLog(String[] lines, String[] keywords) {

        HashMap<String, ArrayList<Integer>> results =
                new HashMap<>();

        for (String keyword : keywords) {
            results.put(keyword, new ArrayList<>());
        }

        int state = 0;

        for (int lineNumber = 0;
             lineNumber < lines.length;
             lineNumber++) {

            String line = lines[lineNumber].toLowerCase();

            for (char ch : line.toCharArray()) {

                while (state != 0 &&
                       !trie.get(state).children.containsKey(ch)) {

                    state = trie.get(state).fail;
                }

                if (trie.get(state).children.containsKey(ch)) {
                    state = trie.get(state).children.get(ch);
                } else {
                    state = 0;
                }

                for (String keyword :
                        trie.get(state).output) {

                    results.get(keyword)
                           .add(lineNumber + 1);
                }
            }
        }

        System.out.println("\n========== DETECTION RESULTS ==========");

        for (String keyword : keywords) {

            ArrayList<Integer> positions =
                    results.get(keyword);

            System.out.println("\nKeyword: " + keyword);
            System.out.println("Count: " + positions.size());

            if (!positions.isEmpty()) {
                System.out.println(
                    "Found in log lines: " + positions
                );
            } else {
                System.out.println("Not found");
            }
        }
    }

    public static void main(String[] args) {

        String[] keywords = {
            "malware",
            "phishing",
            "ransomware",
            "SQL injection",
            "brute force",
            "unauthorized access"
        };

        System.out.println("======================================");
        System.out.println("   CYBERSHIELD THREAT DETECTION");
        System.out.println("======================================");

        try {

            BufferedReader reader =
                new BufferedReader(
                    new FileReader("sample_log.txt")
                );

            ArrayList<String> logLines =
                    new ArrayList<>();

            String line;

            while ((line = reader.readLine()) != null) {
                logLines.add(line);
            }

            reader.close();

            String[] lines =
                    logLines.toArray(new String[0]);

            System.out.println("\nBuilding Trie...");
            buildTrie(keywords);

            System.out.println(
                "Building Failure Links using BFS..."
            );

            buildFailureLinks();

            System.out.println(
                "Searching Security Log..."
            );

            searchLog(lines, keywords);

        } catch (IOException e) {

            System.out.println(
                "Error reading sample_log.txt"
            );
        }
    }
}