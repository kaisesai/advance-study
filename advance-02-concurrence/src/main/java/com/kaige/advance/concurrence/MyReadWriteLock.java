package com.kaige.advance.concurrence;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 读写锁
 *
 * @author liukai 2021年06月07日23:09:08
 */
public class MyReadWriteLock {
  
  private static final ReentrantLock REENTRANT_LOCK = new ReentrantLock();
  
  private static final ReadWriteLock READ_WRITE_LOCK = new ReentrantReadWriteLock(false);
  
  private static final Lock WRITE_LOCK = READ_WRITE_LOCK.writeLock();
  
  private static final Lock READ_LOCK = READ_WRITE_LOCK.readLock();
  
  private static int value;
  
  /**
   * 读数据
   *
   * @param lock 锁
   * @return
   */
  private static void read(Lock lock) {
    lock.lock();
    try {
      TimeUnit.SECONDS.sleep(1);
      System.out.println("数据已经读完！value: " + value);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new IllegalThreadStateException(e.getMessage());
    } finally {
      lock.unlock();
    }
    
  }
  
  /**
   * 写数据
   *
   * @param lock 锁
   */
  private static void write(Lock lock, int value) {
    lock.lock();
    try {
      TimeUnit.SECONDS.sleep(1);
      MyReadWriteLock.value = value;
      System.out.println("数据已经写完！value: " + value);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new IllegalThreadStateException(e.getMessage());
    } finally {
      lock.unlock();
    }
    
  }
  
  public static void main(String[] args) {
    
    // 创建 10 个线程读数据
    for (int i = 0; i < 10; i++) {
      // 使用读写锁的读锁
      new Thread(() -> read(READ_LOCK)).start();
      // 使用普通的重入锁
      // new Thread(() -> read(REENTRANT_LOCK)).start();
    }
    
    // 创建 2 个线程写数据
    for (int i = 0; i < 2; i++) {
      // 使用读写锁的写锁
      new Thread(() -> write(WRITE_LOCK, ThreadLocalRandom.current().nextInt())).start();
      // 使用普通的重入锁
      // new Thread(() -> write(REENTRANT_LOCK, ThreadLocalRandom.current().nextInt())).start();
    }
    
  }
  
}
