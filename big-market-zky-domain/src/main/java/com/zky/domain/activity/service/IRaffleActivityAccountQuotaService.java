package com.zky.domain.activity.service;

import com.zky.domain.activity.model.entity.ActivityAccountEntity;
import com.zky.domain.activity.model.entity.DeliveryOrderEntity;
import com.zky.domain.activity.model.entity.SkuRechargeEntity;
import com.zky.domain.activity.model.entity.UnpaidActivityOrderEntity;

/**
 * @author: ZhangKaiYuan
 * @description: 抽奖活动订单接口
 * @create: 2025/2/26
 */
public interface IRaffleActivityAccountQuotaService {

    /**
     * 创建 sku 账户充值订单，给用户增加抽奖次数
     * <p>
     * 1. 在【打卡、签到、分享、对话、积分兑换】等行为动作下，创建出活动订单，给用户的活动账户【日、月】充值可用的抽奖次数。
     * 2. 对于用户可获得的抽奖次数，比如首次进来就有一次，则是依赖于运营配置的动作，在前端页面上。用户点击后，可以获得一次抽奖次数。
     *
     * @param skuRechargeEntity 活动商品充值实体对象
     * @return 活动ID
     */
    UnpaidActivityOrderEntity createSkuRechargeOrder(SkuRechargeEntity skuRechargeEntity);

    /**
     * 订单出货 - 积分支付
     * @param deliveryOrderEntity 出货实体
     */
    void updateOrder(DeliveryOrderEntity deliveryOrderEntity);

    /**
     * 查询活动账户 - 日参与次数
     * @param activityId 活动ID
     * @param userId 用户ID
     * @return 日参与次数
     */
    Integer queryRaffleActivityAccountDayPartakeCount(Long activityId, String userId);

    /**
     * 查询活动账户 - 总、日、月参与次数
     * @param userId 用户ID
     * @param activityId 活动ID
     * @return 账户实体对象
     */
    ActivityAccountEntity queryActivityAccountEntity(String userId, Long activityId);

    /**
     * 查询活动账户 - 总次数
     * @param userId 用户ID
     * @param activityId 活动ID
     * @return 用户总次数
     */
    Integer queryRaffleActivityAccountPartakeCount(String userId, Long activityId);
}
