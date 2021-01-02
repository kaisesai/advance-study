package com.liukai.advance.netty.splitpackage;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 自定义的消息编码器
 */
public class MyMessageEncoder extends MessageToByteEncoder<MyMessageProtocol> {
  
  @Override
  protected void encode(ChannelHandlerContext ctx, MyMessageProtocol msg, ByteBuf out)
    throws Exception {
    System.out.println("调用 MessageProtocolHandler");
    // 获取消息长度并写入缓冲区
    out.writeInt(msg.getLen());
    // 写入消息内容
    out.writeBytes(msg.getContent());
  }
  
}
