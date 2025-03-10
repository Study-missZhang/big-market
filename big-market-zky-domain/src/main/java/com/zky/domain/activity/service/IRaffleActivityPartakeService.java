package com.zky.domain.activity.service;

import com.zky.domain.activity.model.entity.PartakeRaffleActivityEntity;
import com.zky.domain.activity.model.entity.UserRaffleOrderEntity;

/**
 * @author: ZhangKaiYuan
 * @description: 抽奖参与活动服务
 * @create: 2025/3/3
 */
public interface IRaffleActivityPartakeService {

    /**
     * 创建抽奖订单：用户参与抽奖活动，扣减活动账户库存，产生抽奖单。
     *              如存在未被使用的抽奖单则直接返回已存在的抽奖单。
     * @param partakeRaffleActivityEntity 参与抽奖活动实体对象
     * @return 用户抽奖订单
     */
    UserRaffleOrderEntity createOrder(PartakeRaffleActivityEntity partakeRaffleActivityEntity);

    /**
     * 创建抽奖订单：用户参与抽奖活动，扣减活动账户库存，产生抽奖单。
     *             如存在未被使用的抽奖单则直接返回已存在的抽奖单。
     * @param userId 用户ID
     * @param activityId 活动ID
     * @return 用户抽奖订单
     */
    UserRaffleOrderEntity createOrder(String userId, Long activityId);
}
