package com.kaige.advance.concurrence;

import java.util.Optional;
import java.util.concurrent.*;

public class MyCyclicBarrier {
  
  public static void main(String[] args) {
    int nThreads = 4;
    // 用于保存计算结果
    ConcurrentHashMap<String, Integer> concurrentHashMap = new ConcurrentHashMap<>();
    
    CyclicBarrier cyclicBarrier = new CyclicBarrier(nThreads, () -> {
      // 统计结果
      Optional<Integer> reduce = concurrentHashMap.values().stream().reduce(Integer::sum);
      reduce.ifPresent(integer -> {
        concurrentHashMap.put("result", integer);
      });
      System.out.println("结果：" + concurrentHashMap);
    });
    
    // 线程池
    ExecutorService executorService = Executors.newFixedThreadPool(nThreads);
    
    // 启动 4 个线程执行统计任务
    for (int i = 0; i < nThreads; i++) {
      int finalI = i;
      executorService.execute(() -> {
        concurrentHashMap.put(Thread.currentThread().getName(), finalI);
        System.out.println(Thread.currentThread().getName() + " put " + finalI);
        try {
          cyclicBarrier.await();
        } catch (InterruptedException | BrokenBarrierException e) {
          e.printStackTrace();
        }
      });
    }
  
    executorService.shutdown();
    
  }
  
  private static void c() {
    CyclicBarrier cyclicBarrier = new CyclicBarrier(2, () -> System.out.println(3));
    
    new Thread(() -> {
      try {
        cyclicBarrier.await();
        // Thread.sleep(1);
      } catch (InterruptedException | BrokenBarrierException e) {
        e.printStackTrace();
      }
      System.out.println(2);
    }, "线程1").start();
    
    try {
      cyclicBarrier.await();
    } catch (InterruptedException | BrokenBarrierException e) {
      e.printStackTrace();
    }
    System.out.println(1);
    
  }
  
}
