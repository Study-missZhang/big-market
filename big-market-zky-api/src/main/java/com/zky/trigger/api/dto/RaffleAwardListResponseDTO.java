package com.zky.trigger.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: ZhangKaiYuan
 * @description: 抽奖奖品列表，响应对象
 * @create: 2025/2/16
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RaffleAwardListResponseDTO {
    //奖品ID
    private Integer awardId;
    //奖品标题
    private String awardTitle;
    //奖品副标题
    private String awardSubtitle;
    //排序
    private Integer sort;
    //奖品次数规则 - 抽奖N次后解锁，未配置则为空
    private Integer awardRuleLockCount;
    //奖品是否解锁 - true 已解锁， false 未解锁
    private Boolean isAwardUnlock;
    //等待解锁次数 - 规定N次解锁减去用户已经抽奖次数
    private Integer waitUnLockCount;
}
