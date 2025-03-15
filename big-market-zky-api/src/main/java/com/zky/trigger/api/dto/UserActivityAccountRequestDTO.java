package com.zky.trigger.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: ZhangKaiYuan
 * @description: 用户活动账户请求对象
 * @create: 2025/3/14
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserActivityAccountRequestDTO {

    /** 用户ID */
    String userId;

    /** 活动ID */
    Long activityId;
}
