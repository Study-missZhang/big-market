package com.zky.domain.strategy.repository;

import com.zky.domain.strategy.model.entity.StrategyAwardEntity;
import com.zky.domain.strategy.model.entity.StrategyEntity;
import com.zky.domain.strategy.model.entity.StrategyRuleEntity;
import com.zky.domain.strategy.model.valobj.RuleTreeVO;
import com.zky.domain.strategy.model.valobj.StrategyAwardRuleModelVO;
import com.zky.domain.strategy.model.valobj.StrategyAwardStockKeyVO;

import java.util.List;
import java.util.Map;

/**
 * @author Fuzhengwei bugstack.cn @小傅哥
 * @description 策略服务仓储接口
 * @create 2023-12-23 09:33
 */
public interface IStrategyRepository {

    List<StrategyAwardEntity> queryStrategyAwardList(Long strategyId);

    void storeStrategyAwardSearchRateTable(String key, Integer rateRange, Map<Integer, Integer> strategyAwardSearchRateTable);

    Integer getStrategyAwardAssemble(String key, Integer rateKey);

    int getRateRange(Long strategyId);

    int getRateRange(String key);

    StrategyEntity queryStrategyEntityByStrategyId(Long strategyId);

    StrategyRuleEntity queryStrategyRule(Long strategyId, String ruleModel);

    String queryStrategyRuleValue(Long strategyId, String ruleModel);

    String queryStrategyRuleValue(Long strategyId, Integer awardId, String ruleModel);

    StrategyAwardRuleModelVO queryStrategyAwardRuleModelVO(Long strategyId, Integer awardId);

    RuleTreeVO queryRuleTreeByTreeId(String treeId);

    /**
     * 缓存奖品库存
     * @param cacheKey key
     * @param awardCount 库存值
     */
    void cacheStrategyAwardCount(String cacheKey, Integer awardCount);

    /**
     * 缓存key, decr方式扣减库存
     * @param cacheKey key
     * @return 扣减结果
     */
    Boolean subtractionAwardStock(String cacheKey);

    /**
     * 写入奖品库存消费队列
     * @param strategyAwardStockKeyVO 对象值对象
     */
    void awardStockConsumeSendQueue(StrategyAwardStockKeyVO strategyAwardStockKeyVO);

    StrategyAwardStockKeyVO takeQueueValue() throws InterruptedException;

    void updateStrategyAwardStock(Long strategyId, Integer awardId);

    /**
     * 根据策略ID+奖品ID的唯一组合，查询奖品信息
     * @param strategyId 策略ID
     * @param awardId 奖品ID
     * @return 奖品信息
     */
    StrategyAwardEntity queryStrategyEntity(Long strategyId, Integer awardId);
}
