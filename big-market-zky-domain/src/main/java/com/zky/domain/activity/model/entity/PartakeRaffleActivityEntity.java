package com.zky.domain.activity.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: ZhangKaiYuan
 * @description: 参与抽奖活动实体对象
 * @create: 2025/3/3
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PartakeRaffleActivityEntity {

    /** 用户ID **/
    private String userId;
    /** 活动ID **/
    private Long activityId;
}
