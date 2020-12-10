import com.liukai.advance.zookeeper.MasterResolve;
import com.liukai.advance.zookeeper.ZkLock;
import org.apache.commons.lang.math.RandomUtils;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

public class ZkTest {
  
  @Test
  public void testZkLock() throws InterruptedException {
    int threadNum = 10;
    CountDownLatch countDownLatch = new CountDownLatch(threadNum);
    
    for (int i = 0; i < threadNum; i++) {
      int finalI = i;
      new Thread(() -> {
        ZkLock zkLock = ZkLock.getZkLockInstance();
        ZkLock.Lock lock = null;
        try {
          lock = zkLock.lock("order", finalI + "-lock-", 100000, ZkLock.LockType.READ);
          System.out.println("执行任务...");
          Thread.sleep(RandomUtils.nextInt(10) * 1000L);
        } catch (InterruptedException e) {
          e.printStackTrace();
        } finally {
          if (lock != null) {
            zkLock.releaseLock(lock);
          }
          countDownLatch.countDown();
        }
      }, "线程" + i).start();
    }
    
    countDownLatch.await();
    System.out.println("所有线程执行完毕");
  }
  
  @Test
  public void testMasterResolve() throws InterruptedException {
    MasterResolve instance = MasterResolve.getInstance();
    System.out.println("instance.isMaster() = " + instance.isMaster());
    Thread.sleep(500 * 1000);
  }
  
}
