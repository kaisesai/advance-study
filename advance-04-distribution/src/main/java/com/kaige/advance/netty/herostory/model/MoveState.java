package com.kaige.advance.netty.herostory.model;

import lombok.Data;

/** 用户移动状态 */
@Data
public class MoveState {
  
  // 起始位置 X
  private float fromPosX;
  
  // 起始位置 Y
  private float fromPosY;
  
  // 移动到位置 X
  private float toPosX;
  
  // 移动到位置 Y
  private float toPosY;
  
  // 启程时间戳
  private long startTime;
  
}
