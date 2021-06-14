package com.kaige.advance.concurrence;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * 双资源锁
 *
 * <p>采用一个共享模式的资源
 */
public class TwinsLock implements Lock {
  
  private final Sync sync = new Sync(2);
  
  public static void main(String[] args) {
    Lock lock = new TwinsLock();
    for (int i = 0; i < 10; i++) {
      new Thread(() -> {
        lock.lock();
        System.out.println(
          Thread.currentThread().getName() + " 线程，获取了锁" + System.currentTimeMillis() / 1000);
        System.out.println();
        try {
          Thread.sleep(1000);
          
        } catch (InterruptedException e) {
          e.printStackTrace();
        } finally {
          lock.unlock();
        }
      }, "t" + i).start();
    }
  }
  
  @Override
  public void lock() {
    sync.acquireShared(1);
  }
  
  @Override
  public void lockInterruptibly() throws InterruptedException {
    sync.acquireSharedInterruptibly(1);
  }
  
  @Override
  public boolean tryLock() {
    return sync.tryAcquireShared(1) >= 0;
  }
  
  @Override
  public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
    sync.tryAcquireSharedNanos(1, unit.toNanos(time));
    return false;
  }
  
  @Override
  public void unlock() {
    sync.releaseShared(1);
  }
  
  @Override
  public Condition newCondition() {
    return null;
  }
  
  /** 自己实现的同步器 */
  private static class Sync extends AbstractQueuedSynchronizer {
    
    public Sync(int count) {
      if (count <= 0) {
        throw new IllegalArgumentException("count must large than zero.");
      }
      // 初始化资源
      setState(count);
    }
    
    /**
     * 尝试获取共享模式的资源
     *
     * @param arg
     * @return
     */
    @Override
    protected int tryAcquireShared(int arg) {
      // 无限循环尝试获取，直到获取成功或者资源不足
      for (; ; ) {
        // 先判断是否有排队的前驱节点
        if (hasQueuedPredecessors()) {
          return -1;
        }
        // 当前资源
        int state = getState();
        // 新的资源
        int newState = state - arg;
        // 检查新的资源
        if (newState < 0 || compareAndSetState(state, newState)) {
          return newState;
        }
      }
    }
    
    /**
     * 尝试释放共享模式的资源
     *
     * @param arg
     * @return
     */
    @Override
    protected boolean tryReleaseShared(int arg) {
      // 无限循环尝试释放，直到成功或者资源不足
      for (; ; ) {
        int state = getState();
        int newState = state + arg;
        if (compareAndSetState(state, newState)) {
          return true;
        }
      }
    }
    
  }
  
}
