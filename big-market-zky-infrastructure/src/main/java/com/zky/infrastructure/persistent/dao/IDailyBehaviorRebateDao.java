package com.zky.infrastructure.persistent.dao;

import com.zky.infrastructure.persistent.po.DailyBehaviorRebate;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author: ZhangKaiYuan
 * @description: 日常行为返利
 * @create: 2025/3/12
 */
@Mapper
public interface IDailyBehaviorRebateDao {
    /**
     * 根据用户行为查询返利配置
     * @param behaviorType 行为类型【sign、openai_pay】
     * @return 返利配置
     */
    List<DailyBehaviorRebate> queryDailyBehaviorRebateByBehaviorType(String behaviorType);
}
