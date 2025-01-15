package com.zky.infrastructure.persistent.dao;

import com.zky.infrastructure.persistent.po.Strategy;
import com.zky.infrastructure.persistent.po.StrategyRule;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 策略规则Dao
 */
@Mapper
public interface IStrategyRuleDao {

    List<StrategyRule> queryStrategyRuleList();

    StrategyRule queryStrategyRule(StrategyRule strategyRule);

    String queryStrategyRuleValue(StrategyRule strategyRule);
}
