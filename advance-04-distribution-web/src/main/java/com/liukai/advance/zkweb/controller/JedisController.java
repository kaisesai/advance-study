package com.liukai.advance.zkweb.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JedisController {
  
  @Autowired
  private StringRedisTemplate stringRedisTemplate;
  
  /**
   * 测试主从节点架构，主节点挂掉，哨兵自动切换 master，客户端能否感知。
   * 客户端实现了监听机制，当哨兵把新 master 的消息发布出去，客户端会立刻感知到 master，从而动态切换 master ip。
   *
   * @return
   */
  @RequestMapping(value = "/test-sentinel")
  public String testSentinel() {
    int i = 1;
    while (true) {
      try {
        
        // stringRedisTemplate.opsForValue().setBit()
        
        // stringRedisTemplate.opsForValue().set("kaiser_sentinel" + i, "" +i);
        stringRedisTemplate.opsForValue().set("kaiser_cluster" + i++, "" + i);
        System.out.println("设置 key = " + i);
        Thread.sleep(1000L);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }
  
}
