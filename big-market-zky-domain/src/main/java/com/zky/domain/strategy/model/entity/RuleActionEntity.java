package com.zky.domain.strategy.model.entity;

import com.zky.domain.strategy.model.vo.RuleLogicCheckTypeVO;
import lombok.*;

/**
 * @author: ZhangKaiYuan
 * Date: 2025/1/10
 * 抽奖规则动作实体
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RuleActionEntity<T extends RuleActionEntity.RaffleEntity>{

    private String code = RuleLogicCheckTypeVO.ALLOW.getCode();
    private String info = RuleLogicCheckTypeVO.ALLOW.getInfo();
    private String ruleModel;
    private T data;

    static public class RaffleEntity{

    }

    //抽奖前
    @EqualsAndHashCode(callSuper = true)
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    static public class RaffleBeforeEntity extends RaffleEntity{
        /**
         * 策略ID
         */
        private Long strategyId;
        /**
         * 权重Key值:在IStrategyDispatch中有使用策略ID和权重Key值查询奖品ID
         */
        private String ruleWeightValueKey;
        /**
         * 奖品ID
         */
        private Integer awardId;
    }

    // 抽奖之中
    static public class RaffleCenterEntity extends RaffleEntity {
    }

    // 抽奖之后
    static public class RaffleAfterEntity extends RaffleEntity {

    }

}
