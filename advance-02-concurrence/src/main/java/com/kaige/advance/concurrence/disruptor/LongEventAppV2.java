package com.kaige.advance.concurrence.disruptor;

import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;

import java.nio.ByteBuffer;
import java.util.concurrent.Executors;

public class LongEventAppV2 {
  
  public static void main(String[] args) {
    
    // 定义一个队列
    Disruptor<LongEvent> disruptor = new Disruptor<LongEvent>(new LongEventFactory(), 8,
                                                              Executors.defaultThreadFactory());
    
    // 添加事件处理器（消费者）
    disruptor.handleEventsWith(new LongEventHandler(1), new LongEventHandler(2));
    
    // 队列开始
    disruptor.start();
    
    RingBuffer<LongEvent> ringBuffer = disruptor.getRingBuffer();
    // 队列生产者
    LongEventProducerV2 producer = new LongEventProducerV2(ringBuffer);
    
    ByteBuffer byteBuffer = ByteBuffer.allocate(8);
    for (int i = 0; i < 100; i++) {
      // 生产数据
      byteBuffer.putLong(0, i);
      
      producer.onData(byteBuffer);
    }
    
    // 队列停止
    disruptor.shutdown();
  }
  
}
