package com.kaige.advance.concurrence;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 用多线程顺序打印出 A1B2C3D4E5F6G7
 *
 * @author liukai 2021年06月16日22:35:52
 */
public class MyAlternateOutApp {
  
  private static final char[] CHARS_1 = {'A', 'B', 'C', 'D', 'E', 'F', 'G'};
  
  private static final char[] CHARS_2 = {'1', '2', '3', '4', '5', '6', '7'};
  
  private volatile static boolean flag = false;
  
  private static Thread t1 = null;
  
  private static Thread t2 = null;
  
  private volatile static OptThread optThread = OptThread.T1;
  
  public static void main(String[] args) throws InterruptedException {
    
    // 方案一：使用 synchronized、wait、notify 机制，本质：wait、notify、synchronized
    // way1WaitNotify();
    
    // 方法二：使用 LockSupport 类，本质：Unsafe 类的 park、unpark 方法
    // way2LockSupport();
    
    // 方法三：使用 CAS，本质：无锁
    // way3CAS();
    
    // 方法四：使用 Lock&Condition，本质：AQS 的等待队列、同步队列，底层也是借助 Unsafe 类的 park、unpark 方法阻塞，无限循环
    // way3LockCondition();
    
    // 方法五：使用 AtomicInteger，自定义的 CAS，本质：利用无限循环 + 原子读写操作
    // way4AtomicInteger();
    
    // 方法六：使用两个阻塞队列，本质：AQS，等待队列 + 同步队列，park、unpark 方法
    // way5BlockingQueue();
    
    // 方法七：使用 transfer 交换队列
    way6TransferQueue();
    
  }
  
