package com.kaige.distribution.transaction.pay.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import java.util.Date;

/**
 * 支付信息表(PayInfo)表实体类
 *
 * @author kaige
 * @since 2021-12-12 16:53:15
 */
@SuppressWarnings("serial")
@Data
public class PayInfo extends Model<PayInfo> {

  // 主键
  @TableId(type = IdType.AUTO)
  private Long id;
  // 支付金额（单位：分）
  private Long amount;
  // 用户 id
  private Long userId;
  // 支付状态
  private Integer state;
  // 更新时间
  private Date updateDate;
  // 创建时间
  private Date createDate;
  // 订单id
  private Long orderId;

  /**
   * 获取主键值
   *
   * @return 主键值
   */
  @Override
  protected Long pkVal() {
    return this.id;
  }
}
