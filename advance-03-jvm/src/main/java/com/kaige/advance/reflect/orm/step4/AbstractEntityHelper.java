package com.kaige.advance.reflect.orm.step4;

import java.sql.ResultSet;

/** 抽象的实体类辅助器 */
public abstract class AbstractEntityHelper<T> {
  
  /**
   * 根据结果集构建实体类
   *
   * @param resultSet
   * @return
   */
  public abstract T createEntity(ResultSet resultSet);
  
}
