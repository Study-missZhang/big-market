package com.zky.infrastructure.persistent.dao;

import cn.bugstack.middleware.db.router.annotation.DBRouter;
import com.zky.infrastructure.persistent.po.RaffleActivityAccountMonth;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author: ZhangKaiYuan
 * @description:
 * @create: 2025/3/3
 */
@Mapper
public interface IRaffleActivityAccountMonthDao {

    @DBRouter
    RaffleActivityAccountMonth queryActivityAccountMonthByUserId(RaffleActivityAccountMonth raffleActivityAccountMonthReq);

    int updateActivityAccountMonthSubtractionQuota(RaffleActivityAccountMonth raffleActivityAccountMonth);

    void insertActivityAccountMonth(RaffleActivityAccountMonth raffleActivityAccountMonth);
}
