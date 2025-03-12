package com.zky.domain.rebate.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author: ZhangKaiYuan
 * @description: 行为类型枚举值对象
 * @create: 2025/3/12
 */
@Getter
@AllArgsConstructor
public enum BehaviorTypeVO {

    SIGN("sign", "签到(日历)"),
    OPENAI_PAY("openai_pay", "openai 外部支付完成")
    ;

    private String code;
    private String info;
}
