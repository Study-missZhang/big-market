package com.zky.domain.award.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author: ZhangKaiYuan
 * @description: 用户积分奖品对象
 * @create: 2025/3/16
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserCreditAwardEntity {
    /** 用户ID */
    private String userId;
    /** 积分值 */
    private BigDecimal creditAmount;
}
