package com.zky.domain.activity.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: ZhangKaiYuan
 * @description: 抽奖活动账户实体对象
 * @create: 2025/2/26
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ActivityAccountEntity {
    /** 用户ID **/
    private String userId;
    /** 活动ID **/
    private Long activityId;
    /** 总次数 **/
    private Integer totalCount;
    /**
     * 总次数-剩余
     */
    private Integer totalCountSurplus;
    /** 日次数 **/
    private Integer dayCount;
    /** 日次数-剩余 **/
    private Integer dayCountSurplus;
    /** 月次数 **/
    private Integer monthCount;
    /** 月次数-剩余 **/
    private Integer monthCountSurplus;
}
