package com.liukai.advance.distribution.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liukai.advance.distribution.web.entity.ReadBookPd;
import com.liukai.advance.distribution.web.mapper.ReadBookPdMapper;
import com.liukai.advance.distribution.web.service.ReadBookPdService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

/**
 * (ReadBookPd)表服务实现类
 *
 * @author liukai
 * @since 2021-01-02 21:44:38
 */
@Slf4j
@Service("readBookPdService")
public class ReadBookPdServiceImpl extends ServiceImpl<ReadBookPdMapper, ReadBookPd>
  implements ReadBookPdService {
  
  @Autowired
  private ExecutorService myExecutorService;
  
  @Autowired
  private ElasticsearchRestTemplate elasticsearchTemplate;
  
  @Override
  public void putAllDataToElasticsearch() {
    // 读取 book 数据灌入到 es 中
    // 开启多线程查询数据
    // 先查出最后一条数据的 id，然后分批次查询
    Page<ReadBookPd> readBookPdPage = super.getBaseMapper().selectPage(new Page<>(1, 1), Wrappers
      .lambdaQuery(ReadBookPd.class).orderByDesc(ReadBookPd::getId));
    if (readBookPdPage.getTotal() == 0) {
      log.info("不好意思，没有 ReadBookPd 数据~");
      return;
    }
    
    ReadBookPd readBookPd = readBookPdPage.getRecords().get(0);
    Integer maxId = readBookPd.getId();
    log.info("ReadBookPd 最大 id 值为：{}", maxId);
    int total = maxId;
    int battch = total / 1000;
    log.info("ReadBookPd 分批：{} 次", battch);
    for (int i = 0; i < battch; i++) {
      // 查询数据，执行推送
      int finalI = i;
      // myExecutorService.execute(() -> {
      // 分批次执行
      // select * from table where id >x*1000 and id <=x*1000+1000
      int startBookId = finalI * 1000;
      int endBookId = startBookId + 1000;
      queryBookDataAndPutToES(startBookId, endBookId);
      // });
    }
    
    int count = battch * 1000;
    // 将剩余的数据灌入es
    if ((total - count) > 0) {
      queryBookDataAndPutToES(count, total);
    }
  }
  
  private void queryBookDataAndPutToES(int startBookId, int endBookId) {
    log.info("ReadBookPd 查询数据，startBookId：{}, endBookId：{}", startBookId, endBookId);
    LambdaQueryWrapper<ReadBookPd> query = Wrappers.lambdaQuery(ReadBookPd.class);
    query.gt(ReadBookPd::getId, startBookId).le(ReadBookPd::getId, endBookId);
    // 根据条件查询
    List<ReadBookPd> readBookPds = super.getBaseMapper().selectList(query);
    log.info("ReadBookPd 查询数据，startBookId：{}, endBookId：{}, 结果数：{}", startBookId, endBookId,
             readBookPds.size());
    
    // 创建文档
    List<IndexQuery> indexQueries = readBookPds.stream().map(
      readBookPd -> new IndexQueryBuilder().withId(readBookPd.getId().toString())
        .withObject(readBookPd).build()).collect(Collectors.toList());
    elasticsearchTemplate.bulkIndex(indexQueries, ReadBookPd.class);
  }
  
}
