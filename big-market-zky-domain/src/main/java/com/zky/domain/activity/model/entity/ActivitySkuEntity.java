package com.zky.domain.activity.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: ZhangKaiYuan
 * @description: 抽奖活动sku实体对象
 * @create: 2025/2/26
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ActivitySkuEntity {
    /** 商品sku - 把每一个组合当做一个商品 **/
    private Long sku;
    /** 活动ID **/
    private Long activityId;
    /** 活动个人参与次数ID **/
    private Long activityCountId;
    /** 商品库存 **/
    private Integer stockCount;
    /** 剩余库存 **/
    private Integer stockCountSurplus;
}
