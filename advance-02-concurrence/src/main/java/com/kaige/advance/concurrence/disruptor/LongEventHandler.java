package com.kaige.advance.concurrence.disruptor;

import com.lmax.disruptor.EventHandler;

/**
 * 消费者
 */
public class LongEventHandler implements EventHandler<LongEvent> {
  
  private final long id;
  
  public LongEventHandler(long id) {
    this.id = id;
  }
  
  @Override
  public void onEvent(LongEvent event, long sequence, boolean endOfBatch) throws Exception {
    System.out.println("id:" + id + "\t" + event.get());
  }
  
}
