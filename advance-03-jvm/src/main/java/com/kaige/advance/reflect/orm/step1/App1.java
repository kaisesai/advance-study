package com.kaige.advance.reflect.orm.step1;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/** 使用 JDBC 读取数据库数据的程序 */
public class App1 {
  
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
          // 将映射用户实体类代码逻辑抽取出来，未来有数据变更时可以直接修改抽取的逻辑
          UserEntityHelper.createUserEntity(resultSet);
          // System.out.println("userEntity = " + userEntity);
        }
        long end = System.currentTimeMillis();
        // 实例化花费时间：61ms
        System.out.println("实例化花费时间：" + (end - start) + "ms");
      }
    }
  }
  
}
