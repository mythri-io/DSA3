import java.util.*;

public class ThreatAnalyzer {
    public static class Result {
        public String conversationId, status;
        public int score;
        public Map<String,Integer> categoryCounts = new TreeMap<>();
        public List<AhoCorasick.Match> matches = new ArrayList<>();
        public int messageCount;
    }
    private final AhoCorasick matcher;
    public ThreatAnalyzer(List<ThreatPattern> patterns) { matcher = new AhoCorasick(patterns); }

    public Result analyze(String id, List<ConversationMessage> messages) {
        Result r = new Result();
        r.conversationId = id; r.messageCount = messages.size();
        for (ConversationMessage m : messages) {
            List<AhoCorasick.Match> found = matcher.search(m.message);
            r.matches.addAll(found);
            for (AhoCorasick.Match hit : found) {
                r.categoryCounts.merge(hit.category, 1, Integer::sum);
                r.score += hit.severity;
            }
        }
        // Demonstration review score; tune and validate before any real deployment.
        r.score += Math.max(0, r.categoryCounts.size() - 1) * 2;
        r.status = r.score >= 10 ? "FLAGGED FOR REVIEW" :
                   r.score > 0 ? "INDICATORS FOUND" : "NORMAL";
        return r;
    }
}
