import java.util.*;
public class KMP {
    public static List<Integer> search(String text, String pattern) {
        List<Integer> out = new ArrayList<>();
        if (pattern.isEmpty()) return out;
        int[] lps = new int[pattern.length()];
        for (int i=1, len=0; i<pattern.length();) {
            if (pattern.charAt(i)==pattern.charAt(len)) lps[i++]=++len;
            else if (len>0) len=lps[len-1];
            else lps[i++]=0;
        }
        for (int i=0,j=0; i<text.length();) {
            if (text.charAt(i)==pattern.charAt(j)) { i++; j++; }
            if (j==pattern.length()) { out.add(i-j); j=lps[j-1]; }
            else if (i<text.length() && text.charAt(i)!=pattern.charAt(j)) {
                if (j>0) j=lps[j-1]; else i++;
            }
        }
        return out;
    }
}
