package com.zky.domain.activity.repository;

import com.zky.domain.activity.model.entity.ActivityCountEntity;
import com.zky.domain.activity.model.entity.ActivityEntity;
import com.zky.domain.activity.model.entity.ActivitySkuEntity;
import org.springframework.stereotype.Repository;

/**
 * @author: ZhangKaiYuan
 * @description: 活动仓储接口
 * @create: 2025/2/26
 *
 *  定义一个仓储接口，命令类去做需要干的事。
 */

public interface IActivityRepository {

    ActivitySkuEntity queryActivitySku(Long sku);
    ActivityEntity queryRaffleActivityByActivityId(Long activityId);

    ActivityCountEntity queryRaffleActivityCountByActivityCountId(Long activityCountId);
}
