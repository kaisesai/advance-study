package com.liukai.advance.zookeeper;

import com.github.zkclient.IZkDataListener;
import com.github.zkclient.ZkClient;
import lombok.Data;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 分布式的读写锁
 */
public class ZkLock {
  
  public static final String ROOT_PATH = "/kaisai/lock";
  
  private static final ZkLock ZK_LOCK = new ZkLock();
  
  private ZkClient zkClient;
  
  private ZkLock() {
    init();
  }
  
  public static ZkLock getZkLockInstance() {
    return ZK_LOCK;
  }
  
  private void init() {
    zkClient = new ZkClient(ZookeeperTest.ZookeeperHolder.CONNECT_STR, 5000, 10000);
    // 创建根节点
    if (!zkClient.exists(ROOT_PATH)) {
      zkClient.createPersistent(ROOT_PATH, true);
    }
  }
  
  /**
   * 获取锁
   * 流程：
   * 1. 创建锁信息
   * 2. 尝试激活锁，即获取所有指定业务锁节点，获取最小的节点是否为自己，如果是，那就激活成功，否则就添加监听，并且阻塞等待
   * 3. 在规定时间内返回锁
   *
   * @param business 业务场景
   * @param lockId   锁 id
   * @param timeout  获取锁超时时间
   * @return
   */
  public Lock lock(String business, String lockId, long timeout, LockType lockType) {
    // 创建锁业务节点
    String businessPath = ROOT_PATH + "/" + business;
    if (!zkClient.exists(businessPath)) {
      zkClient.createPersistent(businessPath);
    }
    
    Lock lock = createLock(lockId, lockType, businessPath);
    // 激活锁
    boolean activeLock = tryActiveLock(lock);
    // 锁激活失败则休眠，等待其他监听器线程通知
    if (!activeLock) {
      // 睡眠一段时间
      try {
        synchronized (lock) {
          lock.wait(timeout);
        }
      } catch (InterruptedException e) {
        throw new RuntimeException("获取锁失败", e);
      }
      
      if (!lock.isActive()) {
        throw new RuntimeException("获取锁失败，超时");
      }
    }
    System.out.println(Thread.currentThread().getName() + " 获取到 lock = " + lock);
    return lock;
    
  }
  
  public void releaseLock(Lock lock) {
    // 删除锁临时节点
    zkClient.delete(lock.path);
    System.out.println(Thread.currentThread().getName() + " 释放了 lock = " + lock);
  }
  
  /**
   * 创建锁节点
   *
   * @param lockId
   * @param lockType
   * @param businessPath
   * @return
   */
  private Lock createLock(String lockId, LockType lockType, String businessPath) {
    // 创建锁节点
    String lockPath = zkClient.createEphemeralSequential(businessPath + "/" + lockId,
                                                         lockType.name()
                                                           .getBytes(StandardCharsets.UTF_8));
    // 创建 lock 对象
    Lock lock = new Lock(businessPath, lockId, lockPath, lockType);
    return lock;
  }
  
  /**
   * 尝试激活锁
   *
   * @param businessPath
   * @param lock
   */
  private boolean tryActiveLock(Lock lock) {
    // 激活锁
    // 获取全部的锁业务节点，按照升序排序
    List<String> allLockPaths = zkClient.getChildren(lock.businessPath).stream().sorted()
      .map(p -> lock.getBusinessPath() + "/" + p).collect(Collectors.toList());
    
    // 获取第一个锁节点
    String firstLockPath = allLockPaths.get(0);
    if (lock.getPath().equals(firstLockPath)) {
      // 第一个节点就是自己，则表示激活成功
      // 设置激活状态
      lock.setActive(true);
      return true;
    } else {
      // 否则，获取它的上一个节点，监听变化
      String prewLockPath = allLockPaths.get(allLockPaths.indexOf(lock.getPath()) - 1);
      // 对上一个节点添加监听器，监听节点变化
      zkClient.subscribeDataChanges(prewLockPath, new IZkDataListener() {
        @Override
        public void handleDataChange(String dataPath, byte[] data) throws Exception {
          // do no thing
        }
        
        @Override
        public void handleDataDeleted(String dataPath) throws Exception {
          // 节点被删除，则尝试再次激活
          boolean tryActiveLock = tryActiveLock(lock);
          if (tryActiveLock) {
            synchronized (lock) {
              // 唤醒主线程
              lock.notify();
            }
          }
          System.out.println(
            Thread.currentThread().getName() + " 监听到上一个锁节点被删除，执行激活锁，deletedDataPath：" + dataPath
              + ", 锁信息：" + lock);
        }
      });
      return false;
    }
  }
  
  public enum LockType {
    READ,// 读锁
    WRITE// 写锁
  }
  
  @Data
  public static class Lock {
    
    private String businessPath;
    
    private String lockId;
    
    private String path;// 节点路径
    
    private LockType lockType;
    
    private volatile boolean isActive = false;
    
    public Lock(String businessPath, String lockId, String path, LockType lockType) {
      this.businessPath = businessPath;
      this.lockId = lockId;
      this.path = path;
      this.lockType = lockType;
    }
    
  }
  
}
