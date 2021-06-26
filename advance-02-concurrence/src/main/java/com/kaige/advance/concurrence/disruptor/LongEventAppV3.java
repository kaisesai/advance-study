package com.kaige.advance.concurrence.disruptor;

import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.util.DaemonThreadFactory;

import java.nio.ByteBuffer;

/**
 * 使用 lambda 表达式
 */
public class LongEventAppV3 {
  
  public static void main(String[] args) {
    
    // 创建队列
    Disruptor<LongEvent> disruptor = new Disruptor<>(LongEvent::new, 1024,
                                                     DaemonThreadFactory.INSTANCE);
    
    // 添加消费者
    disruptor.handleEventsWith((event, sequence, endOfBatch) -> {
      System.out.println(Thread.currentThread().getName() + " " + event.get());
    });
    
    // 启动队列
    disruptor.start();
    
    ByteBuffer byteBuffer = ByteBuffer.allocate(8);
    for (int i = 0; i < 100; i++) {
      byteBuffer.putLong(0, i);
      // 生产数据
      disruptor.publishEvent((event, sequence, arg0) -> event.set(arg0.getLong(0)), byteBuffer);
    }
    
    // 关闭队列
    disruptor.shutdown();
  }
  
}
