package com.kaige.advance.zookeeper;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * zk 测试
 *
 * @author kaige 2021年05月03日
 */
@Slf4j
public class ZkTest {
  
  public static void main(String[] args) throws Exception {
    CountDownLatch countDownLatch = new CountDownLatch(1);
    try (
    /*
      zk 是 session 的概念，没有连接池的概念
      watch 是观察，它注册值发生在，该类型调用 get、exists
      watch 有两类：
      第一类是 new ZooKeeper 的时候，传入的 watch，属于 session 级别的，与 path、node 没有关系
      第二类是 path、node 级别的，只是与客户端查询的某一个 path 或者 node 值有关

      一个 watch 就是单独一个线程

    */
      ZooKeeper zk = new ZooKeeper(ZookeeperHolder.CONNECT_STR, 1000, event -> {
        // watch 的回调方法
        System.out.println("new zk event = " + event);
        // 检测事件状态
        switch (event.getState()) {
          case Unknown:
            System.out.println("new zk event = Unknown");
            break;
          case Disconnected:
            System.out.println("new zk event = Disconnected");
            break;
          case NoSyncConnected:
            System.out.println("new zk event = NoSyncConnected");
            break;
          case SyncConnected:
            System.out.println("new zk event = SyncConnected");
            countDownLatch.countDown();
            break;
          case AuthFailed:
            System.out.println("new zk event = AuthFailed");
            break;
          case ConnectedReadOnly:
            System.out.println("new zk event = ConnectedReadOnly");
            break;
          case SaslAuthenticated:
            System.out.println("new zk event = SaslAuthenticated");
            break;
          case Expired:
            System.out.println("new zk event = Expired");
            break;
          case Closed:
            System.out.println("new zk event = Closed");
            break;
        }
        
        // 检测事件类型
        switch (event.getType()) {
          case None:
            System.out.println("new zk type = None");
            break;
          case NodeCreated:
            System.out.println("new zk type = NodeCreated");
            break;
          case NodeDeleted:
            System.out.println("new zk type = NodeDeleted");
            break;
          case NodeDataChanged:
            System.out.println("new zk type = NodeDataChanged");
            break;
          case NodeChildrenChanged:
            System.out.println("new zk type = NodeChildrenChanged");
            break;
          case DataWatchRemoved:
            System.out.println("new zk type = DataWatchRemoved");
            break;
          case ChildWatchRemoved:
            System.out.println("new zk type = ChildWatchRemoved");
            break;
          case PersistentWatchRemoved:
            System.out.println("new zk type = PersistentWatchRemoved");
            break;
        }
        // ...
      })
    ) {
      
      countDownLatch.await();
      
      // 创建一个节点
      String pathName = zk
        .create("/ooxx", "mydata".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
      System.out.println("pathName = " + pathName);

      /*
       获取节点 API 有两类，两类 api 都可以传入是否需要 watch
       第一类是同步获取
       第二类是异步获取，需要传入一个回调函数处理逻辑
      */
      // 1. 同步获取数据，不传 watch
      byte[] data = zk.getData("/ooxx", false, null);
      System.out.println("getData1 = " + new String(data));
      
      System.out.println();
      
      // // 2. 同步获取数据，传入 watch true，表示采用 session 级别的 watch
      data = zk.getData("/ooxx", true, null);
      System.out.println("getData2 = " + new String(data));
      
      System.out.println();
      
      // // 3. 同步获取数据，传入自定义的 watch
      data = zk.getData("/ooxx", new Watcher() {
        @Override
        public void process(WatchedEvent event) {
          System.out.println("getData3 process event = " + event);
        }
      }, null);
      System.out.println("getData3 = " + new String(data));
      
      System.out.println();
      
      // // 4. 异步获取数据，watch 为 false
      zk.getData("/ooxx", false, new AsyncCallback.DataCallback() {
        @Override
        public void processResult(int rc, String path, Object ctx, byte[] data, Stat stat) {
          System.out.println(
            "getData4 async callback... rc = " + rc + ", path = " + path + ", ctx = " + ctx
              + ", data = " + new String(data) + ", stat = " + stat);
        }
      }, "myctx");
      
      System.out.println();
      //
      // // 5. 异步获取数据，watch 为 true
      zk.getData("/ooxx", true, new AsyncCallback.DataCallback() {
        @Override
        public void processResult(int rc, String path, Object ctx, byte[] data, Stat stat) {
          System.out.println(
            "getData5 async callback... rc = " + rc + ", path = " + path + ", ctx = " + ctx
              + ", data = " + new String(data) + ", stat = " + stat);
        }
      }, "myctx");
      
      System.out.println();
      
      // 6. 异步获取数据，自定义 watch
      zk.getData("/ooxx", new Watcher() {
        @Override
        public void process(WatchedEvent event) {
          System.out.println("getData6 async event = " + event);
          // 重新触发注册
          try {
            zk.getData("/ooxx", this, null);
          } catch (KeeperException | InterruptedException e) {
            e.printStackTrace();
          }
        }
      }, new AsyncCallback.DataCallback() {
        @Override
        public void processResult(int rc, String path, Object ctx, byte[] data, Stat stat) {
          System.out.println(
            "getData6 async callback... rc = " + rc + ", path = " + path + ", ctx = " + ctx
              + ", data = " + new String(data) + ", stat = " + stat);
        }
      }, "myctx");
      
      // 设置路径，触发自定义 watch
      Stat stat = zk.setData("/ooxx", "newdata".getBytes(), 0);
      zk.setData("/ooxx", "newdata2".getBytes(), stat.getVersion());
      
      TimeUnit.SECONDS.sleep(5);
    }
  }
  
}
