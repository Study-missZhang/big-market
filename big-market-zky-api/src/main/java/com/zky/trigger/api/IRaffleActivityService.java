package com.zky.trigger.api;

import com.zky.trigger.api.dto.ActivityDrawRequestDTO;
import com.zky.trigger.api.dto.ActivityDrawResponseDTO;
import com.zky.types.model.Response;

/**
 * @author: ZhangKaiYuan
 * @description: 抽奖活动服务
 * @create: 2025/3/6
 */
public interface IRaffleActivityService {

    /**
     * 预热装配接口
     * @param activityId 用户传入的活动id
     * @return 装配结果
     */
    Response<Boolean> armory(Long activityId);

    /**
     * 活动抽奖接口
     * @param request 请求对象
     * @return 返回结果
     */
    Response<ActivityDrawResponseDTO> draw(ActivityDrawRequestDTO request);
}
