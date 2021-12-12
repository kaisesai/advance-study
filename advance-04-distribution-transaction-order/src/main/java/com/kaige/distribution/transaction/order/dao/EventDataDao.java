package com.kaige.distribution.transaction.order.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kaige.distribution.transaction.order.entity.EventData;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 本地事件表(EventData)表数据库访问层
 *
 * @author kaige
 * @since 2021-11-28 00:06:51
 */
public interface EventDataDao extends BaseMapper<EventData> {

  /**
   * 批量新增数据（MyBatis原生foreach方法）
   *
   * @param entities List<EventData> 实例对象列表
   * @return 影响行数
   */
  int insertBatch(@Param("entities") List<EventData> entities);

  /**
   * 批量新增或按主键更新数据（MyBatis原生foreach方法）
   *
   * @param entities List<EventData> 实例对象列表
   * @return 影响行数
   * @throws org.springframework.jdbc.BadSqlGrammarException 入参是空List的时候会抛SQL语句错误的异常，请自行校验入参
   */
  int insertOrUpdateBatch(@Param("entities") List<EventData> entities);
}
