import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
public class ReportGenerator {
    public static void write(String path, List<ThreatAnalyzer.Result> results) throws IOException {
        Path p=Path.of(path); if(p.getParent()!=null) Files.createDirectories(p.getParent());
        int flagged=0, totalMatches=0;
        try(BufferedWriter w=Files.newBufferedWriter(p, StandardCharsets.UTF_8)){
            w.write("CYBERSECURITY CONVERSATION ANALYSIS REPORT\n");
            w.write("==========================================\n\n");
            for(ThreatAnalyzer.Result r:results){
                if(r.score>=10) flagged++;
                totalMatches+=r.matches.size();
                w.write(r.conversationId+" | "+r.status+" | score="+r.score+
                    " | messages="+r.messageCount+" | categories="+r.categoryCounts+"\n");
            }
            w.write("\nSummary: conversations="+results.size()+", flagged="+flagged+
                    ", pattern matches="+totalMatches+"\n");
            w.write("Reminder: keyword matches are indicators, not proof of malicious intent.\n");
        }
    }
}
