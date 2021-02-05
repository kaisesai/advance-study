package com.kaige.advance.netty.herostory.cmdhandler;

import com.kaige.advance.netty.herostory.model.MoveState;
import com.kaige.advance.netty.herostory.model.User;
import com.kaige.advance.netty.herostory.model.UserManager;
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
    if (CollectionUtils.isEmpty(users)) {
      return;
    }
  
    users.forEach(user -> {
      if (Objects.nonNull(user)) {
        builder.addUserInfo(buildUserInfo(user));
      }
    });
  
    // 写回消息
    GameMsgProtocol.WhoElseIsHereResult newResult = builder.build();
    ctx.writeAndFlush(newResult);
  }
  
  /**
   * 构建用户信息消息
   *
   * @param user
   * @return
   */
  private GameMsgProtocol.WhoElseIsHereResult.UserInfo buildUserInfo(User user) {
    // 移动状态消息
    GameMsgProtocol.WhoElseIsHereResult.UserInfo.MoveState.Builder moveState = buildMoveState(
      user.getMoveState());
    // 用户信息消息
    return GameMsgProtocol.WhoElseIsHereResult.UserInfo.newBuilder().setUserId(user.getUserId())
      .setHeroAvatar(user.getHeroAvatar()).setMoveState(moveState).build();
  }
  
  /**
   * 构建用户移动状态消息
   *
   * @param moveState
   * @return
   */
  private GameMsgProtocol.WhoElseIsHereResult.UserInfo.MoveState.Builder buildMoveState(
    MoveState moveState) {
    return GameMsgProtocol.WhoElseIsHereResult.UserInfo.MoveState.newBuilder()
      .setFromPosX(moveState.getFromPosX()).setFromPosY(moveState.getFromPosY())
      .setToPosX(moveState.getToPosX()).setToPosY(moveState.getToPosY())
      .setStartTime(moveState.getStartTime());
  }
  
}
