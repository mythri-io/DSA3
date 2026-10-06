import java.util.*;
/** Simple single-pattern timing comparison; timings vary by machine and input. */
public class AlgorithmComparison {
    public static void compare(String text,String pattern) {
        long t=System.nanoTime(); int a=KMP.search(text,pattern).size(); long k=System.nanoTime()-t;
        t=System.nanoTime(); int b=RabinKarp.search(text,pattern).size(); long r=System.nanoTime()-t;
        t=System.nanoTime(); int c=ZAlgorithm.search(text,pattern).size(); long z=System.nanoTime()-t;
        System.out.printf("Single-pattern benchmark '%s': KMP=%d (%.3f ms), Rabin-Karp=%d (%.3f ms), Z=%d (%.3f ms)%n",
            pattern,a,k/1e6,b,r/1e6,c,z/1e6);
    }
}
