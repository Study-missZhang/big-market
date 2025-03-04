package com.zky.infrastructure.persistent.repository;

import cn.bugstack.middleware.db.router.strategy.IDBRouterStrategy;
import com.zky.domain.activity.event.ActivitySkuStockZeroMessageEvent;
import com.zky.domain.activity.model.aggregate.CreateOrderAggregate;
import com.zky.domain.activity.model.aggregate.CreatePartakeOrderAggregate;
import com.zky.domain.activity.model.entity.*;
import com.zky.domain.activity.model.valobj.ActivitySkuStockKeyVO;
import com.zky.domain.activity.model.valobj.ActivityStateVO;
import com.zky.domain.activity.model.valobj.UserRaffleOrderStateVO;
import com.zky.domain.activity.repository.IActivityRepository;
import com.zky.infrastructure.event.EventPublisher;
import com.zky.infrastructure.persistent.dao.*;
import com.zky.infrastructure.persistent.po.*;
import com.zky.infrastructure.persistent.redis.IRedisService;
import com.zky.types.common.Constants;
import com.zky.types.enums.ResponseCode;
import com.zky.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBlockingQueue;
import org.redisson.api.RDelayedQueue;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.sql.Time;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author: ZhangKaiYuan
 * @description: 活动仓储实现
 * @create: 2025/2/26
 */
@Repository
@Slf4j
public class ActivityRepository implements IActivityRepository {

    @Resource
    private IRedisService redisService;
    @Resource
    private IRaffleActivitySkuDao raffleActivitySkuDao;
    @Resource
    private IRaffleActivityDao raffleActivityDao;
    @Resource
    private IRaffleActivityCountDao raffleActivityCountDao;
    @Resource
    private IRaffleActivityAccountDao raffleActivityAccountDao;
    @Resource
    private IRaffleActivityAccountMonthDao raffleActivityAccountMonthDao;
    @Resource
    private IRaffleActivityAccountDayDao raffleActivityAccountDayDao;
    @Resource
    private IRaffleActivityOrderDao raffleActivityOrderDao;
    @Resource
    private TransactionTemplate transactionTemplate;
    @Resource
    private IDBRouterStrategy dbRouter;
    @Resource
    private EventPublisher eventPublisher;
    @Resource
    private ActivitySkuStockZeroMessageEvent activitySkuStockZeroMessageEvent;
    @Resource
    private IUserRaffleOrderDao userRaffleOrderDao;


    @Override
    public ActivitySkuEntity queryActivitySku(Long sku) {
        //直接从数据库中查询
        RaffleActivitySku raffleActivitySku = raffleActivitySkuDao.queryActivitySku(sku);
        return ActivitySkuEntity.builder()
                .sku(raffleActivitySku.getSku())
                .activityId(raffleActivitySku.getActivityId())
                .activityCountId(raffleActivitySku.getActivityCountId())
                .stockCount(raffleActivitySku.getStockCount())
                .stockCountSurplus(raffleActivitySku.getStockCountSurplus())
                .build();
    }

    @Override
    public ActivityEntity queryRaffleActivityByActivityId(Long activityId) {
        //优先缓存查询
        String cacheKey = Constants.RedisKey.ACTIVITY_KEY + activityId;
        ActivityEntity activityEntity = redisService.getValue(cacheKey);
        if(null != activityEntity) return activityEntity;
        //从数据库中获取
        RaffleActivity raffleActivity = raffleActivityDao.queryRaffleActivityByActivityId(activityId);
        activityEntity = ActivityEntity.builder()
                .strategyId(raffleActivity.getStrategyId())
                .activityName(raffleActivity.getActivityName())
                .activityDesc(raffleActivity.getActivityDesc())
                .state(ActivityStateVO.valueOf(raffleActivity.getState()))
                .activityId(raffleActivity.getActivityId())
                .beginDateTime(raffleActivity.getBeginDateTime())
                .endDateTime(raffleActivity.getEndDateTime())
                .build();
        //缓存写入
        redisService.setValue(cacheKey, activityEntity);
        return activityEntity;
    }

