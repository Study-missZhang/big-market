package com.zky.trigger.api;

import com.zky.types.enums.ResponseCode;
import com.zky.types.model.Response;

/**
 * @author: ZhangKaiYuan
 * @description: DCC 动态配置中心
 * @create: 2025/3/20
 */
public interface IDCCService {

    Response<Boolean> updateConfig(String key, String value);
}
