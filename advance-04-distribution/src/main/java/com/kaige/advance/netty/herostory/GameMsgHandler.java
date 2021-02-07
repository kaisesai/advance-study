package com.kaige.advance.netty.herostory;

import com.kaige.advance.netty.herostory.model.UserManager;
import com.kaige.advance.netty.herostory.msg.GameMsgProtocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * 游戏消息处理器
 */
@Slf4j
public class GameMsgHandler extends SimpleChannelInboundHandler<Object> {
  
  @Override
  public void channelActive(ChannelHandlerContext ctx) {
    if (Objects.isNull(ctx)) {
      return;
    }
    try {
      // 添加信道
      Broadcaster.addChannel(ctx.channel());
      // 将客户端连接板并且激活的 channel 添加到全局信道上
      super.channelActive(ctx);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
  }
  
  @Override
  public void handlerRemoved(ChannelHandlerContext ctx) {
    log.info("handlerRemoved...");
    if (Objects.isNull(ctx)) {
      return;
    }
    // 移除全局信道
    Broadcaster.removeChannel(ctx.channel());
  
    // 移除用户信息
    Integer userId = UserManager.getUserIdFromCtx(ctx);
    if (Objects.isNull(userId)) {
      return;
    }
    // 移除用户
    UserManager.removeUser(userId);
    // 用户下线消息
    GameMsgProtocol.UserQuitResult userQuitResult = GameMsgProtocol.UserQuitResult.newBuilder()
      .setQuitUserId(userId).build();
    // 广播消息
    Broadcaster.broadcast(userQuitResult);
  }
  
  @Override
  public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    log.info("channelInactive...");
    super.channelInactive(ctx);
  }
  
  @Override
  protected void channelRead0(ChannelHandlerContext ctx, Object msg) {
    if (Objects.isNull(ctx) || Objects.isNull(msg)) {
      return;
    }
    log.info("收到客户端的消息，msgClass = {}，msgBody = {}", msg.getClass().getSimpleName(), msg.toString());
    // 处理消息
    GameMsgProcessor.getInstance().process(ctx, msg);
  }
  
}
