package com.zky.domain.award.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author: ZhangKaiYuan
 * @description: 账户状态枚举
 * @create: 2025/3/16
 */
@Getter
@AllArgsConstructor
public enum AccountStatusVO {
    open("open", "开启"),
    close("close", "冻结"),
    ;

    private final String code;
    private final String desc;
}
