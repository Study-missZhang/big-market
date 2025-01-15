package com.zky.domain.strategy.service;

import com.zky.domain.strategy.model.entity.RaffleAwardEntity;
import com.zky.domain.strategy.model.entity.RaffleFactorEntity;

/**
 * @author: ZhangKaiYuan
 * Date: 2025/1/9
 * 抽奖策略接口
 */
public interface IRaffleStrategy {
    RaffleAwardEntity performRaffle(RaffleFactorEntity raffleFactorEntity);
}
