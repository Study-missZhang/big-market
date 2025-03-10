package com.zky.infrastructure.persistent.dao;

import com.zky.infrastructure.persistent.po.RaffleActivity;
import com.zky.infrastructure.persistent.po.RaffleActivityAccount;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author: ZhangKaiYuan
 * @description: 抽奖活动表Dao
 * @create: 2025/2/25
 */
@Mapper
public interface IRaffleActivityDao {
    RaffleActivity queryRaffleActivityByActivityId(Long activityId);

    Long queryStrategyIdByActivityId(Long activityId);

    Long queryActivityIdByStrategyId(Long strategyId);

}
