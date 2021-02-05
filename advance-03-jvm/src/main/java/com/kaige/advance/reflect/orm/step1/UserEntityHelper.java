package com.kaige.advance.reflect.orm.step1;

import com.kaige.advance.reflect.orm.entity.UserEntity;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

/**
 * 用户实体类辅助器
 *
 * @author liukai 2021年02月04日
 */
public class UserEntityHelper {
  
  /**
   * 创建用户实体类
   *
   * @param resultSet
   * @return
   * @throws SQLException
   */
  public static UserEntity createUserEntity(ResultSet resultSet) throws SQLException {
    if (Objects.isNull(resultSet)) {
      return null;
    }
    UserEntity userEntity = new UserEntity();
    userEntity.setId(resultSet.getLong("id"));
    userEntity.setName(resultSet.getString("name"));
    userEntity.setAge(resultSet.getInt("age"));
    userEntity.setCreateTime(resultSet.getTimestamp("create_time"));
    userEntity.setUpdateTime(resultSet.getTimestamp("update_time"));
    
    // 这里我们还可以使用反射来优化下
    
    return userEntity;
  }
  
}