    @Override
    public ActivityCountEntity queryRaffleActivityCountByActivityCountId(Long activityCountId) {
        //优先缓存获取
        String cacheKey = Constants.RedisKey.ACTIVITY_COUNT_KEY + activityCountId;
        ActivityCountEntity activityCountEntity = redisService.getValue(cacheKey);
        if(null != activityCountEntity) return activityCountEntity;
        //从数据库中获取
        RaffleActivityCount raffleActivityCount = raffleActivityCountDao.queryRaffleActivityCountByActivityCountId(activityCountId);
        activityCountEntity = ActivityCountEntity.builder()
                .activityCountId(raffleActivityCount.getActivityCountId())
                .totalCount(raffleActivityCount.getTotalCount())
                .dayCount(raffleActivityCount.getDayCount())
                .monthCount(raffleActivityCount.getMonthCount())
                .build();
        //缓存写入
        redisService.setValue(cacheKey, activityCountEntity);
        return activityCountEntity;
    }

    @Override
    public void doSaveOrder(CreateOrderAggregate createOrderAggregate) {
        //在一个事务下进行
        try{
            //订单对象
            ActivityOrderEntity activityOrderEntity = createOrderAggregate.getActivityOrderEntity();
            RaffleActivityOrder raffleActivityOrder = new RaffleActivityOrder();
            raffleActivityOrder.setUserId(activityOrderEntity.getUserId());
            raffleActivityOrder.setSku(activityOrderEntity.getSku());
            raffleActivityOrder.setActivityId(activityOrderEntity.getActivityId());
            raffleActivityOrder.setActivityName(activityOrderEntity.getActivityName());
            raffleActivityOrder.setStrategyId(activityOrderEntity.getStrategyId());
            raffleActivityOrder.setOrderId(activityOrderEntity.getOrderId());
            raffleActivityOrder.setOrderTime(activityOrderEntity.getOrderTime());
            raffleActivityOrder.setTotalCount(activityOrderEntity.getTotalCount());
            raffleActivityOrder.setDayCount(activityOrderEntity.getDayCount());
            raffleActivityOrder.setMonthCount(activityOrderEntity.getMonthCount());
            raffleActivityOrder.setTotalCount(createOrderAggregate.getTotalCount());
            raffleActivityOrder.setDayCount(createOrderAggregate.getDayCount());
            raffleActivityOrder.setMonthCount(createOrderAggregate.getMonthCount());
            raffleActivityOrder.setState(activityOrderEntity.getState().getCode());
            raffleActivityOrder.setOutBusinessNo(activityOrderEntity.getOutBusinessNo());

            //账户对象
            RaffleActivityAccount raffleActivityAccount = new RaffleActivityAccount();
            raffleActivityAccount.setUserId(createOrderAggregate.getUserId());
            raffleActivityAccount.setActivityId(createOrderAggregate.getActivityId());
            raffleActivityAccount.setTotalCount(createOrderAggregate.getTotalCount());
            raffleActivityAccount.setTotalCountSurplus(createOrderAggregate.getTotalCount());
            raffleActivityAccount.setDayCount(createOrderAggregate.getDayCount());
            raffleActivityAccount.setDayCountSurplus(createOrderAggregate.getDayCount());
            raffleActivityAccount.setMonthCount(createOrderAggregate.getMonthCount());
            raffleActivityAccount.setMonthCountSurplus(createOrderAggregate.getMonthCount());

            //以用户ID作为切分键，通过 doRouter 设定路由【这样就保证了下面的操作，都是同一个链接下，也就保证了事务的特性】
            dbRouter.doRouter(createOrderAggregate.getUserId());
            //  编程事务
            transactionTemplate.execute(status -> {
                try {
                    //1.写入订单账户
                    raffleActivityOrderDao.insert(raffleActivityOrder);

                    //2.更新订单账户
                    int count = raffleActivityAccountDao.updateAccountQuota(raffleActivityAccount);

                    //3.创建账户 - 当更新为0时，创建新账户
                    if(0 == count){
                        raffleActivityAccountDao.insert(raffleActivityAccount);
                    }
                    return 1;
                }catch (DuplicateKeyException e){
                    status.setRollbackOnly();
                    log.error("写入订单记录，唯一索引冲突 userId: {} activityId: {} sku: {}", activityOrderEntity.getUserId(), activityOrderEntity.getActivityId(), activityOrderEntity.getSku(), e);
                    throw new AppException(ResponseCode.INDEX_DUP.getCode());
                }
            });
        }finally {
            dbRouter.clear();
        }
    }

