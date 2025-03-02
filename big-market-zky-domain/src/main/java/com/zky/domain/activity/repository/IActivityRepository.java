package com.zky.domain.activity.repository;

import com.zky.domain.activity.model.aggregate.CreateOrderAggregate;
import com.zky.domain.activity.model.entity.ActivityCountEntity;
import com.zky.domain.activity.model.entity.ActivityEntity;
import com.zky.domain.activity.model.entity.ActivitySkuEntity;
import com.zky.domain.activity.model.valobj.ActivitySkuStockKeyVO;

import java.util.Date;

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

    void doSaveOrder(CreateOrderAggregate createOrderAggregate);

    void cacheActivitySkuStockCount(String cacheKey, Integer stockCount);

    boolean subtractionActivitySkuStock(Long sku, String cacheKey, Date endDateTime);

    void activitySkuStockConsumeSendQueue(ActivitySkuStockKeyVO activitySkuStockKeyVO);

    ActivitySkuStockKeyVO takeQueueValue();

    void clearQueueValue();

    void updateActivitySkuStock(Long sku);

    void clearActivitySkuStock(Long sku);

}
