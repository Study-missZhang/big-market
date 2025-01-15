package com.zky.domain.strategy.service.annotation;


import com.zky.domain.strategy.service.rule.factor.DefaultLogicFactory;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Fuzhengwei bugstack.cn @小傅哥
 * @description 策略自定义枚举
 * @create 2023-12-31 11:29
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface LogicStrategy {

    DefaultLogicFactory.LogicModel logicMode();

}
