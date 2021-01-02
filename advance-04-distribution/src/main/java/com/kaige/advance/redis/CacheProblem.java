package com.kaige.advance.redis;

import com.google.common.collect.Maps;
import org.apache.commons.lang.math.RandomUtils;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.params.SetParams;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 缓存问题：
 * <p>
 * 1. 缓存穿透
 * <p>
 * 2. 缓存失效
 * <p>
 * 3. 缓存雪崩
 * <p>
 * 4. 热点 key 的重建优化
 */
public class CacheProblem {
  
  private static final Map<String, String> storage = Maps.newHashMap();
  
  static {
    storage.put("order", "123");
  }
  
  
  
  /*
    缓存问题：
     1. 缓存穿透：访问大量不存在缓存中的数据，导致直接穿透缓存层到数据库层，造成数据库层面压力激增
       解决方案：
         1. 缓存空对象
         2. 使用布隆过滤器过滤数据
     2. 缓存失效：大量的 key 在同一时间失效了，导致流量打到了数据库层面
       解决方案：
         1. 缓存预热时使用一个时间段内不同的时间来构建缓存有效期
     3. 缓存雪崩：单一节点的缓存挂了，导致其他节点的压力激增，影响到数据库，从而造成整个系统的雪崩
       解决方案：
         1. 构建高可用的缓存架构，redis 哨兵、集群
         2. 针对缓存的层面使用熔断限流的方案，防止过多的流量进来
         3. 使用本地缓存来抗住一部分流量
         4. 提前演练，应对此类问题做一些预案设定
     4. 热点 key 的重建优化
       - 当一个 key 是热点 key，并发量很大
       - 重建缓存不能在短时间内完成，可能是一个复杂的过程，例如复杂 SQL、多次 IO、多个依赖等
       解决方案：
         在缓存失效的瞬间，有大量线程进来，造成后端负载加大，解决这个问题主要是避免大量线程同时重建缓存，可以利用互斥锁（分布式锁）来解决.
        当查到缓存为空时，加锁，构建完缓存再去释放锁
   */
  
  public static void main(String[] args) {
    
    // 多线程并发构建热点 key 缓存
    mutilThreadBuildHotKey();
    
    // 缓存失效：缓存随机时间
    // String order = getKeyForInvalid("order");
    // System.out.println("order = " + order);
    
    // 缓存穿透：布隆过滤器
    // String value = getValueFromBloomFilter("order");
    // System.out.println("value = " + value);
    
    // 缓存穿透：缓存空值
    // String order = getValueFromCacheAndSetEmptyValue("order1");
    // System.out.println("order = " + order);
  }
  
  private static void mutilThreadBuildHotKey() {
    for (int i = 0; i < 100; i++) {
      new Thread(() -> {
        // 热点 key 重建优化
        try (Jedis jedis = new Jedis()) {
          getAndBuildHotKey("order1", jedis);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }, "线程" + i).start();
    }
  }
  
  /**
   * 热点缓存 key 重建优化
   *
   * @param key
   * @return
   */
  private static String getAndBuildHotKey(String key, Jedis jedis) throws InterruptedException {
    // 从缓存中获取数据
    String value = jedis.get(key);
    if (value != null) {
      System.out
        .println(Thread.currentThread().getName() + " 从缓存中获取数据，key=" + key + ", value=" + value);
      return value;
    }
    // 加锁
    String lockKey = "lock:" + key;
    String setResult = jedis.set(lockKey, "1", SetParams.setParams().nx().ex(180));
    if ("OK".equalsIgnoreCase(setResult)) {
      // 加锁成功
      // 从数据库加载数据
      Thread.sleep(1000);
      value = storage.get(key);
      if (value == null) {
        value = "-999";
      }
      jedis.setex(key, 100, value);
      System.out.println(
        Thread.currentThread().getName() + " 从数据库中获取并缓存到缓存数据，key=" + key + ", value=" + value);
      // 释放锁
      jedis.del(lockKey);
      return value;
    } else {
      // 加锁失败
      // 休眠一会再重试
      System.out.println(
        Thread.currentThread().getName() + " 休眠一会，再重新构建缓存，key=" + key + ", value=" + value);
      Thread.sleep(50);
      return getAndBuildHotKey(key, jedis);
    }
    
  }
  
  /**
   * 缓存失效：
   * 解决方案：缓存时间设置随机数
   *
   * @param key
   * @return
   */
  private static String getKeyForInvalid(String key) {
    try (Jedis jedis = new Jedis()) {
      // 从缓存中获取数据
      String value = jedis.get(key);
      if (value != null) {
        System.out.println("从缓存中获取数据：key=" + key + ", value=" + value);
        return value;
      }
      // 从数据库获取
      value = storage.get(key);
      if (value == null) {
        value = "-999";
      }
      // 设置随机过期时间
      jedis.setex(key, RandomUtils.nextInt(5) + 100, value);
      return value;
    }
  }
  
  /**
   * 缓存穿透：
   * 解决方案二：使用布隆过滤器
   *
   * @param key
   * @return
   */
  private static String getValueFromBloomFilter(String key) {
    
    RedissonClient redissonClient = RedissonTest.getRedissonClient();
    try {
      // Redisson 的布隆过滤器
      RBloomFilter<Object> bloomFilter = redissonClient.getBloomFilter("redisson:bloom:order");
      // 分配空间
      bloomFilter.tryInit(100, 0.03);
      bloomFilter.add("order");
      
      // 先从布隆过滤器中过滤数据
      boolean contains = bloomFilter.contains(key);
      if (!contains) {
        System.out.println("该 key:" + key + " 不存在布隆过滤器");
        return null;
      }
      
      // 从缓存中获取数据
      RBucket<String> order = redissonClient.getBucket("order", StringCodec.INSTANCE);
      String value = order.get();
      if (value != null) {
        System.out.println("从缓存中获取：" + value);
        return value;
      }
      
      // 从数据库中查询
      value = storage.get(key);
      if (value != null) {
        // 放入缓存
        order.set(value);
        System.out.println("从数据库中获取并放入缓存：" + value);
      } else {
        // 一般数据库中肯定存在某个元素，如果没有元素，说明数据被删除了，这样的话，就得重新初始化布隆过滤器，或者配合缓存设置一个空值
        // 设置空值到缓存，过期时间 300 秒
        value = "-999";
        order.set(value, 300, TimeUnit.SECONDS);
        System.out.println("从数据库中无法获取，设置默认值并放入缓存：" + value);
      }
      return value;
    } finally {
      redissonClient.shutdown();
      
    }
  }
  
  /**
   * 缓存穿透：
   * 解决方案一：值为空时，设置空值到缓存
   *
   * @param key
   * @return
   */
  private static String getValueFromCacheAndSetEmptyValue(String key) {
    try (Jedis jedis = new Jedis()) {
      
      // 缓存穿透：缓存空对象
      // 从缓存中获取
      String value = jedis.get(key);
      if (value != null) {
        return value;
      }
      
      // 从数据库中查询
      value = storage.get(key);
      if (value == null) {
        value = "-999";
        // 写入缓存，并设置过期时间
        jedis.setex(key, RandomUtils.nextInt(1000), value);
      } else {
        // 写入缓存
        jedis.set(key, value);
      }
      return value;
    }
  }
  
}
