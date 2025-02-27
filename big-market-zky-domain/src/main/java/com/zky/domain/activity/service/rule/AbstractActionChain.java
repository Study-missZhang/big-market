package com.zky.domain.activity.service.rule;

import com.zky.domain.activity.model.entity.ActivityCountEntity;
import com.zky.domain.activity.model.entity.ActivityEntity;
import com.zky.domain.activity.model.entity.ActivitySkuEntity;

/**
 * @author: ZhangKaiYuan
 * @description: 下单规则责任链抽象类
 * @create: 2025/2/27
 */
public abstract class AbstractActionChain implements IActionChain{
    private IActionChain next;
    @Override
    public IActionChain next() {
        return next;
    }

    @Override
    public IActionChain appendNext(IActionChain next) {
        this.next = next;
        return next;
    }
}