  private static void way6TransferQueue() {
    TransferQueue<Character> tq = new LinkedTransferQueue<>();
    
    // 思路：一个线程生产数据并且同时阻塞的等待另一个线程接消费
    new Thread(() -> {
      for (char c : CHARS_1) {
        try {
          // 阻塞的传输，等待被消费
          tq.transfer(c);
          // 消费数据
          System.out.print(tq.take());
          
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
      
    }, "t1").start();
    
    new Thread(() -> {
      for (char c : CHARS_2) {
        try {
          System.out.print(tq.take());
          tq.transfer(c);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
      
    }, "t2").start();
  }
  
  /**
   * 使用两个阻塞队列实现
   * <p>
   * 为什么要用来两个阻塞队列？
   * 因为阻塞队列的 put、take 方法与打印的方法不是一个原子操作，两个线程需要借助两个队列来完成
   */
  private static void way5BlockingQueue() {
    ArrayBlockingQueue<Integer> q1 = new ArrayBlockingQueue<>(1);
    ArrayBlockingQueue<Integer> q2 = new ArrayBlockingQueue<>(1);
    
    new Thread(() -> {
      for (char c : CHARS_1) {
        // 打印
        System.out.print(c);
        try {
          // q1 阻塞的添加数据
          q1.put(1);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        
        try {
          // q2 阻塞的获取数据
          q2.take();
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }, "t1").start();
    
    new Thread(() -> {
      for (char c : CHARS_2) {
        try {
          // 阻塞式消费队列数据
          q1.take();
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        System.out.print(c);
        try {
          q2.put(1);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        
      }
    }, "t2").start();
    
  }
  
  /**
   * 使用 AtomicInteger 借助原子的 get、set 方法 + while 循环实现打印、
   */
  private static void way4AtomicInteger() {
    AtomicInteger a1 = new AtomicInteger(1);
    
    new Thread(() -> {
      for (char c : CHARS_1) {
        while (a1.get() != 1) {
        }
        System.out.print(c);
        a1.set(2);
      }
    }, "t1").start();
    
    new Thread(() -> {
      for (char c : CHARS_2) {
        while (a1.get() != 2) {
        }
        System.out.print(c);
        a1.set(1);
      }
    }, "t2").start();
  }
  
  /**
   * 使用 CountDownLatch、ReentrantLock、Condition 实现两个线程交替打印字符
   */
  private static void way3LockCondition() {
    CountDownLatch countDownLatch = new CountDownLatch(1);
    
    // 方法四：使用 Condition
    ReentrantLock lock = new ReentrantLock();
    // Condition condition = lock.newCondition();
    Condition c1 = lock.newCondition();
    Condition c2 = lock.newCondition();
    
    new Thread(() -> {
      
      try {
        TimeUnit.SECONDS.sleep(1);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      
      lock.lock();
      try {
        for (char c : CHARS_1) {
          // 打印
          System.out.print(c);
          // 唤醒 c2
          c2.signal();
          countDownLatch.countDown();
          // condition.signal();
          // c1 等待
          try {
            c1.await();
            // condition.await();
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
          // 唤醒 c2，这里是为了防止 t2 答应到 7 之后一直睡眠；
          // 假如 t1 答应到了 G 调用 c2.signal 环境了 c2，然后自己 c1.wait 睡眠，t2 被唤醒，打印了 7，然后调用 c.signal，又调用 c2.wait，此时 t2 就睡眠了，t1 醒来之后就直接结束线程，t2 一直在阻塞等待中。
          // 所以需要 t1 醒来之后调用 c2.signal 来叫醒 t2，这样程序才能终止。
          
          // c2.signal();
          // condition.signal();
        }
        
      } finally {
        lock.unlock();
      }
    }, "t1").start();
    
    new Thread(() -> {
      
      try {
        countDownLatch.await();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      
      lock.lock();
      try {
        for (char c : CHARS_2) {
          // 打印
          System.out.print(c);
          // 唤醒 c1
          c1.signal();
          // condition.signal();
          // c1 等待
          try {
            c2.await();
            // condition.await();
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
          
        }
      } finally {
        lock.unlock();
      }
    }, "t2").start();
  }
  
  /**
   * 采用自旋式的方式实现
   */
  private static void way3CAS() {
    new Thread(() -> {
      
      for (char c : CHARS_1) {
        // 自旋的判断 optThread 是否为 T2，否则就打印，最后更新 optThread 为 T2
        while (OptThread.T2.equals(optThread)) {
        }
        System.out.print(c);
        optThread = OptThread.T2;
      }
      
    }, "t1").start();
    
    new Thread(() -> {
      
      for (char c : CHARS_2) {
        // 自旋的判断 optThread 是否为 T1，否则就打印，最后更新 optThread 为 T1
        while (OptThread.T1.equals(optThread)) {
        }
        System.out.print(c);
        optThread = OptThread.T1;
      }
    }, "t2").start();
  }
  
  /**
   * 使用 LockSupport 工具类实现
   */
  private static void way2LockSupport() {
    t1 = new Thread(() -> {
      for (char c : CHARS_1) {
        System.out.print(c);
        // 叫醒 t2
        LockSupport.unpark(t2);
        // 阻塞自己
        LockSupport.park();
      }
    }, "t1");
    
    t2 = new Thread(() -> {
      for (char c : CHARS_2) {
        // 阻塞自己
        LockSupport.park();
        // 打印
        System.out.print(c);
        // 叫醒 t1
        LockSupport.unpark(t1);
      }
    }, "t1");
    
    t1.start();
    t2.start();
  }
  
  /**
   * 采用 wait + notify 的方式
   */
  private static void way1WaitNotify() {
    final Object lock = new Object();
    
    // t1 线程打印 ABCDEFG
    new Thread(() -> {
      
      synchronized (lock) {
        for (char c : CHARS_1) {
          // t1 线程打印自己数字
          System.out.print(c);
          flag = true;
          lock.notify();
          try {
            // 接着睡眠
            lock.wait();
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
        // t1 需要在打印完 G 之后唤醒处于打印完 7 后等待状态的 t2 线程
        lock.notify();
        
      }
      
    }, "t1").start();
    
    // t2 线程打印 1234567
    new Thread(() -> {
      synchronized (lock) {
        // 先判断标记是否为 false，否则就等待
        while (!flag) {
          try {
            lock.wait();
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
        
        for (char c : CHARS_2) {
          System.out.print(c);
          lock.notify();
          try {
            lock.wait();
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
        // LOCK.notify();
      }
    }, "t2").start();
  }
  
  enum OptThread {
    T1, T2
  }
  
}
