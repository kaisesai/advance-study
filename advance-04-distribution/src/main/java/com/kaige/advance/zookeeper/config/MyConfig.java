package com.kaige.advance.zookeeper.config;

import com.kaige.advance.zookeeper.DefaultWatchCallback;
import com.kaige.advance.zookeeper.ZookeeperHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.util.Objects;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

/**
 * 配置中心处理类
 */
@Slf4j
public class MyConfig
  implements Watcher, AsyncCallback.DataCallback, AsyncCallback.StatCallback, AutoCloseable {
  
  private volatile String config = null;
  
  private CyclicBarrier cyclicBarrier;
  
  private ZooKeeper zooKeeper;
  
  public MyConfig() {
    init();
  }
  
  /**
   * 初始化配置信息
   */
  private void init() {
    try {
      String connectStr = ZookeeperHolder.CONNECT_STR + "/MyConfig";
      // 两个线程组成屏障参数
      this.cyclicBarrier = new CyclicBarrier(2);
      
      CountDownLatch downLatch = new CountDownLatch(1);
      // 默认的监视回调
      DefaultWatchCallback defaultWatchCallback = new DefaultWatchCallback(downLatch);
      this.zooKeeper = new ZooKeeper(connectStr, 1000, defaultWatchCallback);
      // 等待 zk 真正连接成功
      downLatch.await();
    } catch (Exception e) {
      log.error("MyConfig init error", e);
      throw new IllegalStateException("myconfig init error");
    }
  }
  
  /**
   * @return 获取配置数据
   */
  public void loadConfig() {
    zooKeeper.getData("/", this, this, "abc");
    try {
      cyclicBarrier.await();
    } catch (InterruptedException | BrokenBarrierException e) {
      throw new IllegalStateException(e);
    }
  }
  
  public String getConfig() {
    return config;
  }
  
  public void await() {
    try {
      // 重新触发节点是否存在 api
      zooKeeper.exists("/", this, this, "await");
      int numberWaiting = cyclicBarrier.getNumberWaiting();
      log.info("cyclicBarrier await() numberWaiting: {}", numberWaiting);
      cyclicBarrier.await();
    } catch (Exception e) {
      log.error("cyclicBarrier await error", e);
      throw new IllegalStateException(e);
    }
  }
  
  @Override
  public void close() throws Exception {
    if (zooKeeper != null) {
      zooKeeper.close();
    }
  }
  
  @Override
  public void processResult(int rc, String path, Object ctx, byte[] data, Stat stat) {
    log.info("processResult rc:{}, path:{}, ctx:{}, data:{}, stata:{} ", rc, path, ctx, data, stat);
    // 监听到结果
    if (Objects.nonNull(data)) {
      // 数据不为空，将数据写入 config
      config = new String(data);
      log.info("config: {}", config);
      try {
        int numberWaiting = cyclicBarrier.getNumberWaiting();
        log.info("cyclicBarrier getNumberWaiting: {}", numberWaiting);
        if (numberWaiting != 0) {
          log.info("cyclicBarrier await()");
          cyclicBarrier.await();
        }
      } catch (Exception e) {
        log.info("processResult await error", e);
        throw new IllegalStateException("processResult await error", e);
      }
    } else {
      log.info("config is null");
      config = null;
    }
  }
  
  @Override
  public void process(WatchedEvent event) {
    // 监听到事件变化
    log.info("监听到事件变化 event: {}", event);
    switch (event.getType()) {
      case None:
        break;
      case NodeCreated:
        // 节点被创建
      case NodeDataChanged:
        // 节点数据变更
        // 重新查询
        zooKeeper.getData("/", this, this, "dataChanged");
        break;
      case NodeDeleted:
        // 节点被删除
        // 再次注册节点是否存在的事件
        zooKeeper.exists("/", this, this, "NodeDeleted");
        break;
      case DataWatchRemoved:
        break;
      case NodeChildrenChanged:
        break;
      case ChildWatchRemoved:
        break;
      case PersistentWatchRemoved:
        break;
    }
  }
  
  /**
   * 监听 exists 的回调函数
   *
   * @param rc
   * @param path
   * @param ctx
   * @param stat
   */
  @Override
  public void processResult(int rc, String path, Object ctx, Stat stat) {
    // 表示数据存在的回调
    log.info("StatCallback 执行异步处理 processResult rc:{}, path:{}, ctx:{}, stat:{}", rc, path, ctx,
             stat);
    // if(stat != null){
    //   zooKeeper.getData("/", this, this, "a");
    // }
  }
  
}
