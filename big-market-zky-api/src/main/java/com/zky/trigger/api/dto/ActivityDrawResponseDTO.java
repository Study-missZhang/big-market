package com.zky.trigger.api.dto;

import lombok.Builder;
import lombok.Data;

/**
 * @author: ZhangKaiYuan
 * @description: 活动抽奖返回对象
 * @create: 2025/3/6
 */
@Data
@Builder
public class ActivityDrawResponseDTO {

    /** 奖品ID **/
    private Integer awardId;
    /** 奖品名称 **/
    private String awardTitle;
    /** 奖品排序【策略奖品配置的奖品顺序】 **/
    private Integer awardIndex;
}
