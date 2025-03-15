package com.zky.domain.rebate.repository;

import com.zky.domain.rebate.model.aggregate.BehaviorRebateAggregate;
import com.zky.domain.rebate.model.entity.BehaviorRebateOrderEntity;
import com.zky.domain.rebate.model.valobj.BehaviorTypeVO;
import com.zky.domain.rebate.model.valobj.DailyBehaviorRebateVO;

import java.util.List;

/**
 * @author: ZhangKaiYuan
 * @description: 行为返利服务仓储接口
 * @create: 2025/3/12
 */
public interface IBehaviorRebateRepository {
    List<DailyBehaviorRebateVO> queryDailyBehaviorRebateConfig(BehaviorTypeVO behaviorTypeVO);

    void saveUserRebateRecord(String userId, List<BehaviorRebateAggregate> behaviorRebateAggregates);

    List<BehaviorRebateOrderEntity> queryOrderByOutBusinessNo(String userId, String outBusinessNo);
}
