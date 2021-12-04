package com.kaige.distribution.transaction.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** 事件业务类型 */
@Getter
@AllArgsConstructor
public enum EventBusinessEnum implements CommonEnumInterface<String> {
  NONE("NONE", "无"),
  ORDER_CREATE("ORDER_CREATE", "创建订单");

  /** 简码 */
  private String code;

  /** 描述 */
  private String desc;
}
