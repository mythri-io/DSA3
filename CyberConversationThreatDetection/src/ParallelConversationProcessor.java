import java.util.*;
import java.util.concurrent.*;
/** Demonstrates independent conversation processing using a fixed thread pool. */
public class ParallelConversationProcessor {
    public static int countMessagesInParallel(List<ConversationMessage> messages) throws Exception {
        Map<String,List<ConversationMessage>> grouped=new HashMap<>();
        for(var m:messages)grouped.computeIfAbsent(m.conversationId,k->new ArrayList<>()).add(m);
        ExecutorService pool=Executors.newFixedThreadPool(Math.max(2,Runtime.getRuntime().availableProcessors()));
        try{
            List<Future<Integer>> futures=new ArrayList<>();
            for(var list:grouped.values())futures.add(pool.submit(()->list.size()));
            int total=0; for(var f:futures)total+=f.get(); return total;
        } finally {pool.shutdown();}
    }
}
