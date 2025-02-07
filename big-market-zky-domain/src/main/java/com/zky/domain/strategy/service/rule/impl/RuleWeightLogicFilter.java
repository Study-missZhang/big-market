package com.zky.domain.strategy.service.rule.impl;

import com.zky.domain.strategy.model.entity.RuleActionEntity;
import com.zky.domain.strategy.model.entity.RuleMatterEntity;
import com.zky.domain.strategy.model.valobj.RuleLogicCheckTypeVO;
import com.zky.domain.strategy.repository.IStrategyRepository;
import com.zky.domain.strategy.service.annotation.LogicStrategy;
import com.zky.domain.strategy.service.rule.ILogicFilter;
import com.zky.domain.strategy.service.rule.factor.DefaultLogicFactory;
import com.zky.types.common.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author: ZhangKaiYuan
 * Date: 2025/1/10
 * 【抽奖前规则】根据抽奖权重返回可抽奖范围KEY
 */
@Slf4j
@Component
@LogicStrategy(logicMode = DefaultLogicFactory.LogicModel.RULE_WIGHT)
public class RuleWeightLogicFilter implements ILogicFilter<RuleActionEntity.RaffleBeforeEntity> {
    @Resource
    private IStrategyRepository repository;

    //先设置一个用户积分。后续调取redis进行更改
    public long userScore = 4500L;
    @Override
    public RuleActionEntity<RuleActionEntity.RaffleBeforeEntity> filter(RuleMatterEntity ruleMatterEntity) {
        log.info("规则过滤-权重范围 userId:{}, strategyId:{}, ruleModel:{}",
                ruleMatterEntity.getUserId(),
                ruleMatterEntity.getStrategyId(),
                ruleMatterEntity.getRuleModel());

        String userId = ruleMatterEntity.getUserId();
        Long strategyId = ruleMatterEntity.getStrategyId();
        String ruleValue = repository.queryStrategyRuleValue(ruleMatterEntity.getStrategyId(),
                ruleMatterEntity.getAwardId(), ruleMatterEntity.getRuleModel());

        //4000:102,103,104,105 5000:102,103,104,105,106,107 6000:102,103,104,105,106,107,108,109
        //1.根据用户ID查询用户消耗的积分值，本章节我们写死，后续从数据库中查询
        Map<Long, String > analyticalValueGroup = getAnalyticalValue(ruleValue);
        if(null == analyticalValueGroup || analyticalValueGroup.isEmpty()){
            return RuleActionEntity.<RuleActionEntity.RaffleBeforeEntity>builder()
                    .code(RuleLogicCheckTypeVO.ALLOW.getCode())
                    .info(RuleLogicCheckTypeVO.ALLOW.getInfo())
                    .build();
        }

        //2.转换成keys值，并默认排序
        List<Long> analyticalSortedKeys = new ArrayList<>(analyticalValueGroup.size());
        Collections.sort(analyticalSortedKeys);

        // 3. 找出最小符合的值，也就是【4500 积分，能找到 4000:102,103,104,105】、【5000 积分，能找到 5000:102,103,104,105,106,107】
       Long nextValue = analyticalSortedKeys.stream()
               .filter(key -> userScore >= key)
               .findFirst()
               .orElse(null);

        if(null != nextValue){
            return RuleActionEntity.<RuleActionEntity.RaffleBeforeEntity>builder()
                    .data(RuleActionEntity.RaffleBeforeEntity.builder()
                            .strategyId(strategyId)
                            .ruleWeightValueKey(analyticalValueGroup.get(nextValue))
                            .build())
                    .ruleModel(DefaultLogicFactory.LogicModel.RULE_WIGHT.getCode())
                    .code(RuleLogicCheckTypeVO.TAKE_OVER.getCode())
                    .info(RuleLogicCheckTypeVO.TAKE_OVER.getInfo())
                    .build();
        }

        return RuleActionEntity.<RuleActionEntity.RaffleBeforeEntity>builder()
                .code(RuleLogicCheckTypeVO.ALLOW.getCode())
                .info(RuleLogicCheckTypeVO.ALLOW.getInfo())
                .build();
    }

//解析ruleValue值
private Map<Long, String> getAnalyticalValue(String ruleValue) {
    String[] ruleValueGroups = ruleValue.split(Constants.SPACE);
    Map<Long, String> ruleValueMap = new HashMap<>();
    for (String ruleValueKey : ruleValueGroups){
        //检查是否为空
        if(null == ruleValueKey || ruleValueKey.isEmpty()){
            return ruleValueMap;
        }

        //不为空分割字符串求取键和值
        String[] parts = ruleValueKey.split(Constants.COLON);
        if(parts.length != 2){
            throw new IllegalArgumentException("rule_weight rule_rule invalid input format" + ruleValueKey);
        }
        ruleValueMap.put(Long.parseLong(parts[0]), ruleValueKey);
    }
    return ruleValueMap;
    }
}
