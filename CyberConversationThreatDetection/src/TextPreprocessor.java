import java.text.Normalizer;
import java.util.Locale;
public class TextPreprocessor {
    public static String normalize(String text) {
        return Normalizer.normalize(text == null ? "" : text, Normalizer.Form.NFC)
                .toLowerCase(Locale.ROOT).trim();
    }
}
