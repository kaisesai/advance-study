package com.kaige.advance.reflect.orm.step3;

import com.kaige.advance.reflect.orm.Column;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

/**
 * 通用的实体类辅助器
 *
 * @author liukai 2021年02月04日
 */
public class CommonEntityHelper {
  
  /**
   * 根据类型创建对应的实体类
   *
   * @param clazz
   * @param resultSet
   * @param <T>
   * @return
   * @throws SQLException
   */
  public static <T> T createEntity(Class<T> clazz, ResultSet resultSet) throws SQLException {
    if (Objects.isNull(resultSet)) {
      return null;
    }
    // 使用反射来动态设置这些字段
    try {
      T t = clazz.newInstance();

      /*
       反射性能慢的点：
         1. 每次都要调用 getDeclaredFields() 方法获取声明的字段
         2. 每个字段都要调用 getAnnotation() 方法获取自定义注解
      */
      
      // 获取声明的字段
      Field[] fields = clazz.getDeclaredFields();
      
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
            field.set(t, value);
          } catch (IllegalAccessException e) {
            e.printStackTrace();
          }
        }
      }
      return t;
    } catch (InstantiationException | IllegalAccessException e) {
      e.printStackTrace();
    }
    return null;
    
    // 它的缺点是性能差，但是为什么呢？具体哪个方法慢呢？
  }
  
}
