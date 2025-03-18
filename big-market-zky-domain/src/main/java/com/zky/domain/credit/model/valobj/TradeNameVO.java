package com.zky.domain.credit.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @author: ZhangKaiYuan
 * @description: 交易名称枚举值
 * @create: 2025/3/17
 */
@Getter
@AllArgsConstructor
public enum TradeNameVO {

    REBATE("行为返利"),
    CONVERT_SKU("兑换抽奖"),

    ;

    private final String name;

}
