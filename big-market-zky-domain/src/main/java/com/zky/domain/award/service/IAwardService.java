package com.zky.domain.award.service;

import com.zky.domain.award.model.entity.UserAwardRecordEntity;

/**
 * @author: ZhangKaiYuan
 * @description: 奖品服务接口
 * @create: 2025/3/4
 */
public interface IAwardService {

    /**
     * 保存用户中奖流水，并且写一张Task表
     * @param userAwardRecordEntity
     */
    void saveUserAwardRecord(UserAwardRecordEntity userAwardRecordEntity);
}
