package com.kaige.advance.zookeeper.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.TimeUnit;

/**
 * zookeeper 配置中心
 *
 * @author liukai 2021年05月03日
 */
@Slf4j
public class ZKConfig {
  
  public static void main(String[] args) {
    // 创建一个 zk 客户端连接
    // 创建一个跟节点，以及配置节点
    try (MyConfig myConfig = new MyConfig()) {
      
      myConfig.loadConfig();
      String config;
      while (true) {
        config = myConfig.getConfig();
        // 利用 watch，监控获取配置节点数据
        if (StringUtils.isBlank(config)) {
          // 没有配置数据
          log.info("config is null, wait load...");
          myConfig.await();
        } else {
          // 有配置数据
          // 业务代码，读取配置数据打印，如果获取不到则阻塞获取
          log.info("获取到数据 config: {}", config);
        }
        
        try {
          TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
          break;
        }
      }
    } catch (Exception e) {
      log.error("myConfig 异常", e);
    }
  }
  
}
