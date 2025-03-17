package com.zky.domain.award.repository;

import com.zky.domain.award.model.aggregate.GiveOutPrizesAggregate;
import com.zky.domain.award.model.aggregate.UserAwardRecordAggregate;

/**
 * @author: ZhangKaiYuan
 * @description: 奖品服务仓储接口
 * @create: 2025/3/4
 */
public interface IAwardRepository {
    void saveUserAwardRecord(UserAwardRecordAggregate userAwardRecordAggregate);

    /**
     * 存储发放奖品
     * @param giveOutPrizesAggregate 发放奖品聚合对象
     */
    void saveGiveOutPrizesAggregate(GiveOutPrizesAggregate giveOutPrizesAggregate);

    /**
     * 根据award查询奖品配置信息
     * @param awardId
     * @return
     */
    String queryAwardConfig(Integer awardId);

    String queryAwardKey(Integer awardId);
}
