package com.zky.domain.activity.model.aggregate;

import com.zky.domain.activity.model.entity.ActivityAccountEntity;
import com.zky.domain.activity.model.entity.ActivityOrderEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: ZhangKaiYuan
 * @description: 下单聚合对象
 * @create: 2025/2/26
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateOrderAggregate {

    /** 活动账户实体 **/
    private ActivityAccountEntity activityAccountEntity;
    /** 活动订单实体 **/
    private ActivityOrderEntity activityOrderEntity;
}
