package com.kaige.advance.netty.simple;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;

/**
 * 客户端处理器
 */
@Slf4j
public class NettyClientHandler extends ChannelInboundHandlerAdapter {
  
  /**
   * 当客户端连接服务器完成就会触发该方法
   *
   * @param ctx
   * @throws Exception
   */
  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    ByteBuf byteBuf = Unpooled.copiedBuffer("helloServer", StandardCharsets.UTF_8);
    ctx.writeAndFlush(byteBuf);
  }
  
  /**
   * 当通道有读取事件时会触发，即服务端发送数据给客户端
   *
   * @param ctx
   * @param msg
   * @throws Exception
   */
  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    ByteBuf buf = (ByteBuf) msg;
    log.info("收到服务端的消息：{}", buf.toString(StandardCharsets.UTF_8));
    log.info("服务端的地址：{}", ctx.channel().remoteAddress());
  }
  
  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    cause.printStackTrace();
    ctx.channel().close();
  }
  
}
