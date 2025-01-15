package com.zky.infrastructure.persistent.dao;

import com.zky.infrastructure.persistent.po.Strategy;
import com.zky.infrastructure.persistent.po.StrategyAward;
import com.zky.infrastructure.persistent.po.StrategyRule;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 抽奖策略Dao
 */
@Mapper
public interface IStrategyDao {

    List<Strategy> queryStrategyList();

    Strategy queryStrategyByStrategyId(Long strategyId);

}
