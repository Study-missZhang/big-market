package com.zky.trigger.api.dto;

import lombok.Data;

/**
 * @author: ZhangKaiYuan
 * @description: 抽奖奖品列表，请求对象
 * @create: 2025/2/16
 */
@Data
public class RaffleAwardListRequestDTO {
    @Deprecated
    //抽奖策略ID
    Long strategyId;
    //活动ID
    Long activityId;
    //用户ID
    String userId;
}
