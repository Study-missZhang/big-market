package com.zky.domain.rebate.service;

import com.zky.domain.rebate.model.entity.BehaviorEntity;

import java.util.List;

/**
 * @author: ZhangKaiYuan
 * @description: 行为返利服务接口
 * @create: 2025/3/12
 */
public interface IBehaviorRebateService {

    /**
     * 创建行为动作的入账订单
     * @param behaviorEntity 行为实体对象
     * @return 订单ID
     */
    List<String> createOrder(BehaviorEntity behaviorEntity);

}
