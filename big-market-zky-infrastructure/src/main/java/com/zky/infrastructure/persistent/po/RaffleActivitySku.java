package com.zky.infrastructure.persistent.po;

import lombok.Data;

import java.util.Date;

/**
 * @author: ZhangKaiYuan
 * @description: 抽奖活动sku持久对象
 * @create: 2025/2/26
 */
@Data
public class RaffleActivitySku {
    /** 自增ID **/
    private Long id;
    /** 商品sku - 把每一个组合当做一个商品 **/
    private Long sku;
    /** 活动ID **/
    private Long activityId;
    /** 活动个人参与次数ID **/
    private Long activityCountId;
    /** 商品库存 **/
    private int stockCount;
    /** 剩余库存 **/
    private int stockCountSurplus;
    /** 创建时间 **/
    private Date createTime;
    /** 更新时间 **/
    private Date updateTime;
}
