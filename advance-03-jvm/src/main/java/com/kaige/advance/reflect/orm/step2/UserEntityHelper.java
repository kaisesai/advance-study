package com.kaige.advance.reflect.orm.step2;

import com.kaige.advance.reflect.orm.Column;
import com.kaige.advance.reflect.orm.entity.UserEntity2;

import java.lang.reflect.Field;
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
  public static UserEntity2 createUserEntity(ResultSet resultSet) throws SQLException {
    if (Objects.isNull(resultSet)) {
      return null;
    }
    // 使用反射来动态设置这些字段
    UserEntity2 userEntity = new UserEntity2();
    
    // 获取声明的字段
    Field[] fields = userEntity.getClass().getDeclaredFields();
    
    for (Field field : fields) {
      // 过滤没有自定义注解的字段
      Column column = field.getAnnotation(Column.class);
      if (Objects.isNull(column)) {
        continue;
      }
      
      // 获取数据集的数据
      Object value = resultSet.getObject(column.value());
      if (Objects.nonNull(value)) {
        try {
          field.setAccessible(true);
          field.set(userEntity, value);
        } catch (IllegalAccessException e) {
          e.printStackTrace();
        }
      }
    }
    
    // 还可以继续优化，当有多个实体类时，可以通过泛型抽象类或者泛型接口的方式，通过反射来创建
    return userEntity;
  }
  
}
