package com.kaige.advance.zookeeper.lock;

import com.kaige.advance.zookeeper.DefaultWatchCallback;
import com.kaige.advance.zookeeper.ZookeeperHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.ZooKeeper;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * ZK 分布式锁
 *
 * @author liukai 2021年05月04日
 */
@Slf4j
public class ZkLock {
  
  public static void main(String[] args) throws Exception {
    
    CountDownLatch countDownLatch = new CountDownLatch(1);
    DefaultWatchCallback defaultWatchCallback = new DefaultWatchCallback(countDownLatch);
    try (
      ZooKeeper zk = new ZooKeeper(ZookeeperHolder.CONNECT_STR + "/mylock", 1000,
                                   defaultWatchCallback)
    ) {
      
      String business = "order123";
      for (int i = 0; i < 20; i++) {
        new Thread(() -> {
          try (MyLock myLock = new MyLock(zk, business, MyLock.LockType.READ)) {
            
            if (myLock.lock()) {
              log.info("开始处理业务....");
              TimeUnit.SECONDS.sleep(1);
            } else {
              log.error("锁获取失败....");
            }
          } catch (Exception e) {
            e.printStackTrace();
          }
        }, "线程" + i).start();
      }
      
      // 主线程阻塞
      TimeUnit.SECONDS.sleep(100);
    }
    
  }
  
}
