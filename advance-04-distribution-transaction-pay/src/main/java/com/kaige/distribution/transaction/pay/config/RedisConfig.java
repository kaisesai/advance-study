package com.kaige.distribution.transaction.pay.config;

import com.alibaba.fastjson.support.spring.FastJsonRedisSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.integration.redis.util.RedisLockRegistry;

@Configuration
public class RedisConfig {

  @Bean
  public RedisTemplate<String, Object> redisTemplate(
      LettuceConnectionFactory redisConnectionFactory) {
    RedisTemplate<String, Object> template = new RedisTemplate<>();
    // 使用fastjson序列化
    FastJsonRedisSerializer<Object> fastJsonRedisSerializer =
        new FastJsonRedisSerializer<>(Object.class);
    // value值的序列化采用fastJsonRedisSerializer
    template.setValueSerializer(fastJsonRedisSerializer);
    template.setHashValueSerializer(fastJsonRedisSerializer);
    // key的序列化采用StringRedisSerializer
    template.setKeySerializer(new StringRedisSerializer());
    template.setHashKeySerializer(new StringRedisSerializer());
    template.setConnectionFactory(redisConnectionFactory);
    return template;
  }

  /**
   * @param redisConnectionFactory
   * @return redis 锁注册器
   */
  @Bean
  public RedisLockRegistry redisLockRegistry(RedisConnectionFactory redisConnectionFactory) {
    // 该锁实现的优点在于利用本地可重入锁支持相同线程重复获取锁，这一点是优点也是缺点，在有些场景下是不允许重复获取锁的，需要注意，同时还需要注意该释放锁时可能会释放其他线程的锁
    // 具体配置见：https://docs.spring.io/spring-integration/docs/current/reference/html/redis.html#redis-lock-registry
    return new RedisLockRegistry(redisConnectionFactory, "pay_lock");
  }
}
