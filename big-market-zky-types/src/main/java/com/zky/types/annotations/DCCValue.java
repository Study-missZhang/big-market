package com.zky.types.annotations;

import java.lang.annotation.*;

/**
 * 自定注解，动态配置中心
 */
/*
RetentionPolicy.RUNTIME 表示这个注解会在程序运行时（即 JVM 中）保留，可以通过反射访问。
具体来说：
    RetentionPolicy.SOURCE：注解只在源码中保留，编译时会被丢弃。
    RetentionPolicy.CLASS：注解会在编译时被保留，但在运行时会被丢弃。
    RetentionPolicy.RUNTIME：注解会在编译时和运行时都保留，可以通过反射来访问。
因为 RUNTIME 策略，所以 DDCValue 注解可以在程序运行时被反射获取和使用。
 */
@Retention(RetentionPolicy.RUNTIME)
/*
指定 DDCValue 可以应用的目标元素类型。
    ElementType.FIELD 表示这个注解只会应用于字段（即类的成员变量）。
    ElementType.METHOD：表示该注解可用于方法。
    ElementType.TYPE：表示该注解可用于类、接口、枚举等类型。
 */
@Target({ElementType.FIELD})
@Documented
public @interface DCCValue {

    String value() default "";
}
