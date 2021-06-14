package com.kaige.advance.distribution.web.config;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import org.springframework.context.annotation.Bean;

import java.net.InetSocketAddress;

// @EnableScheduling
// @Configuration
public class CanalConfig {
  
  @Bean(value = "canalBookConnector", destroyMethod = "disconnect")
  public CanalConnector canalBookConnector() {
    // canal 的集群中只能使用一个，只有当其中一个挂了，才会切换下一个
    CanalConnector canalConnector = CanalConnectors
      .newSingleConnector(new InetSocketAddress("127.0.0.1", 11111), "book", "", "");
    
    canalConnector.connect();
    // 指定filter，格式{database}.{table}
    canalConnector.subscribe("eshop.read_book_pd"); // 指定我们要监听的表 秒杀的订单很大
    // 如果你们要监听的表，这里最好建议用多线程，每一个canal服务只处理几张表就行,
    // 我给你们一个例子
    // canalConnector.subscribe("test.read_book_pd");
    // 回滚寻找上次中断的为止
    canalConnector.rollback();
    return canalConnector;
  }
  
}
