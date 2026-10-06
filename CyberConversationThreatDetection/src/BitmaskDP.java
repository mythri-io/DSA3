import java.util.*;
/** Small supporting example: minimum number of supplied groups needed to cover target bits. */
public class BitmaskDP {
    public static int minGroups(int targetMask, int[] masks) {
        int n=Integer.bitCount(targetMask), inf=1_000_000;
        int[] dp=new int[1<<Math.min(20,n+1)]; // generic demo; masks must fit this state space
        Arrays.fill(dp,inf); dp[0]=0;
        for(int mask:masks) for(int s=dp.length-1;s>=0;s--){
            int ns=s|mask;
            if(ns<dp.length) dp[ns]=Math.min(dp[ns],dp[s]+1);
        }
        return targetMask<dp.length && dp[targetMask]<inf ? dp[targetMask] : -1;
    }
}
