package com.kaige.advance.netty.herostory.cmdhandler;

import com.kaige.advance.netty.herostory.Broadcaster;
import com.kaige.advance.netty.herostory.model.User;
import com.kaige.advance.netty.herostory.model.UserManager;
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
    
    int targetUserId = cmd.getTargetUserId();
    // 广播攻击消息结果
    broadcastAttkResult(userId, targetUserId);
    
    // 获取目标用户
    User targetUser = UserManager.getUserById(targetUserId);
    if (Objects.isNull(targetUser)) {
      return;
    }
  
    // 用户减血
    int dmgPoint = 10;
  
    int currentHp = targetUser.getCurrentHp() - dmgPoint;
    targetUser.setCurrentHp(currentHp);
  
    log.info(Thread.currentThread().getName() + " handle 当前用户 HP : {}", currentHp);
  
    // 广播减血消息结果
    broadcastSubtractHpResult(targetUserId, dmgPoint);
  
    // 广播用户死亡结果
    if (currentHp <= 0) {
      // 广播用户死亡消息结果
      broadcastUserDieResult(targetUserId);
    }
  }
  
  /**
   * 广播用户死亡消息结果
   *
   * @param targetUserId
   */
  private void broadcastUserDieResult(int targetUserId) {
    GameMsgProtocol.UserDieResult userDieResult = GameMsgProtocol.UserDieResult.newBuilder()
      .setTargetUserId(targetUserId).build();
    // 广播消息结果
    Broadcaster.broadcast(userDieResult);
  }
  
  private void broadcastSubtractHpResult(int targetUserId, int dmgPoint) {
    // 构建减血消息
    GameMsgProtocol.UserSubtractHpResult subtractHpResult = GameMsgProtocol.UserSubtractHpResult
      .newBuilder().setSubtractHp(dmgPoint).setTargetUserId(targetUserId).build();
    
    // 广播减血消息结果
    Broadcaster.broadcast(subtractHpResult);
  }
  
  /**
   * 广播攻击消息结果
   *
   * @param userId
   * @param targetUserId
   */
  private void broadcastAttkResult(Integer userId, int targetUserId) {
    // 构建攻击消息
    GameMsgProtocol.UserAttkResult newResult = GameMsgProtocol.UserAttkResult.newBuilder()
      .setAttkUserId(userId).setTargetUserId(targetUserId).build();
    // 广播消息
    Broadcaster.broadcast(newResult);
  }
  
}
