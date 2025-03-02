package com.zky.domain.activity.service.armory;

import com.zky.domain.activity.model.entity.ActivitySkuEntity;

/**
 * @author: ZhangKaiYuan
 * @description: 活动装配预热
 * @create: 2025/3/2
 */
public interface IActivityArmory {

    boolean assembleActivitySku(Long sku);

}