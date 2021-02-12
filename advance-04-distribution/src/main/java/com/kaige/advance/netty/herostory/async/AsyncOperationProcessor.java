package com.kaige.advance.netty.herostory.async;

import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 异步操作处理器
 * <p>
 * 单例，使用多个单线程池来执行
 */
@Slf4j
public class AsyncOperationProcessor {
  
  public static final int EXECUTOR_SERVICE_NUM = 10;
  
  private static final AsyncOperationProcessor PROCESSOR = new AsyncOperationProcessor();
  
  /**
   * 线程池数组
   */
  private final ExecutorService[] executorServices;
  
  private AsyncOperationProcessor() {
    // 初始化线程池
    executorServices = new ExecutorService[EXECUTOR_SERVICE_NUM];
    for (int i = 0; i < executorServices.length; i++) {
      String threadName = "AsyncOperationProcessorThread-" + i;
      executorServices[i] = Executors.newSingleThreadExecutor(r -> new Thread(r, threadName));
    }
  }
  
  public static AsyncOperationProcessor getInstance() {
    return PROCESSOR;
  }
  
  public void process(IAsyncOperation operation) {
    if (Objects.isNull(operation)) {
      return;
    }
    // 选择一个处理器
    // 执行业务
    executorServices[operation.getBindId()].execute(() -> {
      try {
        // 执行业务操作
        operation.doAsync();
        // 执行业务结束操作
        operation.doFinish();
      } catch (Exception e) {
        log.error(e.getMessage(), e);
      }
    });
    
  }
  
}
