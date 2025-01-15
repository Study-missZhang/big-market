package com.zky.domain.strategy.service.rule;

import com.zky.domain.strategy.model.entity.RuleActionEntity;
import com.zky.domain.strategy.model.entity.RuleMatterEntity;

/**
 * @author: ZhangKaiYuan
 * Date: 2025/1/10
 * 抽奖规则过滤接口
 */
public interface ILogicFilter<T extends RuleActionEntity.RaffleEntity> {
    RuleActionEntity<T> filter(RuleMatterEntity ruleMatterEntity);
}
