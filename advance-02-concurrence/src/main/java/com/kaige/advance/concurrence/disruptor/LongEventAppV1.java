package com.kaige.advance.concurrence.disruptor;

import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;

import java.nio.ByteBuffer;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class LongEventAppV1 {
  
  public static void main(String[] args) {
    
    LongEventFactory longEventFactory = new LongEventFactory();
    
    int ringBufferSize = 1024;
    
    // 队列
    Disruptor<LongEvent> disruptor = new Disruptor<>(longEventFactory, ringBufferSize,
                                                     Executors.defaultThreadFactory());
    
    // 发布两个事件处理器（消费者）
    disruptor.handleEventsWith(new LongEventHandler(1), new LongEventHandler(2));
    
    // 启动队列
    disruptor.start();
    
    // 获取环
    RingBuffer<LongEvent> ringBuffer = disruptor.getRingBuffer();
    
    // 定义一个生产者
    LongEventProducer producer = new LongEventProducer(ringBuffer);
    
    ByteBuffer bb = ByteBuffer.allocate(8);
    
    for (int i = 0; i < 100; i++) {
      bb.putLong(0, i);
      
      // 生产数据
      producer.onData(bb);
      
      try {
        TimeUnit.SECONDS.sleep(1);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      
    }
    
    // 停止队列
    disruptor.shutdown();
    
  }
  
}
