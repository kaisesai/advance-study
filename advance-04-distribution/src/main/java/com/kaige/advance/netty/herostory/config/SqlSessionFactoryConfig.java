package com.kaige.advance.netty.herostory.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.Reader;
import java.util.Objects;

@Slf4j
public class SqlSessionFactoryConfig {
  
  private static SqlSessionFactory sqlSessionFactory;
  
  /**
   * 初始化 {@link SqlSessionFactory}
   */
  public static void init() {
    SqlSessionFactoryBuilder factoryBuilder = new SqlSessionFactoryBuilder();
    try (Reader reader = Resources.getResourceAsReader("MyBatisConfig.xml")) {
      sqlSessionFactory = factoryBuilder.build(reader);
      
      // 测试数据库连接
      SqlSession tempSession = openSession();
      assert tempSession != null;
      
      tempSession.getConnection().createStatement().execute("SELECT -1");
      tempSession.close();
      
      log.error("SqlSessionFactory 初始化完毕");
    } catch (Exception e) {
      throw new IllegalStateException("SqlSessionFactory 初始化异常", e);
    }
    
  }
  
  /**
   * 打开一个 session
   *
   * @return
   */
  public static SqlSession openSession() {
    try {
      if (Objects.isNull(sqlSessionFactory)) {
        throw new IllegalStateException("SqlSessionFactory 未初始化");
      }
      return sqlSessionFactory.openSession(true);
    } catch (IllegalStateException e) {
      log.error(e.getMessage(), e);
    }
    return null;
  }
  
}
