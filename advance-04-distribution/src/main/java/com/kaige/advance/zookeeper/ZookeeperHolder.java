package com.kaige.advance.zookeeper;

import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.Objects;

/** zk 持有类 */
public class ZookeeperHolder {
  
  public static final String CONNECT_STR = "www.kaige.com:2181";
  
  private static final int SESSION_TIMEOUT = 5000;
  
  private static ZooKeeper zk;
  
  static {
    try {
      Watcher watcher = event -> System.out.println(Thread.currentThread().getName() + event);
      zk = new ZooKeeper(CONNECT_STR, SESSION_TIMEOUT, watcher);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  private ZookeeperHolder() {
    throw new IllegalStateException("不能实例化这个类");
  }
  
  public static ZooKeeper getZk() {
    if (Objects.isNull(zk)) {
      throw new IllegalStateException("ZK 客户端未初始化");
    }
    return zk;
  }
  
}
