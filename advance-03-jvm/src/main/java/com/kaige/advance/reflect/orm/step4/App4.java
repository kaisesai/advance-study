package com.kaige.advance.reflect.orm.step4;

import com.kaige.advance.reflect.orm.entity.UserEntity2;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Objects;

/**
 * 使用 JDBC 读取数据库数据的程序
 */
@Slf4j
public class App4 {
  
  public static void main(String[] args) throws Exception {
    
    // 加载数据库程序
    
    String dbUrl = "jdbc:mysql://www.kaige.com:3306/eshop?user=eshop&password=eshop2020";
    
    // 将映射用户实体类代码逻辑抽取出来，未来有数据变更时可以直接修改抽取的逻辑
    AbstractEntityHelper<UserEntity2> entityHelper = EntityHelperFactory
      .getEntityHelper(UserEntity2.class);
    if (Objects.isNull(entityHelper)) {
      log.warn("没有 UserEntity2 对应的辅助器");
      return;
    }
    
    try (Connection connection = DriverManager.getConnection(dbUrl)) {
      ResultSet resultSet;
      try (Statement statement = connection.createStatement()) {
        
        String sql = "select * from eshop.user limit 20000";
        resultSet = statement.executeQuery(sql);
        
        long start = System.currentTimeMillis();
        while (resultSet.next()) {
          UserEntity2 userEntity = entityHelper.createEntity(resultSet);
          // System.out.println("userEntity = " + userEntity);
        }
        long end = System.currentTimeMillis();
        // 实例化花费时间：64ms
        // 发现它的运行时间和 App0 的实例化花费时间一样
        System.out.println("实例化花费时间：" + (end - start) + "ms");
      }
    }
  }
  
}
