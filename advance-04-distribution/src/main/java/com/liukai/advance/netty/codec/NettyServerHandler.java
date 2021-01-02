package com.liukai.advance.netty.codec;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * 服务端消息处理器
 */
public class NettyServerHandler extends ChannelInboundHandlerAdapter {
  
  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    System.out.println("客户端建立连接");
  }
  
  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    System.out.println("读取客户端消息：" + msg);
    // 发送 long 消息
    // ctx.writeAndFlush(2000L);
    // 发送字符串消息
    // ctx.writeAndFlush("helloClient");
    // 发送对象类型消息
    // 发送对象消息
    ctx.writeAndFlush(new MyObject(2, "kaiye"));
  }
  
}
