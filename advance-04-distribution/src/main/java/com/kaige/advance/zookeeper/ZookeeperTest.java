package com.kaige.advance.zookeeper;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class ZookeeperTest {
  
  static CountDownLatch countDownLatch = new CountDownLatch(1);
  
  private static ZooKeeper getZooKeeper() {
    return ZookeeperHolder.zk;
  }
  
  public static void main(String[] args) throws KeeperException, InterruptedException {
    // 常规原生 API 的使用
    
    // 10. 异步获取数据，并自定义监听节点，并无限监听
    getData7();
    
    // 9. 获取数据监听事件并执行异步任务
    // getData6();
    
    // 8. 获取数据并执行异步任务
    // getData5();
    
    // 7. 获取子节点并监听
    // getChild2();
    
    // 6. 获取子节点
    // getChild();
    
    // 5. 创建一个节点
    // createData();
    
    // 4. 获取节点数据，并且无限监听
    // getData4();
    
    // 3. 获取节点数据并且使用自己的监听器监听
    // getData3();
    
    // 2. 获取节点数据并且监听一次
    // getData2();
    
    // 1. 直接获取节点数据
    // getData1();
    
    countDownLatch.await();
  }
  
  private static void getData7() {
    getZooKeeper().getData("/kaisai", new Watcher() {
      @Override
      public void process(WatchedEvent event) {
        System.out.println(Thread.currentThread().getName() + "触发自定义监听" + event);
        getData7();
      }
    }, (rc, path, ctx, data, stat) -> {
      System.out.println("异步获取数据，并自定义监听节点，并且无限监听");
      System.out.println("rc = " + rc);
      System.out.println("path = " + path);
      System.out.println("ctx = " + ctx);
      System.out.println("data = " + new String(data, StandardCharsets.UTF_8));
      System.out.println("stat = " + stat);
    }, "");
  }
  
  private static void getData6() {
    getZooKeeper().getData("/kaisai", true, (rc, path, ctx, data, stat) -> {
      System.out.println("异步获取数据，并监听节点");
      System.out.println("rc = " + rc);
      System.out.println("path = " + path);
      System.out.println("ctx = " + ctx);
      System.out.println("data = " + new String(data, StandardCharsets.UTF_8));
      System.out.println("stat = " + stat);
    }, "");
  }
  
  private static void getData5() {
    getZooKeeper().getData("/kaisai", false, (rc, path, ctx, data, stat) -> {
      System.out.println("异步获取数据");
      System.out.println("rc = " + rc);
      System.out.println("path = " + path);
      System.out.println("ctx = " + ctx);
      System.out.println("data = " + new String(data, StandardCharsets.UTF_8));
      System.out.println("stat = " + stat);
    }, "");
  }
  
  private static void getChild2() throws KeeperException, InterruptedException {
    List<String> children = getZooKeeper().getChildren("/kaisai", event -> {
      System.out.println(event);
      countDownLatch.countDown();
    });
    System.out.println("children = " + children);
  }
  
  private static void getChild() throws KeeperException, InterruptedException {
    List<String> children = getZooKeeper().getChildren("/kaisai", false);
    System.out.println("children = " + children);
  }
  
  private static void createData() throws KeeperException, InterruptedException {
    // 添加一个授权用户
    // getZooKeeper().addAuthInfo("digest", "kaige:123456".getBytes(StandardCharsets.UTF_8));
    
    List<ACL> aclList = new ArrayList<>();
    // world 类型
    // ACL acl = new ACL(ZooDefs.Perms.ALL, new Id("world", "anyone"));
    // ip 类型
    // ACL acl = new ACL(ZooDefs.Perms.ALL, new Id("ip", "127.0.0.1"));
    // auth
    // ACL acl = new ACL(ZooDefs.Perms.ALL, new Id("auth", "kaige:123456"));
    // digest
    ACL acl = new ACL(ZooDefs.Perms.ALL, new Id("digest", "kaige:25QwgsAiAYagRwEJEHCGja7ojtA="));
    aclList.add(acl);
    
    getZooKeeper().create("/kaisai/app/digestacl2", "".getBytes(StandardCharsets.UTF_8), aclList,
                          CreateMode.PERSISTENT);
  }
  
  private static void getData4() throws KeeperException, InterruptedException {
    ZooKeeper zk = getZooKeeper();
    byte[] data = zk.getData("/kaisai", new Watcher() {
      @Override
      public void process(WatchedEvent event) {
        // 处理事件
        System.out.println(event);
        // 再次监听
        try {
          byte[] zkData = zk.getData("/kaisai", this, null);
          System.out.println(new String(zkData, StandardCharsets.UTF_8));
        } catch (KeeperException | InterruptedException e) {
          e.printStackTrace();
        }
      }
    }, null);
    System.out.println(new String(data, StandardCharsets.UTF_8));
    Thread.sleep(1000 * 1000);
  }
  
  private static void getData3() throws KeeperException, InterruptedException {
    ZooKeeper zk = getZooKeeper();
    Stat stat = new Stat();
    byte[] data = zk.getData("/kaisai", System.out::println, stat);
    System.out.println(new String(data, StandardCharsets.UTF_8));
    System.out.println(stat);
  }
  
  private static void getData2() throws KeeperException, InterruptedException {
    ZooKeeper zk = getZooKeeper();
    // byte[] data = zk.getData("/kaisai", System.out::println, null);
    byte[] data = zk.getData("/kaisai", true, null);
    System.out.println(new String(data, StandardCharsets.UTF_8));
  }
  
  private static void getData1() throws KeeperException, InterruptedException {
    ZooKeeper zk = getZooKeeper();
    byte[] data = zk.getData("/kaisai", false, null);
    System.out.println(new String(data, StandardCharsets.UTF_8));
  }
  
  public static class ZookeeperHolder {
    
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
    
  }
  
}
