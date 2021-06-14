package com.kaige.advance.reflect.orm.entity;

import com.kaige.advance.reflect.orm.Column;
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
public class UserEntity2 implements Serializable {
  
  private static final long serialVersionUID = -64655809848501395L;
  
  /** 主键 */
  @Column(value = "id")
  private Long id;
  
  /** 姓名 */
  @Column(value = "name")
  private String name;
  
  /** 年龄 */
  @Column(value = "age")
  private Integer age;
  
  /** 创建时间 */
  @Column(value = "create_time")
  private Date createTime;
  
  /** 更新时间 */
  @Column(value = "update_time")
  private Date updateTime;
  
}
