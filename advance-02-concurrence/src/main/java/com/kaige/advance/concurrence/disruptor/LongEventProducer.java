package com.kaige.advance.concurrence.disruptor;

import com.lmax.disruptor.RingBuffer;

import java.nio.ByteBuffer;

public class LongEventProducer {
  
  private final RingBuffer<LongEvent> ringBuffer;
  
  public LongEventProducer(RingBuffer<LongEvent> ringBuffer) {
    this.ringBuffer = ringBuffer;
  }
  
  public void onData(ByteBuffer byteBuffer) {
    // 从环形 buffer 获取下一个序列
    long sequence = ringBuffer.next();
    // 获取环形 buffer 上的事件
    LongEvent longEvent = ringBuffer.get(sequence);
    // 更新 longEvent 值
    longEvent.set(byteBuffer.getLong(0));
    // 发布环形 buffer 上的序列
    ringBuffer.publish(sequence);
  }
  
}
