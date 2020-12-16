package com.liukai.advance.redis;

import org.apache.commons.lang.RandomStringUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.params.SetParams;

import java.io.Closeable;
import java.util.Collections;

/**
 * 实现一个 redis 分布式锁
 * 要求满足的功能：
 * 1. 自己创建的锁只有自己才能释放
 * 2. 锁有过期时间
 * 3. 锁支持自动续命
 * 4. 支持指定超时时间内获取锁功能
 */
public class RedisLock implements Closeable {
  
  private static final String LOCK_PREFFIX = "lock:";
  
  private static final String LUA_UNLOCK = "if redis.call('get', KEYS[1]) == ARGV[1] " + "then "
    + "  return redis.call('del', KEYS[1]) " + "else " + " return 0 " + "end ";
  
  private final Jedis jedis;
  
  private final String lockName;
  
  // 超时时间默认为 -1 秒，永远阻塞
  private final int timeout = -1;
  
  // 过期时间默认为 5 秒
  private int secondsToExpire = 5;
  
  // 监视器
  private Thread watchdog;
  
  private String lockValue;
  
  private String lockKey;
  
  private volatile boolean isLocked = false;
  
  public RedisLock(Jedis jedis, String lockName, int secondsToExpire) {
    this.jedis = jedis;
    this.lockName = lockName;
    this.secondsToExpire = secondsToExpire;
    
    init();
  }
  
  /**
   * 睡眠一定 mills，加上 nanos
   *
   * @param millis
   */
  private static void sleep(long millis) {
    try {
      Thread.sleep(millis);
    } catch (Exception e) {
      System.out.println(Thread.currentThread().getName() + "线程被中断");
    }
  }
  
  private void init() {
    // 锁的 key
    lockKey = LOCK_PREFFIX + lockName;
    
    String randomize = randomLockValue();
    // 锁的值
    lockValue = randomize;
    // 监视狗
    watchdog = new Thread(() -> {
      try {
        isLocked = true;
        // 下次续命的时间
        int continueTime = secondsToExpire * 1000 / 3;
        System.out.println(Thread.currentThread().getName() + " 开始执行监视狗任务");
        // 不断重试
        // 功能：判断当前锁是否还在运行，如果运行时间超过了锁的过期时间的一半，就给锁延长时间
        while (true) {
          // 查询锁是否存在，不存在直接退出
          Boolean exists = null;
          try {
            exists = jedis.exists(lockKey);
          } catch (Exception e) {
            e.printStackTrace();
          }
          if (exists == null || !exists) {
            System.out
              .println(Thread.currentThread().getName() + " lockKey: " + lockKey + " 不存在，停止监控");
            return;
          }
          
          // 查询锁的值是否为自己的值，不是自己的值直接退出
          String queryLockValue = jedis.get(lockKey);
          if (!lockValue.equalsIgnoreCase(queryLockValue)) {
            System.out.println(Thread.currentThread().getName() + "锁 lockKey: " + lockKey
                                 + " 的值 lockValue不是自己创建的，停止监控。lockValue: " + lockValue
                                 + ", queryLockValue: " + queryLockValue);
            return;
          }
          
          // 给锁续命
          jedis.pexpire(lockKey, secondsToExpire * 1000L);
          System.out.println(
            Thread.currentThread().getName() + " 锁续命，lockey: " + lockKey + ", lockValue: "
              + lockValue + ", expireTime: " + secondsToExpire + "s");
          
          // 休眠过期时间的 1/3
          sleep(continueTime);
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
      
    }, "redis-lock-watchdog-" + randomize);
  }
  
  /**
   * 尝试在指定时间内获取锁
   *
   * @param timeout 超时时间，单位秒
   * @return
   */
  public boolean tryLock(int timeout) {
    System.out.println(Thread.currentThread().getName() + " 开始获取锁 tryLock");
    long timeoutNanos = timeout * 1_000_000_000L;
    long currentNanoTime = System.nanoTime();
    SetParams params = getSetParams();
    String result;
    while ((System.nanoTime() - currentNanoTime) < timeoutNanos) {
      result = getSet(params);
      if (isOK(result)) {
        // 获取锁，启动监视狗
        watchdog.start();
        return true;
      }
      sleep(10);
    }
    System.out.println(Thread.currentThread().getName() + " tryLock 时间超时，获取锁失败");
    return false;
  }
  
  private String getSet(SetParams params) {
    return jedis.set(lockKey, lockValue, params);
  }
  
  private SetParams getSetParams() {
    return SetParams.setParams().ex(secondsToExpire).nx();
  }
  
  /**
   * 获取锁，无论结果如果都立即返回
   */
  public boolean lock() {
    System.out.println(Thread.currentThread().getName() + " 开始获取锁 lock");
    // 无限尝试获取锁
    SetParams params = getSetParams();
    String result = getSet(params);
    if (isOK(result)) {
      // 获取锁，启动监视狗
      watchdog.start();
      return true;
    }
    return false;
  }
  
  /**
   * 阻塞的获取锁
   */
  public boolean lockBlock() {
    System.out.println(Thread.currentThread().getName() + " 开始获取锁 lockBlock");
    SetParams params = getSetParams();
    String result;
    while (true) {
      result = getSet(params);
      if (isOK(result)) {
        // 获取锁，启动监视狗
        watchdog.start();
        return true;
      }
      // 睡眠一定时间
      sleep(100);
    }
  }
  
  private boolean isOK(String result) {
    return "OK".equalsIgnoreCase(result);
  }
  
  private String randomLockValue() {
    return RandomStringUtils.randomAlphabetic(5);
  }
  
  public boolean unlock() {
    if (isLocked) {
      // 先中断监视狗
      System.out.println(Thread.currentThread().getName() + " 关闭监视狗线程：" + watchdog.getName());
      watchdog.interrupt();
    }
    
    // 执行 lua 解锁脚本
    Long aLong = (Long) jedis
      .eval(LUA_UNLOCK, Collections.singletonList(lockKey), Collections.singletonList(lockValue));
    boolean result = aLong == 1;
    if (1 == aLong) {
      isLocked = false;
    }
    System.out.println(Thread.currentThread().getName() + " unlock: " + result);
    return result;
    
  }
  
  @Override
  public void close() {
    try {
      unlock();
    } finally {
      jedis.close();
    }
    // 释放 jedis
  }
  
}
