package com.kaige.distribution.transaction.pay.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.api.ApiController;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kaige.distribution.transaction.pay.entity.PayInfo;
import com.kaige.distribution.transaction.pay.service.PayInfoService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.List;

/**
 * 支付信息表(PayInfo)表控制层
 *
 * @author kaige
 * @since 2021-12-12 16:52:59
 */
@RestController
@RequestMapping("/payInfo")
public class PayInfoController extends ApiController {
  /** 服务对象 */
  @Resource private PayInfoService payInfoService;

  /**
   * 分页查询所有数据
   *
   * @param page 分页对象
   * @param payInfo 查询实体
   * @return 所有数据
   */
  @GetMapping(value = "/selectAll")
  public R selectAll(Page<PayInfo> page, PayInfo payInfo) {
    return success(this.payInfoService.page(page, new QueryWrapper<>(payInfo)));
  }

  /**
   * 通过主键查询单条数据
   *
   * @param id 主键
   * @return 单条数据
   */
  @GetMapping("get/{id}")
  public R selectOne(@PathVariable Serializable id) {
    return success(this.payInfoService.getById(id));
  }

  /**
   * 新增数据
   *
   * @param payInfo 实体对象
   * @return 新增结果
   */
  @PostMapping(value = "/insert")
  public R insert(@RequestBody PayInfo payInfo) {
    return success(this.payInfoService.save(payInfo));
  }

  /**
   * 修改数据
   *
   * @param payInfo 实体对象
   * @return 修改结果
   */
  @PutMapping(value = "/update")
  public R update(@RequestBody PayInfo payInfo) {
    return success(this.payInfoService.updateById(payInfo));
  }

  /**
   * 删除数据
   *
   * @param idList 主键结合
   * @return 删除结果
   */
  @DeleteMapping
  public R delete(@RequestParam("idList") List<Long> idList) {
    return success(this.payInfoService.removeByIds(idList));
  }
}
