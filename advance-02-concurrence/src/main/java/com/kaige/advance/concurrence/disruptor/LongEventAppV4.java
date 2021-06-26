package com.kaige.advance.concurrence.disruptor;

import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.util.DaemonThreadFactory;

import java.nio.ByteBuffer;

/**
 * 使用 lambda 表达式，优化添加事件处理器写法
 * <p>
 * 捕获表达式与非捕获表达式的性能不同，参考：<a href= "https://www.logicbig.com/tutorials/core-java-tutorial/java-8-enhancements/java-capturing-lambda.html">
 * 捕获与非捕获 Lambda</a>
 */
public class LongEventAppV4 {
  
  public static void main(String[] args) {
    
    // 创建队列
    Disruptor<LongEvent> disruptor = new Disruptor<>(LongEvent::new, 1024,
                                                     DaemonThreadFactory.INSTANCE);
    
    // 添加消费者
    disruptor.handleEventsWith(LongEventAppV4::onEvent);
    
    // 启动队列
    disruptor.start();
    
    ByteBuffer byteBuffer = ByteBuffer.allocate(8);
    for (int i = 0; i < 100; i++) {
      byteBuffer.putLong(0, i);
      // 生产数据
      disruptor.publishEvent(LongEventAppV4::translateTo, byteBuffer);
    }
    
    // 关闭队列
    disruptor.shutdown();
  }
  
  private static void onEvent(LongEvent event, long sequence, boolean endOfBatch) {
    System.out.println(Thread.currentThread().getName() + " " + event.get());
  }
  
  private static void translateTo(LongEvent event, long sequence, ByteBuffer arg0) {
    event.set(arg0.getLong(0));
  }
  
}
