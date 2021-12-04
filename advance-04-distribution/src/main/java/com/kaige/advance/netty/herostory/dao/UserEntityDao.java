package com.kaige.advance.netty.herostory.dao;

import com.kaige.advance.netty.herostory.entity.UserEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户表(UserEntity)表数据库访问层
 *
 * @author kaige
 * @since 2021-02-06 23:26:10
 */
public interface UserEntityDao {
  
  /**
   * 通过ID查询单条数据
   *
   * @return 实例对象
   */
  UserEntity queryByUserName(@Param("name") String userName);
  
  /**
   * 通过ID查询单条数据
   *
   * @param id 主键
   * @return 实例对象
   */
  UserEntity queryById(@Param("id") int id);
  
  /**
   * 查询指定行数据
   *
   * @param offset 查询起始位置
   * @param limit  查询条数
   * @return 对象列表
   */
  List<UserEntity> queryAllByLimit(@Param("offset") int offset, @Param("limit") int limit);
  
  /**
   * 通过实体作为筛选条件查询
   *
   * @param userEntity 实例对象
   * @return 对象列表
   */
  List<UserEntity> queryAll(UserEntity userEntity);
  
  /**
   * 新增数据
   *
   * @param userEntity 实例对象
   * @return 影响行数
   */
  int insert(UserEntity userEntity);
  
  /**
   * 批量新增数据（MyBatis原生foreach方法）
   *
   * @param entities List<UserEntity> 实例对象列表
   * @return 影响行数
   */
  int insertBatch(@Param("entities") List<UserEntity> entities);
  
  /**
   * 批量新增或按主键更新数据（MyBatis原生foreach方法）
   *
   * @param entities List<UserEntity> 实例对象列表
   * @return 影响行数
   */
  int insertOrUpdateBatch(@Param("entities") List<UserEntity> entities);
  
  /**
   * 修改数据
   *
   * @param userEntity 实例对象
   * @return 影响行数
   */
  int update(UserEntity userEntity);
  
  /**
   * 通过主键删除数据
   *
   * @param id 主键
   * @return 影响行数
   */
  int deleteById(int id);
  
}
