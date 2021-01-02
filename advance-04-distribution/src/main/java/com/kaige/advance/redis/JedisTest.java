package com.kaige.advance.redis;

import com.google.common.collect.Sets;
import redis.clients.jedis.*;

import java.util.HashSet;
import java.util.Set;

public class JedisTest {
  
  public static void main(String[] args) throws InterruptedException {
    JedisPool pool = getJedisPool();
    
    // 从连接池中获取一个连接
    String orderNo = "10086";
    testlock(pool, orderNo);
    Thread.sleep(1000);
    testTryLock(pool, orderNo, 20);
    
    // testLockBlock(pool, orderNo);
    
    // 测试 jedis 集群
    // testJedisCluster();
    
    // 测试 jedis 哨兵连接
    // testJedisSentinel();
    
    // 测试 jedis 连接
    // testJedis();
    
  }
  
  private static void testLockBlock(JedisPool pool, String orderNo) {
    
    for (int i = 0; i < 200; i++) {
      new Thread(() -> {
        try (RedisLock lock = new RedisLock(pool.getResource(), orderNo, 10)) {
          // 加锁
          lock.lockBlock();
          // 执行业务代码
          System.out.println("lockBlock 获取成功");
          System.out.println(Thread.currentThread().getName() + "执行业务代码");
          Thread.sleep(1000 * 3);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }, "线程testTryLock" + i).start();
    }
    
  }
  
  private static void testTryLock(JedisPool pool, String orderNo, int timeout) {
    
    for (int i = 0; i < 100; i++) {
      new Thread(() -> {
        try (RedisLock lock = new RedisLock(pool.getResource(), orderNo, 10)) {
          // 加锁
          boolean tryLock = lock.tryLock(timeout);
          if (tryLock) {
            // 执行业务代码
            System.out.println("trylock 获取成功");
            System.out.println(Thread.currentThread().getName() + "执行业务代码");
            Thread.sleep(1000 * 3);
          }
        } catch (Exception e) {
          e.printStackTrace();
        }
      }, "线程testTryLock" + i).start();
    }
    
  }
  
  private static void testlock(JedisPool pool, String orderNo) {
    new Thread(() -> {
      try (RedisLock lock = new RedisLock(pool.getResource(), orderNo, 10)) {
        // 加锁
        lock.lock();
        // 执行业务代码
        System.out.println("lock = " + lock);
        System.out.println(Thread.currentThread().getName() + "执行业务代码");
        Thread.sleep(1000 * 3);
        // 释放锁
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }, "线程testlock").start();
  }
  
  private static JedisPool getJedisPool() {
    // 测试 redis 锁
    JedisPoolConfig poolConfig = getJedisPoolConfig();
    // timeout 既是连接时间又是超时时间
    return new JedisPool(poolConfig, "127.0.0.1", 6379, 3000, null);
  }
  
  public static void testJedisCluster() {
    JedisPoolConfig jedisPoolConfig = getJedisPoolConfig();
    
    HashSet<HostAndPort> hostAndPorts = Sets.newHashSet(new HostAndPort("127.0.0.1", 8001));
    try (
      JedisCluster jedisCluster = new JedisCluster(hostAndPorts, 6000, 5000, 10, "kaige",
                                                   jedisPoolConfig)
    ) {
      System.out.println(
        "jedisCluster.set(\"cluster\", \"kaiser\") = " + jedisCluster.set("cluster", "kaiser"));
      System.out.println("jedisCluster.get(\"cluster\") = " + jedisCluster.get("cluster"));
    }
    
  }
  
  public static void testJedisSentinel() {
    JedisPoolConfig poolConfig = getJedisPoolConfig();
    
    Set<String> sentinels = Sets
      .newHashSet("127.0.0.1:26379", "127.0.0.1:26380", "127.0.0.1:26381");
    JedisSentinelPool sentinelPool = new JedisSentinelPool("mymaster", sentinels, poolConfig, 3000,
                                                           null);
    
    try (Jedis jedis = sentinelPool.getResource()) {
      System.out
        .println("jedis.set(\"sentinel\", \"kaiser\") = " + jedis.set("sentinel", "kaiser"));
      System.out.println("jedis.get(\"sentinel\") = " + jedis.get("sentinel"));
    }
    
  }
  
  public static void testJedis() {
    JedisPool pool = getJedisPool();
    
    // 从连接池中获取一个连接
    try (Jedis jedis = pool.getResource()) {
      System.out.println("jedis.set(\"single\", \"kaiser\") = " + jedis.set("single", "kaiser"));
      System.out.println("jedis.get(\"single\") = " + jedis.get("single"));
      
      // 管道
      // lua 脚本
    }
  }
  
  private static JedisPoolConfig getJedisPoolConfig() {
    JedisPoolConfig poolConfig = new JedisPoolConfig();
    poolConfig.setMaxTotal(20);
    poolConfig.setMaxIdle(10);
    poolConfig.setMinIdle(5);
    return poolConfig;
  }
  
}
