package com.kaige.advance.netty.splitpackage;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/** 自定义消息解码器 */
public class MyMessageDecoder extends ByteToMessageDecoder {
  
  private int length = 0;
  
  @Override
  protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
    System.out.println("MyMessageDecoder 被调用");
    // 读取 int 长度值，4 个字节
    if (in.readableBytes() >= 4) {
      // 获取内容长度数据
      if (length == 0) {
        length = in.readInt();
      }
      
      // 判断是否有对应长度的数据
      if (in.readableBytes() < length) {
        System.out.println("消息可读数据不够，继续等待...");
        return;
      }
      byte[] bytes = new byte[length];
      // if(in.readableBytes() >= length){
      in.readBytes(bytes);
      // 封装成 MyMessageProtocol 传递到下一个 handler
      MyMessageProtocol mp = new MyMessageProtocol();
      mp.setLen(length);
      mp.setContent(bytes);
      
      out.add(mp);
      // }
      length = 0;
    }
  }
  
}
