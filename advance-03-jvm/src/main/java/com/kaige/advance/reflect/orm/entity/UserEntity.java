package com.kaige.advance.reflect.orm.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * (User)实体类
 *
 * @author kaige
 * @since 2021-02-04 21:00:19
 */
@Data
public class UserEntity implements Serializable {
  
  private static final long serialVersionUID = -64655809848501395L;
  
  /**
   * 主键
   */
  private Long id;
  
  /**
   * 姓名
   */
  private String name;
  
  /**
   * 年龄
   */
  private Integer age;
  
  /**
   * 创建时间
   */
  private Date createTime;
  
  /**
   * 更新时间
   */
  private Date updateTime;
  
}
