package com.kaige.advance.netty.herostory.util;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Objects;

/** Redis 工具类 */
@Slf4j
public class RedisUtil {
  
  private static JedisPool pool;
  
  public static void main(String[] args) {
    RedisUtil.init();
    
    try (Jedis jedis = RedisUtil.getJedis()) {
      String value = jedis.get("mybitkey");
      System.out.println("value = " + value);
    }
  }
  
  /** 初始化 */
  public static void init() {
    if (Objects.nonNull(pool)) {
      return;
    }
    JedisPoolConfig poolConfig = new JedisPoolConfig();
    poolConfig.setMaxTotal(20);
    poolConfig.setMaxIdle(10);
    poolConfig.setMinIdle(5);
    pool = new JedisPool(poolConfig, "www.kaige.com", 6379, 3000, "redis_ecs02");
  }
  
  /**
   * 获取 Jedis
   *
   * @return
   */
  public static Jedis getJedis() {
    if (Objects.isNull(pool)) {
      throw new IllegalStateException("JedisPool未初始化");
    }
    return pool.getResource();
  }
  
}
