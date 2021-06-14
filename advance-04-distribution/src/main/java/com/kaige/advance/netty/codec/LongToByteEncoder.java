package com.kaige.advance.netty.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/** Long 转成 byte 的转换器 */
public class LongToByteEncoder extends MessageToByteEncoder<Long> {
  
  @Override
  protected void encode(ChannelHandlerContext ctx, Long msg, ByteBuf out) throws Exception {
    System.out.println("LongToByteEncoder 被调用");
    System.out.println("msg: " + msg);
    out.writeLong(msg);
  }
  
}
