package com.kaige.advance.netty.simple;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;

/**
 * 自定义 handler 需要继承 netty 规定好的某个 handlerAdapter（规范）
 *
 * <p>ChannelInboundHandlerAdapter 是一个入站请求处理器，对应的还有出站请求处理器
 */
@Slf4j
public class NettyServerHandler extends ChannelInboundHandlerAdapter {
  
  public NettyServerHandler() {
    super();
  }
  
  /**
   * 读取客户端发送的数据
   *
   * @param ctx 上下文对象，含有通道 channel，管道 pipeline
   * @param msg 客户端发送的消息
   * @throws Exception
   */
  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    // Channel channel = ctx.channel();
    // pipeline 本质是一个双向链表
    // ChannelPipeline pipeline = ctx.pipeline();
    ByteBuf byteBuf = (ByteBuf) msg;
    log.info("客户端发送了消息：{}", byteBuf.toString(StandardCharsets.UTF_8));
  }
  
  /**
   * 数据读取完毕处理方法
   *
   * @param ctx
   * @throws Exception
   */
  @Override
  public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
    ByteBuf byteBuf = Unpooled.copiedBuffer("helloClient", StandardCharsets.UTF_8);
    ctx.writeAndFlush(byteBuf);
  }
  
  /**
   * 处理异常，一般是需要关闭通道
   *
   * @param ctx
   * @param cause
   * @throws Exception
   */
  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    ctx.close();
  }
  
}
