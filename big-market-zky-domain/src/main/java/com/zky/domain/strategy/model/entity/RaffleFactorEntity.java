package com.zky.domain.strategy.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author: ZhangKaiYuan
 * Date: 2025/1/10
 * 抽奖因子（抽奖一些实体）
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RaffleFactorEntity {
    private String userId;
    private Long strategyId;
    /** 结束时间 */
    private Date endDateTime;

}
