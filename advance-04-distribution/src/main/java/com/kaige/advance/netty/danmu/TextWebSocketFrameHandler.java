package com.kaige.advance.netty.danmu;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;

/** 文本格式的 websocket 处理器 */
public class TextWebSocketFrameHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
  
  private static final ChannelGroup channels = new DefaultChannelGroup(
    GlobalEventExecutor.INSTANCE);
  
  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    // 关闭连接
    System.out
      .println("client: " + ctx.channel().remoteAddress() + " 异常，message: " + cause.getMessage());
    cause.printStackTrace();
    ctx.close();
  }
  
  @Override
  protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
    // 读取数据
    Channel c = ctx.channel();
    // 广播给所有 channel
    channels.forEach(channel -> {
      if (c == channel) {
        channel.writeAndFlush(new TextWebSocketFrame("【自己】" + msg.text()));
      } else {
        channel.writeAndFlush(new TextWebSocketFrame(msg.text()));
      }
    });
  }
  
  // @Override
  // public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
  //   String text = "[server-handlerAdded] - " + ctx.channel().remoteAddress().toString() + "加入";
  //   System.out.println(text);
  //   channels.writeAndFlush(new TextWebSocketFrame(text));
  //   // channels.add(ctx.channel());
  // }
  //
  // @Override
  // public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
  //   // channels.remove(ctx.channel());
  //   String text = "[server-handlerRemoved] - " + ctx.channel().remoteAddress().toString() + "离开";
  //   System.out.println(text);
  //   channels.writeAndFlush(new TextWebSocketFrame(text));
  // }
  
  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    String text = "[server-channelActive] - " + ctx.channel().remoteAddress().toString() + "加入";
    System.out.println(text);
    channels.writeAndFlush(new TextWebSocketFrame(
      "[server-channelActive] - " + ctx.channel().remoteAddress().toString() + "加入"));
    channels.add(ctx.channel());
  }
  
  @Override
  public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    channels.remove(ctx.channel());
    String text = "[server-channelInactive] - " + ctx.channel().remoteAddress().toString() + "离开";
    System.out.println(text);
    channels.writeAndFlush(new TextWebSocketFrame(text));
  }
  
}
