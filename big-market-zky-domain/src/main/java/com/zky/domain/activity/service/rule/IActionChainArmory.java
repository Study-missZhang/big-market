package com.zky.domain.activity.service.rule;

/**
 * @author: ZhangKaiYuan
 * @description:
 * @create: 2025/2/27
 */
public interface IActionChainArmory {

    IActionChain next();

    IActionChain appendNext(IActionChain next);
}
