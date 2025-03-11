package com.zky.domain.strategy.service.rule.tree.impl;

import com.zky.domain.strategy.model.valobj.RuleLogicCheckTypeVO;
import com.zky.domain.strategy.model.valobj.StrategyAwardStockKeyVO;
import com.zky.domain.strategy.repository.IStrategyRepository;
import com.zky.domain.strategy.service.armory.IStrategyDispatch;
import com.zky.domain.strategy.service.rule.tree.ILogicTreeNode;
import com.zky.domain.strategy.service.rule.tree.factory.DefaultTreeFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @author: ZhangKaiYuan
 * Date: 2025/2/9
 * 库存处理规则
 */
@Slf4j
@Component("rule_stock")
public class RuleStockLogicTreeNode implements ILogicTreeNode {

    @Resource
    private IStrategyDispatch strategyDispatch;
    @Resource
    private IStrategyRepository strategyRepository;

    @Override
    public DefaultTreeFactory.TreeActionEntity logic(String userId, Long strategyId, Integer awardId, String ruleValue, Date endDateTime) {
        log.info("规则过滤-库存扣减 userId:{} strategyId:{} awardId:{}", userId, strategyId, awardId);
        //扣减库存
        Boolean status = strategyDispatch.subtractionAwardStock(strategyId, awardId, endDateTime);
        if(status){
            log.info("规则过滤-库存扣减-成功 userId:{} strategyId:{} awardId:{}", userId, strategyId, awardId);

            //写入延迟队列，延迟消费更新数据库记录。【在trigger的job；UpdateAwardStockJob 下消费队列，更新数据库记录】
            strategyRepository.awardStockConsumeSendQueue(StrategyAwardStockKeyVO.builder()
                    .strategyId(strategyId)
                    .awardId(awardId)
                    .build());

            return DefaultTreeFactory.TreeActionEntity.builder()
                    .ruleLogicCheckTypeVO(RuleLogicCheckTypeVO.TAKE_OVER)
                    .strategyAwardVO(DefaultTreeFactory.StrategyAwardVO.builder()
                            .awardId(awardId)
                            .awardRuleValue("")
                            .build())
                    .build();
        }

        //扣减失败
        log.info("规则过滤-库存扣减-失败，库存不足 userId:{} strategyId:{} awardId:{}", userId, strategyId, awardId);
        return DefaultTreeFactory.TreeActionEntity.builder()
                .ruleLogicCheckTypeVO(RuleLogicCheckTypeVO.ALLOW)
                .build();
    }
}
