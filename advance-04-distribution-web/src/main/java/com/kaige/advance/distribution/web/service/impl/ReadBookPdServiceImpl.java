package com.kaige.advance.distribution.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kaige.advance.distribution.web.entity.ReadBookPd;
import com.kaige.advance.distribution.web.mapper.ReadBookPdMapper;
import com.kaige.advance.distribution.web.service.ReadBookPdService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

/**
 * (ReadBookPd)表服务实现类
 *
 * @author kaige
 * @since 2021-01-02 21:44:38
 */
@Slf4j
@Service("readBookPdService")
public class ReadBookPdServiceImpl extends ServiceImpl<ReadBookPdMapper, ReadBookPd>
  implements ReadBookPdService {
  
  public static final String BOOK_INDEX_CURRENT = "book:index:current";
  
  public static final String BOOK_INDEX_UPDATE = "book:index:update";
  
  @Autowired
  private ExecutorService myExecutorService;
  
  @Autowired
  private StringRedisTemplate stringRedisTemplate;
  
  @Autowired
  private ElasticsearchRestTemplate elasticsearchTemplate;
  
  @Override
  public void putAllDataToElasticsearch() {

    /*
    // 读取 book 数据灌入到 es 中
    // 开启多线程查询数据
    // 先查出最后一条数据的 id，然后分批次查询
    Page<ReadBookPd> readBookPdPage = super.getBaseMapper().selectPage(new Page<>(1, 1), Wrappers
      .lambdaQuery(ReadBookPd.class).orderByDesc(ReadBookPd::getId));
    if (readBookPdPage.getTotal() == 1) {
      log.info("不好意思，没有 ReadBookPd 数据~");
      return;
    }
    ReadBookPd readBookPd = readBookPdPage.getRecords().get(0);*/
    
    log.info("开始对 book 构建全量索引");
    // 获取当前使用中的索引，比如 book，或者 book_back
    String currentIndex = stringRedisTemplate.opsForValue().get(BOOK_INDEX_CURRENT);
    if (!StringUtils.hasText(currentIndex)) {
      currentIndex = "book";
    }
    String updateIndex = stringRedisTemplate.opsForValue().get(BOOK_INDEX_UPDATE);
    if (!StringUtils.hasText(updateIndex)) {
      updateIndex = "book_back";
    }
    log.info("正在服务中的索引集合为：{}，备份的索引集合为：{}", currentIndex, updateIndex);
    
    // 查询当前
    
    ReadBookPd readBookPd = super.getBaseMapper().selectOne(
      Wrappers.lambdaQuery(ReadBookPd.class).select(ReadBookPd::getId)
        .orderByDesc(ReadBookPd::getId).last(" limit 1 "));
    if (readBookPd == null) {
      log.info("不好意思，没有 ReadBookPd 数据，停止重建索引~");
      return;
    }
    
    Integer maxId = readBookPd.getId();
    log.info("ReadBookPd 最大 id 值为：{}", maxId);
    int total = maxId;
    int battch = total / 1000;
    // 求余，加上剩余批次
    if (total % battch != 0) {
      battch++;
    }
    log.info("ReadBookPd 分批：{} 次", battch);
    for (int i = 0; i < battch; i++) {
      // 查询数据，执行推送
      int finalI = i;
      // myExecutorService.execute(() -> {
      // 分批次执行
      // select * from table where id >x*1000 and id <=x*1000+1000
      int startBookId = finalI * 1000;
      int endBookId = startBookId + 1000;
      queryBookDataAndPutToES(updateIndex, startBookId, endBookId);
      // });
    }
    
    log.info("对 book 重建索引完毕，进行集合索引切换");
    stringRedisTemplate.opsForValue().set(BOOK_INDEX_CURRENT, updateIndex);
    stringRedisTemplate.opsForValue().set(BOOK_INDEX_UPDATE, currentIndex);
    log.info("正在服务中的索引集合为：{}，备份的索引集合为：{}", updateIndex, currentIndex);
  }
  
  private void queryBookDataAndPutToES(String index, int startBookId, int endBookId) {
    // 构件查询条件
    LambdaQueryWrapper<ReadBookPd> query = Wrappers.lambdaQuery(ReadBookPd.class)
      .gt(ReadBookPd::getId, startBookId).le(ReadBookPd::getId, endBookId);
    
    // 根据条件查询
    List<ReadBookPd> readBookPds = super.getBaseMapper().selectList(query);
    // log.info("ReadBookPd 查询数据，startBookId：{}, endBookId：{}, 结果数：{}", startBookId, endBookId,
    //          readBookPds.size());
    
    // 创建文档
    List<IndexQuery> indexQueries = readBookPds.stream().map(
      readBookPd -> new IndexQueryBuilder().withId(readBookPd.getId().toString())
        .withObject(readBookPd).build()).collect(Collectors.toList());
    elasticsearchTemplate.bulkIndex(indexQueries, IndexCoordinates.of(index));
    log.info("重建 book 索引完成，startBookId：{}, endBookId：{}, index:{} ,结果数：{}条", startBookId, endBookId,
             index, indexQueries.size());
  }
  
}
