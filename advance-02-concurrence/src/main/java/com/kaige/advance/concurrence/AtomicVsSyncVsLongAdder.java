package com.kaige.advance.concurrence;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

/**
 * 比较自旋锁、同步锁、LongAdder 分段锁
 *
 * @author liukai 2021年06月06日21:40:52
 */
public class AtomicVsSyncVsLongAdder {
  
  private static final int THREAD_NUM = 1_00;
  
  /**
   * 线程计数器循环次数
   */
  private static final int CYCLIC_NUM = 1_000_000;
  
  /**
   * 原子 CAS 方式的计数器
   */
  private static final AtomicLong ATOMIC_COUNTER = new AtomicLong();
  
  /**
   * 分段锁方式的计数器
   */
  private static final LongAdder LONG_ADDER_COUNTER = new LongAdder();
  
  /**
   * 采用同步 sync 方式的计数器
   */
  private static volatile long syncCounter = 0;
  
  public static void main(String[] args) throws Exception {
    
    Thread[] threads = new Thread[THREAD_NUM];
    
    // 方式一：统计第一种 sync 同步计数器的方式
    Object lock = new Object();
    for (int i = 0; i < threads.length; i++) {
      threads[i] = new Thread(() -> {
        for (int i1 = 0; i1 < CYCLIC_NUM; i1++) {
          synchronized (lock) {
            syncCounter++;
          }
        }
      });
    }
    
    String msg = "sync 同步";
    doExecute(threads, msg);
    
    // 方式二：采用 CAS 自旋式的计数器
    for (int i = 0; i < threads.length; i++) {
      threads[i] = new Thread(() -> {
        for (int i1 = 0; i1 < CYCLIC_NUM; i1++) {
          ATOMIC_COUNTER.incrementAndGet();
        }
      });
    }
    
    msg = "CAS 自旋式同步";
    doExecute(threads, msg);
    
    // 方式三：采用 LongAdder 分段锁式的计数器
    for (int i = 0; i < threads.length; i++) {
      threads[i] = new Thread(() -> {
        for (int i1 = 0; i1 < CYCLIC_NUM; i1++) {
          LONG_ADDER_COUNTER.increment();
        }
      });
    }
    
    msg = "LongAdder 分段锁同步";
    doExecute(threads, msg);
    
    /*
    1000 个线程采用 sync 同步方式，循环次数为 1_000_000 次完成计数，耗时：35190ms
    1000 个线程采用 CAS 自旋式同步方式，循环次数为 1_000_000 次完成计数，耗时：21357ms
    1000 个线程采用 LongAdder 分段锁同步方式，循环次数为 1_000_000 次完成计数，耗时：925ms
    
    1000 个线程采用 sync 同步方式，循环次数为 100_000 次完成计数，耗时：5814ms
    1000 个线程采用 CAS 自旋式同步方式，循环次数为 100_000 次完成计数，耗时：1614ms
    1000 个线程采用 LongAdder 分段锁同步方式，循环次数为 100_000 次完成计数，耗时：117ms
    
    100 个线程采用 sync 同步方式，循环次数为 100_000 次完成计数，耗时：689ms
    100 个线程采用 CAS 自旋式同步方式，循环次数为 100_000 次完成计数，耗时：192ms
    100 个线程采用 LongAdder 分段锁同步方式，循环次数为 100_000 次完成计数，耗时：51ms
    
    100 个线程采用 sync 同步方式，循环次数为 1_000_000 次完成计数，耗时：5117ms
    100 个线程采用 CAS 自旋式同步方式，循环次数为 1_000_000 次完成计数，耗时：1840ms
    100 个线程采用 LongAdder 分段锁同步方式，循环次数为 1_000_000 次完成计数，耗时：105ms
    
     */
  }
  
  private static void doExecute(Thread[] threads, String msg) throws InterruptedException {
    // 记录时间
    long startTime = System.currentTimeMillis();
    
    // 执行线程任务
    for (Thread thread : threads) {
      thread.start();
    }
    
    // 确保线程先执行完任务
    for (Thread thread : threads) {
      thread.join();
    }
    long endTime = System.currentTimeMillis();
    
    // String msg = "采用 sync 同步方式";
    System.out.println(
      THREAD_NUM + " 个线程采用 " + msg + "方式，循环次数为 " + CYCLIC_NUM + " 次完成计数，耗时：" + (endTime - startTime)
        + "ms");
  }
  
}
