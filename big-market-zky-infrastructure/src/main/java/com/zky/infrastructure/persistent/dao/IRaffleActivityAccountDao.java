package com.zky.infrastructure.persistent.dao;

import cn.bugstack.middleware.db.router.annotation.DBRouter;
import com.zky.infrastructure.persistent.po.RaffleActivityAccount;
import com.zky.infrastructure.persistent.po.RaffleActivityAccountDay;
import com.zky.infrastructure.persistent.po.RaffleActivityOrder;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author: ZhangKaiYuan
 * @description: 抽奖活动账户Dao
 * @create: 2025/2/25
 */
@Mapper
public interface IRaffleActivityAccountDao {

    void insert(RaffleActivityAccount raffleActivityAccount);

    int updateAccountQuota(RaffleActivityAccount raffleActivityAccount);

    @DBRouter
    RaffleActivityAccount queryActivityAccountByUserId(RaffleActivityAccount raffleActivityAccountReq);

    int updateActivityAccountSubtractionQuota(RaffleActivityAccount build);

    int updateActivityAccountMonthSurplusImageQuota(RaffleActivityAccount raffleActivityAccount);

    int updateActivityAccountDaySurplusImageQuota(RaffleActivityAccount raffleActivityAccount);

    RaffleActivityAccount queryAccountByUserId(RaffleActivityAccount raffleActivityAccount);

    int updateActivityAccountDaySubtractionQuota(RaffleActivityAccount build);

    int updateActivityAccountMonthSubtractionQuota(RaffleActivityAccount build);
}
