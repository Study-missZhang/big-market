package com.zky.domain.activity.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: ZhangKaiYuan
 * @description: 活动sku库存 key 值对象
 * @create: 2025/3/2
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ActivitySkuStockKeyVO {
    /** 商品sku */
    private Long sku;
    /** 活动ID */
    private Long activityId;
}
