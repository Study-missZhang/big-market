package com.zky.trigger.api.dto;

import lombok.Data;

/**
 * @author: ZhangKaiYuan
 * @description: 活动抽奖请求对象
 * @create: 2025/3/6
 */
@Data
public class ActivityDrawRequestDTO {

    /** 用户ID **/
    private String userId;
    /** 活动ID **/
    private Long activityId;
}
