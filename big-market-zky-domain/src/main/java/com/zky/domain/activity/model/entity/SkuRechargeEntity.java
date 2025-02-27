package com.zky.domain.activity.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: ZhangKaiYuan
 * @description:
 * @create: 2025/2/27
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SkuRechargeEntity {
    /** 用户ID **/
    private String userId;
    /** 商品sku - 把每一个组合当做一个商品 **/
    private Long sku;
    /** 业务仿重ID - 外部透传的，确保幂等 **/
    private String outBusinessNo;
}
