package com.liukai.advance.redis;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * 布隆过滤器的使用
 */
public class BloomFilterTest {
  
  public static void main(String[] args) {
    
    // Redisson 的布隆过滤器
    RedissonClient redissonClient = RedissonTest.getRedissonClient();
    RBloomFilter<Object> bloomFilter = redissonClient.getBloomFilter("redisson:bloom");
    // 分配空间
    bloomFilter.tryInit(1000, 0.03);
    
    for (int i = 0; i < 1000; i++) {
      bloomFilter.add(i + "");
    }
    
    boolean contains = bloomFilter.contains("100");
    System.out.println("contains = " + contains);
    
    redissonClient.shutdown();
    
    // guava 的布隆过滤器
    // guavaBloomFilter();
    
  }
  
  private static void guavaBloomFilter() {
    // 初始化布隆过滤器
    // 1000：期望存入的数据个数，0.001：期望的误差率
    int num = 1000;
    BloomFilter<CharSequence> bloomFilter = BloomFilter
      .create(Funnels.stringFunnel(Charset.forName(StandardCharsets.UTF_8.name())), num, 0.001);
    
    // 把所有的数据存入布隆过滤器
    initBloomFilter(bloomFilter, num);
    
    // 无法从布隆过滤器中删除元素
    
    // 判断布隆过滤器中是否存在数据
    boolean mightContain = bloomFilter.mightContain("101");
    System.out.println("mightContain = " + mightContain);
  }
  
  private static void initBloomFilter(BloomFilter<CharSequence> bloomFilter, int num) {
    // 生成一系列字符串
    for (int i = 0; i < num; i++) {
      bloomFilter.put(i + "");
    }
  }
  
}
