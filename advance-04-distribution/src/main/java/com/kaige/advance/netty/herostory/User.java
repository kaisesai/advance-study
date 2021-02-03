package com.kaige.advance.netty.herostory;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 用户消息
 */
@Data
@AllArgsConstructor
public class User {
  
  // 用户 Id
  private int userId;
  
  // 英雄形象
  private String heroAvatar;
  
}
