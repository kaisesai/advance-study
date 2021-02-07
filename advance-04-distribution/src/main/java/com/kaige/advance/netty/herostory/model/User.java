package com.kaige.advance.netty.herostory.model;

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
  
  // 用户状态
  private MoveState moveState;
  
  // 用户血量
  private int currentHp;
  
  // 用户账号
  private String userName;
  
}
