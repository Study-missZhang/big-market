package com.zky.trigger.api.dto;

import lombok.Data;

/**
 * @author: ZhangKaiYuan
 * @description: 商品购物车请求对象
 * @create: 2025/3/18
 */
@Data
public class SkuProductShopCartRequestDTO {

    /** 用户ID */
    private String userId;
    /** sku商品 */
    private Long sku;
}
