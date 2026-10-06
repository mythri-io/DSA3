import java.util.*;

public class AhoCorasick {
    private static class Node {
        Map<Character, Node> next = new HashMap<>();
        Node fail;
        List<ThreatPattern> output = new ArrayList<>();
    }
    public static class Match {
        public final String pattern, category;
        public final int severity, start, end;
        Match(ThreatPattern p, int start, int end) {
            this.pattern = p.pattern; this.category = p.category;
            this.severity = p.severity; this.start = start; this.end = end;
        }
    }

    private final Node root = new Node();
    private final List<ThreatPattern> patterns;

    public AhoCorasick(List<ThreatPattern> patterns) {
        this.patterns = patterns;
        buildTrie();
        buildFailureLinks();
    }

    private void buildTrie() {
        for (ThreatPattern p : patterns) {
            if (p.pattern.isEmpty()) continue;
            Node n = root;
            for (char ch : p.pattern.toCharArray())
                n = n.next.computeIfAbsent(ch, k -> new Node());
            n.output.add(p);
        }
    }

    private void buildFailureLinks() {
        Queue<Node> q = new ArrayDeque<>();
        root.fail = root;
        for (Node child : root.next.values()) {
            child.fail = root;
            q.add(child);
        }
        while (!q.isEmpty()) {
            Node current = q.remove();
            for (Map.Entry<Character, Node> e : current.next.entrySet()) {
                char ch = e.getKey();
                Node child = e.getValue(), f = current.fail;
                while (f != root && !f.next.containsKey(ch)) f = f.fail;
                if (f.next.containsKey(ch) && f.next.get(ch) != child)
                    child.fail = f.next.get(ch);
                else child.fail = root;
                child.output.addAll(child.fail.output);
                q.add(child);
            }
        }
    }

    public List<Match> search(String text) {
        String s = text.toLowerCase(Locale.ROOT);
        List<Match> matches = new ArrayList<>();
        Node state = root;
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            while (state != root && !state.next.containsKey(ch)) state = state.fail;
            state = state.next.getOrDefault(ch, root);
            for (ThreatPattern p : state.output) {
                int end = i + 1;
                matches.add(new Match(p, end - p.pattern.length(), end));
            }
        }
        return matches;
    }
}
