package com.zky.infrastructure.persistent.po;

import lombok.Data;

import java.util.Date;

/**
 * @author: ZhangKaiYuan
 * @description: 抽奖活动次数配置表
 * @create: 2025/2/25
 */
@Data
public class RaffleActivityCount {
    /** 自增ID **/
    private Long id;
    /** 活动参与次数编号 **/
    private Long activityCountId;
    /** 总次数 **/
    private int totalCount;
    /** 日次数 **/
    private int dayCount;
    /** 月次数 **/
    private int monthCount;
    /** 创建时间 */
    private Date createTime;
    /** 更新时间 */
    private Date updateTime;

}
