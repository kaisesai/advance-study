package com.kaige.advance.netty.herostory.service;

import com.kaige.advance.netty.herostory.config.SqlSessionFactoryConfig;
import com.kaige.advance.netty.herostory.dao.UserEntityDao;
import com.kaige.advance.netty.herostory.entity.UserEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.SqlSession;

import java.util.Objects;

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
   * @return 用户信息
   */
  public UserEntity userLogin(String userName, String password) {
    try (SqlSession sqlSession = SqlSessionFactoryConfig.openSession()) {
      if (Objects.isNull(sqlSession)) {
        return null;
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
      // 密码正确
      // 直接返回
      return userEntity;
    }
  }
  
  /**
   * 通过ID查询单条数据
   *
   * @param id 主键
   * @return 实例对象
   */
  public UserEntity queryById(int id) {
    try (SqlSession sqlSession = SqlSessionFactoryConfig.openSession()) {
      if (Objects.isNull(sqlSession)) {
        return null;
      }
      UserEntityDao mapper = sqlSession.getMapper(UserEntityDao.class);
      return mapper.queryById(id);
    }
  }
  
  /**
   * 新增数据
   *
   * @param userEntity 实例对象
   * @return 实例对象
   */
  public UserEntity insert(UserEntity userEntity) {
    try (SqlSession sqlSession = SqlSessionFactoryConfig.openSession()) {
      if (Objects.isNull(sqlSession)) {
        return null;
      }
      UserEntityDao mapper = sqlSession.getMapper(UserEntityDao.class);
      mapper.insert(userEntity);
      return userEntity;
    }
  }
  
}
