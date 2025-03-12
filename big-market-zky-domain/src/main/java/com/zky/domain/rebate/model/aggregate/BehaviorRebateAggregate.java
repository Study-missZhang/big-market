package com.zky.domain.rebate.model.aggregate;

import com.zky.domain.rebate.model.entity.BehaviorRebateOrderEntity;
import com.zky.domain.rebate.model.entity.TaskEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: ZhangKaiYuan
 * @description: 返利聚合对象【一个聚合对象就是一个事务】
 * @create: 2025/3/12
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BehaviorRebateAggregate {
    //用户ID
    private String userId;
    //返利订单实体对象
    private BehaviorRebateOrderEntity behaviorRebateOrderEntity;
    //Task任务实体对象
    private TaskEntity task;
}
