package com.kaige.advance.netty.herostory.mq;

import lombok.Data;

/** 胜利消息 */
@Data
public class VictorMsg {
  
  /** 胜利者 */
  private int winnerId;
  
  /** 失败者 */
  private int loserId;
  
}
