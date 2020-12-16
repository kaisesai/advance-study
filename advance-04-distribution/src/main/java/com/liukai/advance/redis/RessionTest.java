package com.liukai.advance.redis;

import org.apache.commons.lang.math.RandomUtils;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import java.util.concurrent.CountDownLatch;

public class RessionTest {
  
  private static final CountDownLatch countDownLatch = new CountDownLatch(101);
  
  public static void main(String[] args) throws InterruptedException {
    // 默认连接地址 127.0.0.1:6379
    // RedissonClient redisson = Redisson.create();
    Config config = new Config();
    config.useSingleServer().setAddress("redis://127.0.0.1:6379");
    RedissonClient redisson = Redisson.create(config);
    
    String orderId = "10086";
    lock(redisson, orderId);
    
    tryLock(redisson, orderId);
    
    countDownLatch.await();
    redisson.shutdown();
  }
  
  private static void tryLock(RedissonClient redisson, String orderId) {
    
    for (int i = 0; i < 100; i++) {
      new Thread(() -> {
        RLock lock = redisson.getLock(orderId);
        // lock 无参方法，会启动一个看门狗，定时检查
        lock.lock();
        try {
          // lock 有参方法，不会启动看门狗，只会在指定的时间内释放锁
          System.out.println(Thread.currentThread().getName() + "加锁成功");
          Thread.sleep(RandomUtils.nextInt(10) * 100L);
          System.out.println(Thread.currentThread().getName() + "释放成功");
        } catch (InterruptedException e) {
          e.printStackTrace();
        } finally {
          countDownLatch.countDown();
          lock.unlock();
        }
      }, "线程" + i).start();
    }
    
  }
  
  private static void lock(RedissonClient redisson, String orderId) {
    RLock lock = redisson.getLock(orderId);
    // lock 无参方法，会启动一个看门狗，定时检查
    lock.lock();
    try {
      // lock 有参方法，不会启动看门狗，只会在指定的时间内释放锁
      System.out.println(Thread.currentThread().getName() + "加锁成功");
      Thread.sleep(1000);
      System.out
        .println(Thread.currentThread().getName() + " lock.isLocked() = " + lock.isLocked());
      System.out.println(Thread.currentThread().getName() + "释放成功");
    } catch (InterruptedException e) {
      e.printStackTrace();
    } finally {
      countDownLatch.countDown();
      lock.unlock();
    }
  }
  
}
