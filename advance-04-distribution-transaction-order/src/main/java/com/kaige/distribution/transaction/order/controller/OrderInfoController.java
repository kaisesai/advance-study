package com.kaige.distribution.transaction.order.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.api.ApiController;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kaige.distribution.transaction.order.entity.OrderInfo;
import com.kaige.distribution.transaction.order.service.OrderInfoService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.List;

/**
 * (Order)表控制层
 *
 * @author kaige
 * @since 2021-12-04 22:28:31
 */
@RestController
@RequestMapping("order")
public class OrderInfoController extends ApiController {

  /** 服务对象 */
  @Resource private OrderInfoService orderInfoService;

  /**
   * 分页查询所有数据
   *
   * @param page 分页对象
   * @param orderInfo 查询实体
   * @return 所有数据
   */
  @GetMapping
  public R selectAll(Page<OrderInfo> page, OrderInfo orderInfo) {
    return success(this.orderInfoService.page(page, new QueryWrapper<>(orderInfo)));
  }

  /**
   * 通过主键查询单条数据
   *
   * @param id 主键
   * @return 单条数据
   */
  @GetMapping("{id}")
  public R selectOne(@PathVariable Serializable id) {
    return success(this.orderInfoService.getById(id));
  }

  /**
   * 新增数据
   *
   * @param orderInfo 实体对象
   * @return 新增结果
   */
  @PostMapping
  public R insert(@RequestBody OrderInfo orderInfo) {
    return success(this.orderInfoService.save(orderInfo));
  }

  /**
   * 修改数据
   *
   * @param orderInfo 实体对象
   * @return 修改结果
   */
  @PutMapping
  public R update(@RequestBody OrderInfo orderInfo) {
    return success(this.orderInfoService.updateById(orderInfo));
  }

  /**
   * 删除数据
   *
   * @param idList 主键结合
   * @return 删除结果
   */
  @DeleteMapping
  public R delete(@RequestParam("idList") List<Long> idList) {
    return success(this.orderInfoService.removeByIds(idList));
  }

  /**
   * 创建订单（本地事件表+MQ 方式实现分布式事务）
   *
   * @date 2021年12月12日
   * @param orderInfo 实体对象
   * @return 新增结果
   */
  @PostMapping(value = "createOrderForMQ")
  public R createOrderForMQ(@RequestBody OrderInfo orderInfo) {
    return success(this.orderInfoService.createOrderForMQ(orderInfo));
  }

  /**
   * 创建订单（LCN 模式）
   *
   * @date 2021年12月13日
   * @param orderInfo 实体对象
   * @return 新增结果
   */
  @PostMapping(value = "createOrderForLCN")
  public R createOrderForLCN(@RequestBody OrderInfo orderInfo) {
    return success(this.orderInfoService.createOrderForLCN(orderInfo));
  }

  /**
   * 创建订单（Seata 的 AT 模式）
   *
   * @date 2022年01月09日
   * @param orderInfo 实体对象
   * @return 新增结果
   */
  @PostMapping(value = "createOrderForSeata")
  public R createOrderForSeata(@RequestBody OrderInfo orderInfo) {
    return success(this.orderInfoService.createOrderForSeata(orderInfo));
  }
}
