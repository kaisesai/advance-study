package com.kaige.advance.netty.chat;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * 聊天室消息处理器
 */
@Slf4j
public class ChatServerHandler extends SimpleChannelInboundHandler<String> {
  
  /**
   * 管理所有的客户端 channel
   */
  private static final DefaultChannelGroup CHANNEL_GROUP = new DefaultChannelGroup(
    GlobalEventExecutor.INSTANCE);
  
  /**
   * 有客户端 channel 建立
   *
   * @param ctx
   * @throws Exception
   */
  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    Channel channel = ctx.channel();
    String msg = "【客户端】：" + channel.remoteAddress().toString() + "加入了聊天室";
    log.info(msg);
    // 通知目前所有的客户端 channel
    // CHANNEL_GROUP.forEach(c -> c.writeAndFlush(Unpooled.copiedBuffer(msg, StandardCharsets.UTF_8)));
    CHANNEL_GROUP.forEach(c -> c.writeAndFlush(msg));
    // 把客户端 channel 加入 group 中
    CHANNEL_GROUP.add(channel);
    super.channelActive(ctx);
  }
  
  /**
   * 有客户端 channel 关闭
   *
   * @param ctx
   * @throws Exception
   */
  @Override
  public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    Channel channel = ctx.channel();
    String msg = "【客户端】：" + channel.remoteAddress() + "离开了聊天室";
    log.info(msg);
    // 将客户端 channel 移除 channelGroup
    CHANNEL_GROUP.remove(channel);
    
    // 通知目前所有的客户端 channel
    // CHANNEL_GROUP.forEach(c -> c.writeAndFlush(Unpooled.copiedBuffer(msg, StandardCharsets.UTF_8)));
    CHANNEL_GROUP.forEach(c -> c.writeAndFlush(msg));
    
    super.channelInactive(ctx);
  }
  
  /**
   * 有客户端 channel 发送了消息
   *
   * @param ctx
   * @param msg
   * @throws Exception
   */
  @Override
  public void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
    Channel channel = ctx.channel();
    // ByteBuf byteBuf = (ByteBuf) msg;
    String msgStr = "【客户端: " + channel.remoteAddress() + "】：" + msg;
    log.info(msgStr);
    
    String msgStr2 = "【自己: " + channel.remoteAddress() + "】：" + msg;
    // 给其他客户端发送消息
    CHANNEL_GROUP.forEach(c -> {
      if (!Objects.equals(c, channel)) {
        c.writeAndFlush(msgStr);
      } else {
        c.writeAndFlush(msgStr2);
      }
    });
  }
  
  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    cause.printStackTrace();
    ctx.channel().close();
  }
  
}
