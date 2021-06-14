package com.kaige.advance.netty.codec;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/** 实现了 {@link Serializable} 类的自定义对象 */
@Data
@AllArgsConstructor
public class MyObject implements Serializable {
  
  private Integer id;
  
  private String name;
  
}
