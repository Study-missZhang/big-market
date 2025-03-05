package com.zky.domain.award.repository;

import com.zky.domain.award.model.aggregate.UserAwardRecordAggregate;

/**
 * @author: ZhangKaiYuan
 * @description: 奖品服务仓储接口
 * @create: 2025/3/4
 */
public interface IAwardRepository {
    void saveUserAwardRecord(UserAwardRecordAggregate userAwardRecordAggregate);

}
