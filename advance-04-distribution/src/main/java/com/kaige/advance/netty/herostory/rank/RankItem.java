package com.kaige.advance.netty.herostory.rank;

import lombok.Data;

/**
 * 排名 item
 */
@Data
public class RankItem {
  
  /**
   * 排名 id
   */
  private int rankId;
  
  /**
   * 用户 id
   */
  private int userId;
  
  /**
   * 用户名称
   */
  private String userName;
  
  /**
   * 英雄形象
   */
  private String heroAvatar;
  
  /**
   * 胜利次数
   */
  private int win;
  
}
