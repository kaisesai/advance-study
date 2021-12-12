package com.kaige.distribution.transaction.order.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** 订单状态枚举 */
@Getter
@AllArgsConstructor
public enum MyOrderStateEnum implements CommonEnumInterface<Integer> {
  NONE(0, "无"),
  CREATE(1, "订单创建"),
  CANCELED(2, "订单取消"),
  PAID(3, "订单支付"),
  FINISHED(4, "订单完成");

  /** 简码 */
  private Integer code;

  /** 描述 */
  private String desc;
}
