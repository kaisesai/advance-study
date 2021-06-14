package com.kaige.advance.concurrence;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class MyCondition {
  
  public static void main(String[] args) throws InterruptedException {
    
    // 创建一把锁
    ReentrantLock lock = new ReentrantLock();
    // 创建一个锁上的条件对象
    Condition c1 = lock.newCondition();
    
    // t1 线程调用条件等待
    new Thread(() -> {
      // 加锁
      lock.lock();
      System.out.println(Thread.currentThread().getName() + "：加锁！");
      try {
        System.out.println(Thread.currentThread().getName() + "：太累了，我先睡会，一会叫醒我哈！");
        // 让条件等待
        c1.await();
        System.out.println(Thread.currentThread().getName() + "：我醒来了！");
      } catch (InterruptedException e) {
        e.printStackTrace();
      } finally {
        try {
          Thread.sleep(1000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        System.out.println(Thread.currentThread().getName() + "：解锁！");
        // 解锁
        lock.unlock();
      }
    }, "t1").start();
    
    Thread.sleep(1000);
    
    // t2 线程调用条件唤醒
    new Thread(() -> {
      // 加锁
      lock.lock();
      System.out.println(Thread.currentThread().getName() + "：加锁！");
      try {
        Thread.sleep(1000);
        System.out.println(Thread.currentThread().getName() + "：开饭了，赶紧起来吧！");
        // 条件唤醒
        c1.signal();
      } catch (InterruptedException e) {
        e.printStackTrace();
      } finally {
        try {
          Thread.sleep(5000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        System.out.println(Thread.currentThread().getName() + "：解锁！");
        // 解锁
        lock.unlock();
      }
    }, "t2").start();
  }
  
}
