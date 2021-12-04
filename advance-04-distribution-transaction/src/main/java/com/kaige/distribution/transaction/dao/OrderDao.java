package com.kaige.distribution.transaction.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kaige.distribution.transaction.entity.MyOrder;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * (Order)表数据库访问层
 *
 * @author kaige
 * @since 2021-12-04 22:28:31
 */
public interface OrderDao extends BaseMapper<MyOrder> {

  /**
   * 批量新增数据（MyBatis原生foreach方法）
   *
   * @param entities List<Order> 实例对象列表
   * @return 影响行数
   */
  int insertBatch(@Param("entities") List<MyOrder> entities);

  /**
   * 批量新增或按主键更新数据（MyBatis原生foreach方法）
   *
   * @param entities List<Order> 实例对象列表
   * @return 影响行数
   * @throws org.springframework.jdbc.BadSqlGrammarException 入参是空List的时候会抛SQL语句错误的异常，请自行校验入参
   */
  int insertOrUpdateBatch(@Param("entities") List<MyOrder> entities);
}
