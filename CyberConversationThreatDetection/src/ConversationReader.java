import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

public class ConversationReader {
    public static List<ConversationMessage> readMessages(String path) throws IOException {
        List<ConversationMessage> out = new ArrayList<>();
        try (BufferedReader br = Files.newBufferedReader(Path.of(path), StandardCharsets.UTF_8)) {
            String line = br.readLine(); // header
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;
                List<String> c = Csv.parseLine(line);
                if (c.size() < 6) continue;
                out.add(new ConversationMessage(c.get(0), c.get(1), c.get(2),
                    c.get(3), c.get(4), c.get(5)));
            }
        }
        return out;
    }

    public static List<ThreatPattern> readPatterns(String path) throws IOException {
        List<ThreatPattern> out = new ArrayList<>();
        try (BufferedReader br = Files.newBufferedReader(Path.of(path), StandardCharsets.UTF_8)) {
            String line = br.readLine();
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;
                List<String> c = Csv.parseLine(line);
                if (c.size() >= 3) {
                    try { out.add(new ThreatPattern(c.get(0), c.get(1), Integer.parseInt(c.get(2)))); }
                    catch (NumberFormatException ignored) {}
                }
            }
        }
        return out;
    }
}
