import java.nio.file.*;
import java.util.*;
import java.io.*;

public class Main {
    public static void main(String[] args) throws Exception {
        List<ConversationMessage> messages =
            ConversationReader.readMessages("data/conversations.csv");
        List<ThreatPattern> patterns =
            ConversationReader.readPatterns("data/threat_patterns.csv");
        Map<String,List<ConversationMessage>> grouped = new LinkedHashMap<>();
        for (ConversationMessage m : messages)
            grouped.computeIfAbsent(m.conversationId, k -> new ArrayList<>()).add(m);

        ThreatAnalyzer analyzer = new ThreatAnalyzer(patterns);
        List<ThreatAnalyzer.Result> results = new ArrayList<>();
        for (var e : grouped.entrySet()) results.add(analyzer.analyze(e.getKey(), e.getValue()));

        long flagged = results.stream().filter(r -> r.score >= 10).count();
        System.out.println("CYBERSECURITY CONVERSATION THREAT DETECTION");
        System.out.println("Messages: " + messages.size() + " | Conversations: " +
            grouped.size() + " | Patterns: " + patterns.size());
        System.out.println("Flagged for review: " + flagged);
        results.stream().filter(r -> r.score > 0).limit(20).forEach(r ->
            System.out.println(r.conversationId + " | " + r.status + " | score=" +
                r.score + " | " + r.categoryCounts));
        ReportGenerator.write("reports/cybersecurity_threat_report.txt", results);
        if (!messages.isEmpty()) {
            String sample = messages.get(0).message.toLowerCase(java.util.Locale.ROOT);
            AlgorithmComparison.compare(sample, "security");
        }
        try {
            System.out.println("Parallel message count: " +
                ParallelConversationProcessor.countMessagesInParallel(messages));
        } catch (Exception ex) {
            System.out.println("Parallel demonstration skipped: " + ex.getMessage());
        }
        System.out.println("Report written to reports/cybersecurity_threat_report.txt");
    }
}
