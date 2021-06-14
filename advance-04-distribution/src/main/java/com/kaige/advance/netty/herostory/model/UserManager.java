package com.kaige.advance.netty.herostory.model;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/** 用户管理器 */
public class UserManager {
  
  private static final Map<Integer, User> USER_MAP = new ConcurrentHashMap<>();
  
  private UserManager() {
  }
  
  /**
   * 添加用户
   *
   * @param user
   */
  public static void addUser(User user) {
    USER_MAP.putIfAbsent(user.getUserId(), user);
  }
  
  /**
   * 删除用户
   *
   * @param userId
   */
  public static void removeUser(int userId) {
    USER_MAP.remove(userId);
  }
  
  /** @return 用户列表 */
  public static Collection<User> listUser() {
    return USER_MAP.values();
  }
  
  /**
   * 从 ChannelHandlerContext 中获取用户 id
   *
   * @param ctx
   * @return
   */
  public static Integer getUserIdFromCtx(ChannelHandlerContext ctx) {
    Object value = ctx.channel().attr(AttributeKey.valueOf("userId")).get();
    if (Objects.isNull(value)) {
      return null;
    }
    return (Integer) value;
  }
  
  /**
   * 设置用户 ID 到 channel 中
   *
   * @param userId
   * @param ctx
   */
  public static void setUserIdToChannel(Integer userId, ChannelHandlerContext ctx) {
    ctx.channel().attr(AttributeKey.valueOf("userId")).set(userId);
  }
  
  /**
   * 获取用户信息
   *
   * @param userId
   * @return
   */
  public static User getUserById(Integer userId) {
    if (Objects.isNull(userId)) {
      return null;
    }
    return USER_MAP.get(userId);
  }
  
  /**
   * 获取用户信息
   *
   * @param ctx
   * @return
   */
  public static User getUserFromCtx(ChannelHandlerContext ctx) {
    Integer userId = (Integer) ctx.channel().attr(AttributeKey.valueOf("userId")).get();
    if (Objects.isNull(userId)) {
      return null;
    }
    return getUserById(userId);
  }
  
}
