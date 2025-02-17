package com.zky.domain.strategy.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author: ZhangKaiYuan
 * @description: 策略奖品库存Key标识值对象
 * @create: 2025/2/13
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StrategyAwardStockKeyVO {
    //策略ID
    private Long strategyId;
    //奖品ID
    private Integer awardId;
}
