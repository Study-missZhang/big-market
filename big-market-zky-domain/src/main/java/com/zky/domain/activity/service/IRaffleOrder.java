package com.zky.domain.activity.service;

import com.zky.domain.activity.model.entity.ActivityOrderEntity;
import com.zky.domain.activity.model.entity.ActivityShopCartEntity;

/**
 * @author: ZhangKaiYuan
 * @description: 抽奖活动订单接口
 * @create: 2025/2/26
 */
public interface IRaffleOrder {

    /**
     * 以sku创建抽奖活动订单，获得参与抽奖资格（可消耗次数）
     * @param activityShopCartEntity 活动sku实体，通过sku进行领取活动
     * @return  活动参与订单记录实体
     */
    ActivityOrderEntity createRaffleActivityOrder(ActivityShopCartEntity activityShopCartEntity);


}
