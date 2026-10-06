import java.util.*;
/** Greedy set cover demo for selecting labels that cover observed indicator IDs. */
public class SetCoverApproximation {
    public static List<String> choose(Map<String,Set<String>> groups, Set<String> universe) {
        Set<String> uncovered=new HashSet<>(universe);
        List<String> chosen=new ArrayList<>();
        while(!uncovered.isEmpty()){
            String best=null; int gain=0;
            for(var e:groups.entrySet()){
                int g=0; for(String x:e.getValue()) if(uncovered.contains(x))g++;
                if(g>gain){gain=g;best=e.getKey();}
            }
            if(best==null)break;
            chosen.add(best); uncovered.removeAll(groups.get(best));
        }
        return chosen;
    }
}
