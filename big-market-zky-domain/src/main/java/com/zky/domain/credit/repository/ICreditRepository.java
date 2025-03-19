package com.zky.domain.credit.repository;

import com.zky.domain.credit.model.aggregate.TradeAggregate;
import com.zky.domain.credit.model.entity.CreditAccountEntity;

/**
 * @author: ZhangKaiYuan
 * @description: 用户积分仓储
 * @create: 2025/3/17
 */
public interface ICreditRepository {
    void saveUserCreditTradeOrder(TradeAggregate tradeAggregate);

    CreditAccountEntity queryUserCreditAccount(String userId);
}
