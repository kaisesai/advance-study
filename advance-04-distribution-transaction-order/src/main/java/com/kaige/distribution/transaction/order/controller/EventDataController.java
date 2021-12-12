package com.kaige.distribution.transaction.order.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.api.ApiController;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kaige.distribution.transaction.order.entity.EventData;
import com.kaige.distribution.transaction.order.service.EventDataService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.List;

/**
 * 本地事件表(EventData)表控制层
 *
 * @author kaige
 * @since 2021-11-28 00:06:40
 */
@RestController
@RequestMapping("eventData")
public class EventDataController extends ApiController {

  /** 服务对象 */
  @Resource private EventDataService eventDataService;

  /**
   * 分页查询所有数据
   *
   * @param page 分页对象
   * @param eventData 查询实体
   * @return 所有数据
   */
  @GetMapping
  public R selectAll(Page<EventData> page, EventData eventData) {
    return success(this.eventDataService.page(page, new QueryWrapper<>(eventData)));
  }

  /**
   * 通过主键查询单条数据
   *
   * @param id 主键
   * @return 单条数据
   */
  @GetMapping("{id}")
  public R selectOne(@PathVariable Serializable id) {
    return success(this.eventDataService.getById(id));
  }

  /**
   * 新增数据
   *
   * @param eventData 实体对象
   * @return 新增结果
   */
  @PostMapping
  public R insert(@RequestBody EventData eventData) {
    return success(this.eventDataService.save(eventData));
  }

  /**
   * 修改数据
   *
   * @param eventData 实体对象
   * @return 修改结果
   */
  @PutMapping
  public R update(@RequestBody EventData eventData) {
    return success(this.eventDataService.updateById(eventData));
  }

  /**
   * 删除数据
   *
   * @param idList 主键结合
   * @return 删除结果
   */
  @DeleteMapping
  public R delete(@RequestParam("idList") List<Long> idList) {
    return success(this.eventDataService.removeByIds(idList));
  }
}
