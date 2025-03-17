package com.zky.infrastructure.persistent.dao;

import com.zky.infrastructure.persistent.po.UserCreditAccount;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author: ZhangKaiYuan
 * @description: 用户积分账户Dao
 * @create: 2025/3/16
 */
@Mapper
public interface IUserCreditAccountDao {
    int updateAddAmount(UserCreditAccount userCreditAccountReq);

    void insert(UserCreditAccount userCreditAccountReq);
}
