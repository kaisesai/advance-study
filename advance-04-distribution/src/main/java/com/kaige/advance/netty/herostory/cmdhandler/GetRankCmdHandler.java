package com.kaige.advance.netty.herostory.cmdhandler;

import com.kaige.advance.netty.herostory.msg.GameMsgProtocol;
import com.kaige.advance.netty.herostory.rank.RankService;
import io.netty.channel.ChannelHandlerContext;

/** 获取排行榜命令处理器 */
public class GetRankCmdHandler implements ICmdHandler<GameMsgProtocol.GetRankCmd> {
  
  @Override
  public void handle(ChannelHandlerContext ctx, GameMsgProtocol.GetRankCmd cmd) {
    RankService.getInstance().getRank(rankItems -> {
      // 构建排行榜数据并返回
      GameMsgProtocol.GetRankResult.Builder builder = GameMsgProtocol.GetRankResult.newBuilder();
      rankItems.forEach(rankItem -> builder.addRankItem(buildRankItem(rankItem)));
      ctx.writeAndFlush(builder.build());
      return null;
    });
  }
  
  /**
   * 构建排名明细
   *
   * @param rankItem
   * @return
   */
  private GameMsgProtocol.GetRankResult.RankItem buildRankItem(
    com.kaige.advance.netty.herostory.rank.RankItem rankItem) {
    return GameMsgProtocol.GetRankResult.RankItem.newBuilder().setRankId(rankItem.getRankId())
      .setUserId(rankItem.getUserId()).setUserName(rankItem.getUserName())
      .setHeroAvatar(rankItem.getHeroAvatar()).setWin(rankItem.getWin()).build();
  }
  
}
