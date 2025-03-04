package com.zky.domain.activity.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author: ZhangKaiYuan
 * @description:
 * @create: 2025/3/3
 */
@Getter
@AllArgsConstructor
public enum UserRaffleOrderStateVO {

    create("create", "创建"),
    used("used", "已使用"),
    cancel("cancel", "已做废"),
    ;

    private final String code;
    private final String desc;
}
