package com.zky.domain.activity.service.quota;

import com.zky.domain.activity.model.entity.ActivityCountEntity;
import com.zky.domain.activity.model.entity.ActivityEntity;
import com.zky.domain.activity.model.entity.ActivitySkuEntity;
import com.zky.domain.activity.repository.IActivityRepository;
import com.zky.domain.activity.service.quota.rule.factory.DefaultActivityChainFactory;

/**
 * @author: ZhangKaiYuan
 * @description: 抽奖活动的支撑类
 * @create: 2025/2/27
 */
public class RaffleActivityAccountQuotaSupport {

    protected IActivityRepository activityRepository;

    protected DefaultActivityChainFactory defaultActivityChainFactory;

    public RaffleActivityAccountQuotaSupport(IActivityRepository activityRepository, DefaultActivityChainFactory defaultActivityChainFactory){
        this.activityRepository = activityRepository;
        this.defaultActivityChainFactory = defaultActivityChainFactory;
    }

    public ActivitySkuEntity queryActivitySku(Long sku){
        return activityRepository.queryActivitySku(sku);
    }
    public ActivityEntity queryRaffleActivityByActivityId(Long activityId){
        return activityRepository.queryRaffleActivityByActivityId(activityId);
    }

    public ActivityCountEntity queryRaffleActivityCountByActivityCountId(Long activityCountId){
        return activityRepository.queryRaffleActivityCountByActivityCountId(activityCountId);
    }
}
