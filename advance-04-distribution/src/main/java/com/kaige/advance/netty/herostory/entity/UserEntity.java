package com.kaige.advance.netty.herostory.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户表(UserEntity)实体类
 *
 * @author kaige
 * @since 2021-02-06 23:26:10
 */
@Data
public class UserEntity implements Serializable {
  
  private static final long serialVersionUID = -98860303794932527L;
  
  private Integer id;
  
  private String name;
  
  private String password;
  
  private String heroAvatar;
  
}
