package com.zky.domain.activity.service.quota.rule;

import com.zky.domain.activity.model.entity.ActivityCountEntity;
import com.zky.domain.activity.model.entity.ActivityEntity;
import com.zky.domain.activity.model.entity.ActivitySkuEntity;

/**
 * @author: ZhangKaiYuan
 * @description: 下单规则过滤接口
 * @create: 2025/2/27
 */
public interface IActionChain extends IActionChainArmory{

    boolean action(ActivitySkuEntity activitySkuEntity, ActivityEntity activityEntity, ActivityCountEntity activityCountEntity);
}
