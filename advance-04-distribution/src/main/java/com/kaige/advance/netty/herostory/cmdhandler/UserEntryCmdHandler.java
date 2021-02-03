package com.kaige.advance.netty.herostory.cmdhandler;

import com.kaige.advance.netty.herostory.Broadcaster;
import com.kaige.advance.netty.herostory.User;
import com.kaige.advance.netty.herostory.UserManager;
import com.kaige.advance.netty.herostory.msg.GameMsgProtocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;

/**
 * 用户入场命令处理器
 */
public class UserEntryCmdHandler implements ICmdHandler<GameMsgProtocol.UserEntryCmd> {
  
  @Override
  public void handle(ChannelHandlerContext ctx, GameMsgProtocol.UserEntryCmd cmd) {
    User user = new User(cmd.getUserId(), cmd.getHeroAvatar());
    // 添加用户
    UserManager.addUser(user);
    
    // 将用户 id 保存到 session
    ctx.channel().attr(AttributeKey.valueOf("userId")).set(cmd.getUserId());
    
    // 构建入场消息
    GameMsgProtocol.UserEntryResult newResult = GameMsgProtocol.UserEntryResult.newBuilder()
      .setUserId(cmd.getUserId()).setHeroAvatar(cmd.getHeroAvatar()).build();
    
    // 广播消息
    Broadcaster.broadcast(newResult);
    
  }
  
}
