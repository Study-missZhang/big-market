package com.zky.infrastructure.persistent.repository;

import cn.bugstack.middleware.db.router.strategy.IDBRouterStrategy;
import com.zky.domain.credit.model.aggregate.TradeAggregate;
import com.zky.domain.credit.model.entity.CreditAccountEntity;
import com.zky.domain.credit.model.entity.CreditOrderEntity;
import com.zky.domain.credit.repository.ICreditRepository;
import com.zky.infrastructure.persistent.dao.IUserCreditAccountDao;
import com.zky.infrastructure.persistent.dao.IUserCreditOrderDao;
import com.zky.infrastructure.persistent.po.UserCreditAccount;
import com.zky.infrastructure.persistent.po.UserCreditOrder;
import com.zky.infrastructure.persistent.redis.IRedisService;
import com.zky.types.common.Constants;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @author: ZhangKaiYuan
 * @description:
 * @create: 2025/3/17
 */
@Slf4j
@Repository
public class CreditRepository implements ICreditRepository {

    @Resource
    private IDBRouterStrategy dbRouter;
    @Resource
    private IRedisService redisService;
    @Resource
    private TransactionTemplate transactionTemplate;
    @Resource
    private IUserCreditAccountDao userCreditAccountDao;
    @Resource
    private IUserCreditOrderDao userCreditOrderDao;

    @Override
    public void saveUserCreditTradeOrder(TradeAggregate tradeAggregate) {
        String userId = tradeAggregate.getUserId();
        CreditAccountEntity creditAccountEntity = tradeAggregate.getCreditAccountEntity();
        CreditOrderEntity creditOrderEntity = tradeAggregate.getCreditOrderEntity();

        //积分账户转换为持久化对象
        UserCreditAccount userCreditAccountReq = new UserCreditAccount();
        userCreditAccountReq.setUserId(userId);
        userCreditAccountReq.setTotalAmount(creditAccountEntity.getAdjustAmount());
        // 知识；仓储往上有业务语义，仓储往下到 dao 操作是没有业务语义的。所以不用在乎这块使用的字段名称，直接用持久化对象即可。
        userCreditAccountReq.setAvailableAmount(creditAccountEntity.getAdjustAmount());

        //积分订单账户转换
        UserCreditOrder userCreditOrderReq = new UserCreditOrder();
        userCreditOrderReq.setUserId(creditOrderEntity.getUserId());
        userCreditOrderReq.setOrderId(creditOrderEntity.getOrderId());
        userCreditOrderReq.setTradeName(creditOrderEntity.getTradeName().getName());
        userCreditOrderReq.setTradeType(creditOrderEntity.getTradeType().getCode());
        userCreditOrderReq.setTradeAmount(creditOrderEntity.getTradeAmount());
        userCreditOrderReq.setOutBusinessNo(creditOrderEntity.getOutBusinessNo());

        //事务下进行操作
        RLock lock = redisService.getLock(Constants.RedisKey.USER_CREDIT_ACCOUNT_LOCK + userId + Constants.UNDERLINE + creditOrderEntity.getOutBusinessNo());
        try{
            lock.lock(3, TimeUnit.SECONDS);
            dbRouter.doRouter(userId);
            transactionTemplate.execute(status -> {
                try {
                    //1.保存账户积分
                    UserCreditAccount userCreditAccount = userCreditAccountDao.queryUserCreditAccount(userCreditAccountReq);
                    if(null == userCreditAccount){
                        userCreditAccountDao.insert(userCreditAccountReq);
                    }else {
                        userCreditAccountDao.updateAddAmount(userCreditAccountReq);
                    }
                    //2.保存账户订单
                    userCreditOrderDao.insert(userCreditOrderReq);
                }catch (DuplicateKeyException e){
                    status.setRollbackOnly();
                    log.error("调整账户积分额度异常，唯一索引冲突 userId:{} orderId:{}", userId, creditOrderEntity.getOrderId(), e);
                }catch (Exception e){
                    status.setRollbackOnly();
                    log.error("调整账户积分额度失败 userId:{} orderId:{}", userId, creditOrderEntity.getOrderId(), e);
                }
                return 1;
            });
        }finally {
            dbRouter.clear();
            lock.unlock();
        }
    }
}
