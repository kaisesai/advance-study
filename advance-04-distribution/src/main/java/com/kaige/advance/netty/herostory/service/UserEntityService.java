package com.kaige.advance.netty.herostory.service;

import com.alibaba.fastjson.JSONObject;
import com.kaige.advance.netty.herostory.async.AsyncOperationProcessor;
import com.kaige.advance.netty.herostory.async.UserEntityAsyncOperation;
import com.kaige.advance.netty.herostory.entity.UserEntity;
import com.kaige.advance.netty.herostory.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;

import java.util.Objects;
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
  
  /**
   * 更新用户基本信息到 redis
   *
   * @param userEntity
   */
  public void updateUserBasicInfoToRedis(UserEntity userEntity) {
    if (Objects.isNull(userEntity) || userEntity.getId() <= 0) {
      return;
    }
    try (Jedis jedis = RedisUtil.getJedis()) {
      JSONObject jo = new JSONObject();
      jo.put("username", userEntity.getName());
      jo.put("hero_avatar", userEntity.getHeroAvatar());
      String value = jo.toJSONString();
      // 保存数据到 redis
      jedis.hset("u_" + userEntity.getId(), "basicinfo", value);
      
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
  }
  
}
