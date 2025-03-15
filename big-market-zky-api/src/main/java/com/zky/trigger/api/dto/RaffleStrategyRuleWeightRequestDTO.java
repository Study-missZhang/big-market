package com.zky.trigger.api.dto;

import lombok.Data;

/**
 * @author: ZhangKaiYuan
 * @description: 抽奖策略规则，权重配置，查询N次抽奖可解锁奖品范围，请求对象
 * @create: 2025/3/15
 */
@Data
public class RaffleStrategyRuleWeightRequestDTO {
    /** 用户ID */
    String userId;
    /** 活动ID */
    Long activityId;
}
