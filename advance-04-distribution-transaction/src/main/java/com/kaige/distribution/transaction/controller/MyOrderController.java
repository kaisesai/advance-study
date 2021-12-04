package com.kaige.distribution.transaction.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.api.ApiController;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kaige.distribution.transaction.entity.MyOrder;
import com.kaige.distribution.transaction.service.MyOrderService;
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
public class MyOrderController extends ApiController {

  /** 服务对象 */
  @Resource private MyOrderService myOrderService;

  /**
   * 分页查询所有数据
   *
   * @param page 分页对象
   * @param myOrder 查询实体
   * @return 所有数据
   */
  @GetMapping
  public R selectAll(Page<MyOrder> page, MyOrder myOrder) {
    return success(this.myOrderService.page(page, new QueryWrapper<>(myOrder)));
  }

  /**
   * 通过主键查询单条数据
   *
   * @param id 主键
   * @return 单条数据
   */
  @GetMapping("{id}")
  public R selectOne(@PathVariable Serializable id) {
    return success(this.myOrderService.getById(id));
  }

  /**
   * 新增数据
   *
   * @param myOrder 实体对象
   * @return 新增结果
   */
  @PostMapping
  public R insert(@RequestBody MyOrder myOrder) {
    return success(this.myOrderService.save(myOrder));
  }

  /**
   * 修改数据
   *
   * @param myOrder 实体对象
   * @return 修改结果
   */
  @PutMapping
  public R update(@RequestBody MyOrder myOrder) {
    return success(this.myOrderService.updateById(myOrder));
  }

  /**
   * 删除数据
   *
   * @param idList 主键结合
   * @return 删除结果
   */
  @DeleteMapping
  public R delete(@RequestParam("idList") List<Long> idList) {
    return success(this.myOrderService.removeByIds(idList));
  }

  /**
   * 创建订单
   *
   * @param myOrder 实体对象
   * @return 新增结果
   */
  @PostMapping(value = "createOrder")
  public R createOrder(@RequestBody MyOrder myOrder) {
    return success(this.myOrderService.createOrder(myOrder));
  }
}
