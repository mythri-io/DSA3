import java.util.Random;
public class RandomizedHash {
    public static long hash(String s, long base, long mod) {
        long h=0;
        for(char c:s.toCharArray()) h=(h*base+c)%mod;
        return h;
    }
    public static long sampleHash(String s) {
        Random r=new Random(42);
        long base=257+r.nextInt(1000), mod=1_000_000_007L;
        return hash(s,base,mod);
    }
}
