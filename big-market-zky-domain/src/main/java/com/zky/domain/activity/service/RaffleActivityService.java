package com.zky.domain.activity.service;

import com.zky.domain.activity.repository.IActivityRepository;
import org.springframework.stereotype.Service;

/**
 * @author: ZhangKaiYuan
 * @description: 抽奖活动服务
 * @create: 2025/2/26
 */
@Service
public class RaffleActivityService extends AbstractRaffleActivity{
    public RaffleActivityService(IActivityRepository activityRepository) {
        super(activityRepository);
    }
}
