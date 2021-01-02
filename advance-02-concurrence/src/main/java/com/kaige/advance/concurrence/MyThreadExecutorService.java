package com.kaige.advance.concurrence;

import java.util.HashSet;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class MyThreadExecutorService extends AbstractExecutorService {
  
  // 运行状态
  public static final int RUNNING = -1;
  
  public static final int SHUTDOWN = 0;
  
  public static final int STOP = 1;
  
  public static final int TIDYING = 2;
  
  public static final int TERMINATED = 3;
  
  private final AtomicInteger runStateCounter = new AtomicInteger(RUNNING);
  
  private final BlockingQueue<Runnable> workerQueue = new LinkedBlockingQueue<>();
  
  private final ReentrantLock mainLock = new ReentrantLock();
  
  private final HashSet<Worker> workers = new HashSet<>();
  
  private final Condition termination = mainLock.newCondition();
  
  private final AtomicInteger workerCounter = new AtomicInteger();
  
  private final int corePoolSize;
  
  private final int queueMaxSize;
  
  private int largestPoolSize;
  
  private long completedTaskCount;
  
  private final AtomicInteger threadNum = new AtomicInteger();
  
  private final ThreadFactory threadFactory = r -> new Thread(r, "myThread" + threadNum.getAndIncrement());
  
  private final RejectedExecutionHandler rejectedExecutionHandler = (r, executor) -> {
    System.out.println("任务队列已满，无法提交任务");
    throw new RuntimeException("任务队列已满，无法提交任务");
  };
  
  public MyThreadExecutorService(int corePoolSize, int queueMaxSize) {
    this.corePoolSize = corePoolSize;
    this.queueMaxSize = queueMaxSize;
  }
  
  public static void main(String[] args) {
    MyThreadExecutorService es = new MyThreadExecutorService(5, 10);
    for (int i = 0; i < 10; i++) {
      int finalI = i;
      es.execute(() -> {
        System.out.println(Thread.currentThread().getName() + "开始做任务！" + finalI);
        try {
          Thread.sleep(1000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      });
    }
  
    try {
      Thread.sleep(10000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  
    es.shutdown();
  }
  
  private static boolean isRunning(int c) {
    return c < SHUTDOWN;
  }
  
  public ThreadFactory getThreadFactory() {
    return threadFactory;
  }
  
  @Override
  public void shutdown() {
    ReentrantLock mainLock = this.mainLock;
    mainLock.lock();
    try {
      // 设置运行状态为 SHUTDOWN
      advanceRunState(SHUTDOWN);
      // 中断全部的工作者
      interruptIdleWorker(false);
    } finally {
      mainLock.unlock();
    }
    // 执行 TERMINATED
    tryTerminate();
    
  }
  
  private void advanceRunState(int targetState) {
    for (; ; ) {
      int runState = runStateCounter.get();
      if (runStateAtLeast(runState, targetState) || runStateCounter
        .compareAndSet(runState, targetState)) {
        break;
      }
    }
  }
  
  @Override
  public List<Runnable> shutdownNow() {
    return null;
  }
  
  @Override
  public boolean isShutdown() {
    return false;
  }
  
  @Override
  public boolean isTerminated() {
    return false;
  }
  
  @Override
  public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
    return false;
  }
  
  @Override
  public void execute(Runnable command) {
    if (command == null) {
      throw new NullPointerException();
    }
    int runState = runStateCounter.get();
    
    // 判断线程池数量
    int workerCount = workerCounter.get();
    if (workerCount < corePoolSize) {
      if (addWorker(command)) {
        return;
      }
      runState = runStateCounter.get();
    }
    
    // 判断线程池的运行状态，队列添加是否已满
    if (runState < SHUTDOWN && workerQueue.size() < queueMaxSize && workerQueue.offer(command)) {
      int recheck = runStateCounter.get();
      int workCountRecheck = workerCounter.get();
      if (recheck >= SHUTDOWN && remove(command)) {
        reject(command);
      } else if (workCountRecheck == 0) {
        addWorker(command);
      }
    } else if (!addWorker(command)) {
      reject(command);
    }
    
  }
  
  private void reject(Runnable command) {
    rejectedExecutionHandler.rejectedExecution(command, null);
  }
  
  private boolean remove(Runnable runnable) {
    boolean removed = workerQueue.remove(runnable);
    tryTerminate();
    return removed;
  }
  
  final void runWorker(Worker worker) {
    Thread workerThread = Thread.currentThread();
    Runnable task = worker.firstTask;
    worker.firstTask = null;
    // 初始化工作者
    worker.unlock();
    // 是否是突然完成，即没有正确的完成
    boolean completedAbruptly = true;
    try {
      while (task != null || (task = getTask()) != null) {
        // 加锁
        worker.lock();
        // 判断中断
        if (
          (runStateAtLeast(runStateCounter.get(), STOP) || (Thread.interrupted() && runStateAtLeast(
            runStateCounter.get(), STOP))) && !workerThread.isInterrupted()) {
          workerThread.interrupt();
        }
        
        try {
          // 执行任务
          task.run();
        } finally {
          task = null;
          worker.completedTasks++;
          // 释放锁
          worker.unlock();
        }
      }
      completedAbruptly = false;
    } finally {
      processWorkerExit(worker, completedAbruptly);
    }
    
  }
  
  private void processWorkerExit(Worker worker, boolean completedAbruptly) {
    if (completedAbruptly) {
      decrementWorkerCount();
    }
    
    // 移除工作者
    ReentrantLock mainLock = this.mainLock;
    mainLock.lock();
    try {
      completedTaskCount += worker.completedTasks;
      workers.remove(worker);
    } finally {
      mainLock.unlock();
    }
    
    tryTerminate();
    int runState = runStateCounter.get();
    // 再次检查线程池的状态，
    if (runStateLessThan(runState, STOP)) {
      // 任务不是突然完成时
      if (!completedAbruptly) {
        int min = corePoolSize;
        
        if (workerCounter.get() >= corePoolSize) {
          return;
        }
      }
      addWorker(null);
    }
    
  }
  
  private boolean addWorker(Runnable firstTask) {
    retry:
    for (; ; ) {
      int runState = runStateCounter.get();
      // 检查线程池的状态
      // 线程池的状态是已经停止
      if (runState >= SHUTDOWN && !(runState == SHUTDOWN && firstTask == null && !workerQueue
        .isEmpty())) {
        return false;
      }
      
      for (; ; ) {
        int workerCount = workerCounter.get();
        // 线程池中的工作者数量大于等于核心线程数
        if (workerCount >= corePoolSize) {
          return false;
        }
        
        if (compareAndIncrementWorkCount(workerCount)) {
          break retry;
        }
        
        if (runState != runStateCounter.get()) {
          continue retry;
        }
      }
    }
    
    boolean workerStarted = false;
    boolean workerAdded = false;
    Worker w = null;
    try {
      w = new Worker(firstTask);
      final Thread t = w.thread;
      if (t != null) {
        ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
          int runState = runStateCounter.get();
          // 再次判断线程池状态
          if (runState < SHUTDOWN || (runState == SHUTDOWN && firstTask == null)) {
            if (t.isAlive()) {
              throw new IllegalStateException();
            }
          }
          // 添加到工作者集合
          workers.add(w);
          int s = workers.size();
          if (s > largestPoolSize) {
            largestPoolSize = s;
          }
          // 标记已经添加
          workerAdded = true;
          
        } finally {
          mainLock.unlock();
        }
        
        // 启动线程
        if (workerAdded) {
          t.start();
          workerStarted = true;
        }
      }
    } finally {
      if (!workerStarted) {
        addWorkerFailed(w);
      }
    }
    return workerStarted;
  }
  
  private void addWorkerFailed(Worker w) {
    ReentrantLock mainLock = this.mainLock;
    mainLock.lock();
    try {
      if (w != null) {
        workers.remove(w);
      }
      decrementWorkerCount();
      // 尝试终止
      tryTerminate();
    } finally {
      mainLock.unlock();
    }
  }
  
  private boolean compareAndIncrementWorkCount(int except) {
    return workerCounter.compareAndSet(except, except + 1);
  }
  
  final void tryTerminate() {
    for (; ; ) {
      int runState = runStateCounter.get();
      // 检查线程池状态，队列状态
      if (isRunning(runState) || runStateAtLeast(runState, TIDYING) || (runState == SHUTDOWN
        && !workerQueue.isEmpty())) {
        // 状态变化必须是由 RUNNING -> SHUTDOWN -> STOP -> TIDYING -> TERMINATED
        // 当线程状态是 SHUTDOWN 时，队列必须为空
        return;
      }
      
      // 工作者数量大于 0
      if (workerCounter.get() != 0) {
        interruptIdleWorker(true);
        return;
      }
      
      ReentrantLock mainLock = this.mainLock;
      mainLock.lock();
      try {
        // 设置线程池状态为 TIDYING
        if (runStateCounter.compareAndSet(runState, TIDYING)) {
          
          // 设置线程池状态为 TERMINATED
          runStateCounter.set(TERMINATED);
          // 唤醒条件
          termination.signalAll();
          return;
        }
      } finally {
        mainLock.unlock();
      }
      
    }
  }
  
  private void interruptIdleWorker(boolean onlyOne) {
    ReentrantLock mainLock = this.mainLock;
    mainLock.lock();
    try {
      for (Worker worker : workers) {
        Thread t = worker.thread;
        if (!t.isInterrupted() && worker.tryLock()) {
          try {
            t.interrupt();
          } finally {
            worker.unlock();
          }
        }
        
        if (onlyOne) {
          break;
        }
        
      }
      
    } finally {
      mainLock.unlock();
    }
  }
  
  private boolean runStateLessThan(int c, int s) {
    return c < s;
  }
  
  private boolean runStateAtLeast(int c, int s) {
    return c >= s;
  }
  
  private Runnable getTask() {
    for (; ; ) {
      // 线程池运行状态
      int runState = this.runStateCounter.get();
      
      // 检查队列的运行状态
      // 线程池的状态已经关闭，并且队列为空
      if (runState >= SHUTDOWN && (runState >= STOP || workerQueue.isEmpty())) {
        decrementWorkerCount();
        return null;
      }
      
      int workerCount = workerCounter.get();
      if (workerCount > corePoolSize && (workerCount > 1 || workerQueue.isEmpty())) {
        if (compareAndDecrementWorkerCount(workerCount)) {
          return null;
        }
        continue;
      }
      
      // int workerCount = this.workerCounter.get();
      // 从队列中获取任务
      try {
        return workerQueue.take();
      } catch (InterruptedException ignored) {
      }
      
    }
    
  }
  
  private boolean compareAndDecrementWorkerCount(int expect) {
    return workerCounter.compareAndSet(expect, expect - 1);
  }
  
  /**
   * 工作者线程数量减一
   */
  private void decrementWorkerCount() {
    do {
    }
    while (!compareAndDecrementWorkerCount(workerCounter.get()));
  }
  
  private final class Worker extends AbstractQueuedSynchronizer implements Runnable {
    
    final Thread thread;
    
    Runnable firstTask;
    
    volatile long completedTasks;
    
    public Worker(Runnable firstTask) {
      super.setState(-1);
      this.firstTask = firstTask;
      this.thread = getThreadFactory().newThread(this);
    }
    
    @Override
    public void run() {
      runWorker(this);
    }
    
    // 锁相关方法
    
    @Override
    protected boolean tryAcquire(int arg) {
      // 设置资源状态为 1
      if (super.compareAndSetState(0, 1)) {
        // 设置独占模式
        super.setExclusiveOwnerThread(Thread.currentThread());
        return true;
      }
      
      return false;
    }
    
    @Override
    protected boolean tryRelease(int arg) {
      // 设置独占模式
      super.setExclusiveOwnerThread(null);
      // 设置资源状态为 0
      super.setState(0);
      return true;
    }
    
    /**
     * @return 是否持有独占模式
     */
    @Override
    protected boolean isHeldExclusively() {
      // 资源状态不为 0
      return super.getState() != 0;
    }
    
    public void lock() {
      super.acquire(1);
    }
    
    public boolean tryLock() {
      return tryAcquire(1);
    }
    
    public void unlock() {
      super.release(1);
    }
    
    public boolean isLocked() {
      return isHeldExclusively();
    }
    
    void interruptIfStarted() {
      Thread t;
      if (super.getState() >= 0 && (t = thread) != null && !t.isInterrupted()) {
        try {
          t.interrupt();
        } catch (Exception ignored) {
        }
      }
    }
    
  }
  
}
