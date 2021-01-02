package com.kaige.advance.zookeeper;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.framework.recipes.locks.Locker;
import org.apache.curator.retry.RetryForever;

public class ZkCuratorTest {
  
  public static void main(String[] args) throws Exception {
    CuratorFramework client = CuratorFrameworkFactory
      .newClient(ZookeeperTest.ZookeeperHolder.CONNECT_STR, new RetryForever(1000));
    client.start();
    
    getData(client);
    
    distributionLock(client);
    
    Thread.sleep(100 * 1000);
    
    client.close();
    
  }
  
  private static void getData(CuratorFramework client) throws Exception {
    byte[] bytes = client.getData().forPath("/kaisai");
    System.out.println("new String(bytes) = " + new String(bytes));
  }
  
  private static void distributionLock(CuratorFramework client) {
    final InterProcessMutex lock = new InterProcessMutex(client, "/kaisai/lock");
    Runnable runnable = () -> {
      
      try (Locker locker = new Locker(lock)) {
        System.out.println(Thread.currentThread().getName() + " 获取锁");
        Thread.sleep(1000 * 5);
      } catch (Exception e) {
        e.printStackTrace();
      }
      System.out.println(Thread.currentThread().getName() + " 释放锁");
    };
    
    new Thread(runnable, "线程1").start();
    
    new Thread(runnable, "线程2").start();
  }
  
}
