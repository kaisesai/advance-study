package com.kaige.advance.netty.herostory.rank;

import com.kaige.advance.netty.herostory.async.AsyncOperationProcessor;
import com.kaige.advance.netty.herostory.async.GetRankAsyncOperation;
import com.kaige.advance.netty.herostory.mq.VictorMsg;
import com.kaige.advance.netty.herostory.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import redis.clients.jedis.Jedis;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * 排名服务
 */
@Slf4j
public class RankService {
  
  private static final RankService SERVICE = new RankService();
  
  private RankService() {
  }
  
  public static RankService getInstance() {
    return SERVICE;
  }
  
  /**
   * 获取排名
   *
   * @param callback 回调函数
   */
  public void getRank(Function<List<RankItem>, Void> callback) {
    if (Objects.isNull(callback)) {
      return;
    }
    // 使用 IO 线程池异步处理
    AsyncOperationProcessor.getInstance().process(new GetRankAsyncOperation(callback));
  }
  
  /**
   * 刷新排行版信息
   *
   * @param victorMsg
   */
  public void refreshRank(VictorMsg victorMsg) {
    if (Objects.isNull(victorMsg) || victorMsg.getWinnerId() <= 0 || victorMsg.getLoserId() <= 0) {
      return;
    }
    try (Jedis jedis = RedisUtil.getJedis()) {
      // 更新胜利次数
      jedis.hincrBy("u_" + victorMsg.getWinnerId(), "win", 1);
      // 更新失败次数
      jedis.hincrBy("u_" + victorMsg.getLoserId(), "loser", 1);
      
      // String key = "u_"+victorMsg.getWinnerId();
      // String basicinfo = jedis.hget(key, "basicinfo");
      // 查看胜利的的胜利次数
      String win = jedis.hget("u_" + victorMsg.getWinnerId(), "win");
      int winNum = NumberUtils.toInt(win);
      
      // 更新排行榜成员信息
      jedis.zadd("rank", winNum, String.valueOf(victorMsg.getWinnerId()));
      
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
  }
  
}
