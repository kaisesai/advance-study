package com.kaige.advance.netty.herostory.cmdhandler;

import com.kaige.advance.netty.herostory.User;
import com.kaige.advance.netty.herostory.UserManager;
import com.kaige.advance.netty.herostory.msg.GameMsgProtocol;
import io.netty.channel.ChannelHandlerContext;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collection;
import java.util.Objects;

/**
 * 还有谁命令处理器
 */
public class WhoElseIsHereCmdHandler implements ICmdHandler<GameMsgProtocol.WhoElseIsHereCmd> {
  
  @Override
  public void handle(ChannelHandlerContext ctx, GameMsgProtocol.WhoElseIsHereCmd cmd) {
    // 构建消息
    GameMsgProtocol.WhoElseIsHereResult.Builder builder = GameMsgProtocol.WhoElseIsHereResult
      .newBuilder();
    Collection<User> users = UserManager.listUser();
    
    if (CollectionUtils.isNotEmpty(users)) {
      users.forEach(user -> {
        if (Objects.nonNull(user)) {
          builder.addUserInfo(
            GameMsgProtocol.WhoElseIsHereResult.UserInfo.newBuilder().setUserId(user.getUserId())
              .setHeroAvatar(user.getHeroAvatar()).build());
        }
      });
    }
    
    // 写回消息
    GameMsgProtocol.WhoElseIsHereResult newResult = builder.build();
    ctx.writeAndFlush(newResult);
  }
  
}
