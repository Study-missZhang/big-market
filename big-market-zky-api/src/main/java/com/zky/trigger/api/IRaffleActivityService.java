package com.zky.trigger.api;

import com.zky.trigger.api.dto.ActivityDrawRequestDTO;
import com.zky.trigger.api.dto.ActivityDrawResponseDTO;
import com.zky.trigger.api.dto.UserActivityAccountRequestDTO;
import com.zky.trigger.api.dto.UserActivityAccountResponseDTO;
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

    /**
     * 日历签到返利接口
     * @param userId 用户ID
     * @return 返利结果
     */
    Response<Boolean> calendarSignRebate(String userId);

    /**
     * 判断是否完成日历签到返利接口
     * @param userId 用户ID
     * @return 是否完成签到返利
     */
    Response<Boolean> isCalendarSignRebate(String userId);

    /**
     * 查询账户额度
     * @param request 用户请求对象
     * @return 用户账户应答对象
     */
    Response<UserActivityAccountResponseDTO> queryUserActivityAccount(UserActivityAccountRequestDTO request);
}