    @Override
    public void cacheActivitySkuStockCount(String cacheKey, Integer stockCount) {
        if (redisService.isExists(cacheKey)) return;
        redisService.setAtomicLong(cacheKey, stockCount);
    }

    @Override
    public boolean subtractionActivitySkuStock(Long sku, String cacheKey, Date endDateTime) {
        long surplus = redisService.decr(cacheKey);
        if (surplus == 0) {
            // 库存消耗没了以后，发送MQ消息，更新数据库库存
            eventPublisher.publish(activitySkuStockZeroMessageEvent.topic(), activitySkuStockZeroMessageEvent.buildEventMessage(sku));
            return false;
        } else if (surplus < 0) {
            // 库存小于0，恢复为0个
            redisService.setAtomicLong(cacheKey, 0);
            return false;
        }

        // 1. 按照cacheKey decr 后的值，如 99、98、97 和 key 组成为库存锁的key进行使用。
        // 2. 加锁为了兜底，如果后续有恢复库存，手动处理等【运营是人来操作，会有这种情况发放，系统要做防护】，也不会超卖。因为所有的可用库存key，都被加锁了。
        // 3. 设置加锁时间为活动到期 + 延迟1天
        String lockKey = cacheKey + Constants.UNDERLINE + surplus;
        long expireMillis = endDateTime.getTime() - System.currentTimeMillis() + TimeUnit.DAYS.toMillis(1);
        Boolean lock = redisService.setNx(lockKey, expireMillis, TimeUnit.MILLISECONDS);
        if (!lock) {
            log.info("活动sku库存加锁失败 {}", lockKey);
        }
        return lock;
    }

    @Override
    public void activitySkuStockConsumeSendQueue(ActivitySkuStockKeyVO activitySkuStockKeyVO) {
        String cacheKey = Constants.RedisKey.ACTIVITY_SKU_COUNT_QUERY_KEY;
        RBlockingQueue<ActivitySkuStockKeyVO> blockingQueue = redisService.getBlockingQueue(cacheKey);
        RDelayedQueue<ActivitySkuStockKeyVO> delayedQueue = redisService.getDelayedQueue(blockingQueue);
        delayedQueue.offer(activitySkuStockKeyVO, 3, TimeUnit.SECONDS);
    }

    @Override
    public ActivitySkuStockKeyVO takeQueueValue() {
        String cacheKey = Constants.RedisKey.ACTIVITY_SKU_COUNT_QUERY_KEY;
        RBlockingQueue<ActivitySkuStockKeyVO> destinationQueue = redisService.getBlockingQueue(cacheKey);
        return destinationQueue.poll();
    }

    @Override
    public void clearQueueValue() {
        String cacheKey = Constants.RedisKey.ACTIVITY_SKU_COUNT_QUERY_KEY;
        RBlockingQueue<ActivitySkuStockKeyVO> destinationQueue = redisService.getBlockingQueue(cacheKey);
        destinationQueue.clear();
    }

