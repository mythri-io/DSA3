import java.util.*;
/** Generic Edmonds-Karp maximum-flow implementation; included as a graph-algorithm study component. */
public class EdmondsKarp {
    public static int maxFlow(int[][] capacity,int source,int sink){
        int n=capacity.length, flow=0; int[][] residual=new int[n][n];
        for(int i=0;i<n;i++) residual[i]=capacity[i].clone();
        int[] parent=new int[n];
        while(true){
            Arrays.fill(parent,-1); parent[source]=source;
            Queue<Integer> q=new ArrayDeque<>(); q.add(source);
            while(!q.isEmpty() && parent[sink]==-1){
                int u=q.remove();
                for(int v=0;v<n;v++) if(parent[v]==-1 && residual[u][v]>0){
                    parent[v]=u; q.add(v);
                }
            }
            if(parent[sink]==-1)break;
            int add=Integer.MAX_VALUE;
            for(int v=sink;v!=source;v=parent[v]) add=Math.min(add,residual[parent[v]][v]);
            for(int v=sink;v!=source;v=parent[v]){
                int u=parent[v]; residual[u][v]-=add; residual[v][u]+=add;
            }
            flow+=add;
        }
        return flow;
    }
}
