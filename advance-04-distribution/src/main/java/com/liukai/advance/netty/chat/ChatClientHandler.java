package com.liukai.advance.netty.chat;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * 聊天室客户端处理器
 */
@Slf4j
public class ChatClientHandler extends SimpleChannelInboundHandler<String> {
  
  /**
   * 客户端通道建立
   *
   * @param ctx
   * @throws Exception
   */
  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    Channel channel = ctx.channel();
    String msg = "【我】：" + channel.remoteAddress().toString() + "进入聊天室";
    log.info(msg);
    super.channelActive(ctx);
  }
  
  /**
   * 客户端通道关闭
   *
   * @param ctx
   * @throws Exception
   */
  @Override
  public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    Channel channel = ctx.channel();
    String msg = "【我】：" + channel.remoteAddress().toString() + "离开聊天室";
    log.info(msg);
    super.channelInactive(ctx);
  }
  
  /**
   * 客户端 channel 读取数据
   *
   * @param ctx
   * @param msg
   * @throws Exception
   */
  @Override
  public void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
    log.info(msg);
  }
  
  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    cause.printStackTrace();
    ctx.channel().close();
  }
  
}
