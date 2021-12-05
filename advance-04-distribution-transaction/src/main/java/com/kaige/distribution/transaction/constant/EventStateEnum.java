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
  NONE(null, "无"),
  CREATE(0, "新建"),
  // PROCESSING(1, "处理中"),
  SUCCESS(1, "处理成功"),
  FAIL(2, "处理失败");

  /** 简码 */
  private Integer code;

  /** 描述 */
  private String desc;
}
