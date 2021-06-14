package com.kaige.advance.netty.herostory.cmdhandler;

import com.kaige.advance.netty.herostory.Broadcaster;
import com.kaige.advance.netty.herostory.model.User;
import com.kaige.advance.netty.herostory.model.UserManager;
import com.kaige.advance.netty.herostory.msg.GameMsgProtocol;
import io.netty.channel.ChannelHandlerContext;

import java.util.Objects;

/** 用户移动命令处理器 */
public class UserMoveToCmdHandler implements ICmdHandler<GameMsgProtocol.UserMoveToCmd> {
  
  @Override
  public void handle(ChannelHandlerContext ctx, GameMsgProtocol.UserMoveToCmd cmd) {
    // 获取用户 id
    Integer userId = UserManager.getUserIdFromCtx(ctx);
    if (Objects.isNull(userId)) {
      return;
    }
    
    // 获取用户信息
    User user = UserManager.getUserById(userId);
    if (Objects.isNull(user)) {
      return;
    }
    
    long nowTime = System.currentTimeMillis();
    // 修改用户移动状态
    user.getMoveState().setStartTime(nowTime);
    user.getMoveState().setFromPosX(cmd.getMoveFromPosX());
    user.getMoveState().setFromPosY(cmd.getMoveFromPosY());
    user.getMoveState().setToPosX(cmd.getMoveToPosX());
    user.getMoveState().setToPosY(cmd.getMoveToPosY());
    
    // 用户移动消息
    GameMsgProtocol.UserMoveToResult userMoveToResult = GameMsgProtocol.UserMoveToResult
      .newBuilder().setMoveUserId(userId).setMoveFromPosX(cmd.getMoveFromPosX())
      .setMoveFromPosY(cmd.getMoveFromPosY()).setMoveToPosX(cmd.getMoveToPosX())
      .setMoveToPosY(cmd.getMoveToPosY()).setMoveStartTime(nowTime).build();
    
    // 广播用户移动结果
    Broadcaster.broadcast(userMoveToResult);
  }
  
}
