import com.sun.net.httpserver.*;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

public class WebServer {
    static List<ThreatPattern> patterns;
    static List<ConversationMessage> messages;
    static ThreatAnalyzer analyzer;

    public static void main(String[] args) throws Exception {
        patterns = ConversationReader.readPatterns("data/threat_patterns.csv");
        messages = ConversationReader.readMessages("data/conversations.csv");
        analyzer = new ThreatAnalyzer(patterns);
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/api/summary", ex -> {
            String json = summaryJson();
            send(ex, 200, "application/json; charset=utf-8", json);
        });
        server.createContext("/api/analyze", ex -> {
            if (!ex.getRequestMethod().equalsIgnoreCase("POST")) {
                send(ex, 405, "application/json", "{\"error\":\"POST required\"}"); return;
            }
            String body = new String(ex.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            String text = extractJsonText(body);
            List<ConversationMessage> one = List.of(new ConversationMessage(
                "LIVE-ANALYSIS", "1", "User A", "User B", "", text));
            ThreatAnalyzer.Result r = analyzer.analyze("LIVE-ANALYSIS", one);
            send(ex, 200, "application/json; charset=utf-8", resultJson(r));
        });
        server.createContext("/api/rescan", ex -> {
            Map<String,List<ConversationMessage>> grouped = new LinkedHashMap<>();
            for (ConversationMessage m : messages)
                grouped.computeIfAbsent(m.conversationId, k -> new ArrayList<>()).add(m);
            List<ThreatAnalyzer.Result> results = new ArrayList<>();
            for (var e : grouped.entrySet()) results.add(analyzer.analyze(e.getKey(), e.getValue()));
            long flagged = results.stream().filter(r -> r.score >= 10).count();
            send(ex, 200, "application/json; charset=utf-8",
                "{\"conversations\":"+results.size()+",\"messages\":"+messages.size()+
                ",\"flagged\":"+flagged+"}");
        });
        server.createContext("/", ex -> {
            String path = ex.getRequestURI().getPath();
            if (path.equals("/")) path = "/index.html";
            Path file = Path.of("web", path.substring(1)).normalize();
            if (!file.startsWith(Path.of("web")) || !Files.exists(file) || Files.isDirectory(file)) {
                send(ex, 404, "text/plain; charset=utf-8", "Not found"); return;
            }
            String type = file.toString().endsWith(".css") ? "text/css; charset=utf-8" :
                          file.toString().endsWith(".js") ? "application/javascript; charset=utf-8" :
                          "text/html; charset=utf-8";
            send(ex, 200, type, Files.readString(file, StandardCharsets.UTF_8));
        });
        server.setExecutor(java.util.concurrent.Executors.newFixedThreadPool(8));
        server.start();
        System.out.println("Dashboard: http://localhost:8080");
    }

    static String summaryJson() {
        Map<String,List<ConversationMessage>> grouped = new LinkedHashMap<>();
        for (ConversationMessage m : messages)
            grouped.computeIfAbsent(m.conversationId, k -> new ArrayList<>()).add(m);
        int flagged=0, matches=0;
        for (var e:grouped.entrySet()) {
            ThreatAnalyzer.Result r=analyzer.analyze(e.getKey(),e.getValue());
            if(r.score>=10) flagged++; matches+=r.matches.size();
        }
        return "{\"conversations\":"+grouped.size()+",\"messages\":"+messages.size()+
            ",\"patterns\":"+patterns.size()+",\"flagged\":"+flagged+",\"matches\":"+matches+"}";
    }
    static String resultJson(ThreatAnalyzer.Result r) {
        StringBuilder cats=new StringBuilder("{"); boolean first=true;
        for(var e:r.categoryCounts.entrySet()){
            if(!first) cats.append(","); first=false;
            cats.append("\"").append(jsonEscape(e.getKey())).append("\":").append(e.getValue());
        }
        cats.append("}");
        StringBuilder ms=new StringBuilder("[");
        for(int i=0;i<r.matches.size();i++){
            var m=r.matches.get(i); if(i>0)ms.append(",");
            ms.append("{\"pattern\":\"").append(jsonEscape(m.pattern)).append("\",\"category\":\"")
              .append(jsonEscape(m.category)).append("\",\"start\":").append(m.start)
              .append(",\"end\":").append(m.end).append(",\"severity\":").append(m.severity).append("}");
        }
        ms.append("]");
        return "{\"conversationId\":\"LIVE-ANALYSIS\",\"score\":"+r.score+
            ",\"status\":\""+r.status+"\",\"categories\":"+cats+",\"matches\":"+ms+"}";
    }
    static String extractJsonText(String body) {
        int key=body.indexOf("\"text\"");
        if(key<0) return body;
        int colon=body.indexOf(':',key), start=body.indexOf('"',colon+1);
        if(start<0)return "";
        StringBuilder s=new StringBuilder(); boolean esc=false;
        for(int i=start+1;i<body.length();i++){
            char c=body.charAt(i);
            if(esc){ if(c=='n')s.append('\n'); else if(c=='r')s.append('\r'); else s.append(c); esc=false; }
            else if(c=='\\')esc=true;
            else if(c=='"')break;
            else s.append(c);
        }
        return s.toString();
    }
    static String jsonEscape(String s) {
        return s.replace("\\","\\\\").replace("\"","\\\"").replace("\n","\\n").replace("\r","\\r");
    }
    static void send(HttpExchange ex,int code,String type,String body)throws IOException{
        byte[] b=body.getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().set("Content-Type",type);
        ex.getResponseHeaders().set("Access-Control-Allow-Origin","*");
        ex.sendResponseHeaders(code,b.length);
        try(OutputStream os=ex.getResponseBody()){os.write(b);}
    }
}
