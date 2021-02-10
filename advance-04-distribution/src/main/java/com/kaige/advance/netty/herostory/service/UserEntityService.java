package com.kaige.advance.netty.herostory.service;

import com.kaige.advance.netty.herostory.async.AsyncOperationProcessor;
import com.kaige.advance.netty.herostory.async.UserEntityAsyncOperation;
import com.kaige.advance.netty.herostory.entity.UserEntity;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Function;

/**
 * 用户表(UserEntity)表服务实现类
 *
 * @author kaige
 * @since 2021-02-06 23:26:34
 */
@Slf4j
public class UserEntityService {
  
  private static final UserEntityService INSTANCE = new UserEntityService();
  
  private UserEntityService() {
  }
  
  public static UserEntityService getInstance() {
    return INSTANCE;
  }
  
  /**
   * 用户登录，没有用户就进行注册
   *
   * @param userName 用户名
   * @param password 密码
   * @param callback 回调任务
   */
  public void userLogin(String userName, String password, Function<UserEntity, Void> callback) {
    // 使用线程池来异步处理
    AsyncOperationProcessor.getInstance()
      .process(new UserEntityAsyncOperation(userName, password, callback));
  }
  
}
