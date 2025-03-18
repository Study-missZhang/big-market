package com.zky.domain.activity.service.quota.policy.impl;

import com.zky.domain.activity.model.aggregate.CreateQuotaOrderAggregate;
import com.zky.domain.activity.model.valobj.OrderStateVO;
import com.zky.domain.activity.repository.IActivityRepository;
import com.zky.domain.activity.service.quota.policy.ITradePolicy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author: ZhangKaiYuan
 * @description: 积分兑换，支付类订单
 * @create: 2025/3/18
 */
@Service("credit_pay_trade")
public class CreditPayTradePolicy implements ITradePolicy {

    @Resource
    private IActivityRepository activityRepository;

    @Override
    public void trade(CreateQuotaOrderAggregate createQuotaOrderAggregate) {
        createQuotaOrderAggregate.setOrderState(OrderStateVO.wait_pay);
        activityRepository.doSaveCreditPayOrder(createQuotaOrderAggregate);
    }
}
