package com.zky.aop;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.util.concurrent.RateLimiter;
import com.zky.types.annotations.DCCValue;
import com.zky.types.annotations.RateLimiterAccessInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * @author: ZhangKaiYuan
 * @description: 限流切面
 * @create: 2025/3/22
 */
@Slf4j
@Aspect
@Component
public class RateLimiterAOP {

    @DCCValue("rateLimiterSwitch:close")
    private String rateLimiterSwitch;


    private final Cache<String, RateLimiter> loginRecord = CacheBuilder.newBuilder()
            .expireAfterWrite(1, TimeUnit.MINUTES)
            .build();

    // 个人限频黑名单24h - 分布式业务场景，可以记录到 Redis 中
    private final Cache<String, Long> blacklist = CacheBuilder.newBuilder()
            .expireAfterWrite(24, TimeUnit.HOURS)
            .build();

    @Pointcut("@annotation(com.zky.types.annotations.RateLimiterAccessInterceptor)")
    public void aopPoint(){
    }

    /**
     * 实现限流和黑名单拦截
     * @param jp 连接点，可以控制目标方法的执行。如： jp.proceed() 来继续执行目标方法
     * @param rateLimiterAccessInterceptor
     * @return
     * @throws Throwable
     */
    @Around("aopPoint() && @annotation(rateLimiterAccessInterceptor)")
    public Object doRouter(ProceedingJoinPoint jp, RateLimiterAccessInterceptor rateLimiterAccessInterceptor) throws Throwable {
        //判断限流开关【open开启，close关闭】 关闭后，不会走限流拦截
        if(StringUtils.isBlank(rateLimiterSwitch) || "close".equals(rateLimiterSwitch)){
            return jp.proceed();
        }

        String key = rateLimiterAccessInterceptor.key();
        if(StringUtils.isBlank(key)){
            throw new RuntimeException("annotation RateLimiter uId is null！");
        }
        //1.获取拦截字段
        //使用getAttrValue 方法从目标方法的参数中提取与 key 对应的字段值，作为限流的唯一标识
        String keyAttr = getAttrValue(key, jp.getArgs());
        log.info("aop attr:{}", keyAttr);

        //2.黑名单拦截
        if(!"all".equals(keyAttr) && rateLimiterAccessInterceptor.blacklistCount() != 0 && null != blacklist.getIfPresent(keyAttr) && blacklist.getIfPresent(keyAttr) > rateLimiterAccessInterceptor.blacklistCount()){
            log.info("限流-黑名单拦截24h :{}", keyAttr);
            //回退方法。
            return fallbackMethodResult(jp, rateLimiterAccessInterceptor.fallBackMethod());
        }

        //3.获取限流->存储本地(Guava)缓存1分钟
        RateLimiter rateLimiter = loginRecord.getIfPresent(keyAttr);
        if(null == rateLimiter){
             rateLimiter = RateLimiter.create(rateLimiterAccessInterceptor.permitsPerSecond());
             loginRecord.put(keyAttr, rateLimiter);
        }

        //4.限流拦截
        if(!rateLimiter.tryAcquire()){
            if(rateLimiterAccessInterceptor.blacklistCount() != 0){
                if(null == blacklist.getIfPresent(keyAttr)){
                    blacklist.put(keyAttr, 1L);
                }else {
                    blacklist.put(keyAttr, blacklist.getIfPresent(keyAttr) + 1L);
                }
            }
            log.info("限流-超频次拦截：{}", keyAttr);
            return fallbackMethodResult(jp, rateLimiterAccessInterceptor.fallBackMethod());
        }

        //5.返回结果
        return jp.proceed();
    }

    /**
     * 调用用户配置的回调方法，当拦截后，返回回调结果。
     */
    private Object fallbackMethodResult(JoinPoint jp, String fallbackMethod) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Signature sig = jp.getSignature();
        MethodSignature methodSignature = (MethodSignature) sig;
        Method method = jp.getTarget().getClass().getMethod(fallbackMethod, methodSignature.getParameterTypes());
        return method.invoke(jp.getThis(), jp.getArgs());
    }


    /**
     * 实际根据自身业务调整，主要是为了获取通过某个值做拦截
     */
    public String getAttrValue(String attr, Object[] args){
        if(args[0] instanceof String){
            return args[0].toString();
        }
        String filedValue = null;
        for(Object arg : args){
            try {
                if(StringUtils.isNoneBlank(filedValue)){
                    break;
                }
                filedValue = String.valueOf(this.getValueByName(arg, attr));
            }catch (Exception e){
                log.error("获取路由属性值失败 attr{}", attr, e);
            }
        }
        return filedValue;
    }

    /**
     * 获取对象的特定属性值
     *
     * @param item 对象
     * @param name 属性名
     * @return 属性值
     * @author tang
     */
    private Object getValueByName(Object item, String name) {
        try {
            Field field = getFieldByName(item, name);
            if (field == null) {
                return null;
            }
            field.setAccessible(true);
            Object o = field.get(item);
            field.setAccessible(false);
            return o;
        } catch (IllegalAccessException e) {
            return null;
        }
    }

    /**
     * 根据名称获取方法，该方法同时兼顾继承类获取父类的属性
     *
     * @param item 对象
     * @param name 属性名
     * @return 该属性对应方法
     * @author tang
     */
    private Field getFieldByName(Object item, String name) {
        try {
            Field field;
            try {
                field = item.getClass().getDeclaredField(name);
            } catch (NoSuchFieldException e) {
                field = item.getClass().getSuperclass().getDeclaredField(name);
            }
            return field;
        } catch (NoSuchFieldException e) {
            return null;
        }
    }

}
