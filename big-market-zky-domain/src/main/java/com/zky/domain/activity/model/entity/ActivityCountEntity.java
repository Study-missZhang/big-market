package com.zky.domain.activity.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: ZhangKaiYuan
 * @description: 抽奖活动次数实体对象
 * @create: 2025/2/26
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ActivityCountEntity {
    /** 活动参与次数编号 **/
    private Long activityCountId;
    /** 总次数 **/
    private Integer totalCount;
    /** 日次数 **/
    private Integer dayCount;
    /** 月次数 **/
    private Integer monthCount;
}
