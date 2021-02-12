package com.kaige.advance.netty.herostory.async;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.kaige.advance.netty.herostory.MainThreadProcessor;
import com.kaige.advance.netty.herostory.rank.RankItem;
import com.kaige.advance.netty.herostory.util.RedisUtil;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Tuple;

import java.util.List;
import java.util.Set;
import java.util.function.Function;

/**
 * 获取排名异步操作
 */
public class GetRankAsyncOperation implements IAsyncOperation {
  
  /**
   * 回调函数
   */
  private final Function<List<RankItem>, Void> callback;
  
  private List<RankItem> rankItems;
  
  public GetRankAsyncOperation(Function<List<RankItem>, Void> callback) {
    this.callback = callback;
  }
  
  @Override
  public int getBindId() {
    // 使用用户名的哈希值与线程池数量取模操作
    return RandomUtils.nextInt(0, AsyncOperationProcessor.EXECUTOR_SERVICE_NUM);
  }
  
  @Override
  public void doAsync() {
    // 从 redis 中获取排行榜数据
    try (Jedis jedis = RedisUtil.getJedis()) {
      Set<Tuple> tuples = jedis.zrevrangeWithScores("rank", 0, 9);
      List<RankItem> objects = Lists.newArrayListWithCapacity(tuples.size());
      int i = 1;
      for (Tuple tuple : tuples) {
        // 分数（胜利次数）
        int winNum = (int) tuple.getScore();
        // 用户 id
        int userId = NumberUtils.toInt(tuple.getElement());
        
        // 获取用户基本信息
        String basicinfo = jedis.hget("u_" + userId, "basicinfo");
        if (StringUtils.isBlank(basicinfo)) {
          continue;
        }
        JSONObject jsonObject = JSONObject.parseObject(basicinfo);
        
        RankItem rankItem = new RankItem();
        rankItem.setRankId(i++);
        rankItem.setWin(winNum);
        rankItem.setUserName(jsonObject.getString("username"));
        rankItem.setHeroAvatar(jsonObject.getString("hero_avatar"));
        rankItem.setUserId(userId);
        
        objects.add(rankItem);
      }
      
      rankItems = objects;
    }
  }
  
  @Override
  public void doFinish() {
    // 使用主线程处理器
    MainThreadProcessor.getInstance().process(() -> callback.apply(rankItems));
    
  }
  
}
