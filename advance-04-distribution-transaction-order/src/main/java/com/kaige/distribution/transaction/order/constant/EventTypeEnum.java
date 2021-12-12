package com.kaige.distribution.transaction.order.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 事件类型枚举
 *
 * @author kaige
 * @date 2021年12月04日
 */
@Getter
@AllArgsConstructor
public enum EventTypeEnum implements CommonEnumInterface<Integer> {
  NONE(0, "无"),
  ORDER_CREATE(100, "订单创建");

  /** 简码 */
  private Integer code;

  /** 描述 */
  private String desc;
}
