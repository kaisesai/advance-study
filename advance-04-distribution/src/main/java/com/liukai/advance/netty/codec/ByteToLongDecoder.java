package com.liukai.advance.netty.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * Byte 到 Long 的转换器
 */
public class ByteToLongDecoder extends ByteToMessageDecoder {
  
  @Override
  protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
    System.out.println("ByteToLongDecoder 被调用");
    // 因为 long 是需要 8 个字节，所以需要判断消息是否大于 8 个字节
    if (in.readableBytes() >= 8) {
      long msg = in.readLong();
      System.out.println("msg: " + msg);
      out.add(msg);
    }
    
  }
  
}
