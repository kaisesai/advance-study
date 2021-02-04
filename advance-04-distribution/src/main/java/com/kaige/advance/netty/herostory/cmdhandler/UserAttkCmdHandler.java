package com.kaige.advance.netty.herostory.cmdhandler;

import com.kaige.advance.netty.herostory.Broadcaster;
import com.kaige.advance.netty.herostory.UserManager;
import com.kaige.advance.netty.herostory.msg.GameMsgProtocol;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * 用户攻击命令处理器
 */
@Slf4j
public class UserAttkCmdHandler implements ICmdHandler<GameMsgProtocol.UserAttkCmd> {
  
  @Override
  public void handle(ChannelHandlerContext ctx, GameMsgProtocol.UserAttkCmd cmd) {
    
    Integer userId = UserManager.getUserIdFromCtx(ctx);
    if (Objects.isNull(userId)) {
      return;
    }
    
    // 构建攻击消息
    GameMsgProtocol.UserAttkResult newResult = GameMsgProtocol.UserAttkResult.newBuilder()
      .setAttkUserId(userId).setTargetUserId(cmd.getTargetUserId()).build();
    
    // 广播消息
    Broadcaster.broadcast(newResult);
  }
  
}
