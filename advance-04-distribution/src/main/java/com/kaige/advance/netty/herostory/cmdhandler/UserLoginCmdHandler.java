package com.kaige.advance.netty.herostory.cmdhandler;

import com.kaige.advance.netty.herostory.entity.UserEntity;
import com.kaige.advance.netty.herostory.model.MoveState;
import com.kaige.advance.netty.herostory.model.User;
import com.kaige.advance.netty.herostory.model.UserManager;
import com.kaige.advance.netty.herostory.msg.GameMsgProtocol;
import com.kaige.advance.netty.herostory.service.UserEntityService;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * 用户登录命令处理器
 */
@Slf4j
public class UserLoginCmdHandler implements ICmdHandler<GameMsgProtocol.UserLoginCmd> {
  
  @Override
  public void handle(ChannelHandlerContext ctx, GameMsgProtocol.UserLoginCmd cmd) {
    if (Objects.isNull(ctx) || Objects.isNull(cmd)) {
      return;
    }
    
    if (StringUtils.isBlank(cmd.getUserName()) || StringUtils.isBlank(cmd.getPassword())) {
      return;
    }
    
    try {
      GameMsgProtocol.UserLoginResult.Builder builder = GameMsgProtocol.UserLoginResult
        .newBuilder();
      
      UserEntityService userEntityService = UserEntityService.getInstance();
      // 执行用户登录
      UserEntity userEntity = userEntityService.userLogin(cmd.getUserName(), cmd.getPassword());
      log.info("userEntity: {}", userEntity);
      
      if (Objects.isNull(userEntity)) {
        builder.setUserId(-1);
      } else {
        // 初始化用户信息，并放入 channel
        User user = new User(userEntity.getId(), userEntity.getHeroAvatar(), new MoveState(), 100,
                             userEntity.getName());
        // 添加用户
        UserManager.addUser(user);
        // 将用户 id 保存到 session
        UserManager.setUserIdToChannel(user.getUserId(), ctx);
        
        builder.setUserId(user.getUserId()).setHeroAvatar(user.getHeroAvatar())
          .setUserName(userEntity.getName());
      }
      
      ctx.writeAndFlush(builder.build());
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
    
  }
  
}
