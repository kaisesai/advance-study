package com.kaige.advance.zookeeper.lock;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 自定义锁
 *
 * @author liukai 2021年05月04日
 */
@Slf4j
@Data
public class MyLock implements Watcher, AsyncCallback.StatCallback, AsyncCallback.Create2Callback,
                               AsyncCallback.Children2Callback, AutoCloseable {
  
  public static final String LOCK_PATH = "/lock";
  
  public static final String LOCK_KEY_PREFIX = LOCK_PATH + "/";
  
  private final String businessKey;
  
  private final ZooKeeper zooKeeper;
  
  private final CountDownLatch countDownLatch = new CountDownLatch(1);
  
  private long defaultTime;
  
  /** 锁的路径，格式为 /lock/{businessKey} */
  private String businessLockPath;
  
  /** 最终锁的 key, /lock/order1230000000085 */
  private volatile String lockKey;
  
  /** 锁类型 */
  private LockType lockType;
  
  /** 锁的次数 */
  private AtomicInteger lockTime = new AtomicInteger(0);
  
  public MyLock(ZooKeeper zooKeeper, String businessKey, LockType lockType) {
    if (StringUtils.isBlank(businessKey)) {
      throw new IllegalArgumentException("businessKey is not null");
    }
    this.zooKeeper = zooKeeper;
    this.businessKey = businessKey;
    this.lockType = lockType;
    this.businessLockPath = LOCK_KEY_PREFIX + businessKey;
  }
  
  /**
   * 构建锁 value，格式为 {lockType}_{lockTime}
   *
   * @param lockType
   * @param lockTime
   * @return
   */
  public static String buildLockValue(LockType lockType, int lockTime) {
    return lockType + "_" + lockTime;
  }
  
  /**
   * 获取锁
   *
   * @return
   */
  public boolean lock() {
    log.info("准备获取锁");
    // 判断 lock 目录是否存在，TODO 这里是否要监控这个节点？
    zooKeeper.exists(LOCK_PATH, this, this, "a");
    try {
      countDownLatch.await();
      log.info("获取到锁, lockKey:{}", lockKey);
    } catch (InterruptedException e) {
      log.error("await error", e);
      return false;
    }
    return true;
  }
  
  /** 释放锁 */
  public void unLock() {
    if (StringUtils.isBlank(this.lockKey)) {
      return;
    }
    
    try {
      Stat stat = zooKeeper.exists(this.lockKey, false);
      if (stat == null) {
        return;
      }
      
      byte[] data = zooKeeper.getData(this.lockKey, false, stat);
      String lockValue = new String(data);
      
      String[] split = StringUtils.split(lockValue, "_");
      int lockKeyTime = NumberUtils.toInt(split[1], 0);
      lockKeyTime--;
      if (lockKeyTime > 0) {
        // 重入锁减少锁调用次数
        lockTime.set(lockKeyTime);
        log.info("释放锁 减少锁调用次数 lockKey:{}", lockKey);
        zooKeeper.setData(this.lockKey, buildLockValue(lockType, lockTime.get()).getBytes(),
                          stat.getVersion());
      } else {
        // 直接删除
        log.info("释放锁 delete lockKey:{}", lockKey);
        zooKeeper.delete(this.lockKey, stat.getVersion());
      }
      log.info("释放锁 lockKey:{}", lockKey);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  /**
   * 处理 exists 方法获取到的数据
   *
   * @param rc
   * @param path
   * @param ctx
   * @param stat
   */
  @Override
  public void processResult(int rc, String path, Object ctx, Stat stat) {
    log.info("监听到 StatCallback 回调 rc:{}, path:{}, ctx:{}, stat:{}", rc, path, ctx, stat);
    // LOCK_PATH 目录
    if (StringUtils.equals(LOCK_PATH, path)) {
      // 不存在，则创建
      if (stat == null) {
        try {
          log.info("创建 LOCK_PATH 路径 /lock, lockKey:{}", this.lockKey);
          zooKeeper
            .create(LOCK_PATH, "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT,
                    this, "lockPath");
        } catch (Exception e) {
          log.error("create lock_path error", e);
          return;
        }
      } else {
        // 创建锁
        createLock();
      }
      
    } else {
      // TODO: 2021/5/4 检测到 path 为我们的业务 key
      // 监听到上一个节点的变化，不管，只关心是否被删除
      
    }
  }
  
  /** 创建锁 */
  private void createLock() {
    // log.info("createLock lockkey:{}, lockValue:{}", this.lockKey, lockValue);
    log.info("开始创建锁createLock...");
    // 创建临时序号节点
    zooKeeper.create(businessLockPath, "init".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,
                     CreateMode.EPHEMERAL_SEQUENTIAL, this, "create lock");
  }
  
  /**
   * 监听到事件变化
   *
   * @param event
   */
  @Override
  public void process(WatchedEvent event) {
    log.info("监听到事件 event:{}", event);
    String path = event.getPath();
    if (!StringUtils.equals(LOCK_PATH, path)) {
      if (event.getType() == Event.EventType.NodeDeleted) { // 调用获取子节点信息
        log.info("节点被删除 path: {}", path);
        zooKeeper.getChildren(LOCK_PATH, false, this, "getChild2");
      }
    }
  }
  
  @Override
  public void close() throws Exception {
    unLock();
  }
  
  @Override
  public void processResult(int rc, String path, Object ctx, String name, Stat stat) {
    log.info("监听到 create2callback 回调，rc:{}, path:{}, ctx:{}, name:{}, stat:{}", rc, path, ctx, name,
             stat);
    // create2callback 回调
    if (StringUtils.equals(LOCK_PATH, path)) {
      // lockPath 创建成功
      // 判断 lock 目录是否存在
      // zooKeeper.exists(LOCK_PATH, this, this, "b");
      // 这里可以直接调用创建业务锁，不用再调监听回调方法了
      createLock();
    } else if (StringUtils.equals(businessLockPath, path)) {
      // businessLockPath 创建成功
      // 保存锁信息
      // /lock/order1230000000050
      log.info("path:{}, name:{}, businessKey:{}", path, name, businessKey);
      this.lockKey = name;
      
      // 执行创建真正锁业务 key
      String lockValue = buildLockValue(lockType, lockTime.incrementAndGet());
      
      try {
        // 这里使用同步阻塞方式，防止监听与回调太快，导致主线程全部阻塞
        log.info("set data lockKey:{}, lockValue:{}", this.lockKey, lockValue);
        zooKeeper.setData(name, lockValue.getBytes(), stat.getVersion());
        
        log.info("get getChildren lockKey:{}, lockValue:{}", this.lockKey, lockValue);
        // 获取 lockPath 的子路径，即所有的锁序列信息
        zooKeeper.getChildren(LOCK_PATH, false, this, "getChild");
      } catch (KeeperException | InterruptedException e) {
        log.error("set data error lockKey:{}, lockValue:{}", this.lockKey, lockValue);
      }
    }
  }
  
  @Override
  public void processResult(int rc, String path, Object ctx, List<String> children, Stat stat) {
    log.info("监听到 Children2Callback 回调 lockKey:{}, rc:{}, path:{}, ctx:{}, stat:{}, children:{}",
             lockKey, rc, path, ctx, stat, children);
    // TODO: 2021/5/4
    if (Objects.isNull(children)) {
      log.warn("children is null lockKey:{}", this.lockKey);
      return;
    }
    // 对子节点进行排序
    Collections.sort(children);
    
    // 这里注意是读锁或者写锁
    
    // 注意需要解析锁的 key
    String key = StringUtils.substringAfter(lockKey, LOCK_KEY_PREFIX);
    int indexOf = children.indexOf(key);
    log.info("parse lockkey:{}, key:{}, indexOf:{}", lockKey, key, indexOf);
    // TODO: 2021/5/4 indexOf 小于 0 时？
    if (indexOf < 0) {
      log.info("indexOf < 0 error 继续监听 lockKey:{}", this.lockKey);
      // zooKeeper.getChildren(LOCK_PATH, false, this, "getChild");
      return;
    }

    /*
     读锁的情况
       获取比它小的第一个写锁即可
       可以独占锁，也可以是共享锁
       共享锁：
         1. 没有比自己小的节点，或者 2. 所有比自己小的都是读请求

     写锁的情况
       监听上一个读锁即可
       一定是独占锁
    */
    
    // 获取第一个
    if (indexOf == 0) {
      // 当前锁获取成功
      log.info("获取到锁：path:{}, lockKey:{}, key:{}, indexOf:{}", path, this.lockKey, key, indexOf);
      countDownLatch.countDown();
      return;
    }
    
    // if (lockType == LockType.READ) {
    //   // 比自己小的都是读请求，则占用锁
    //   // boolean allRead = false;
    //   for (int i = indexOf - 1; i >= 0; i--) {
    //     String preLockKey = children.get(i);
    //     String watchPreLock = LOCK_KEY_PREFIX + preLockKey;
    //     try {
    //       byte[] data = zooKeeper.getData(watchPreLock, false, null);
    //       String preLockKeyData = new String(data);
    //       if (StringUtils.startsWith(preLockKeyData, LockType.WRITE.name())) {
    //         // allRead = true;
    //         watchPreLock(preLockKey, watchPreLock);
    //       }
    //     } catch (Exception e) {
    //       log.error("获取节点数据失败 watchPreLock:{}", watchPreLock, e);
    //       return;
    //       // throw new IllegalArgumentException("获取节点失败", e);
    //     }
    //   }
    //
    // }
    
    // 写请求或者不满足共享锁的读请求，监听上一个节点
    
    // 监听它的上一个节点发生变化
    String preLockKey = children.get(indexOf - 1);
    String watchPreLock = LOCK_KEY_PREFIX + preLockKey;
    watchPreLock(preLockKey, watchPreLock);
  }
  
  private void watchPreLock(String preLockKey, String watchPreLock) {
    log.info("preLock:{}, exists preLockKey:{}", preLockKey, watchPreLock);
    zooKeeper.exists(watchPreLock, this, this, "preLockKey");
  }
  
  /** 锁类型 */
  public enum LockType {
    
    /** 读锁 */
    READ,
    /** 写锁 */
    WRITE
  }
  
}
