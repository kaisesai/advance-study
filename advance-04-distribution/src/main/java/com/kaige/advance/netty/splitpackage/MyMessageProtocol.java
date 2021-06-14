package com.kaige.advance.netty.splitpackage;

import lombok.Data;

/** 自定义的消息协议 */
@Data
public class MyMessageProtocol {
  
  /** 消息长度 */
  private int len;
  
  /** 消息内容 */
  private byte[] content;
  
}
