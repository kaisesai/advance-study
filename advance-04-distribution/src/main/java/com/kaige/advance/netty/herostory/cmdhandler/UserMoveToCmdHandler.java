package com.kaige.advance.netty.herostory.cmdhandler;

import com.kaige.advance.netty.herostory.Broadcaster;
import com.kaige.advance.netty.herostory.UserManager;
import com.kaige.advance.netty.herostory.msg.GameMsgProtocol;
import io.netty.channel.ChannelHandlerContext;

import java.util.Objects;

/**
 * 用户移动命令处理器
 */
public class UserMoveToCmdHandler implements ICmdHandler<GameMsgProtocol.UserMoveToCmd> {
  
  @Override
  public void handle(ChannelHandlerContext ctx, GameMsgProtocol.UserMoveToCmd cmd) {
    // 获取用户 id
    Integer userId = UserManager.getUserIdFromCtx(ctx);
    if (Objects.isNull(userId)) {
      return;
    }
    
    GameMsgProtocol.UserMoveToResult userMoveToResult = GameMsgProtocol.UserMoveToResult
      .newBuilder().setMoveUserId(userId).setMoveToPosX(cmd.getMoveToPosX())
      .setMoveToPosY(cmd.getMoveToPosY()).build();
    
    // 广播用户移动结果
    Broadcaster.broadcast(userMoveToResult);
  }
  
}
