package com.kaige.distribution.transaction.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * (Order)表实体类
 *
 * @author kaige
 * @since 2021-12-04 22:28:31
 */
@SuppressWarnings("serial")
@Data
@TableName(value = "my_order")
public class MyOrder extends Model<MyOrder> {

  // 主键
  @TableId(type = IdType.AUTO)
  private Long id;

  // 订单金额（单位分）
  private Long amount;

  // 订单状态
  private Integer state;

  // 创建时间
  private Date createDate;

  // 更新时间
  private Date updateDate;

  /**
   * 获取主键值
   *
   * @return 主键值
   */
  @Override
  protected Serializable pkVal() {
    return this.id;
  }
}
