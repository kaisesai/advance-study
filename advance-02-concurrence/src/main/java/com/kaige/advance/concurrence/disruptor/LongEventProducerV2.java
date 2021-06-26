package com.kaige.advance.concurrence.disruptor;

import com.lmax.disruptor.EventTranslatorOneArg;
import com.lmax.disruptor.RingBuffer;

import java.nio.ByteBuffer;

/**
 * 事件生产者
 */
public class LongEventProducerV2 {
  
  /**
   * 事件转化器
   */
  private static final EventTranslatorOneArg<LongEvent, ByteBuffer> EVENT_TRANSLATOR_ONE_ARG
    = new EventTranslatorOneArg<>() {
    @Override
    public void translateTo(LongEvent event, long sequence, ByteBuffer byteBuffer) {
      event.set(byteBuffer.getLong(0));
    }
  };
  
  private final RingBuffer<LongEvent> ringBuffer;
  
  public LongEventProducerV2(RingBuffer<LongEvent> ringBuffer) {
    this.ringBuffer = ringBuffer;
  }
  
  public void onData(ByteBuffer byteBuffer) {
    // 发布事件
    ringBuffer.publishEvent(EVENT_TRANSLATOR_ONE_ARG, byteBuffer);
  }
  
}
