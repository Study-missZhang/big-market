package com.zky.domain.credit.service;

import com.zky.domain.credit.model.entity.CreditAccountEntity;
import com.zky.domain.credit.model.entity.TradeEntity;

/**
 * @author: ZhangKaiYuan
 * @description: 积分调额接口【正逆向，增减积分】
 * @create: 2025/3/17
 */
public interface ICreditAdjustService {

    /**
     * 创建增加积分额度订单
     * @param tradeEntity 交易实体对象
     * @return 单号
     */
    String createOrder(TradeEntity tradeEntity);

    /**
     * 查询用户积分账户
     * @param userId 用户ID
     * @return 积分账户实体
     */
    CreditAccountEntity queryUserCreditAccount(String userId);
}
