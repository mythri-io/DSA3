import java.util.*;
public class ZAlgorithm {
    public static List<Integer> search(String text, String pattern) {
        List<Integer> out = new ArrayList<>();
        if(pattern.isEmpty()) return out;
        String s=pattern+"$"+text;
        int n=s.length(); int[] z=new int[n]; int l=0,r=0;
        for(int i=1;i<n;i++){
            if(i<=r) z[i]=Math.min(r-i+1,z[i-l]);
            while(i+z[i]<n && s.charAt(z[i])==s.charAt(i+z[i])) z[i]++;
            if(i+z[i]-1>r){l=i;r=i+z[i]-1;}
            if(z[i]>=pattern.length() && i>pattern.length()) out.add(i-pattern.length()-1);
        }
        return out;
    }
}
