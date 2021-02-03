package com.kaige.advance.netty.herostory.cmdhandler;

import com.google.protobuf.GeneratedMessageV3;
import io.netty.channel.ChannelHandlerContext;

/**
 * CMD 处理器接口
 */
public interface ICmdHandler<T extends GeneratedMessageV3> {
  
  void handle(ChannelHandlerContext ctx, T cmd);
  
}
