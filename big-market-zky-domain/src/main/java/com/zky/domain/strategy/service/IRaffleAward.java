package com.zky.domain.strategy.service;

import com.zky.domain.strategy.model.entity.StrategyAwardEntity;

import java.util.List;

/**
 * @author: ZhangKaiYuan
 * @description: 策略奖品查询接口
 * @create: 2025/2/16
 */
public interface IRaffleAward {

    /**
     * 根据策略ID查询抽奖奖品列表配置
     * @param strategyId
     * @return 奖品列表
     */
    List<StrategyAwardEntity> queryRaffleStrategyAwardList(Long strategyId);
}
