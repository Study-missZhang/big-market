package com.zky.domain.strategy.service;

import com.zky.domain.strategy.model.valobj.RuleWeightVO;

import java.util.List;
import java.util.Map;

/**
 * @author: ZhangKaiYuan
 * @description: 抽奖规则接口；提供对规则的业务功能查询
 * @create: 2025/3/11
 */
public interface IRaffleRule {

    /**
     * 根据规则树ID集合查询奖品中加锁数量的配置「部分奖品需要抽奖N次解锁」
     * @param treeIds 规则树ID值
     * @return key 规则树，value rule_lock 加锁值
     */
    Map<String, Integer> queryAwardRuleLockCount(String[] treeIds);

    /**
     * 查询奖品权重配置【使用策略ID查询】
     *
     * @param strategyId 策略ID
     * @return 权重规则
     */
    List<RuleWeightVO> queryAwardRuleWeight(Long strategyId);


    /**
     * 查询奖品权重配置【用户传进来的是activityID进行查询】
     *
     * @param activityId 活动ID
     * @return 权重规则
     */
    List<RuleWeightVO> queryAwardRuleWeightByActivityId(Long activityId);
}
