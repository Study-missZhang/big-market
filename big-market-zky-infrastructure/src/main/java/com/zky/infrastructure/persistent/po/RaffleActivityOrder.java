package com.zky.infrastructure.persistent.po;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author: ZhangKaiYuan
 * @description: 抽奖活动单
 * @create: 2025/2/25
 */
@Data
public class RaffleActivityOrder {
    /** 自增ID **/
    private Long id;
    /** sku **/
    private Long sku;
    /** 用户ID **/
    private String userId;
    /** 活动ID **/
    private Long activityId;
    /** 活动名称 **/
    private String activityName;
    /** 策略ID **/
    private Long strategyId;
    /** 订单ID **/
    private String orderId;
    /** 下单时间 **/
    private Date orderTime;
    /** 总次数 **/
    private int totalCount;
    /** 日次数 **/
    private int dayCount;
    /** 月次数 **/
    private int monthCount;
    /** 支付金额【积分】 */
    private BigDecimal payAmount;
    /** 订单状态（not_used、used、expire） **/
    private String state;
    /** 业务仿重ID - 外部透传的，确保幂等 **/
    private String outBusinessNo;
    /** 创建时间 */
    private Date createTime;
    /** 更新时间 */
    private Date updateTime;

}
