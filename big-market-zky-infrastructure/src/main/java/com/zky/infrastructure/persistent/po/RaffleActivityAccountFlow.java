package com.zky.infrastructure.persistent.po;

import lombok.Data;

import java.util.Date;

/**
 * @author: ZhangKaiYuan
 * @description: 抽奖活动账户流水表
 * @create: 2025/2/25
 */
@Data
public class RaffleActivityAccountFlow {
    /** 自增ID **/
    private Long id;
    /** 用户ID **/
    private String userId;
    /** 活动ID **/
    private Long activityId;
    /** 总次数 **/
    private int totalCount;
    /** 日次数 **/
    private int dayCount;
    /** 月次数 **/
    private int monthCount;
    /** 流水ID - 生成的唯一ID **/
    private String flowId;
    /** 流水渠道（activity-活动领取、sale-购买、redeem-兑换、free-免费赠送） **/
    private String flowChannel;
    /** 业务ID（外部透传，活动ID、订单ID） **/
    private String bizId;
    /** 创建时间 */
    private Date createTime;
    /** 更新时间 */
    private Date updateTime;
}
