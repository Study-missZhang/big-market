package com.zky.domain.activity.service;

import com.alibaba.fastjson.JSON;
import com.zky.domain.activity.model.entity.*;
import com.zky.domain.activity.repository.IActivityRepository;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;

/**
 * @author: ZhangKaiYuan
 * @description: 抽奖活动抽象类，定义标准的流程
 * @create: 2025/2/26
 */
@Slf4j
public abstract class AbstractRaffleActivity implements IRaffleOrder{


    protected IActivityRepository activityRepository;

    //使用构造函数注入
    public AbstractRaffleActivity(IActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    //获得活动参与订单实体
    @Override
    public ActivityOrderEntity createRaffleActivityOrder(ActivityShopCartEntity activityShopCartEntity) {
        //1.通过sku查询活动信息
        ActivitySkuEntity activitySkuEntity = activityRepository.queryActivitySku(activityShopCartEntity.getSku());
        //2.查询活动信息
        ActivityEntity activityEntity = activityRepository.queryRaffleActivityByActivityId(activitySkuEntity.getActivityId());
        //3.查询活动次数
        ActivityCountEntity activityCountEntity = activityRepository.queryRaffleActivityCountByActivityCountId(activitySkuEntity.getActivityCountId());

        log.info("查询结果： {} {} {}", JSON.toJSONString(activitySkuEntity),
                JSON.toJSONString(activityEntity),
                JSON.toJSONString(activityCountEntity));

        return ActivityOrderEntity.builder().build();
    }
}
