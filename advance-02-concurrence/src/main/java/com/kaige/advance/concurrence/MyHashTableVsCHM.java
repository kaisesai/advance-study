package com.kaige.advance.concurrence;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 测试 hashtable、hashmap、同步 map、concurrenthashmap 的性能
 *
 * @author liukai 2021年06月14日16:32:30
 */
public class MyHashTableVsCHM {
  
  // 线程数量
  public static final int THREAD_COUNT = 10;
  
  private static final int COUNT = 1_000_000;
  
  public static final int GAP = COUNT / THREAD_COUNT;
  
  private static final Thread[] THREADS = new Thread[THREAD_COUNT];
  
  private static final UUID[] KEYS = new UUID[COUNT];
  
  private static final UUID[] VALUES = new UUID[COUNT];
  
  static {
    // 初始化数据
    for (int i = 0; i < COUNT; i++) {
      KEYS[i] = UUID.randomUUID();
      VALUES[i] = UUID.randomUUID();
    }
  }
  
  public static void main(String[] args) throws Exception {
    // 测试 hashtable
    Map<UUID, UUID> map = new Hashtable<>();
    // 100 个线程，每个线程往 Hashtable 中 添加 100000 元素后，总的执行时间为：4597毫秒，容器的元素个数为：10000000
    doTest(map);
    // 测试 hashmap
    // map = new HashMap<>();
    // 出现死循环
    // doTest(map);
    // 测试同步容器
    map = Collections.synchronizedMap(new HashMap<>());
    // 100 个线程，每个线程往 SynchronizedMap 中 添加 100000 元素后，总的执行时间为：4463毫秒，容器的元素个数为：10000000
    doTest(map);
    // 测试 ConcurrentHashMap
    map = new ConcurrentHashMap<>();
    // 100 个线程，每个线程往 ConcurrentHashMap 中 添加 100000 元素后，总的执行时间为：6229毫秒，容器的元素个数为：10000000
    doTest(map);
    
    /*
      10 个线程，每个线程往 Hashtable 中 添加 1000000 元素后，总的执行时间为：4108毫秒，容器的元素个数为：10000000
      10 个线程，每个线程往 SynchronizedMap 中 添加 1000000 元素后，总的执行时间为：4038毫秒，容器的元素个数为：10000000
      10 个线程，每个线程往 ConcurrentHashMap 中 添加 1000000 元素后，总的执行时间为：1721毫秒，容器的元素个数为：10000000
      
      100 个线程，每个线程往 Hashtable 中 添加 100000 元素后，总的执行时间为：4871毫秒，容器的元素个数为：10000000
      100 个线程，每个线程往 SynchronizedMap 中 添加 100000 元素后，总的执行时间为：4379毫秒，容器的元素个数为：10000000
      100 个线程，每个线程往 ConcurrentHashMap 中 添加 100000 元素后，总的执行时间为：5118毫秒，容器的元素个数为：10000000
      
      10 个线程，每个线程往 Hashtable 中 添加 100000 元素后，总的执行时间为：306毫秒，容器的元素个数为：1000000
      10 个线程，每个线程往 SynchronizedMap 中 添加 100000 元素后，总的执行时间为：327毫秒，容器的元素个数为：1000000
      10 个线程，每个线程往 ConcurrentHashMap 中 添加 100000 元素后，总的执行时间为：232毫秒，容器的元素个数为：1000000
      
      100 个线程，每个线程往 Hashtable 中 添加 10000 元素后，总的执行时间为：447毫秒，容器的元素个数为：1000000
      100 个线程，每个线程往 SynchronizedMap 中 添加 10000 元素后，总的执行时间为：403毫秒，容器的元素个数为：1000000
      100 个线程，每个线程往 ConcurrentHashMap 中 添加 10000 元素后，总的执行时间为：1145毫秒，容器的元素个数为：1000000

      从上面的结论来看，相同线程数和写入操作下，ConcurrentHashMap 并不一定是最快的，它适合在读多写少高并发的的场景下
     */
  }
  
  private static void doTest(Map<UUID, UUID> map) {
    for (int i = 0; i < THREAD_COUNT; i++) {
      THREADS[i] = new Thread(new TestMapRunnable(i * GAP, GAP, map), "t" + i);
    }
    
    long startMillis = System.currentTimeMillis();
    
    // 启动线程
    for (int i = 0; i < THREAD_COUNT; i++) {
      THREADS[i].start();
    }
    
    // 等待线程执行完毕
    for (int i = 0; i < THREAD_COUNT; i++) {
      try {
        THREADS[i].join();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    
    // 统计时间
    System.out.printf("%s 个线程，每个线程往 %s 中 添加 %s 元素后，总的执行时间为：%s毫秒，容器的元素个数为：%s%n", THREAD_COUNT,
                      map.getClass().getSimpleName(), GAP,
                      (System.currentTimeMillis() - startMillis), map.size());
  }
  
  /**
   * 往 Map 中添加元素测试任务
   */
  private static class TestMapRunnable implements Runnable {
    
    private final int start;
    
    private final int gap;
    
    private final Map<UUID, UUID> map;
    
    public TestMapRunnable(int start, int gap, Map<UUID, UUID> map) {
      this.start = start;
      this.gap = gap;
      this.map = map;
    }
    
    @Override
    public void run() {
      // 往 map 中添加元素
      // int gap = COUNT / THREAD_COUNT;
      for (int i = start; i < gap + start; i++) {
        map.put(KEYS[i], VALUES[i]);
      }
    }
    
  }
  
}
