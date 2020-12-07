package com.liukai.advance.jvm;

public class DeadLockTest {
  
  private static final Object LOCK_1 = new Object();
  
  private static final Object LOCK_2 = new Object();
  
  public static void main(String[] args) {
    
    new Thread(() -> {
      
      synchronized (LOCK_1) {
        
        System.out.println(Thread.currentThread().getName() + "加锁 lock1");
        try {
          Thread.sleep(2000L);
        } catch (InterruptedException e) {
        }
        
        synchronized (LOCK_2) {
          System.out.println(Thread.currentThread().getName() + "加锁 lock2");
        }
      }
      
    }, "线程1").start();
    
    new Thread(() -> {
      
      synchronized (LOCK_2) {
        
        System.out.println(Thread.currentThread().getName() + "加锁 lock2");
        try {
          Thread.sleep(2000L);
        } catch (InterruptedException e) {
        }
        
        synchronized (LOCK_1) {
          System.out.println(Thread.currentThread().getName() + "加锁 lock1");
        }
      }
      
    }, "线程2").start();
    
  }
  
}
