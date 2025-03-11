package com.zky.domain.strategy.service.rule.tree.factory.engine.impl;

import com.zky.domain.strategy.model.valobj.RuleLogicCheckTypeVO;
import com.zky.domain.strategy.model.valobj.RuleTreeNodeLineVO;
import com.zky.domain.strategy.model.valobj.RuleTreeNodeVO;
import com.zky.domain.strategy.model.valobj.RuleTreeVO;
import com.zky.domain.strategy.service.rule.tree.ILogicTreeNode;
import com.zky.domain.strategy.service.rule.tree.factory.DefaultTreeFactory;
import com.zky.domain.strategy.service.rule.tree.factory.engine.IDecisionTreeEngine;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author: ZhangKaiYuan
 * @description: 决策树引擎
 * @create: 2025/2/9
 */
@Slf4j
public class DecisionTreeEngine implements IDecisionTreeEngine {

    private final Map<String, ILogicTreeNode> logicTreeNodeGroup;
    private final RuleTreeVO ruleTreeVO;

    public DecisionTreeEngine(Map<String, ILogicTreeNode> logicTreeNodeGroup, RuleTreeVO ruleTreeVO) {
        this.logicTreeNodeGroup = logicTreeNodeGroup;
        this.ruleTreeVO = ruleTreeVO;
    }

    @Override
    public DefaultTreeFactory.StrategyAwardVO process(String userId, Long strategyId, Integer awardId, Date endDateTime) {
        DefaultTreeFactory.StrategyAwardVO strategyAwardData = null;

        /**
         * 获取起始节点[根节点记录了第一个要执行的规则]
         * 根据Map根据规则树根节点获取到规则树根节点对象
         */
        String nextNode = ruleTreeVO.getTreeRootRuleNode();
        Map<String, RuleTreeNodeVO> treeNodeMap = ruleTreeVO.getTreeNodeMap();

        // 获取起始节点「根节点记录了第一个要执行的规则」
        RuleTreeNodeVO ruleTreeNode = treeNodeMap.get(nextNode);

        //循环这个节点，判断是否还有下一个规则
        while(null != nextNode){
            //获取决策节点
            ILogicTreeNode logicTreeNode = logicTreeNodeGroup.get(ruleTreeNode.getRuleKey());
            String ruleValue = ruleTreeNode.getRuleValue();

            //对决策节点过滤逻辑
            DefaultTreeFactory.TreeActionEntity logicEntity = logicTreeNode.logic(userId, strategyId, awardId, ruleValue, endDateTime);
            //通过逻辑获取到结果
            RuleLogicCheckTypeVO ruleLogicCheckTypeVO = logicEntity.getRuleLogicCheckTypeVO();
            strategyAwardData = logicEntity.getStrategyAwardVO();
            log.info("决策树引擎【{}】treeId:{} node:{} code:{}", ruleTreeVO.getTreeName(), ruleTreeVO.getTreeId(), nextNode, ruleLogicCheckTypeVO.getCode());

            //获取下一个节点
            nextNode = nextNode(ruleLogicCheckTypeVO.getCode(), ruleTreeNode.getTreeNodeLineVOList());
            ruleTreeNode = treeNodeMap.get(nextNode);
        }
        //返回最终结果
        return strategyAwardData;
    }

    private String nextNode(String matterValue, List<RuleTreeNodeLineVO> ruleTreeNodeLineVOList){
        //如果没有下一个节点 直接返回空
        if(null == ruleTreeNodeLineVOList || ruleTreeNodeLineVOList.isEmpty()) return null;

        for(RuleTreeNodeLineVO nodeLine : ruleTreeNodeLineVOList){
            if(decisionLogic(matterValue, nodeLine)){
                return nodeLine.getRuleNodeTo();
            }
        }
        return null;
    }

    public boolean decisionLogic(String matterValue, RuleTreeNodeLineVO nodeLine){
        switch (nodeLine.getRuleLimitType()) {
            case EQUAL:
                return matterValue.equals(nodeLine.getRuleLimitValue().getCode());
            // 以下规则暂时不需要实现
            case GT:
            case LT:
            case GE:
            case LE:
            default:
                return false;
        }
    }
}
