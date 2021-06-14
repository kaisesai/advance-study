package com.kaige.advance.reflect.orm.step0;

import com.kaige.advance.reflect.orm.entity.UserEntity;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/** 使用 JDBC 读取数据库数据的程序 */
public class App0 {
  
  public static void main(String[] args) throws Exception {
    
    // 加载数据库程序
    
    String dbUrl = "jdbc:mysql://www.kaige.com:3306/eshop?user=eshop&password=eshop2020";
    
    try (Connection connection = DriverManager.getConnection(dbUrl)) {
      ResultSet resultSet;
      try (Statement statement = connection.createStatement()) {
        
        String sql = "select * from eshop.user limit 20000";
        resultSet = statement.executeQuery(sql);
        
        long start = System.currentTimeMillis();
        while (resultSet.next()) {
          UserEntity userEntity = new UserEntity();
          userEntity.setId(resultSet.getLong("id"));
          userEntity.setName(resultSet.getString("name"));
          userEntity.setAge(resultSet.getInt("age"));
          userEntity.setCreateTime(resultSet.getTimestamp("create_time"));
          userEntity.setUpdateTime(resultSet.getTimestamp("update_time"));
          // System.out.println("userEntity = " + userEntity);
          
          // 上述代码我们把它重构下，这些逻辑移动到一个 UserEntityHelper 类中，未来表结构有变化时，可以直接修改 helper 类
        }
        
        long end = System.currentTimeMillis();
        // 实例化花费时间：67ms
        System.out.println("实例化花费时间：" + (end - start) + "ms");
      }
    }
  }
  
}
