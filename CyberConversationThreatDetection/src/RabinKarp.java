import java.util.*;
public class RabinKarp {
    public static List<Integer> search(String text, String pattern) {
        List<Integer> out = new ArrayList<>();
        int n=text.length(), m=pattern.length();
        if (m==0 || m>n) return out;
        long base=257, mod=1_000_000_007L, power=1, ph=0, th=0;
        for(int i=0;i<m;i++){ ph=(ph*base+pattern.charAt(i))%mod; th=(th*base+text.charAt(i))%mod; if(i<m-1) power=power*base%mod; }
        for(int i=0;i<=n-m;i++){
            if(ph==th && text.regionMatches(i,pattern,0,m)) out.add(i);
            if(i<n-m){ th=(th-text.charAt(i)*power%mod+mod)%mod; th=(th*base+text.charAt(i+m))%mod; }
        }
        return out;
    }
}
