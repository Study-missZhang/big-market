package com.zky.domain.award.service.distribute;

import com.zky.domain.award.model.entity.DistributeAwardEntity;

/**
 * @author: ZhangKaiYuan
 * @description: 发放奖品接口
 * @create: 2025/3/16
 */
public interface IDistributeAward {

    //发放奖品方法
    void giveOutPrizes(DistributeAwardEntity distributeAwardEntity);
}
