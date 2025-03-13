package com.zky.domain.rebate.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author: ZhangKaiYuan
 * @description: 返利类型（sku 活动库存充值商品、integral 用户活动积分）
 * @create: 2025/3/13
 */
@Getter
@AllArgsConstructor
public enum RebateTypeVO {

    SKU("sku", "活动库存充值商品"),
    INTEGRAL("integral", "用户活动积分"),
    ;

    private String code;
    private String info;
}
