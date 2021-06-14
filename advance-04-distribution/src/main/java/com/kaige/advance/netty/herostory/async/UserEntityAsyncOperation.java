package com.kaige.advance.netty.herostory.async;

import com.kaige.advance.netty.herostory.MainThreadProcessor;
import com.kaige.advance.netty.herostory.config.SqlSessionFactoryConfig;
import com.kaige.advance.netty.herostory.dao.UserEntityDao;
import com.kaige.advance.netty.herostory.entity.UserEntity;
import com.kaige.advance.netty.herostory.service.UserEntityService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.SqlSession;

import java.util.Objects;
import java.util.function.Function;

/** 用户实体类异步操作 */
@Slf4j
public class UserEntityAsyncOperation implements IAsyncOperation {
  
  private final Function<UserEntity, Void> function;
  
  private final String userName;
  
  private final String password;
  
  private UserEntity userEntity;
  
  public UserEntityAsyncOperation(String userName, String password,
                                  Function<UserEntity, Void> callback) {
    this.userName = userName;
    this.password = password;
    this.function = callback;
  }
  
  @Override
  public void doAsync() {
    try (SqlSession sqlSession = SqlSessionFactoryConfig.openSession()) {
      if (Objects.isNull(sqlSession)) {
        return;
      }
      UserEntityDao mapper = sqlSession.getMapper(UserEntityDao.class);
      
      log.info("当前线程执行 userLogin...");
      
      UserEntity userEntity = mapper.queryByUserName(userName);
      if (userEntity == null) {
        // 没有该用户
        // 则创建用户，默认英雄角色是萨满
        userEntity = new UserEntity();
        userEntity.setName(userName);
        userEntity.setPassword(password);
        userEntity.setHeroAvatar("Hero_Shaman");
        // 保存用户信息
        mapper.insert(userEntity);
      } else if (!StringUtils.equals(userEntity.getPassword(), password)) {
        // 密码不正确
        throw new IllegalStateException("密码不正确，userName: " + userName + ", password: " + password);
      }
      
      // 保存到 redis 中
      UserEntityService.getInstance().updateUserBasicInfoToRedis(userEntity);
      
      // 密码正确
      this.userEntity = userEntity;
    }
  }
  
  @Override
  public void doFinish() {
    // 应用函数，使用 MainThreadProcessor 主线程处理消息
    if (Objects.nonNull(function)) {
      MainThreadProcessor.getInstance().process(() -> function.apply(this.userEntity));
    }
  }
  
  @Override
  public int getBindId() {
    // 使用用户名的哈希值与线程池数量取模操作
    int charInt = userName.charAt(userName.length() - 1);
    return charInt % AsyncOperationProcessor.EXECUTOR_SERVICE_NUM;
  }
  
}
