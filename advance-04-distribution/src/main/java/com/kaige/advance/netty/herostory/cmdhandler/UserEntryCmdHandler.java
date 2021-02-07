package com.kaige.advance.netty.herostory.cmdhandler;

import com.kaige.advance.netty.herostory.Broadcaster;
import com.kaige.advance.netty.herostory.model.User;
import com.kaige.advance.netty.herostory.model.UserManager;
import com.kaige.advance.netty.herostory.msg.GameMsgProtocol;
import io.netty.channel.ChannelHandlerContext;

import java.util.Objects;

/**
 * 用户入场命令处理器
 */
public class UserEntryCmdHandler implements ICmdHandler<GameMsgProtocol.UserEntryCmd> {
  
  @Override
  public void handle(ChannelHandlerContext ctx, GameMsgProtocol.UserEntryCmd cmd) {
    // 获取用户信息
    User user = UserManager.getUserFromCtx(ctx);
    if (Objects.isNull(user)) {
      return;
    }
    
    // 构建入场消息
    GameMsgProtocol.UserEntryResult newResult = GameMsgProtocol.UserEntryResult.newBuilder()
      .setUserId(user.getUserId()).setHeroAvatar(user.getHeroAvatar())
      .setUserName(user.getUserName()).build();
    
    // 广播消息
    Broadcaster.broadcast(newResult);
  }
  
}
