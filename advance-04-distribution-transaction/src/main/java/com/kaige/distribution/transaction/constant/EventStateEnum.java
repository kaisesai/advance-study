package com.kaige.distribution.transaction.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 事件状态枚举
 *
 * @author kaige
 * @date 2021年12月04日
 */
@Getter
@AllArgsConstructor
public enum EventStateEnum implements CommonEnumInterface<Integer> {
  NONE(0, "无"),
  CREATE(1, "新建"),
  PROCESSING(2, "处理中"),
  SUCCESS(3, "成功"),
  FAIL(4, "失败");

  /** 简码 */
  private Integer code;

  /** 描述 */
  private String desc;
}
