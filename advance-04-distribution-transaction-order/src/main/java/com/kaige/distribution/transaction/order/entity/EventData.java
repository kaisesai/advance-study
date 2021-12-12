package com.kaige.distribution.transaction.order.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 本地事件表(EventData)表实体类
 *
 * @author kaige
 * @since 2021-11-28 00:07:02
 */
@Data
@SuppressWarnings("serial")
public class EventData extends Model<EventData> {

  @TableId(type = IdType.INPUT)
  // 主键，防重（业务单号）
  private String id;

  // 业务类型
  private Integer type;

  // 状态，0 为新建；1 为处理中；2为处理成功；3 处理失败；
  private Integer state;

  // 事件内容
  private String content;

  // 创建时间
  private Date createDate;

  // 更新时间
  private Date updateDate;

  // 错误消息
  private String errorMsg;

  /**
   * 获取主键值
   *
   * @return 主键值
   */
  @Override
  public Serializable pkVal() {
    return this.id;
  }
}
