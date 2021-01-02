package com.liukai.advance.netty.codec;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * 消息入站处理器
 */
public class NettyClientHandler extends ChannelInboundHandlerAdapter {
  
  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    System.out.println("NettyClientHandler 发送数据");
    // 发送 long 消息
    // ctx.writeAndFlush(1000L);
    // 发送 string 编码
    // ctx.writeAndFlush("HelloServer");
    // 发送对象消息
    ctx.writeAndFlush(new MyObject(1, "kaige"));
  }
  
  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    System.out.println("读取到服务端消息：" + msg);
  }
  
}