    @Override
    public void updateActivitySkuStock(Long sku) {
        raffleActivitySkuDao.updateActivitySkuStock(sku);
    }

    @Override
    public void clearActivitySkuStock(Long sku) {
        raffleActivitySkuDao.clearActivitySkuStock(sku);
    }

    @Override
    public UserRaffleOrderEntity queryNoUserRaffleOrder(PartakeRaffleActivityEntity partakeRaffleActivityEntity) {
        //查询数据
        UserRaffleOrder userRaffleOrderReq = new UserRaffleOrder();
        userRaffleOrderReq.setUserId(partakeRaffleActivityEntity.getUserId());
        userRaffleOrderReq.setActivityId(partakeRaffleActivityEntity.getActivityId());
        UserRaffleOrder userRaffleOrderReqs = userRaffleOrderDao.queryNoUserRaffleOrder(userRaffleOrderReq);
        //不存在未使用的对象 返回null，进行下一步
        if(null == userRaffleOrderReqs) return null;
        //存在，构建实体对象
        UserRaffleOrderEntity userRaffleOrderEntity = new UserRaffleOrderEntity();
        userRaffleOrderEntity.setUserId(userRaffleOrderReqs.getUserId());
        userRaffleOrderEntity.setActivityId(userRaffleOrderReqs.getActivityId());
        userRaffleOrderEntity.setActivityName(userRaffleOrderReqs.getActivityName());
        userRaffleOrderEntity.setStrategyId(userRaffleOrderReqs.getStrategyId());
        userRaffleOrderEntity.setOrderId(userRaffleOrderReqs.getOrderId());
        userRaffleOrderEntity.setOrderTime(userRaffleOrderReqs.getOrderTime());
        userRaffleOrderEntity.setOrderState(UserRaffleOrderStateVO.valueOf(userRaffleOrderReqs.getOrderState()));
        return userRaffleOrderEntity;
    }

    @Override
    public ActivityAccountEntity queryActivityAccountByUserId(String userId, Long activityId) {
        //查询活动账户总额度
        RaffleActivityAccount raffleActivityAccountReq = new RaffleActivityAccount();
        raffleActivityAccountReq.setUserId(userId);
        raffleActivityAccountReq.setActivityId(activityId);
        RaffleActivityAccount raffleActivityAccountReqs = raffleActivityAccountDao.queryActivityAccountByUserId(raffleActivityAccountReq);

        return ActivityAccountEntity.builder()
                .userId(raffleActivityAccountReqs.getUserId())
                .activityId(raffleActivityAccountReqs.getActivityId())
                .totalCount(raffleActivityAccountReqs.getTotalCount())
                .totalCountSurplus(raffleActivityAccountReqs.getTotalCountSurplus())
                .dayCount(raffleActivityAccountReqs.getDayCount())
                .dayCountSurplus(raffleActivityAccountReqs.getDayCountSurplus())
                .monthCount(raffleActivityAccountReqs.getMonthCount())
                .monthCountSurplus(raffleActivityAccountReqs.getMonthCountSurplus())
                .build();
    }

    @Override
    public ActivityAccountMonthEntity queryActivityAccountMonthByUserId(String userId, Long activityId) {
        RaffleActivityAccountMonth raffleActivityAccountMonthReq = new RaffleActivityAccountMonth();
        raffleActivityAccountMonthReq.setUserId(userId);
        raffleActivityAccountMonthReq.setActivityId(activityId);
        RaffleActivityAccountMonth raffleActivityAccountMonthReqs = raffleActivityAccountMonthDao.queryActivityAccountMonthByUserId(raffleActivityAccountMonthReq);
        if(null == raffleActivityAccountMonthReqs) return null;
        return ActivityAccountMonthEntity.builder()
                .userId(raffleActivityAccountMonthReqs.getUserId())
                .activityId(raffleActivityAccountMonthReqs.getActivityId())
                .month(raffleActivityAccountMonthReqs.getMonth())
                .monthCount(raffleActivityAccountMonthReqs.getMonthCount())
                .monthCountSurplus(raffleActivityAccountMonthReqs.getMonthCountSurplus())
                .build();
    }

