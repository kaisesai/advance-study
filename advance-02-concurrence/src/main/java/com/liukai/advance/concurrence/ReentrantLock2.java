package com.liukai.advance.concurrence;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 重入锁，相比 {@link java.util.concurrent.locks.ReentrantLock} 增加了提供当前等待队列中的线程的方法
 */
public class ReentrantLock2 extends ReentrantLock {
  
  public ReentrantLock2(boolean fair) {
    super(fair);
  }
  
  public static void main(String[] args) {
    ReentrantLock2 lock = new ReentrantLock2(false);
    
    for (int i = 0; i < 1; i++) {
      new Thread(() -> {
        doLock(lock);
        // doLock(lock);
      }, "t" + i).start();
     /*
        创建公平锁，10 个线程竞争锁的情况：是按照顺序，每次都是从队列的头部节点获取锁，上下文切换次数 20 次。
          t0 线程拿到了锁，当前排队中的线程有：[]
          t0 线程释放了锁，当前排队中的线程有：[Thread[t1,5,main], Thread[t2,5,main], Thread[t3,5,main], Thread[t4,5,main], Thread[t5,5,main]]
          
          t1 线程拿到了锁，当前排队中的线程有：[Thread[t2,5,main], Thread[t3,5,main], Thread[t4,5,main], Thread[t5,5,main], Thread[t6,5,main], Thread[t7,5,main], Thread[t0,5,main]]
          t1 线程释放了锁，当前排队中的线程有：[Thread[t2,5,main], Thread[t3,5,main], Thread[t4,5,main], Thread[t5,5,main], Thread[t6,5,main], Thread[t7,5,main], Thread[t0,5,main], Thread[t8,5,main]]
          
          t2 线程拿到了锁，当前排队中的线程有：[Thread[t3,5,main], Thread[t4,5,main], Thread[t5,5,main], Thread[t6,5,main], Thread[t7,5,main], Thread[t0,5,main], Thread[t8,5,main], Thread[t9,5,main], Thread[t1,5,main]]
          t2 线程释放了锁，当前排队中的线程有：[Thread[t3,5,main], Thread[t4,5,main], Thread[t5,5,main], Thread[t6,5,main], Thread[t7,5,main], Thread[t0,5,main], Thread[t8,5,main], Thread[t9,5,main], Thread[t1,5,main]]
          
          t3 线程拿到了锁，当前排队中的线程有：[Thread[t4,5,main], Thread[t5,5,main], Thread[t6,5,main], Thread[t7,5,main], Thread[t0,5,main], Thread[t8,5,main], Thread[t9,5,main], Thread[t1,5,main], Thread[t2,5,main]]
          t3 线程释放了锁，当前排队中的线程有：[Thread[t4,5,main], Thread[t5,5,main], Thread[t6,5,main], Thread[t7,5,main], Thread[t0,5,main], Thread[t8,5,main], Thread[t9,5,main], Thread[t1,5,main], Thread[t2,5,main]]
          
          t4 线程拿到了锁，当前排队中的线程有：[Thread[t5,5,main], Thread[t6,5,main], Thread[t7,5,main], Thread[t0,5,main], Thread[t8,5,main], Thread[t9,5,main], Thread[t1,5,main], Thread[t2,5,main], Thread[t3,5,main]]
          t4 线程释放了锁，当前排队中的线程有：[Thread[t5,5,main], Thread[t6,5,main], Thread[t7,5,main], Thread[t0,5,main], Thread[t8,5,main], Thread[t9,5,main], Thread[t1,5,main], Thread[t2,5,main], Thread[t3,5,main]]
          
          t5 线程拿到了锁，当前排队中的线程有：[Thread[t6,5,main], Thread[t7,5,main], Thread[t0,5,main], Thread[t8,5,main], Thread[t9,5,main], Thread[t1,5,main], Thread[t2,5,main], Thread[t3,5,main], Thread[t4,5,main]]
          t5 线程释放了锁，当前排队中的线程有：[Thread[t6,5,main], Thread[t7,5,main], Thread[t0,5,main], Thread[t8,5,main], Thread[t9,5,main], Thread[t1,5,main], Thread[t2,5,main], Thread[t3,5,main], Thread[t4,5,main]]
          
          t6 线程拿到了锁，当前排队中的线程有：[Thread[t7,5,main], Thread[t0,5,main], Thread[t8,5,main], Thread[t9,5,main], Thread[t1,5,main], Thread[t2,5,main], Thread[t3,5,main], Thread[t4,5,main], Thread[t5,5,main]]
          t6 线程释放了锁，当前排队中的线程有：[Thread[t7,5,main], Thread[t0,5,main], Thread[t8,5,main], Thread[t9,5,main], Thread[t1,5,main], Thread[t2,5,main], Thread[t3,5,main], Thread[t4,5,main], Thread[t5,5,main]]
          
          t7 线程拿到了锁，当前排队中的线程有：[Thread[t0,5,main], Thread[t8,5,main], Thread[t9,5,main], Thread[t1,5,main], Thread[t2,5,main], Thread[t3,5,main], Thread[t4,5,main], Thread[t5,5,main], Thread[t6,5,main]]
          t7 线程释放了锁，当前排队中的线程有：[Thread[t0,5,main], Thread[t8,5,main], Thread[t9,5,main], Thread[t1,5,main], Thread[t2,5,main], Thread[t3,5,main], Thread[t4,5,main], Thread[t5,5,main], Thread[t6,5,main]]
          
          t0 线程拿到了锁，当前排队中的线程有：[Thread[t8,5,main], Thread[t9,5,main], Thread[t1,5,main], Thread[t2,5,main], Thread[t3,5,main], Thread[t4,5,main], Thread[t5,5,main], Thread[t6,5,main], Thread[t7,5,main]]
          t0 线程释放了锁，当前排队中的线程有：[Thread[t8,5,main], Thread[t9,5,main], Thread[t1,5,main], Thread[t2,5,main], Thread[t3,5,main], Thread[t4,5,main], Thread[t5,5,main], Thread[t6,5,main], Thread[t7,5,main]]
          
          t8 线程拿到了锁，当前排队中的线程有：[Thread[t9,5,main], Thread[t1,5,main], Thread[t2,5,main], Thread[t3,5,main], Thread[t4,5,main], Thread[t5,5,main], Thread[t6,5,main], Thread[t7,5,main]]
          t8 线程释放了锁，当前排队中的线程有：[Thread[t9,5,main], Thread[t1,5,main], Thread[t2,5,main], Thread[t3,5,main], Thread[t4,5,main], Thread[t5,5,main], Thread[t6,5,main], Thread[t7,5,main]]
          
          t9 线程拿到了锁，当前排队中的线程有：[Thread[t1,5,main], Thread[t2,5,main], Thread[t3,5,main], Thread[t4,5,main], Thread[t5,5,main], Thread[t6,5,main], Thread[t7,5,main], Thread[t8,5,main]]
          t9 线程释放了锁，当前排队中的线程有：[Thread[t1,5,main], Thread[t2,5,main], Thread[t3,5,main], Thread[t4,5,main], Thread[t5,5,main], Thread[t6,5,main], Thread[t7,5,main], Thread[t8,5,main]]
          
          t1 线程拿到了锁，当前排队中的线程有：[Thread[t2,5,main], Thread[t3,5,main], Thread[t4,5,main], Thread[t5,5,main], Thread[t6,5,main], Thread[t7,5,main], Thread[t8,5,main], Thread[t9,5,main]]
          t1 线程释放了锁，当前排队中的线程有：[Thread[t2,5,main], Thread[t3,5,main], Thread[t4,5,main], Thread[t5,5,main], Thread[t6,5,main], Thread[t7,5,main], Thread[t8,5,main], Thread[t9,5,main]]
          
          t2 线程拿到了锁，当前排队中的线程有：[Thread[t3,5,main], Thread[t4,5,main], Thread[t5,5,main], Thread[t6,5,main], Thread[t7,5,main], Thread[t8,5,main], Thread[t9,5,main]]
          t2 线程释放了锁，当前排队中的线程有：[Thread[t3,5,main], Thread[t4,5,main], Thread[t5,5,main], Thread[t6,5,main], Thread[t7,5,main], Thread[t8,5,main], Thread[t9,5,main]]
          
          t3 线程拿到了锁，当前排队中的线程有：[Thread[t4,5,main], Thread[t5,5,main], Thread[t6,5,main], Thread[t7,5,main], Thread[t8,5,main], Thread[t9,5,main]]
          t3 线程释放了锁，当前排队中的线程有：[Thread[t4,5,main], Thread[t5,5,main], Thread[t6,5,main], Thread[t7,5,main], Thread[t8,5,main], Thread[t9,5,main]]
          
          t4 线程拿到了锁，当前排队中的线程有：[Thread[t5,5,main], Thread[t6,5,main], Thread[t7,5,main], Thread[t8,5,main], Thread[t9,5,main]]
          t4 线程释放了锁，当前排队中的线程有：[Thread[t5,5,main], Thread[t6,5,main], Thread[t7,5,main], Thread[t8,5,main], Thread[t9,5,main]]
          
          t5 线程拿到了锁，当前排队中的线程有：[Thread[t6,5,main], Thread[t7,5,main], Thread[t8,5,main], Thread[t9,5,main]]
          t5 线程释放了锁，当前排队中的线程有：[Thread[t6,5,main], Thread[t7,5,main], Thread[t8,5,main], Thread[t9,5,main]]
          
          t6 线程拿到了锁，当前排队中的线程有：[Thread[t7,5,main], Thread[t8,5,main], Thread[t9,5,main]]
          t6 线程释放了锁，当前排队中的线程有：[Thread[t7,5,main], Thread[t8,5,main], Thread[t9,5,main]]
          
          t7 线程拿到了锁，当前排队中的线程有：[Thread[t8,5,main], Thread[t9,5,main]]
          t7 线程释放了锁，当前排队中的线程有：[Thread[t8,5,main], Thread[t9,5,main]]
          
          t8 线程拿到了锁，当前排队中的线程有：[Thread[t9,5,main]]
          t8 线程释放了锁，当前排队中的线程有：[Thread[t9,5,main]]
          
          t9 线程拿到了锁，当前排队中的线程有：[]
          t9 线程释放了锁，当前排队中的线程有：[]


          
        非公平锁，10 个线程竞争锁的情况：并不是每次都从队列头节点获取锁，上下文切换次数少，只有 10 次
          t0 线程拿到了锁，当前排队中的线程有：[]
          t0 线程释放了锁，当前排队中的线程有：[Thread[t1,5,main], Thread[t2,5,main], Thread[t3,5,main], Thread[t4,5,main]]
          
          t0 线程拿到了锁，当前排队中的线程有：[Thread[t1,5,main], Thread[t2,5,main], Thread[t3,5,main], Thread[t4,5,main], Thread[t5,5,main], Thread[t6,5,main]]
          t0 线程释放了锁，当前排队中的线程有：[Thread[t1,5,main], Thread[t2,5,main], Thread[t3,5,main], Thread[t4,5,main], Thread[t5,5,main], Thread[t6,5,main], Thread[t7,5,main]]
          
          t1 线程拿到了锁，当前排队中的线程有：[Thread[t2,5,main], Thread[t3,5,main], Thread[t4,5,main], Thread[t5,5,main], Thread[t6,5,main], Thread[t7,5,main], Thread[t8,5,main]]
          t1 线程释放了锁，当前排队中的线程有：[Thread[t2,5,main], Thread[t3,5,main], Thread[t4,5,main], Thread[t5,5,main], Thread[t6,5,main], Thread[t7,5,main], Thread[t8,5,main], Thread[t9,5,main]]
          
          t1 线程拿到了锁，当前排队中的线程有：[Thread[t2,5,main], Thread[t3,5,main], Thread[t4,5,main], Thread[t5,5,main], Thread[t6,5,main], Thread[t7,5,main], Thread[t8,5,main], Thread[t9,5,main]]
          t1 线程释放了锁，当前排队中的线程有：[Thread[t2,5,main], Thread[t3,5,main], Thread[t4,5,main], Thread[t5,5,main], Thread[t6,5,main], Thread[t7,5,main], Thread[t8,5,main], Thread[t9,5,main]]
          
          t2 线程拿到了锁，当前排队中的线程有：[Thread[t3,5,main], Thread[t4,5,main], Thread[t5,5,main], Thread[t6,5,main], Thread[t7,5,main], Thread[t8,5,main], Thread[t9,5,main]]
          t2 线程释放了锁，当前排队中的线程有：[Thread[t3,5,main], Thread[t4,5,main], Thread[t5,5,main], Thread[t6,5,main], Thread[t7,5,main], Thread[t8,5,main], Thread[t9,5,main]]
          
          t2 线程拿到了锁，当前排队中的线程有：[Thread[t3,5,main], Thread[t4,5,main], Thread[t5,5,main], Thread[t6,5,main], Thread[t7,5,main], Thread[t8,5,main], Thread[t9,5,main]]
          t2 线程释放了锁，当前排队中的线程有：[Thread[t3,5,main], Thread[t4,5,main], Thread[t5,5,main], Thread[t6,5,main], Thread[t7,5,main], Thread[t8,5,main], Thread[t9,5,main]]
          
          t3 线程拿到了锁，当前排队中的线程有：[Thread[t4,5,main], Thread[t5,5,main], Thread[t6,5,main], Thread[t7,5,main], Thread[t8,5,main], Thread[t9,5,main]]
          t3 线程释放了锁，当前排队中的线程有：[Thread[t4,5,main], Thread[t5,5,main], Thread[t6,5,main], Thread[t7,5,main], Thread[t8,5,main], Thread[t9,5,main]]
          
          t3 线程拿到了锁，当前排队中的线程有：[Thread[t4,5,main], Thread[t5,5,main], Thread[t6,5,main], Thread[t7,5,main], Thread[t8,5,main], Thread[t9,5,main]]
          t3 线程释放了锁，当前排队中的线程有：[Thread[t4,5,main], Thread[t5,5,main], Thread[t6,5,main], Thread[t7,5,main], Thread[t8,5,main], Thread[t9,5,main]]
          
          t4 线程拿到了锁，当前排队中的线程有：[Thread[t5,5,main], Thread[t6,5,main], Thread[t7,5,main], Thread[t8,5,main], Thread[t9,5,main]]
          t4 线程释放了锁，当前排队中的线程有：[Thread[t5,5,main], Thread[t6,5,main], Thread[t7,5,main], Thread[t8,5,main], Thread[t9,5,main]]
          
          t4 线程拿到了锁，当前排队中的线程有：[Thread[t5,5,main], Thread[t6,5,main], Thread[t7,5,main], Thread[t8,5,main], Thread[t9,5,main]]
          t4 线程释放了锁，当前排队中的线程有：[Thread[t5,5,main], Thread[t6,5,main], Thread[t7,5,main], Thread[t8,5,main], Thread[t9,5,main]]
          
          t5 线程拿到了锁，当前排队中的线程有：[Thread[t6,5,main], Thread[t7,5,main], Thread[t8,5,main], Thread[t9,5,main]]
          t5 线程释放了锁，当前排队中的线程有：[Thread[t6,5,main], Thread[t7,5,main], Thread[t8,5,main], Thread[t9,5,main]]
          
          t5 线程拿到了锁，当前排队中的线程有：[Thread[t6,5,main], Thread[t7,5,main], Thread[t8,5,main], Thread[t9,5,main]]
          t5 线程释放了锁，当前排队中的线程有：[Thread[t6,5,main], Thread[t7,5,main], Thread[t8,5,main], Thread[t9,5,main]]
          
          t6 线程拿到了锁，当前排队中的线程有：[Thread[t7,5,main], Thread[t8,5,main], Thread[t9,5,main]]
          t6 线程释放了锁，当前排队中的线程有：[Thread[t7,5,main], Thread[t8,5,main], Thread[t9,5,main]]
          
          t6 线程拿到了锁，当前排队中的线程有：[Thread[t7,5,main], Thread[t8,5,main], Thread[t9,5,main]]
          t6 线程释放了锁，当前排队中的线程有：[Thread[t7,5,main], Thread[t8,5,main], Thread[t9,5,main]]
          
          t7 线程拿到了锁，当前排队中的线程有：[Thread[t8,5,main], Thread[t9,5,main]]
          t7 线程释放了锁，当前排队中的线程有：[Thread[t8,5,main], Thread[t9,5,main]]
          
          t7 线程拿到了锁，当前排队中的线程有：[Thread[t8,5,main], Thread[t9,5,main]]
          t7 线程释放了锁，当前排队中的线程有：[Thread[t8,5,main], Thread[t9,5,main]]
          
          t8 线程拿到了锁，当前排队中的线程有：[Thread[t9,5,main]]
          t8 线程释放了锁，当前排队中的线程有：[Thread[t9,5,main]]
          
          t8 线程拿到了锁，当前排队中的线程有：[Thread[t9,5,main]]
          t8 线程释放了锁，当前排队中的线程有：[Thread[t9,5,main]]
          
          t9 线程拿到了锁，当前排队中的线程有：[]
          t9 线程释放了锁，当前排队中的线程有：[]
          
          t9 线程拿到了锁，当前排队中的线程有：[]
          t9 线程释放了锁，当前排队中的线程有：[]
          
      */
    }
    
    doLock(lock);
    // System.out.println("获取锁");
  }
  
  private static void doLock(ReentrantLock2 lock) {
    try {
      lock.lock();
      System.out
        .println(Thread.currentThread().getName() + " 线程拿到了锁，当前排队中的线程有：" + lock.getQueuedThreads());
      // 线程睡眠
      Thread.sleep(1000);
      System.out
        .println(Thread.currentThread().getName() + " 线程释放了锁，当前排队中的线程有：" + lock.getQueuedThreads());
    } catch (InterruptedException e) {
      e.printStackTrace();
    } finally {
      System.out.println();
      lock.unlock();
    }
  }
  
  /**
   * @return 返回当前等待队列中的线程
   */
  public Collection<Thread> getQueuedThreads() {
    List<Thread> queuedThreads = new ArrayList<>(super.getQueuedThreads());
    // 把等待线程进行翻转
    Collections.reverse(queuedThreads);
    return queuedThreads;
  }
  
}
