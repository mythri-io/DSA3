public class ThreatPattern {
    public final String pattern;
    public final String category;
    public final int severity;

    public ThreatPattern(String pattern, String category, int severity) {
        this.pattern = pattern.toLowerCase(java.util.Locale.ROOT);
        this.category = category;
        this.severity = severity;
    }
}
