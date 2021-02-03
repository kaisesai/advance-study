package com.kaige.advance.jvm;

// import sun.misc.Contended;

/**
 * 验证更新缓存行时引发的性能问题
 * <p>
 * Intel CPU 的缓存行默认大小是 64byte，
 * 所以我们构建两个数组，一个数组大小小于 64byte，处于一个缓存行中；
 * 第二个数组大于 64byte，操作的数据不在同一个缓存行中；
 * 执行程序比较两者计算耗时
 */
public class CacheLinePadding {
  
  public static final int MAX_NUM = 100_000_000;
  
  /**
   * 数组小于 64byte
   */
  public static volatile long[] arr = new long[2];
  
  /**
   * 数组大于 64byte
   */
  public static volatile long[] arr2 = new long[16];
  
  /**
   * JDK8 之后，通过使用加上 @Contended 注解，可以保证字段处于不同的缓存行中，jvm 默认是开启，即允许竞争，可以通过增加 -XX:-RestrictContended 参数关闭禁止竞争
   */
  // @Contended
  public volatile long x;
  
  // @Contended
  public volatile long y;
  
  public static void main(String[] args) throws InterruptedException {
    // 分别执行两次方法，传入不同 byte 的数组，比对执行时间
    // calArr(arr, 0, 1, "arr");
    // calArr(arr2, 0, 8, "arr2");
    
    // 测试 @Contended 竞争
    testContended();
  }
  
  private static void testContended() throws InterruptedException {
    CacheLinePadding padding = new CacheLinePadding();
    
    Thread t1 = new Thread(() -> {
      for (long i = 0; i < MAX_NUM; i++) {
        padding.x = i;
      }
    }, "t1");
    
    Thread t2 = new Thread(() -> {
      for (long i = 0; i < MAX_NUM; i++) {
        padding.y = i;
      }
    }, "t2");
    
    long currentTimeMillis = System.currentTimeMillis();
    t1.start();
    t2.start();
    t1.join();
    t2.join();
    System.out.println("两个线程计算耗时：" + (System.currentTimeMillis() - currentTimeMillis) + "ms");
  }
  
  private static void calArr(long[] arr, int index1, int index2, String name)
    throws InterruptedException {
    Thread t1 = new Thread(() -> {
      for (int i = 0; i < MAX_NUM; i++) {
        arr[index1] = i;
      }
    }, "t1" + name);
    
    Thread t2 = new Thread(() -> {
      for (int i = 0; i < MAX_NUM; i++) {
        arr[index2] = i;
      }
    }, "t2" + name);
    
    long currentTimeMillis = System.currentTimeMillis();
    t1.start();
    t2.start();
    t1.join();
    t2.join();
    System.out
      .println(name + " 两个线程计算耗时：" + (System.currentTimeMillis() - currentTimeMillis) + "ms");
  }
  
}
