package com.zky.domain.strategy.service.rule.tree;

import com.zky.domain.strategy.service.rule.tree.factory.DefaultTreeFactory;

import java.util.Date;

/**
 * @author: ZhangKaiYuan
 * Date: 2025/2/9
 * 规则树接口
 */
public interface ILogicTreeNode {

    DefaultTreeFactory.TreeActionEntity logic(String userId, Long strategyId, Integer awardId, String ruleValue, Date endDateTime);
}
