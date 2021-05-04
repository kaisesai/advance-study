package com.kaige.advance.zookeeper;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

import java.util.concurrent.CountDownLatch;

/**
 * 默认的回调类
 *
 * @author liukai  2021年05月04日
 */
@Slf4j
public class DefaultWatchCallback implements Watcher {
  
  private final CountDownLatch countDownLatch;
  
  public DefaultWatchCallback(CountDownLatch countDownLatch) {
    this.countDownLatch = countDownLatch;
  }
  
  @Override
  public void process(WatchedEvent event) {
    switch (event.getState()) {
      case Unknown:
        break;
      case Disconnected:
        break;
      case NoSyncConnected:
        break;
      case SyncConnected:
        countDownLatch.countDown();
        break;
      case AuthFailed:
        break;
      case ConnectedReadOnly:
        break;
      case SaslAuthenticated:
        break;
      case Expired:
        break;
      case Closed:
        break;
    }
  }
  
}
