package com.zky.domain.strategy.service.rule.tree.impl;

import com.zky.domain.strategy.model.valobj.RuleLogicCheckTypeVO;
import com.zky.domain.strategy.service.rule.tree.ILogicTreeNode;
import com.zky.domain.strategy.service.rule.tree.factory.DefaultTreeFactory;
import com.zky.types.common.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author: ZhangKaiYuan
 * Date: 2025/2/9
 * 兜底奖励规则
 */
@Slf4j
@Component("rule_luck_award")
public class RuleLuckAwardLogicTreeNode implements ILogicTreeNode {
    @Override
    public DefaultTreeFactory.TreeActionEntity logic(String userId, Long strategyId, Integer awardId, String ruleValue, Date endDateTime) {
        log.info("规则过滤-兜底奖品 userId:{} strategyId:{} awardId:{} ruleValue:{}", userId, strategyId, awardId, ruleValue);
        String[] splits = ruleValue.split(Constants.COLON);
        if(splits.length == 0){
            log.error("规则过滤-兜底奖品，兜底奖品未配置告警 userId:{} strategyId:{} awardId:{}", userId, strategyId, awardId);
            throw new RuntimeException("兜底奖品未配置 " + ruleValue);
        }

        //兜底奖励配置
        Integer luckAwardId = Integer.valueOf(splits[0]);
        String awardRuleValue = splits.length > 1 ? splits[1] : "";
        // 返回兜底奖品
        log.info("规则过滤-兜底奖品 userId:{} strategyId:{} awardId:{} awardRuleValue:{}", userId, strategyId, luckAwardId, awardRuleValue);
        return DefaultTreeFactory.TreeActionEntity.builder()
                .ruleLogicCheckTypeVO(RuleLogicCheckTypeVO.TAKE_OVER)
                .strategyAwardVO(DefaultTreeFactory.StrategyAwardVO.builder()
                        .awardId(awardId)
                        .awardRuleValue(ruleValue)
                        .build())
                .build();
    }
}
