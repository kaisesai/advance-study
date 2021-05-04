import com.google.common.collect.Lists;
import org.apache.commons.collections4.ListUtils;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ListTest {
  
  @Test
  public void testCyc() throws InterruptedException {
    CyclicBarrier cyclicBarrier = new CyclicBarrier(20);
    
    Runnable runnable = () -> {
      try {
        cyclicBarrier.await();
      } catch (InterruptedException | BrokenBarrierException e) {
        e.printStackTrace();
      }
    };
    
    int num = 5;
    Thread[] ts = new Thread[num];
    for (int i = 0; i < num; i++) {
      ts[i] = new Thread(runnable, "线程" + i);
      ts[i].start();
    }
    // for (Thread t : ts) {
    //   t.join();
    // }
    Thread.sleep(1000);
    
    int numberWaiting = cyclicBarrier.getNumberWaiting();
    System.out.println("numberWaiting = " + numberWaiting);
    
    Thread.sleep(1000 * 100);
  }
  
  @Test
  public void testSplitList() {
    List<Integer> listI = IntStream.rangeClosed(0, 643).boxed().collect(Collectors.toList());
    List<List<Integer>> lists = Lists.partition(listI, 100);
    for (int i = 0; i < lists.size(); i++) {
      System.out.println("Lists.partition i = " + i + " list: " + lists.get(i));
    }
    System.out.println("============================");
    
    List<List<Integer>> partition = ListUtils.partition(listI, 100);
    for (int i = 0; i < partition.size(); i++) {
      System.out.println("ListUtils.partition i = " + i + " list: " + partition.get(i));
    }
    System.out.println("============================");
    
    List<List<Integer>> collect = IntStream.range(0, 7).boxed().parallel()
      .map(i -> listI.stream().skip(i * 100).limit(100).parallel().collect(Collectors.toList()))
      .collect(Collectors.toList());
    for (int i = 0; i < collect.size(); i++) {
      System.out.println("stream parallel i = " + i + " list: " + collect.get(i));
    }
  }
  
}
