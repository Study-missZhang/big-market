package com.zky.infrastructure.persistent.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author: ZhangKaiYuan
 * @description: 抽奖活动账户-总次数
 * @create: 2025/2/25
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RaffleActivityAccount {
    /** 自增ID **/
    private Long id;
    /** 用户ID **/
    private String userId;
    /** 活动ID **/
    private Long activityId;
    /** 总次数 **/
    private int totalCount;
    /**
     * 总次数-剩余
     */
    private Integer totalCountSurplus;
    /** 日次数 **/
    private int dayCount;
    /** 日次数-剩余 **/
    private int dayCountSurplus;
    /** 月次数 **/
    private int monthCount;
    /** 月次数-剩余 **/
    private int monthCountSurplus;
    /** 创建时间 */
    private Date createTime;
    /** 更新时间 */
    private Date updateTime;

}
