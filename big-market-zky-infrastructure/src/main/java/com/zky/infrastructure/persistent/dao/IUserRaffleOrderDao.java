package com.zky.infrastructure.persistent.dao;

import cn.bugstack.middleware.db.router.annotation.DBRouter;
import cn.bugstack.middleware.db.router.annotation.DBRouterStrategy;
import com.zky.domain.activity.model.entity.PartakeRaffleActivityEntity;
import com.zky.domain.activity.model.entity.UserRaffleOrderEntity;
import com.zky.infrastructure.persistent.po.UserRaffleOrder;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author: ZhangKaiYuan
 * @description:
 * @create: 2025/3/3
 */
@Mapper
@DBRouterStrategy(splitTable = true)
public interface IUserRaffleOrderDao {

    @DBRouter
    UserRaffleOrder queryNoUserRaffleOrder(UserRaffleOrder userRaffleOrder);

    void insert(UserRaffleOrder userRaffleOrder);

    int updateUserRaffleOrderStateUsed(UserRaffleOrder userRaffleOrderReq);

}
