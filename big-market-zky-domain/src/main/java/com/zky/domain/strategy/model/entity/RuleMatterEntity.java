package com.zky.domain.strategy.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: ZhangKaiYuan
 * Date: 2025/1/10
 * 规则物料实体类对象，用于过滤规则的必要参数信息
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RuleMatterEntity {
    /**用户ID*/
    private String userId;
    /**策略ID*/
    private Long strategyId;
    /**奖品ID【则类型为策略，则不需要奖品ID】*/
    private Integer awardId;
    /**抽奖规则类型*/
    private String ruleModel;
}
