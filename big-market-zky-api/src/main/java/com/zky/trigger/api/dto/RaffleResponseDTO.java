package com.zky.trigger.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: ZhangKaiYuan
 * @description: 抽奖响应参数
 * @create: 2025/2/16
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RaffleResponseDTO {
    //奖品ID
    private Integer awardId;
    //奖品排序
    private Integer awardIndex;
}