    @Override
    public ActivityAccountDayEntity queryActivityAccountDayByUserId(String userId, Long activityId) {
        RaffleActivityAccountDay raffleActivityAccountDayReq = new RaffleActivityAccountDay();
        raffleActivityAccountDayReq.setUserId(userId);
        raffleActivityAccountDayReq.setActivityId(activityId);
        RaffleActivityAccountDay raffleActivityAccountDayReqs = raffleActivityAccountDayDao.queryActivityAccountDayByUserId(raffleActivityAccountDayReq);
        if(null == raffleActivityAccountDayReqs) return null;
        return ActivityAccountDayEntity.builder()
                .userId(raffleActivityAccountDayReqs.getUserId())
                .activityId(raffleActivityAccountDayReqs.getActivityId())
                .day(raffleActivityAccountDayReqs.getDay())
                .dayCount(raffleActivityAccountDayReqs.getDayCount())
                .dayCountSurplus(raffleActivityAccountDayReqs.getDayCountSurplus())
                .build();
    }

    @Override
    public void saveCreatePartakeOrderAggregate(CreatePartakeOrderAggregate createPartakeOrderAggregate) {
        //在事务里进行保存
        try {
            String userId = createPartakeOrderAggregate.getUserId();
            Long activityId = createPartakeOrderAggregate.getActivityId();
            ActivityAccountEntity activityAccountEntity = createPartakeOrderAggregate.getActivityAccountEntity();
            ActivityAccountMonthEntity activityAccountMonthEntity = createPartakeOrderAggregate.getActivityAccountMonthEntity();
            ActivityAccountDayEntity activityAccountDayEntity = createPartakeOrderAggregate.getActivityAccountDayEntity();
            UserRaffleOrderEntity userRaffleOrderEntity = createPartakeOrderAggregate.getUserRaffleOrderEntity();

            //统一切换路由，以下事务内的所有操作，都走一个路由
            dbRouter.doRouter(userId);
            transactionTemplate.execute(status -> {
                try {
                    //1.更新总账户 剩余库存 - 1
                    int totalCount = raffleActivityAccountDao.updateActivityAccountSubtractionQuota(
                            RaffleActivityAccount.builder()
                                    .userId(userId)
                                    .activityId(activityId)
                                    .build()
                    );
                    if(1 != totalCount){
                        status.setRollbackOnly();
                        log.warn("写入创建参与活动记录，更新总账户额度不足，异常 userId: {} activityId: {}", userId, activityId);
                        throw new AppException(ResponseCode.ACCOUNT_QUOTA_ERROR.getCode(), ResponseCode.ACCOUNT_QUOTA_ERROR.getInfo());
                    }

                    //2.月账户新增或者更新 判断月账户是否存在，当不存在则新增
                    if(createPartakeOrderAggregate.isExistAccountMonth()){
                        //存在，则更新月账户
                        int updateMonthCount = raffleActivityAccountMonthDao.updateActivityAccountMonthSubtractionQuota(
                                RaffleActivityAccountMonth.builder()
                                        .userId(userId)
                                        .activityId(activityId)
                                        .month(activityAccountMonthEntity.getMonth())
                                        .build()
                        );
                        if(1 != updateMonthCount){
                            //没有进行更新，回滚数据
                            status.setRollbackOnly();
                            log.warn("写入创建参与活动记录，更新月账户额度不足，异常 userId: {} activityId: {} month: {}", userId, activityId, activityAccountMonthEntity.getMonth());
                            throw new AppException(ResponseCode.ACCOUNT_MONTH_QUOTA_ERROR.getCode(), ResponseCode.ACCOUNT_MONTH_QUOTA_ERROR.getInfo());
                        }
                    }else {
                        //不存在，新增月账户
                        raffleActivityAccountMonthDao.insertActivityAccountMonth(
                                RaffleActivityAccountMonth.builder()
                                        .userId(activityAccountMonthEntity.getUserId())
                                        .activityId(activityAccountMonthEntity.getActivityId())
                                        .month(activityAccountMonthEntity.getMonth())
                                        .monthCount(activityAccountMonthEntity.getMonthCount())
                                        .monthCountSurplus(activityAccountMonthEntity.getMonthCountSurplus() - 1)
                                        .build());

                        //新创建月账户，则更新总账表中月镜像额度
                        raffleActivityAccountDao.updateActivityAccountMonthSurplusImageQuota(
                                RaffleActivityAccount.builder()
                                        .userId(userId)
                                        .activityId(activityId)
                                        .monthCountSurplus(activityAccountEntity.getMonthCountSurplus())
                                        .build()
                        );
                    }

                    //3.日账户新增或者更新 判断日账户是否存在，当不存在则新增
                    if(createPartakeOrderAggregate.isExistAccountDay()){
                        //存在，更新账户
                        int updateDayCount = raffleActivityAccountDayDao.updateActivityAccountDay(
                                RaffleActivityAccountDay.builder()
                                        .userId(activityAccountDayEntity.getUserId())
                                        .activityId(activityAccountDayEntity.getActivityId())
                                        .day(activityAccountDayEntity.getDay())
                                        .build()
                        );

                        if(1 != updateDayCount){
                            //更新失败，回滚
                            status.setRollbackOnly();
                            log.warn("写入创建参与活动记录，更新日账户额度不足，异常 userId: {} activityId: {} day: {}", userId, activityId, activityAccountDayEntity.getDay());
                            throw new AppException(ResponseCode.ACCOUNT_DAY_QUOTA_ERROR.getCode(), ResponseCode.ACCOUNT_DAY_QUOTA_ERROR.getInfo());
                        }
                    }else {
                        //不存在，创建一个日账户
                        raffleActivityAccountDayDao.insertActivityAccountDay(
                                RaffleActivityAccountDay.builder()
                                        .userId(activityAccountDayEntity.getUserId())
                                        .activityId(activityAccountDayEntity.getActivityId())
                                        .day(activityAccountDayEntity.getDay())
                                        .dayCount(activityAccountDayEntity.getDayCount())
                                        .dayCountSurplus(activityAccountDayEntity.getDayCountSurplus() - 1)
                                        .build()
                        );
                        //新创建月账户，则更新总账表中月镜像额度
                        raffleActivityAccountDao.updateActivityAccountDaySurplusImageQuota(
                                RaffleActivityAccount.builder()
                                        .userId(userId)
                                        .activityId(activityId)
                                        .dayCountSurplus(activityAccountEntity.getDayCountSurplus())
                                        .build()
                        );
                    }

                    //4.写入用户参与活动订单
                    userRaffleOrderDao.insert(
                            UserRaffleOrder.builder()
                                    .userId(userRaffleOrderEntity.getUserId())
                                    .activityId(userRaffleOrderEntity.getActivityId())
                                    .activityName(userRaffleOrderEntity.getActivityName())
                                    .strategyId(userRaffleOrderEntity.getStrategyId())
                                    .orderId(userRaffleOrderEntity.getOrderId())
                                    .orderTime(userRaffleOrderEntity.getOrderTime())
                                    .orderState(userRaffleOrderEntity.getOrderState().getCode())
                                    .build());

                    return 1;
                }catch (DuplicateKeyException e){
                    status.setRollbackOnly();
                    log.error("写入创建参与活动记录，唯一索引冲突 userId: {} activityId: {}", userId, activityId, e);
                    throw new AppException(ResponseCode.INDEX_DUP.getCode(), e);
                }
            });
        }finally {
            dbRouter.clear();
        }
    }

}
